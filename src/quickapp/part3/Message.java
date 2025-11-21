/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quickapp.part3;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Message class used in Part 2 and Part 3
 */
public class Message {
    private final String messageID;
    private final int messageNum;
    private final String recipient;
    private final String text;
    private final String messageHash;
    private String status;

    private static int totalSent = 0;

    public Message(String recipient, String text, int messageNum) {
        this.messageID = generateMessageID();
        this.messageNum = messageNum;
        this.recipient = recipient;
        this.text = text;
        this.messageHash = createMessageHash(messageID, messageNum, text);
    }

    private String generateMessageID() {
        long id = (long) (Math.random() * 1_000_000_0000L);
        return String.format("%010d", id);
    }

    public boolean checkMessageID() { return messageID.length() <= 10; }
    public boolean checkRecipientCell() { return recipient != null && recipient.matches("^\\+27\\d{9,10}$"); }

    public String createMessageHash(String id, int num, String msg) {
        String[] words = msg.trim().split("\\s+");
        String first = words[0].toUpperCase();
        String last = words[words.length - 1].toUpperCase();
        return id.substring(0, 2) + ":" + num + ":" + first + last;
    }

    public boolean validateMessageLength() {
        return text != null && text.length() <= 250;
    }

    public String sendMessage() {
        String[] options = {"Send Message", "Disregard Message", "Store Message"};
        int choice = JOptionPane.showOptionDialog(null,
                "Choose an action for this message:",
                "Send Message",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);

        switch (choice) {
            case 0 -> { status = "Message successfully sent."; totalSent++; }
            case 1 -> { status = "Message disregarded."; }
            case 2 -> { status = "Message successfully stored."; }
            default -> status = "No action taken.";
        }
        JOptionPane.showMessageDialog(null, status);
        return status;
    }

    public String printMessage() {
        String info = "Message ID: " + messageID +
                "\nMessage Hash: " + messageHash +
                "\nRecipient: " + recipient +
                "\nMessage: " + text +
                "\nStatus: " + status;
        JOptionPane.showMessageDialog(null, info, "Message Details", JOptionPane.INFORMATION_MESSAGE);
        return info;
    }

    public void storeMessageToFile() {
        File file = new File("storedMessages.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<Message> messages = new ArrayList<>();
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Message[] existing = gson.fromJson(reader, Message[].class);
                if (existing != null) messages.addAll(Arrays.asList(existing));
            } catch (IOException ignored) {}
        }
        messages.add(this);
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(messages, writer);
        } catch (IOException ignored) {}
    }

    // Getters and setters for manager/tests
    public String getMessageID() { return messageID; }
    public int getMessageNum() { return messageNum; }
    public String getRecipient() { return recipient; }
    public String getText() { return text; }
    public String getMessageHash() { return messageHash; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public static int returnTotalMessages() { return totalSent; }

    void storeMessage() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
