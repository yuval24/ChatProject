package com.example.server;

import com.example.sharedmodule.Chat;
import com.example.sharedmodule.ChatMessage;


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
        String password = "password";

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

    public void setNewChat(String chatId, String chatTitle){
        try{
            String sql = "INSERT INTO chats (chat_id, chat_title) VALUES(?, ?)";
            PreparedStatement preparedStatement = c.prepareStatement(sql);

            preparedStatement.setString(1, chatId);
            preparedStatement.setString(2,chatTitle);

            preparedStatement.executeUpdate();
        } catch(SQLException e){
            printSQLException(e);
        }
    }

    public ArrayList<String> getUsernamesInChat(String chatId){
        try{
            String sql = "SELECT username FROM chats_to_usernames WHERE chat_id = ?";
            PreparedStatement preparedStatement = c.prepareStatement(sql);

            preparedStatement.setString(1, chatId);

            ArrayList<String> usernames = new ArrayList<>();

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                String username = resultSet.getString("username");
                usernames.add(username);
            }
            return usernames;
        } catch (SQLException e){
            printSQLException(e);
        }
        return null;
    }

    public void setTheUsersIntoCertainChats(String chatId, ArrayList<String> usernames){
        try{
            String sql = "INSERT INTO chats_to_usernames (chat_id, username) VALUES(?, ?)";
            for (int i = 0; i < usernames.size(); i++){
                PreparedStatement preparedStatement = c.prepareStatement(sql);

                preparedStatement.setString(1,chatId);
                preparedStatement.setString(2, usernames.get(i));

                preparedStatement.executeUpdate();
            }
        } catch(SQLException e){
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

    public void saveMessageInDatabase(ChatMessage message){
        try{
            String sql= "INSERT INTO messages (sender, receivers_id, message_content) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = c.prepareStatement(sql);

            preparedStatement.setString(1, message.getSender());
            preparedStatement.setString(2, message.getRecipient());
            preparedStatement.setString(3, message.getContent());

            preparedStatement.executeUpdate();
        } catch (SQLException e){
            printSQLException(e);
        }
    }

    public ArrayList<Chat> getAllChatsForUsername(String username){
        try{
            String sql= "SElECT * FROM chats_to_usernames WHERE username = ?";
            PreparedStatement preparedStatement = c.prepareStatement(sql);

            preparedStatement.setString(1, username);
            ArrayList<Chat> chats = new ArrayList<>();

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String chatId = resultSet.getString("chat_id");
                String title = getChatTitleForChatId(chatId);
                boolean isPrivate = isChatIsPrivate(chatId);
                Chat chat = new Chat(title, chatId, isPrivate);
                chats.add(chat);
            }
            return chats;
        } catch (SQLException e){
            printSQLException(e);
        }
        return null;
    }

    public boolean isChatIsPrivate(String chatId){
        try{
            String sql= "SElECT * FROM chats_to_usernames WHERE chat_id = ?";
            PreparedStatement preparedStatement = c.prepareStatement(sql);

            preparedStatement.setString(1, chatId);

            ResultSet resultSet = preparedStatement.executeQuery();
            int countForTwoChats = 0;
            while(resultSet.next()){
                countForTwoChats++;
            }

            return countForTwoChats == 2;
        } catch (SQLException e){
            printSQLException(e);
        }
        return false;
    }


    public ArrayList<String> getAllChatsIdForUsername(String username){
        try{
            String sql= "SElECT * FROM chats_to_usernames WHERE username = ?";
            PreparedStatement preparedStatement = c.prepareStatement(sql);

            preparedStatement.setString(1, username);
            ArrayList<String> chatIds = new ArrayList<>();

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String chatId = resultSet.getString("chat_id");
                chatIds.add(chatId);
            }
            return chatIds;
        } catch (SQLException e){
            printSQLException(e);
        }
        return null;
    }

    public String getChatTitleForChatId(String chatId){
        try{
            String sql = "SELECT chat_title FROM chats WHERE chat_id = ?";
            PreparedStatement preparedStatement = c.prepareStatement(sql);

            preparedStatement.setString(1,chatId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getString("chat_title");
            }
            return "FAILED";
        } catch(SQLException e){
            printSQLException(e);
        }
        return null;
    }
    public ArrayList<ChatMessage> getMessagesForCertainChat(String chatId){
        try{
            String sql= "SElECT * FROM messages WHERE receivers_id = ?";
            PreparedStatement preparedStatement = c.prepareStatement(sql);

            preparedStatement.setString(1, chatId);
            ArrayList<ChatMessage> messages = new ArrayList<>();

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String sender = resultSet.getString("sender");
                String content = resultSet.getString("message_content");

                ChatMessage chatMessage = new ChatMessage("CHAT", sender, chatId, content);
                messages.add(chatMessage);
            }
            return messages;
        } catch (SQLException e){
            printSQLException(e);
        }
        return null;
    }



}
