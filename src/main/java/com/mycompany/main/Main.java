/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.main;

/**
 *
 * @author Student
 */
import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

//Main class: entry point for the QuickChat application
public class Main {
    public static void main(String[] args) {

            Scanner input = new Scanner(System.in);
            
            //User account details
            int choice = 0;
            String userFirstName = "";
            String userLastName  = "";
            String storedUsername = "";
            String storedPassword = "";
            
            //Shared MessageStore holds all parallel arrays for the session
            MessageStore store = new MessageStore();
            
            // Display menu in a do-while loop until the user chooses to exit
            do {
                System.out.println("\n==== Main Menu ====");
                System.out.println("1.  Register");
                System.out.println("2.  Login");
                System.out.println("-1. Exit");
                System.out.print("Choose an option: ");
                
                choice = input.nextInt();
                input.nextLine();
                
                if (choice == 1) {
                    //Register a new user and store the returned details
                    String result = Register.registerUser(input);
                    String[] data = result.split("\\|");
                    userFirstName  = data[0];
                    userLastName   = data[1];
                    storedUsername = data[2];
                    storedPassword = data[3];
                    
                } else if (choice == 2) {
                    //Login; on success open the QuickChat session
                    boolean loggedIn = Login.loginUser(input, storedUsername, storedPassword,
                            userFirstName, userLastName);
                    if (loggedIn) {
                        Login.quickChat(input, store);
                    }
                    
                } else if (choice == -1) {
                    System.out.println("Goodbye!");
                    
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
                
            } while (choice != -1);
        }
    }


//Login class: authentication and QuickChat session management
class Login {

    //Returns true when the entered credentials match the stored ones
    public static boolean loginStatus(String username, String password,
                                       String enteredUsername, String enteredPassword) {
        return enteredUsername.equals(username) && enteredPassword.equals(password);
    }

    //Prompts until the user supplies correct credentials
    public static boolean loginUser(Scanner input, String username, String password,
                                     String firstName, String lastName) {
        String enteredUsername;
        String enteredPassword;

        do {
            System.out.print("Enter your username: ");
            enteredUsername = input.nextLine();

            System.out.print("Enter your password: ");
            enteredPassword = input.nextLine();

            if (!loginStatus(username, password, enteredUsername, enteredPassword)) {
                System.out.println("Username or password incorrect, please try again.");
            }

        } while (!loginStatus(username, password, enteredUsername, enteredPassword));

        System.out.println("Welcome " + firstName + " " + lastName
                + ", it is nice to see you again.");
        return true;
    }

    // ----------------------------------------------------------------
    // QuickChat session
    // Uses a FOR LOOP to allow the user to enter the assigned number
    // of messages (loop counter used as message number).
    // Passes MessageStore so all arrays are populated during the session.
    // ----------------------------------------------------------------
    public static void quickChat(Scanner input, MessageStore store) {

        System.out.println("\nWelcome to QuickChat!");

        System.out.print("How many messages would you like to send: ");
        int numMessages = input.nextInt();
        input.nextLine();

        boolean quit = false;

        // Outer while loop keeps the QuickChat menu active
        while (!quit) {

            System.out.println("\n====Quickchat Menu====");
            System.out.println("1. Send Messages");
            System.out.println("2. Show recently sent messages");
            System.out.println("3. Stored Messages");
            System.out.println("4. Quit");
            System.out.print("Choose an option: ");

            int option = input.nextInt();
            input.nextLine();

            if (option == 1) {

                //Using a for loop iterates once per message
                for (int i = 1; i <= numMessages; i++) {

                    System.out.println("\n=== Message " + i + " of " + numMessages + " ===");

                    //Check if numer is correct and capture recipient number
                    System.out.print("Enter recipient number: ");
                    String recipient = input.nextLine();
                    recipient = Message.checkRecipientCell(input, recipient);

                    //Capture message text
                    System.out.print("Enter your message: ");
                    String messageText = input.nextLine();

                    //Build the Message object using the loop counter as message number
                    Message msg = new Message(i, recipient, messageText);

                    // Reject messages that are too long
                    if (!msg.checkMessageLength()) {
                        System.out.println("Message exceeds 250 characters. Message discarded.");
                        i--;
                        continue;
                    }

                    //Let the user choose to Send / Disregard / Store
                    String outcome = msg.sentMessage(input);
                    System.out.println(outcome);

                    //Add the message ID and hash to the parallel arrays unconditionally
                    store.addMessageID(msg.getMessageID());
                    store.addMessageHash(msg.getMessageHash());

                    //Route the message to the correct array based on outcome
                    if (outcome.equals("Message successfully sent.")) {
                        store.addSentMessage(msg);
                        System.out.println(msg.printMessages());

                    } else if (outcome.equals("Message successfully stored.")) {
                        store.addStoredMessage(msg);
                        System.out.println(msg.printMessages());

                    } else {
                        store.addDisregardedMessage(msg);
                    }
                }

                System.out.println("\nAll " + numMessages + " message(s) processed.");

            } else if (option == 2) {

                // Display recently sent messages
                ArrayList<Message> sent = store.getSentMessages();
                if (sent.isEmpty()) {
                    System.out.println("No messages sent yet.");
                } else {
                    System.out.println("\n--- Recently Sent Messages ---");
                    for (Message m : sent) {
                        System.out.println(m.printMessages());
                        System.out.println("------------------------------");
                    }
                }

            } else if (option == 3) {

                // Open the Stored Messages sub-menu (Part 3 feature)
                storedMessagesMenu(input, store);

            } else if (option == 4) {
                System.out.println("Total messages sent: " + store.getSentMessages().size());
                System.out.println("Goodbye!");
                quit = true;

            } else {
                System.out.println("Invalid option. Please choose 1-4.");
            }
        }
    }

