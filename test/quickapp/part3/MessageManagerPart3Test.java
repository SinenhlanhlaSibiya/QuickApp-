/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/EmptyTestNGTest.java to edit this template
 */

package quickapp.part3;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.*;

/**
 * Unit tests for MessageManager using TestNG.
 */
public class MessageManagerPart3Test {

    private MessageManager manager;

    @BeforeMethod
    public void setUp() {
        // Make sure we start with a clean JSON file each test
        File f = new File("storedMessages.json");
        if (f.exists()) {
            // ignore result; if delete fails, tests will still run, just sharing file state
            f.delete();
        }
        manager = new MessageManager();
    }

    @Test
    public void testPopulateWithTestData() {
        manager.populateWithTestData();

        // According to populateWithTestData:
        // Sent: m1, m4 → 2
        // Disregarded: m3 → 1
        // Stored: m2, m5 → 2
        assertEquals(manager.getSentMessages().size(), 2, "Sent messages size should be 2");
        assertEquals(manager.getDisregardedMessages().size(), 1, "Disregarded messages size should be 1");
        assertEquals(manager.getStoredMessages().size(), 2, "Stored messages size should be 2");

        // messageHashes and messageIDs should be in sync with sent messages
        assertEquals(manager.getMessageHashes().size(), 2, "messageHashes size should match sent messages");
        assertEquals(manager.getMessageIDs().size(), 2, "messageIDs size should match sent messages");
    }

    @Test
    public void testAddSentMessageUpdatesHashesAndIds() {
        Message msg = new Message("+27123456789", "Hello from tests", 100);
        msg.setStatus("Sent");

        manager.addSentMessage(msg);

        assertTrue(manager.getSentMessages().contains(msg), "Sent messages should contain the added message");
        assertTrue(manager.getMessageHashes().contains(msg.getMessageHash()), "messageHashes should contain the hash");
        assertTrue(manager.getMessageIDs().contains(msg.getMessageID()), "messageIDs should contain the ID");
    }

    @Test
    public void testDisplaySenderRecipientAllWhenEmpty() {
        String result = manager.displaySenderRecipientAll();
        assertEquals(result, "No sent messages.", "Should indicate no sent messages when list is empty");
    }

    @Test
    public void testDisplaySenderRecipientAllWithData() {
        manager.populateWithTestData();

        String result = manager.displaySenderRecipientAll();
        // Basic checks – we don't know exact IDs but we know structure/text
        assertTrue(result.contains("Message ID:"), "Result should contain 'Message ID:'");
        assertTrue(result.contains("Sender: You"), "Sender should be 'You'");
        // Recipient from test data
        assertTrue(result.contains("+27834557896") || result.contains("0838884567"),
                "Result should contain at least one known recipient");
    }

    @Test
    public void testDisplayLongestSentMessage() {
        manager.populateWithTestData();

        String longestText = manager.displayLongestSentMessage();

        // From populateWithTestData, the longest SENT message is m1: "Did you get the cake?"
        assertEquals(longestText, "Did you get the cake?", "Should return the longest sent message text");
    }

    @Test
    public void testSearchByMessageIDFound() {
        manager.populateWithTestData();

        // Take a real ID from an existing sent message
        Message firstSent = manager.getSentMessages().get(0);
        String id = firstSent.getMessageID();

        String result = manager.searchByMessageID(id);

        assertNotEquals(result, "NOT_FOUND", "Expected message to be found");
        assertTrue(result.contains("Found:"), "Result should start with 'Found:'");
        assertTrue(result.contains(firstSent.getRecipient()), "Result should contain the recipient");
        assertTrue(result.contains(firstSent.getText()), "Result should contain the message text");
    }

    @Test
    public void testSearchByMessageIDNotFound() {
        manager.populateWithTestData();

        String result = manager.searchByMessageID("NON_EXISTING_ID");
        assertEquals(result, "NOT_FOUND", "Non-existing ID should return NOT_FOUND");
    }

    @Test
    public void testSearchByRecipientFound() {
        manager.populateWithTestData();

        // In test data, m1 has recipient "+27834557896"
        String result = manager.searchByRecipient("+27834557896");

        assertFalse(result.equals("No messages for that recipient."),
                "Should return at least one message");
        assertTrue(result.contains("Did you get the cake?"),
                "Should contain the text of the message for that recipient");
    }

    @Test
    public void testSearchByRecipientNotFound() {
        manager.populateWithTestData();

        String result = manager.searchByRecipient("+00000000000");
        assertEquals(result, "No messages for that recipient.",
                "Unknown recipient should give appropriate message");
    }

    @Test
    public void testDeleteByHashSuccess() {
        manager.populateWithTestData();

        // Get hash of first sent message
        Message firstSent = manager.getSentMessages().get(0);
        String hash = firstSent.getMessageHash();

        int originalSentSize = manager.getSentMessages().size();
        int originalHashesSize = manager.getMessageHashes().size();
        int originalIdsSize = manager.getMessageIDs().size();

        boolean deleted = manager.deleteByHash(hash);
        assertTrue(deleted, "deleteByHash should return true when hash exists");

        // Sent messages should decrease by 1
        assertEquals(manager.getSentMessages().size(), originalSentSize - 1,
                "Sent messages count should decrease by 1");

        // Hash and corresponding ID should be removed
        assertFalse(manager.getMessageHashes().contains(hash),
                "messageHashes should no longer contain deleted hash");
        assertEquals(manager.getMessageHashes().size(), originalHashesSize - 1,
                "messageHashes size should decrease by 1");
        assertEquals(manager.getMessageIDs().size(), originalIdsSize - 1,
                "messageIDs size should decrease by 1");
    }

    @Test
    public void testDeleteByHashNotFound() {
        manager.populateWithTestData();

        boolean deleted = manager.deleteByHash("NON_EXISTING_HASH");
        assertFalse(deleted, "deleteByHash should return false when hash does not exist");
    }

    @Test
    public void testDisplayFullReportWithNoSentMessages() {
        String result = manager.displayFullReport();
        assertEquals(result, "No sent messages to report.",
                "Should indicate no messages when sentMessages is empty");
    }

    @Test
    public void testDisplayFullReportWithData() {
        manager.populateWithTestData();

        String report = manager.displayFullReport();

        assertTrue(report.startsWith("Full Message Report:\n"),
                "Report should start with 'Full Message Report:'");
        assertTrue(report.contains("Did you get the cake?"),
                "Report should contain first sent message text");
        assertTrue(report.contains("It is dinner time !"),
                "Report should contain second sent message text");
    }

    @Test
    public void testWriteAndLoadStoredMessagesToFile() {
        manager.populateWithTestData(); // writes storedMessages.json

        File f = new File("storedMessages.json");
        assertTrue(f.exists(), "storedMessages.json should be created");

        // New instance should load from JSON in its constructor
        MessageManager reloadedManager = new MessageManager();
        assertEquals(reloadedManager.getStoredMessages().size(), 2,
                "Reloaded manager should have 2 stored messages");

        // Check that at least one known stored message text is present
        boolean foundStored = reloadedManager.getStoredMessages().stream()
                .anyMatch(m -> m.getText().contains("Where are you?"));
        assertTrue(foundStored, "Reloaded stored messages should include known test data");

        // Clean up JSON file after test (optional but nice)
        f.delete();
    }
}
