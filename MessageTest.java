package mycompany.project1;



import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class MessageTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private Message m1, m2, m3, m4, m5;
    private Path jsonFile;

    @Before
    public void setUp() {
        Message.reset();
        jsonFile = new File(tempFolder.getRoot(), "messages.json").toPath();
        Message.setStoragePath(jsonFile);
        Message.setSender("Test User");

       
        m1 = new Message("1000000001", 1, "+27834557896", "Did you get the cake?");
        m2 = new Message("1000000002", 2, "+27838884567", "Where are you? You are late! I have asked you to be on time.");
        m3 = new Message("1000000003", 3, "+27834484567", "Yohoooo, I am at your gate.");
        m4 = new Message("1000000004", 4, "0838884567", "It is dinner time!");
        m5 = new Message("1000000005", 5, "+27838884567", "Ok, I am leaving without you.");

        m1.SentMessage(1);   // Sent
        m2.SentMessage(3);   // Stored
        m3.SentMessage(2);   // Disregard
        m4.SentMessage(1);   // Sent
        m5.SentMessage(3);   // Stored
    }

    @Test
    public void sentMessagesArrayCorrectlyPopulated() {
        List<String> expected = Arrays.asList("Did you get the cake?", "It is dinner time!");
        assertEquals(expected, Message.getSentMessageTexts());
    }

    @Test
    public void disregardedAndStoredArraysPopulated() {
        assertEquals(Arrays.asList("Yohoooo, I am at your gate."), Message.getDisregardedMessageTexts());
        assertEquals(Arrays.asList(
                "Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you."), Message.getStoredMessageTexts());
    }

    @Test
    public void hashAndIdArraysPopulated() {
        assertEquals(5, Message.getMessageHashes().size());
        assertEquals(Arrays.asList("1000000001", "1000000002", "1000000003", "1000000004", "1000000005"),
                Message.getMessageIDs());
    }

    @Test
    public void storedMessagesReadBackFromJsonFile() {
        Message.reset();                 // empty every array, the JSON file stays on disk
        assertEquals(2, Message.loadStoredMessages());
        assertEquals(Arrays.asList(
                "Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you."), Message.getStoredMessageTexts());
    }

    @Test
    public void displayTheLongestMessage() {
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                Message.longestStoredMessage());
    }

    @Test
    public void searchForMessageID() {
        assertEquals("Recipient: 0838884567, Message: It is dinner time!",
                Message.searchByMessageID(m4.getMessageID()));
    }

    @Test
    public void searchAllMessagesForAParticularRecipient() {
        List<String> expected = Arrays.asList(
                "Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you.");
        assertEquals(expected, Message.searchByRecipient("+27838884567"));
    }

    @Test
    public void deleteAMessageUsingTheMessageHash() {
        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.",
                Message.deleteByHash(m2.getMessageHash()));
        assertEquals(Arrays.asList("Ok, I am leaving without you."), Message.getStoredMessageTexts());
    }

    @Test
    public void displayReport() {
        String expected = "===== SENT MESSAGES REPORT =====\n"
                + m1.details() + "\n\n"
                + m4.details();
        assertEquals(expected, Message.sentMessagesReport());
    }
}