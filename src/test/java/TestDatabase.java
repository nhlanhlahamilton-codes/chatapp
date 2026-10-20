import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.co.codes.Database;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

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


}
