import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.codes.Database;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class TestDatabase {

    private static final String TEST_DB = "chat.db";

    @BeforeEach
    public void setUp() {
        // Clean up any old test database before each test
        File dbFile = new File(TEST_DB);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        Database.initialize();
    }

    @AfterEach
    public void tearDown() {
        // Clean up after test
        File dbFile = new File(TEST_DB);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    public void testDatabaseInitializationAndMessagePersistence() {
        Database.initialize();

        Database.saveMessage("Alice", "Hello World!");
        Database.saveMessage("Bob", "Hi Alice!");

        List<String> history = Database.getRecentMessages();

        assertNotNull(history);
        assertEquals(2, history.size());
        assertTrue(history.get(0).contains("Alice: Hello World!"));
        assertTrue(history.get(1).contains("Bob: Hi Alice!"));
    }

    @Test
    void shouldPrintInitializationMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));

            Database.initialize();

            assertEquals(
                    "Database initialized successfully"
                            + System.lineSeparator(),
                    output.toString()
            );

        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("9. Test retrieval when database has no messages")
    public void testRetrieveEmptyDatabase() {
        List<String> history = Database.getRecentMessages();
        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    @DisplayName("10. Test special characters and SQL injection attempts in payload")
    public void testSpecialCharactersAndSqlInjectionSafety() {
        String maliciousPayload = "'; DROP TABLE messages; --";
        Database.saveMessage("Hacker", maliciousPayload);

        List<String> history = Database.getRecentMessages();
        assertEquals(1, history.size());
        assertTrue(history.get(0).contains(maliciousPayload));

        // Verify table still exists and functions
        assertDoesNotThrow(Database::getRecentMessages);
    }

    @Test
    @DisplayName("11. Test Unicode and Emoji support")
    public void testUnicodeAndEmojis() {
        Database.saveMessage("EmojiUser", "🚀 Hello 🌍 café naïve résumé");
        List<String> history = Database.getRecentMessages();

        assertEquals(1, history.size());
        assertTrue(history.get(0).contains("🚀 Hello 🌍 café naïve résumé"));
    }

    @Test
    @DisplayName("12. Test long text message payload")
    public void testLongMessagePayload() {
        String longText = "A".repeat(5000);
        Database.saveMessage("BigSender", longText);

        List<String> history = Database.getRecentMessages();
        assertEquals(1, history.size());
        assertTrue(history.get(0).contains(longText));
    }

}
