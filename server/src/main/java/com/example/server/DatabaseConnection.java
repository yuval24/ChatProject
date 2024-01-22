package com.example.server;

import com.example.sharedmodule.ChatMessage;
import com.example.sharedmodule.Message;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseConnection {
    private static DatabaseConnection databaseConnectionInstance = null;
    private Connection c;
    public static Connection getConnection() throws SQLException {
        String jdbcUrl = "jdbc:postgresql://localhost:5432/postgres";
        String username = "postgres";
        String password = "mipelord73";

        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    private DatabaseConnection(){
        try{
            c = getConnection();

        } catch (SQLException e){
            printSQLException(e);
        }
    }

    public static synchronized DatabaseConnection getInstance(){
        if (databaseConnectionInstance == null) {
            databaseConnectionInstance = new DatabaseConnection();
        }
        return databaseConnectionInstance;
    }

    public void sendUserToDatabase(String username, String password){
        try{
            String sql = "INSERT INTO users (username, password, created_on) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = c.prepareStatement(sql);

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

            preparedStatement.setDate(3, currentDate);  // Replace null with an actual Date object if needed

            preparedStatement.executeUpdate();
        } catch (SQLException e){
            printSQLException(e);
        }

    }

    public boolean isUsernameInDatabase(String username){
        try{
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement preparedStatement = c.prepareStatement(sql);

            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
               return true;
            }
        } catch (SQLException e){
            printSQLException(e);
        }
        return false;
    }

    public boolean isUsernameAndPasswordAreValid(String username, String password){
        try{
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = c.prepareStatement(sql);

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }
        } catch (SQLException e){
            printSQLException(e);
        }
        return false;
    }
    public static void printSQLException(SQLException ex) {
        for (Throwable e: ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }

    public void saveMessageInDatabase(ChatMessage message, String username){
        try{
            String sql= "INSERT INTO messages (sender, reciever, message_content) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = c.prepareStatement(sql);

            preparedStatement.setString(1, message.getSender());
            preparedStatement.setString(2, message.getRecipient());
            preparedStatement.setString(3, message.getContent());

            preparedStatement.executeUpdate();
        } catch (SQLException e){
            printSQLException(e);
        }
    }

    public ArrayList<String> getAllUsers(String currentUsername){
        try{
            String sql= "SElECT * FROM messages";
            PreparedStatement preparedStatement = c.prepareStatement(sql);

            ArrayList<String> users = new ArrayList<>();

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String sender =resultSet.getString("sender");
                String reciever = resultSet.getString("reciever");

                if(sender.equals(currentUsername)){
                    if(!users.contains(reciever)){
                        users.add(reciever);
                    }
                } else if(resultSet.getString("reciever").equals(currentUsername)){
                    if(!users.contains(sender)){
                        users.add(sender);
                    }
                }

            }
            return users;
        } catch (SQLException e){
            printSQLException(e);
        }
        return null;
    }
    public ArrayList<ChatMessage> getMessagesForCertainChat(String currentUsername, String otherUser){
        try{
            String sql= "SElECT * FROM messages";
            PreparedStatement preparedStatement = c.prepareStatement(sql);

            ArrayList<ChatMessage> messages = new ArrayList<>();

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String sender =resultSet.getString("sender");
                String reciever = resultSet.getString("reciever");

                if((sender.equals(currentUsername) || sender.equals(otherUser)) && (reciever.equals(currentUsername) || reciever.equals(otherUser))){
                    ChatMessage chatMessage = new ChatMessage("CHAT", sender, reciever,resultSet.getString("message_content"));
                    messages.add(chatMessage);
                }

            }
            return messages;
        } catch (SQLException e){
            printSQLException(e);
        }
        return null;
    }

}