    // Stored Messages sub-menu
    // All features operate on the parallel arrays in MessageStore.
    private static void storedMessagesMenu(Scanner input, MessageStore store) {

        // Load any JSON-stored messages from file into the stored array
        store.loadStoredMessagesFromJson("messages.json");

        boolean back = false;

        while (!back) {
            System.out.println("\n===== STORED MESSAGES MENU =====");
            System.out.println("a. Display sender and recipient of all stored messages");
            System.out.println("b. Display the longest stored message");
            System.out.println("c. Search for a message by ID");
            System.out.println("d. Search all messages for a recipient");
            System.out.println("e. Delete a message using message hash");
            System.out.println("f. Display full message report");
            System.out.println("x. Back");
            System.out.print("Choose an option: ");
            String choice = input.nextLine().trim().toLowerCase();

            switch(choice) {

                case "a":
                    store.displaySenderAndRecipient();
                    break;

                case "b":
                    store.displayLongestMessage();
                    break;

                case "c":
                    System.out.print("Enter Message ID to search: ");
                    String searchID = input.nextLine().trim();
                    store.searchByMessageID(searchID);
                    break;

                case "d":
                    System.out.print("Enter recipient number to search: ");
                    String searchRecipient = input.nextLine().trim();
                    store.searchByRecipient(searchRecipient);
                    break;

                case "e":
                    System.out.print("Enter message hash to delete: ");
                    String hashToDelete = input.nextLine().trim();
                    store.deleteByHash(hashToDelete);
                    break;

                case "f":
                    store.displayFullReport();
                    break;

                case "x":
                    back = true;
                    break;

                default:
                    System.out.println("Invalid option. Please choose a-f or x.");
            }
        }
    }
}

// MessageStore class: manages all parallel arrays
class MessageStore {

    // Parallel arrays — all indexed consistently
    private ArrayList<Message> sentMessages        = new ArrayList<>();
    private ArrayList<Message> disregardedMessages = new ArrayList<>();
    private ArrayList<Message> storedMessages      = new ArrayList<>();
    private ArrayList<String>  messageHashes       = new ArrayList<>();
    private ArrayList<String>  messageIDs          = new ArrayList<>();

    // --- Array population methods ---

    public void addSentMessage(Message msg)        { sentMessages.add(msg); }
    public void addDisregardedMessage(Message msg) { disregardedMessages.add(msg); }
    public void addStoredMessage(Message msg)      { storedMessages.add(msg); }
    public void addMessageHash(String hash)        { messageHashes.add(hash); }
    public void addMessageID(String id)            { messageIDs.add(id); }

    // --- Getters ---

    public ArrayList<Message> getSentMessages()        { return sentMessages; }
    public ArrayList<Message> getDisregardedMessages() { return disregardedMessages; }
    public ArrayList<Message> getStoredMessages()      { return storedMessages; }
    public ArrayList<String>  getMessageHashes()       { return messageHashes; }
    public ArrayList<String>  getMessageIDs()          { return messageIDs; }

