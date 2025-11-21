/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quickapp.part3;

/**
 *
 * @author RC_Student_Lab
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.swing.JOptionPane;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Part 3 manager that keeps arrays/lists and implements required features:
 * - populate arrays
 * - display sender/recipient
 * - display longest message
 * - search by ID
 * - search by recipient
 * - delete by hash
 * - display full report
 * - load/store JSON
 */
public class MessageManager {

    private final List<Message> sentMessages = new ArrayList<>();
    private final List<Message> disregardedMessages = new ArrayList<>();
    private final List<Message> storedMessages = new ArrayList<>();
    private final List<String> messageHashes = new ArrayList<>();
    private final List<String> messageIDs = new ArrayList<>();

    private final File storageFile = new File("storedMessages.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public MessageManager() {
        loadStoredMessagesFromFile();
    }

    public void addSentMessage(Message m) {
        sentMessages.add(m);
        if (!messageHashes.contains(m.getMessageHash())) messageHashes.add(m.getMessageHash());
        if (!messageIDs.contains(m.getMessageID())) messageIDs.add(m.getMessageID());
    }

    public void addDisregardedMessage(Message m) {
        disregardedMessages.add(m);
    }

    public void addStoredMessage(Message m) {
        storedMessages.add(m);
        // also write to file
        writeAllStoredMessagesToFile();
    }

    public void showAllSentMessagesDialog() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sent messages yet.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.getMessageHash()).append(" | ").append(m.getRecipient()).append(" | ").append(m.getText()).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Recently Sent Messages", JOptionPane.INFORMATION_MESSAGE);
    }

    public String displaySenderRecipientAll() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sent messages.");
            return "No sent messages.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append("Message ID: ").append(m.getMessageID())
                    .append(" | Sender: You")
                    .append(" | Recipient: ").append(m.getRecipient())
                    .append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Senders & Recipients", JOptionPane.INFORMATION_MESSAGE);
        return sb.toString();
    }

    public String displayLongestSentMessage() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sent messages.");
            return "No sent messages.";
        }
        Message longest = sentMessages.get(0);
        for (Message m : sentMessages) {
            if (m.getText().length() > longest.getText().length()) longest = m;
        }
        String result = "Longest message:\n" + longest.getText();
        JOptionPane.showMessageDialog(null, result, "Longest Message", JOptionPane.INFORMATION_MESSAGE);
        return longest.getText();
    }

    public String searchByMessageID(String id) {
        for (Message m : sentMessages) {
            if (m.getMessageID().equals(id)) {
                String res = "Found:\nRecipient: " + m.getRecipient() + "\nMessage: " + m.getText();
                JOptionPane.showMessageDialog(null, res, "Search Result", JOptionPane.INFORMATION_MESSAGE);
                return res;
            }
        }
        JOptionPane.showMessageDialog(null, "Message ID not found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        return "NOT_FOUND";
    }

    public String searchByRecipient(String recipient) {
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            if (m.getRecipient().equals(recipient)) {
                sb.append(m.getMessageHash()).append(" - ").append(m.getText()).append("\n");
            }
        }
        String result = sb.length() == 0 ? "No messages for that recipient." : sb.toString();
        JOptionPane.showMessageDialog(null, result, "Messages for Recipient", JOptionPane.INFORMATION_MESSAGE);
        return result;
    }

    public boolean deleteByHash(String hash) {
        Iterator<Message> it = sentMessages.iterator();
        while (it.hasNext()) {
            Message m = it.next();
            if (m.getMessageHash().equals(hash)) {
                it.remove();
                int idx = messageHashes.indexOf(hash);
                if (idx >= 0) {
                    messageHashes.remove(idx);
                    messageIDs.remove(idx);
                }
                String msg = "Message \"" + m.getText() + "\" successfully deleted.";
                JOptionPane.showMessageDialog(null, msg, "Delete", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        }
        JOptionPane.showMessageDialog(null, "Message hash not found.", "Delete", JOptionPane.INFORMATION_MESSAGE);
        return false;
    }

    public String displayFullReport() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sent messages to report.");
            return "No sent messages to report.";
        }
        StringBuilder sb = new StringBuilder("Full Message Report:\n\n");
        for (Message m : sentMessages) {
            sb.append("Hash: ").append(m.getMessageHash()).append("\n");
            sb.append("ID: ").append(m.getMessageID()).append("\n");
            sb.append("Recipient: ").append(m.getRecipient()).append("\n");
            sb.append("Message: ").append(m.getText()).append("\n\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Full Report", JOptionPane.INFORMATION_MESSAGE);
        return sb.toString();
    }

    public void loadStoredMessagesFromFile() {
        if (!storageFile.exists()) return;
        try (Reader reader = new FileReader(storageFile)) {
            TypeToken<List<Message>> token = new TypeToken<>(){};
            List<Message> existing = gson.fromJson(reader, token.getType());
            if (existing != null) {
                storedMessages.clear();
                storedMessages.addAll(existing);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading stored messages: " + e.getMessage());
        }
    }

    public void writeAllStoredMessagesToFile() {
        try (Writer writer = new FileWriter(storageFile)) {
            gson.toJson(storedMessages, writer);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error writing stored messages: " + e.getMessage());
        }
    }

    // Populate arrays with the assignment test data (messages 1-5)
    public void populateWithTestData() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();

        Message m1 = new Message("+27834557896", "Did you get the cake?", 1);
        m1.setStatus("Sent"); addSentMessage(m1);

        Message m2 = new Message("+27838884567", "Where are you? You are late! I have asked you to be on time.", 2);
        m2.setStatus("Stored"); addStoredMessage(m2);

        Message m3 = new Message("+27834484567", "Yohoooo, I am at your gate.", 3);
        m3.setStatus("Disregard"); addDisregardedMessage(m3);

        Message m4 = new Message("0838884567", "It is dinner time !", 4);
        m4.setStatus("Sent"); addSentMessage(m4);

        Message m5 = new Message("+27838884567", "Ok, I am leaving without you.", 5);
        m5.setStatus("Stored"); addStoredMessage(m5);

        // ensure parallel lists updated
        for (Message s : sentMessages) {
            if (!messageIDs.contains(s.getMessageID())) messageIDs.add(s.getMessageID());
            if (!messageHashes.contains(s.getMessageHash())) messageHashes.add(s.getMessageHash());
        }
        // store storedMessages to file
        writeAllStoredMessagesToFile();
    }

    // Getters for tests
    public List<Message> getSentMessages() { return sentMessages; }
    public List<Message> getDisregardedMessages() { return disregardedMessages; }
    public List<Message> getStoredMessages() { return storedMessages; }
    public List<String> getMessageHashes() { return messageHashes; }
    public List<String> getMessageIDs() { return messageIDs; }
}

