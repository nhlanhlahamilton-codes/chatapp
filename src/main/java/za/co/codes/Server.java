package za.co.codes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static final int PORT = 12345;
    private static Set<PrintWriter> writers = new HashSet<>();

    public static void main(String [] args){
        logger.info("Chat Server is starting up...");
        Database.initialize();
        System.out.println("Server started!");

        try(ServerSocket serverSocket = new ServerSocket(PORT)){

            while(true){
                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start();
            }

        } catch (IOException e){
            System.out.println("Server exception: " + e.getMessage());
        }
    }

    public static synchronized void broadcast(String message, PrintWriter excludeWriter){
        for (PrintWriter writer : writers){
            if (writer != excludeWriter){
                writer.println(message);
            }
        }
    }

    private static class ClientHandler extends Thread{

        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket){
            this.socket = socket;
        }
        public void run(){
            try{
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                username = in.readLine();
                System.out.println(username + " connected!");

                synchronized (writers){
                    writers.add(out);
                }
                broadcast(username + " has joined the chat!", out);
                String clientMessage;
                SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
                String currentTime = timeFormatter.format(new Date());
                while ((clientMessage = in.readLine()) != null){
                    if (clientMessage.equalsIgnoreCase("exit")){
                        break;
                    }
                    String formattedMessage = username + ": " + clientMessage + "\t" + currentTime;
                    System.out.println(formattedMessage);

                    Database.saveMessage(username, clientMessage);
                    broadcast(formattedMessage, null);

                }

            } catch (IOException e){
                System.out.println(username + " connection error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (writers){
                    writers.remove(out);
                }
                if (username != null){
                    System.out.println(username + " disconnected!");
                }
                broadcast(username + " has left the chat", null);
            }
        }
    }
}