    //Display sender and recipient of all stored messages
    public void displaySenderAndRecipient() {
        //If the storeMessages is empty diplay a message
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages found.");
            return;
        }
        System.out.println("\n--- Stored Message Senders & Recipients ---");
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            System.out.println((i + 1) + ". Recipient: " + m.getRecipient()
                    + " | Message: " + m.getMessage());
        }
    }

    //Display the longest stored message
    //Searches all arrays (sent + stored) for the longest message
    public String getLongestMessage() {
        ArrayList<Message> allMessages = new ArrayList<>();
        allMessages.addAll(sentMessages);
        allMessages.addAll(storedMessages);

        if (allMessages.isEmpty()) {
            return "No messages available.";
        }

        Message longest = allMessages.get(0);
        for (Message m : allMessages) {
            if (m.getMessage().length() > longest.getMessage().length()) {
                longest = m;
            }
        }
        return longest.getMessage();
    }

    public void displayLongestMessage() {
        System.out.println("\n--- Longest Message ---");
        System.out.println(getLongestMessage());
    }

    //Search for a message by ID; display recipient and message
    public String searchByMessageID(String id) {
        for (Message m : sentMessages) {
            if (m.getMessageID().equals(id)) {
                String result = "Recipient: " + m.getRecipient()
                        + "\nMessage:   " + m.getMessage();
                System.out.println("\n--- Search Result ---\n" + result);
                return result;
            }
        }
        for (Message m : storedMessages) {
            if (m.getMessageID().equals(id)) {
                String result = "Recipient: " + m.getRecipient()
                        + "\nMessage:   " + m.getMessage();
                System.out.println("\n--- Search Result ---\n" + result);
                return result;
            }
        }
        System.out.println("No message found with ID: " + id);
        return "Message ID not found.";
    }

    //Search all messages sent/stored for a particular recipient
    public ArrayList<String> searchByRecipient(String recipient) {
        ArrayList<String> results = new ArrayList<>();

        //Search through sent messages using index
        for (int i = 0; i < sentMessages.size(); i++) {
            if (sentMessages.get(i).getRecipient().equals(recipient)) {
                results.add(sentMessages.get(i).getMessage());
            }
        }

        //Search through stored messages using index
        for (int i = 0; i < storedMessages.size(); i++) {
            if (storedMessages.get(i).getRecipient().equals(recipient)) {
                results.add(storedMessages.get(i).getMessage());
            }
        }

        System.out.println("\n--- Messages for " + recipient + " ---");
        if (results.isEmpty()) {
            System.out.println("No messages found for this recipient.");
        } else {
            //Print each result using index
            for (int i = 0; i < results.size(); i++) {
                System.out.println("- " + results.get(i));
            }
        }

        return results;
    }

    //Delete a message using the message hash
    public String deleteByHash(String hash) {
        //Search sent messages
        for (int i = 0; i < sentMessages.size(); i++) {
            if (sentMessages.get(i).getMessageHash().equalsIgnoreCase(hash)) {
                String deletedMsg = sentMessages.get(i).getMessage();
                sentMessages.remove(i);
                messageHashes.remove(hash);
                String result = "Message: \"" + deletedMsg + "\" successfully deleted.";
                System.out.println(result);
                return result;
            }
        }
        //Search stored messages
        for (int i = 0; i < storedMessages.size(); i++) {
            if (storedMessages.get(i).getMessageHash().equalsIgnoreCase(hash)) {
                String deletedMsg = storedMessages.get(i).getMessage();
                storedMessages.remove(i);
                messageHashes.remove(hash);
                String result = "Message: \"" + deletedMsg + "\" successfully deleted.";
                System.out.println(result);
                return result;
            }
        }
        System.out.println("No message found with hash: " + hash);
        return "Hash not found.";
    }

    //Display a full report of all sent and stored messages
    public void displayFullReport() {
        System.out.println("\n============================================");
        System.out.println("           FULL MESSAGE REPORT              ");
        System.out.println("============================================");

        System.out.println("\n--- SENT MESSAGES (" + sentMessages.size() + ") ---");
        if (sentMessages.isEmpty()) {
            System.out.println("  No sent messages.");
        } else {
            for (int i = 0; i < sentMessages.size(); i++) {
                Message m = sentMessages.get(i);
                System.out.println("\n  [" + (i + 1) + "]");
                System.out.println("  Message Hash: " + m.getMessageHash());
                System.out.println("  Recipient:    " + m.getRecipient());
                System.out.println("  Message:      " + m.getMessage());
            }
        }

        System.out.println("\n--- STORED MESSAGES (" + storedMessages.size() + ") ---");
        if (storedMessages.isEmpty()) {
            System.out.println("  No stored messages.");
        } else {
            for (int i = 0; i < storedMessages.size(); i++) {
                Message m = storedMessages.get(i);
                System.out.println("\n  [" + (i + 1) + "]");
                System.out.println("  Message Hash: " + m.getMessageHash());
                System.out.println("  Recipient:    " + m.getRecipient());
                System.out.println("  Message:      " + m.getMessage());
            }
        }

        System.out.println("\n============================================");
        System.out.println("Total Messages: "
                + (sentMessages.size() + storedMessages.size()));
        System.out.println("============================================");
    }

    // Read JSON file into stored messages array
    // Source: manual JSON parsing using BufferedReader and String methods
    // Reference: Java BufferedReader — docs.oracle.com/javase/8/docs/api/
    public void loadStoredMessagesFromJson(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;

            String jsonMessageID   = "";
            String jsonMessageHash = "";
            String jsonRecipient   = "";
            String jsonMessage     = "";
            int    lineNumber      = 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                //Parse each JSON field using substring and indexOf
                if (line.contains("\"MessageID\"")) {
                    jsonMessageID = extractJsonValue(line);

                } else if (line.contains("\"MessageHash\"")) {
                    jsonMessageHash = extractJsonValue(line);

                } else if (line.contains("\"Recipient\"")) {
                    jsonRecipient = extractJsonValue(line);

                } else if (line.contains("\"Message\"")) {
                    jsonMessage = extractJsonValue(line);
                    lineNumber++;

                    //Build a Message from JSON data and add to stored array
                    Message jsonMsg = new Message(lineNumber, jsonRecipient,
                            jsonMessage, jsonMessageID, jsonMessageHash);

                    //Avoid duplicate entries on repeated calls
                    boolean alreadyLoaded = false;
                    for (Message existing : storedMessages) {
                        if (existing.getMessageID().equals(jsonMessageID)) {
                            alreadyLoaded = true;
                            break;
                        }
                    }
                    if (!alreadyLoaded) {
                        storedMessages.add(jsonMsg);
                        messageIDs.add(jsonMessageID);
                        messageHashes.add(jsonMessageHash);
                    }
                }
            }
            reader.close();
            System.out.println("Stored messages loaded from " + filePath + ".");

        } catch (IOException e) {
            System.out.println("No stored messages file found or error reading file.");
        }
    }

    //Extracts the value from a JSON key-value line
    private String extractJsonValue(String line) {
        int start = line.indexOf("\"", line.indexOf(":") + 1) + 1;
        int end   = line.lastIndexOf("\"");
        if (start > 0 && end > start) {
            return line.substring(start, end);
        }
        return "";
    }
}


