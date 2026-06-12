/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.main;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

/**
 *
 * @author Student
 */
public class JUnitTest {

    //Shared MessageStore instance re-created before each test for isolation
    private MessageStore store;

    //Test messages
    private Message msg1;
    private Message msg2;
    private Message msg3;
    private Message msg4;
    private Message msg5;

    @BeforeEach
    public void setUp() {
        store = new MessageStore();

        // Create test messages using the secondary constructor so we control IDs and hashes
        msg1 = new Message(1, "+27834557896",
                "Did you get the cake?",
                "1234567890", "12:1:DIDCAKE?");

        msg2 = new Message(2, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.",
                "2345678901", "23:2:WHERETIME.");

        msg3 = new Message(3, "+27834484567",
                "Yohoooo, I am at your gate.",
                "3456789012", "34:3:YOHOOOOGATE.");

        msg4 = new Message(4, "0838884567",
                "It is dinner time !",
                "4567890123", "45:4:ITTIME!");

        msg5 = new Message(5, "+27838884567",
                "Ok, I am leaving without you.",
                "5678901234", "56:5:OKYOU.");

        // Populate the store with test data matching the rubric flags
        store.addSentMessage(msg1);       // Sent
        store.addMessageID(msg1.getMessageID());
        store.addMessageHash(msg1.getMessageHash());

        store.addStoredMessage(msg2);     // Stored
        store.addMessageID(msg2.getMessageID());
        store.addMessageHash(msg2.getMessageHash());

        store.addDisregardedMessage(msg3); // Disregard
        store.addMessageID(msg3.getMessageID());
        store.addMessageHash(msg3.getMessageHash());

        store.addSentMessage(msg4);       // Sent
        store.addMessageID(msg4.getMessageID());
        store.addMessageHash(msg4.getMessageHash());

        store.addStoredMessage(msg5);     // Stored
        store.addMessageID(msg5.getMessageID());
        store.addMessageHash(msg5.getMessageHash());
    }

    //Register tests
    @Test
    @DisplayName("Username: valid — 5 chars with underscore")
    public void testUsernameValid() {
        assertTrue(Register.checkUsername("kyl_1"),
                "kyl_1 should be valid");
    }
    
    @Test
    @DisplayName("Username: too long — more than 5 characters")
    public void testUsernameTooLong() {
        assertFalse(Register.checkUsername("kyle_1"),
                "kyle_1 has 6 chars, should fail");
    }

    @Test
    @DisplayName("Username: no underscore")
    public void testUsernameNoUnderscore() {
        assertFalse(Register.checkUsername("kyle1"),
                "kyle1 has no underscore, should fail");
    }

    @Test
    @DisplayName("Password: valid — meets all requirements")
    public void testPasswordValid() {
        assertTrue(Register.checkPasswordComplexity("Password1!"),
                "Password1! should pass");
    }

    @Test
    @DisplayName("Password: too short")
    public void testPasswordTooShort() {
        assertFalse(Register.checkPasswordComplexity("Pa1!"),
                "Pa1! is too short");
    }

    @Test
    @DisplayName("Password: no uppercase")
    public void testPasswordNoUppercase() {
        assertFalse(Register.checkPasswordComplexity("password1!"),
                "password1! has no uppercase");
    }

    @Test
    @DisplayName("Password: no digit")
    public void testPasswordNoDigit() {
        assertFalse(Register.checkPasswordComplexity("Password!"),
                "Password! has no digit");
    }

    @Test
    @DisplayName("Password: no special character")
    public void testPasswordNoSpecialChar() {
        assertFalse(Register.checkPasswordComplexity("Password1"),
                "Password1 has no special char");
    }

    @Test
    @DisplayName("Cell number: valid SA number passes regex")
    public void testCellNumberValid() {
        assertTrue("+27812345678".matches("\\+27\\d{9}"),
                "+27812345678 is valid");
    }

    @Test
    @DisplayName("Cell number: no country code fails regex")
    public void testCellNumberNoCountryCode() {
        assertFalse("0812345678".matches("\\+27\\d{9}"),
                "0812345678 lacks +27 prefix");
    }

    @Test
    @DisplayName("Cell number: too short fails regex")
    public void testCellNumberTooShort() {
        assertFalse("+278123456".matches("\\+27\\d{9}"),
                "+278123456 is too short");
    }

    //Login tests

    @Test
    @DisplayName("Login: correct credentials return true")
    public void testLoginSuccess() {
        assertTrue(Login.loginStatus("kyl_1", "Password1!", "kyl_1", "Password1!"));
    }

    @Test
    @DisplayName("Login: wrong password returns false")
    public void testLoginWrongPassword() {
        assertFalse(Login.loginStatus("kyl_1", "Password1!", "kyl_1", "wrong"));
    }

    @Test
    @DisplayName("Login: wrong username returns false")
    public void testLoginWrongUsername() {
        assertFalse(Login.loginStatus("kyl_1", "Password1!", "bad_u", "Password1!"));
    }

    // PART 2 — Message tests
    @Test
    @DisplayName("Message ID: length is 10 digits or fewer")
    public void testMessageIDLength() {
        Message m = new Message(1, "+27812345678", "Hello World");
        assertTrue(m.checkMessageID(), "ID should be 10 digits or fewer");
    }

    @Test
    @DisplayName("Message length: within 250 characters")
    public void testMessageLengthValid() {
        Message m = new Message(1, "+27812345678", "Hello World");
        assertTrue(m.checkMessageLength());
    }

