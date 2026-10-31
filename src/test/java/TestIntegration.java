import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class TestIntegration {

    @Test
    public void testClientServerConnection() throws Exception {
        int testPort = 19999; // Use a distinct port for testing

        // Start a mock server thread
        Thread serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(testPort)) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Read username sent by client
                String username = in.readLine();
                assertEquals("TestUser", username);

                // Read incoming message
                String msg = in.readLine();
                assertEquals("Ping", msg);

                // Echo back to client
                out.println("Echo: " + msg);
                clientSocket.close();
            } catch (Exception e) {
                // Ignore for test close
            }
        });
        serverThread.start();

        // Give the server a moment to spin up
        Thread.sleep(200);

        // Connect as a client
        try (Socket socket = new Socket("127.0.0.1", testPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send username
            out.println("TestUser");
            // Send message
            out.println("Ping");

            // Read response from server
            String response = in.readLine();
            assertEquals("Echo: Ping", response);
        }

        serverThread.join(1000);
    }
}