//Represents a single chat message
class Message {

    //Instance attributes
    private String messageID;
    private int    messageNumber;
    private String recipient;
    private String message;
    private String messageHash;

    //Running total of sent and stored messages across all instances
    private static int totalMessages = 0;

    //Primary constructor: generates ID and hash automatically
    public Message(int messageNumber, String recipient, String message) {
        this.messageNumber = messageNumber;
        this.recipient     = recipient;
        this.message       = message;

        //Generate a random 10-digit message ID using string manipulation
        Random random  = new Random();
        long randomNum = 1000000000L + (long)(random.nextDouble() * 9000000000L);
        this.messageID   = String.valueOf(randomNum);
        this.messageHash = createMessageHash();
    }

    //Secondary constructor: used when loading messages from JSON
    //Allows pre-supplying the ID and hash read from the file
    public Message(int messageNumber, String recipient, String message,
                   String messageID, String messageHash) {
        this.messageNumber = messageNumber;
        this.recipient     = recipient;
        this.message       = message;
        this.messageID     = messageID;
        this.messageHash   = messageHash;
    }

    //Returns true if the message ID is 10 digits or fewer
    public boolean checkMessageID() {
        return messageID.length() <= 10;
    }

    // Validates recipient using regex; re-prompts until correct
    public static String checkRecipientCell(Scanner input, String recipientNum) {
        while (!recipientNum.matches("\\+27\\d{9}")) {
            System.out.println("Cell phone number incorrectly formatted.");
            System.out.println("Must start with +27 followed by 9 digits (e.g. +27812345678).");
            System.out.print("Enter recipient number again: ");
            recipientNum = input.nextLine();
        }
        System.out.println("Cell phone number successfully captured.");
        return recipientNum;
    }