    @Test
    @DisplayName("Message length: exceeds 250 characters")
    public void testMessageLengthTooLong() {
        String longMsg = "A".repeat(251);
        Message m = new Message(1, "+27812345678", longMsg);
        assertFalse(m.checkMessageLength());
    }

    @Test
    @DisplayName("Message hash: is uppercase")
    public void testMessageHashUppercase() {
        Message m = new Message(1, "+27812345678", "Hello World");
        assertEquals(m.getMessageHash(), m.getMessageHash().toUpperCase());
    }

    @Test
    @DisplayName("Message hash: contains first and last word")
    public void testMessageHashWords() {
        Message m = new Message(1, "+27812345678", "Hello World");
        assertTrue(m.getMessageHash().contains("HELLO"));
        assertTrue(m.getMessageHash().contains("WORLD"));
    }

    // Array population tests
    @Test
    @DisplayName("Sent messages array: contains expected messages 1 and 4")
    public void testSentMessagesArrayPopulated() {
        // Rubric: sent messages should contain msg1 and msg4
        ArrayList<Message> sent = store.getSentMessages();
        assertEquals(2, sent.size(), "Should have 2 sent messages");

        assertEquals("Did you get the cake?", sent.get(0).getMessage(),
                "First sent message should match msg1");
        assertEquals("It is dinner time !", sent.get(1).getMessage(),
                "Second sent message should match msg4");
    }

    @Test
    @DisplayName("Stored messages array: contains messages 2 and 5")
    public void testStoredMessagesArrayPopulated() {
        ArrayList<Message> stored = store.getStoredMessages();
        assertEquals(2, stored.size(), "Should have 2 stored messages");
    }

    @Test
    @DisplayName("Disregarded messages array: contains message 3")
    public void testDisregardedMessagesArrayPopulated() {
        ArrayList<Message> disregarded = store.getDisregardedMessages();
        assertEquals(1, disregarded.size(), "Should have 1 disregarded message");
        assertEquals("Yohoooo, I am at your gate.", disregarded.get(0).getMessage());
    }

    @Test
    @DisplayName("Message hash array: all 5 hashes stored")
    public void testMessageHashArrayPopulated() {
        assertEquals(5, store.getMessageHashes().size(),
                "Should have 5 hashes stored");
    }

    @Test
    @DisplayName("Message ID array: all 5 IDs stored")
    public void testMessageIDArrayPopulated() {
        assertEquals(5, store.getMessageIDs().size(),
                "Should have 5 IDs stored");
    }

    //Display longest message
    @Test
    @DisplayName("Longest message: returns message 2 (the longest)")
    public void testLongestMessage() {
        String longest = store.getLongestMessage();
        assertEquals(
            "Where are you? You are late! I have asked you to be on time.",
            longest,
            "Longest message should be message 2"
        );
    }

    //Search by message ID
    @Test
    @DisplayName("Search by ID: message 4 returns correct message")
    public void testSearchByMessageID() {
        // Rubric: searching msg4's ID should return "It is dinner time !"
        String result = store.searchByMessageID(msg4.getMessageID());
        assertTrue(result.contains("It is dinner time !"),
                "Search result should contain msg4's message");
    }

    @Test
    @DisplayName("Search by ID: unknown ID returns not-found message")
    public void testSearchByMessageIDNotFound() {
        String result = store.searchByMessageID("9999999999");
        assertEquals("Message ID not found.", result);
    }

    //Search by recipient
    @Test
    @DisplayName("Search by recipient: +27838884567 returns messages 2 and 5")
    public void testSearchByRecipient() {
        // Rubric: both msg2 and msg5 share recipient +27838884567
        ArrayList<String> results = store.searchByRecipient("+27838884567");
        assertEquals(2, results.size(),
                "Should find 2 messages for +27838884567");
        assertTrue(results.contains(
                "Where are you? You are late! I have asked you to be on time."),
                "Should contain msg2");
        assertTrue(results.contains("Ok, I am leaving without you."),
                "Should contain msg5");
    }

    @Test
    @DisplayName("Search by recipient: unknown recipient returns empty list")
    public void testSearchByRecipientNotFound() {
        ArrayList<String> results = store.searchByRecipient("+27000000000");
        assertTrue(results.isEmpty(), "Unknown recipient should return empty list");
    }

    //Delete by hash
    @Test
    @DisplayName("Delete by hash: message 2 successfully deleted")
    public void testDeleteByHash() {
        // Rubric: delete msg2; system should confirm deletion
        String result = store.deleteByHash(msg2.getMessageHash());
        assertEquals(
            "Message: \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.",
            result,
            "Deletion confirmation should match expected format"
        );
    }

    @Test
    @DisplayName("Delete by hash: stored array shrinks after deletion")
    public void testDeleteByHashReducesArray() {
        int before = store.getStoredMessages().size();
        store.deleteByHash(msg2.getMessageHash());
        int after = store.getStoredMessages().size();
        assertEquals(before - 1, after,
                "Stored messages should decrease by 1 after deletion");
    }

    @Test
    @DisplayName("Delete by hash: unknown hash returns not-found")
    public void testDeleteByHashNotFound() {
        String result = store.deleteByHash("XX:99:FAKEHASH");
        assertEquals("Hash not found.", result);
    }

    //Display report (smoke test — verifies no exceptions thrown)
    @Test
    @DisplayName("Display report: runs without throwing exceptions")
    public void testDisplayReportNoException() {
        assertDoesNotThrow(() -> store.displayFullReport(),
                "displayFullReport should not throw any exceptions");
    }
}






