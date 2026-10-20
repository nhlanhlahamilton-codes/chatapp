package za.co.codes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static final String URL = "jdbc:sqlite:chat.db";

    public static void initialize(){

        String sql = "CREATE TABLE IF NOT EXISTS messages(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "sender TEXT NOT NULL," +
                "message TEXT NOT NULL," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";

        try (Connection connection = DriverManager.getConnection(URL);
             Statement statement = connection.createStatement()){
             statement.execute(sql);
             System.out.println("Database initialized successfully");

        } catch (SQLException e){
            System.out.println("Database initialization error: " + e.getMessage());
        }
    }

    public static void saveMessage(String sender, String message){
        String sql = "INSERT INTO messages (sender, message) VALUES (?, ?)";

        try(Connection connection = DriverManager.getConnection(URL);
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, sender);
            preparedStatement.setString(2, message);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error saving message to database: " + e.getMessage());

        }
    }

    public static List<String> getRecentMessages() {
        List<String> history = new ArrayList<>();
        String sql = "SELECT sender, message FROM messages;";

        try (Connection connection = DriverManager.getConnection(URL);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                String sender = resultSet.getString("sender");
                String message = resultSet.getString("message");
                history.add( sender + ": " + message);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving chat history: " + e.getMessage());
        }
        return history;
    }

}