    //firstTwoDigitsOfID:messageNumber:FIRSTWORDlastword
    public String createMessageHash() {
        String[] words        = message.split(" ");
        String   firstWord    = words[0];
        String   lastWord     = words[words.length - 1];
        String   firstTwoDigits = messageID.substring(0, 2);
        return (firstTwoDigits + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
    }

    //Prompts the user to send, disregard, or store; returns outcome string
    public String sentMessage(Scanner input) {
        System.out.println("\nChoose an option:");
        System.out.println("1. Send Message");
        System.out.println("2. Disregard Message");
        System.out.println("3. Store Message");
        System.out.print("Option: ");

        int choice = input.nextInt();
        input.nextLine();

        if (choice == 1) {
            totalMessages++;
            return "Message successfully sent.";

        } else if (choice == 2) {
            return "Message disregarded.";

        } else if (choice == 3) {
            storeMessage();
            totalMessages++;
            return "Message successfully stored.";

        } else {
            return "Invalid option. Message discarded.";
        }
    }

    //Returns a formatted string of all message details
    public String printMessages() {
        return "\nMessage ID:   " + messageID
             + "\nMessage Hash: " + messageHash
             + "\nRecipient:    " + recipient
             + "\nMessage:      " + message;
    }

    //Returns the running total of sent and stored messages
    public int returnTotalMessages() {
        return totalMessages;
    }

    //Writes this message as a JSON object to messages.json (append mode)
    public void storeMessage() {
        try {
            FileWriter file = new FileWriter("messages.json", true);
            file.write("{\n");
            file.write("  \"MessageID\": \""   + messageID   + "\",\n");
            file.write("  \"MessageHash\": \"" + messageHash + "\",\n");
            file.write("  \"Recipient\": \""   + recipient   + "\",\n");
            file.write("  \"Message\": \""     + message     + "\"\n");
            file.write("}\n");
            file.close();
            System.out.println("Message saved to messages.json.");
        } catch (IOException e) {
            System.out.println("Error storing message: " + e.getMessage());
        }
    }

    //Returns true if the message is 250 characters or fewer
    public boolean checkMessageLength() {
        return message.length() <= 250;
    }

    public String getMessageID()     { return messageID; }
    public String getMessageHash()   { return messageHash; }
    public String getRecipient()     { return recipient; }
    public String getMessage()       { return message; }
    public int    getMessageNumber() { return messageNumber; }
}


//Handles new user registration
class Register {

    //Username must be NO MORE than 5 characters AND contain an underscore
    public static boolean checkUsername(String username) {
        boolean correctLength = username.length() <= 5;
        boolean hasUnderscore = username.contains("_");
        return correctLength && hasUnderscore;
    }

    //Password must be 8+ chars with uppercase, digit, and special character
    public static boolean checkPasswordComplexity(String password) {
        boolean enoughChars  = password.length() >= 8;
        boolean hasUppercase = false;
        boolean hasDigit     = false;
        boolean hasSpecial   = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isUpperCase(c))      hasUppercase = true;
            if (Character.isDigit(c))          hasDigit     = true;
            if (!Character.isLetterOrDigit(c)) hasSpecial   = true;
        }

        return enoughChars && hasUppercase && hasDigit && hasSpecial;
    }

    // Cell phone validated with regex (SA international format)
    public static boolean checkCellPhoneNumber(Scanner input, String cellPhoneNum) {
        while (!cellPhoneNum.matches("\\+27\\d{9}")) {
            System.out.println("Cellphone number incorrectly formatted.");
            System.out.println("Must start with +27 followed by 9 digits (e.g. +27821234567).");
            System.out.print("Enter your number again: ");
            cellPhoneNum = input.nextLine();
        }
        return true;
    }

    // Collects and validates all registration details
    public static String registerUser(Scanner input) {

        System.out.println("\nThank you for using our app. Please enter your details.");

        System.out.print("\nEnter your first name: ");
        String userFirstName = input.nextLine();

        System.out.print("Enter your last name: ");
        String userLastName = input.nextLine();

        System.out.println("\nCellphone number must start with +27 and be 12 characters total.");
        System.out.print("Enter your cellphone number: ");
        String cellPhoneNum = input.nextLine();
        checkCellPhoneNumber(input, cellPhoneNum);
        System.out.println("Cellphone number successfully captured.");

        System.out.println("\nUsername must be 5 characters or fewer and contain an underscore.");
        System.out.print("Enter your username: ");
        String usernameEntered = input.nextLine();

        while (!checkUsername(usernameEntered)) {
            System.out.println("Invalid username. Must be max 5 characters and contain '_'.");
            System.out.print("Enter your username: ");
            usernameEntered = input.nextLine();
        }
        System.out.println("Username successfully captured.");

        System.out.println("\nPassword must be 8+ characters with a capital letter, number, and special character.");
        System.out.print("Enter your password: ");
        String passwordEntered = input.nextLine();

        while (!checkPasswordComplexity(passwordEntered)) {
            System.out.println("Password does not meet requirements. Please try again.");
            System.out.print("Enter your password: ");
            passwordEntered = input.nextLine();
        }
        System.out.println("Password successfully captured.");

        return userFirstName + "|" + userLastName + "|" + usernameEntered + "|" + passwordEntered;
    }
}









