/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package quickapp.part3;
import java.util.*;
import javax.swing.*;
import java.util.regex.*;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
/**
 *
 * @author RC_Student_lab
 */
// Main.java
import javax.swing.JOptionPane;

/**
 * Entry point: integrates Part1 (Login), Part2 (QuickChat), Part3 (MessageManager)
 */
public class QuickAppMainPart3 {
    public static void main(String[] args) {
        // Registration & Login (Part 1)
        String firstName = JOptionPane.showInputDialog(null, "Please enter your first name:");
        if (firstName == null) System.exit(0);
        String lastName = JOptionPane.showInputDialog(null, "Please enter your last name:");
        if (lastName == null) System.exit(0);

        Login login = new Login(firstName, lastName);

        String username = JOptionPane.showInputDialog(null,
                "Please enter a username (must contain '_' and be <= 5 chars):");
        if (username == null) System.exit(0);

        String password = JOptionPane.showInputDialog(null,
                "Please enter a password (min 8 chars, 1 capital, 1 number, 1 special):");
        if (password == null) System.exit(0);

        String cellNumber = JOptionPane.showInputDialog(null,
                "Please enter your South African cell phone number (e.g. +27831234567):");
        if (cellNumber == null) System.exit(0);

        String regMessage = login.registerUser(username, password, cellNumber);
        JOptionPane.showMessageDialog(null, regMessage);

        String loginUsername = JOptionPane.showInputDialog(null, "Login - enter username:");
        if (loginUsername == null) System.exit(0);

        String loginPassword = JOptionPane.showInputDialog(null, "Login - enter password:");
        if (loginPassword == null) System.exit(0);

        String loginMsg = login.returnLoginStatus(loginUsername, loginPassword);
        JOptionPane.showMessageDialog(null, loginMsg);

        if (!login.loginUser(loginUsername, loginPassword)) {
            JOptionPane.showMessageDialog(null, "Invalid login. Exiting.");
            System.exit(0);
        }

        // Initialize MessageManager (Part 3)
        MessageManager manager = new MessageManager();

        // Main menu loop (integrates Part2 + Part3)
        boolean running = true;
        while (running) {
            String[] mainOptions = {
                    "Send Messages",
                    "Show Recently Sent Messages",
                    "Part 3 Menu",
                    "Quit"
            };
            int choice = JOptionPane.showOptionDialog(null, "Welcome to QuickChat", "Main Menu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, mainOptions, mainOptions[0]);

            switch (choice) {
                case 0 -> sendMessagesFlow(manager);                // Part2 send messages; update manager
                case 1 -> manager.showAllSentMessagesDialog();      // Part2 view recent (delegates to manager)
                case 2 -> part3Menu(manager);                       // Part3 features
                case 3 -> { JOptionPane.showMessageDialog(null, "Goodbye!"); running = false; }
                default -> JOptionPane.showMessageDialog(null, "Please choose a valid option.");
            }
        }
        System.exit(0);
    }

    // Send messages loop - uses Message class and updates MessageManager lists
    private static void sendMessagesFlow(MessageManager manager) {
        String totalStr = JOptionPane.showInputDialog(null, "How many messages would you like to send?");
        if (totalStr == null) return;
        int total;
        try {
            total = Integer.parseInt(totalStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid number.");
            return;
        }

        for (int i = 1; i <= total; i++) {
            String recipient = JOptionPane.showInputDialog(null, "Enter recipient number (+27...):");
            if (recipient == null) break;
            String text = JOptionPane.showInputDialog(null, "Enter message text (<=250 chars):");
            if (text == null) break;

            Message msg = new Message(recipient, text, i);

            if (!msg.checkRecipientCell()) {
                JOptionPane.showMessageDialog(null, "Recipient invalid. Must be +27XXXXXXXXX.");
                continue;
            }
            if (!msg.validateMessageLength()) {
                JOptionPane.showMessageDialog(null, "Message too long. Skipping.");
                continue;
            }

            String actionStatus = msg.sendMessage(); // will open option dialog: Send/Disregard/Store

            // Update manager lists depending on status
            if ("Message successfully sent.".equals(actionStatus)) {
                manager.addSentMessage(msg);
            } else if ("Message disregarded.".equals(actionStatus) || "Press 0 to delete message.".equals(actionStatus)) {
                manager.addDisregardedMessage(msg);
            } else if ("Message successfully stored.".equals(actionStatus)) {
                manager.addStoredMessage(msg);
            }
            // show details
            msg.printMessage();
        }

        JOptionPane.showMessageDialog(null, "Total messages sent during this session: " + Message.returnTotalMessages());
    }

    // Part 3 menu wrapper
    private static void part3Menu(MessageManager manager) {
        boolean back = false;
        while (!back) {
            int choice = Integer.parseInt( JOptionPane.showInputDialog(null, """
                                                0. Populate Test Data
                                                1. Load Stored Messages (from JSON)
                                                2. Display Sender & Recipient of Sent Messages
                                                3. Display Longest Sent Message
                                                4. Search By Message ID
                                                5. Search Messages By Recipient
                                                6. Delete Message By Hash
                                                7. Display Full Report
                                                8. Back
                                                """));
            
            

            switch (choice) {
                case 0 -> { manager.populateWithTestData(); JOptionPane.showMessageDialog(null, "Test data populated."); }
                case 1 -> { manager.loadStoredMessagesFromFile(); JOptionPane.showMessageDialog(null, "Stored messages loaded."); }
                case 2 -> manager.displaySenderRecipientAll();
                case 3 -> manager.displayLongestSentMessage();
                case 4 -> {
                    String id = JOptionPane.showInputDialog(null, "Enter Message ID to search:");
                    if (id != null) manager.searchByMessageID(id);
                }
                case 5 -> {
                    String recipient = JOptionPane.showInputDialog(null, "Enter recipient (+27...):");
                    if (recipient != null) manager.searchByRecipient(recipient);
                }
                case 6 -> {
                    String hash = JOptionPane.showInputDialog(null, "Enter Message Hash to delete:");
                    if (hash != null) manager.deleteByHash(hash);
                }
                case 7 -> manager.displayFullReport();
                case 8 -> back = true;
                default -> JOptionPane.showMessageDialog(null, "Choose an option.");
            }
        }
    }
}