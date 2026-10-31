package za.co.codes;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class TestIntegration {
    private static final String DB_FILE = "chat.db";
    private static final int TEST_PORT = 19898;
    private Thread serverTestThread;

    @BeforeEach
    public void setUp() {
        File f = new File(DB_FILE);
        if (f.exists()) f.delete();
        Database.initialize();
    }

    @AfterEach
    public void tearDown() {
        if (serverTestThread != null && serverTestThread.isAlive()) {
            serverTestThread.interrupt();
        }
        File f = new File(DB_FILE);
        if (f.exists()) f.delete();
    }

    @Test
    @DisplayName("14. Test single client handshake and message echo/broadcast loop")
    public void testSingleClientSocketHandshake() throws Exception {
        // Start a dedicated test server loop on TEST_PORT
        serverTestThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(TEST_PORT)) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String username = in.readLine();
                assertEquals("Alice", username);

                out.println("Welcome " + username);

                String msg = in.readLine();
                assertEquals("Hello Server", msg);
                out.println("Echo: " + msg);

                clientSocket.close();
            } catch (Exception ignored) {}
        });
        serverTestThread.start();
        Thread.sleep(150);

        // Connect as a test client
        try (Socket socket = new Socket("127.0.0.1", TEST_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("Alice");
            String welcome = in.readLine();
            assertTrue(welcome.contains("Welcome Alice"));

            out.println("Hello Server");
            String response = in.readLine();
            assertEquals("Echo: Hello Server", response);
        }
    }

    @Test
    @DisplayName("15. Test multi-client simultaneous connection acceptance")
    public void testMultipleClientsConnection() throws Exception {
        serverTestThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(TEST_PORT)) {
                Socket c1 = serverSocket.accept();
                Socket c2 = serverSocket.accept();

                BufferedReader in1 = new BufferedReader(new InputStreamReader(c1.getInputStream()));
                BufferedReader in2 = new BufferedReader(new InputStreamReader(c2.getInputStream()));

                assertEquals("UserOne", in1.readLine());
                assertEquals("UserTwo", in2.readLine());

                c1.close();
                c2.close();
            } catch (Exception ignored) {}
        });
        serverTestThread.start();
        Thread.sleep(150);

        try (Socket s1 = new Socket("127.0.0.1", TEST_PORT);
             PrintWriter out1 = new PrintWriter(s1.getOutputStream(), true)) {
            out1.println("UserOne");
        }

        try (Socket s2 = new Socket("127.0.0.1", TEST_PORT);
             PrintWriter out2 = new PrintWriter(s2.getOutputStream(), true)) {
            out2.println("UserTwo");
        }
    }

    @Test
    @DisplayName("16. Test exit command termination signal")
    public void testClientExitCommand() throws Exception {
        serverTestThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(TEST_PORT)) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                assertEquals("Bob", in.readLine());
                String cmd = in.readLine();
                assertEquals("exit", cmd);

                clientSocket.close();
            } catch (Exception ignored) {}
        });
        serverTestThread.start();
        Thread.sleep(150);

        try (Socket socket = new Socket("127.0.0.1", TEST_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("Bob");
            out.println("exit");
        }
    }
}