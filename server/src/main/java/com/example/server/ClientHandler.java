package com.example.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import com.example.sharedmodule.ChatMessage;
import com.example.sharedmodule.ControlMessage;
import com.example.sharedmodule.Message;
import com.google.gson.Gson;



public class ClientHandler implements Runnable{
    private static final ArrayList<ClientHandler> clients = new ArrayList<>();

    private String clientUserName;
    private final Gson gson = new Gson();
    private Socket clientSocket;
    private DatabaseConnection databaseConnection;
    private BufferedReader in;
    private BufferedWriter out;

    //constructor
    public ClientHandler(Socket socket){
        try{
            this.databaseConnection = DatabaseConnection.getInstance();
            this.clientUserName = "";
            this.clientSocket = socket;
            this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            clients.add(this);
        } catch(IOException e){

            closeEverything();
        }
    }



    // This is the Thread that runs for the Current client and handles the communication with him.
    @Override
    public void run() {
        String messageFromClient;

        while(clientSocket.isConnected()){
            try{
                messageFromClient = in.readLine();

                System.out.println(messageFromClient);
                if(messageFromClient != null){
                    handleMessage(messageFromClient);
                } else {
                    closeEverything();
                }
            } catch(IOException e){
                closeEverything();
                break;
            }
        }
    }

    //Gets a message and handles it according to the protocol.
    private void handleMessage(String message){
        try{
            Message parsedMessage = Message.fromJson(message);
            if(parsedMessage.getType().equals("LOGIN") || parsedMessage.getType().equals("SIGNUP") || parsedMessage.getType().equals("LEAVE") || parsedMessage.getType().equals("USEREXISTS")){
                ControlMessage parsedControlMessage = ControlMessage.fromJson(message);
                handleControlMessage(parsedControlMessage);
            } else if(parsedMessage.getType().equals("CHAT") || parsedMessage.getType().equals("GET-USERS") || parsedMessage.getType().equals("GET-MESSAGES")){
                ChatMessage parsedChatMessage = ChatMessage.fromJson(message);
                handleDataMessage(parsedChatMessage);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // handle the chat messages from the client
    private void handleDataMessage(ChatMessage message) {
        switch (message.getType()) {
            case "CHAT":
                for (ClientHandler client : clients) {
                    if (message.getRecipient().equals(client.clientUserName)) {
                        String content = message.getContent();
                        String sender = message.getSender();
                        forwardChatMessage(client, content, sender);
                    } else if (isUsernameExists(message.getRecipient())) {
                        databaseConnection.saveMessageInDatabase(message, this.clientUserName);
                    }
                }
                break;
            case "GET-USERS":
                sendAListOfUsernamesExists(); // setting the content to the ArrayList of users

                break;
            case "GET-MESSAGES":
                sendAListOfMessages(message.getContent());
                break;
        }
    }

    private boolean isUsernameExists(String username){
        return databaseConnection.isUsernameInDatabase(username);
    }
    // handle the control messages from the clients according to the protocol.
    private void handleControlMessage(ControlMessage message) {
        switch (message.getType()) {
            case "SIGNUP": {
                //ADD THE CLIENT TO THE DATABASE
                this.clientUserName = message.getUsername();
                String content;
                if (databaseConnection.isUsernameInDatabase(this.clientUserName)) {
                    content = "FAILED";
                } else {
                    databaseConnection.sendUserToDatabase(this.clientUserName, message.getPassword());
                    content = "OK";
                }

                sendMessageToRecipient(this, content);
                break;
            }
            case "LOGIN": {
                this.clientUserName = message.getUsername();
                String password = message.getPassword();
                String content;
                if (databaseConnection.isUsernameAndPasswordAreValid(this.clientUserName, password)) {
                    content = "OK";
                } else {
                    content = "FAILED";
                }

                sendMessageToRecipient(this, content);
                break;
            }
            case "USEREXISTS": {
                String content;
                if (isUsernameExists(message.getUsername())) {
                    content = "OK";
                } else {
                    content = "FAILED";
                }
                System.out.println(content);
                sendMessageToRecipient(this, content);
                break;
            }
        }
    }
    private void sendAListOfMessages(String otherUser){
        ArrayList<ChatMessage> users = databaseConnection.getMessagesForCertainChat(this.clientUserName, otherUser);
        String usersJson = gson.toJson(users);
        ChatMessage usersMessage = new ChatMessage("GET-MESSAGES", "server", this.clientUserName, usersJson);
        try {
            String messageJson =  gson.toJson(usersMessage);

            this.out.write(messageJson);
            this.out.newLine();
            this.out.flush();
        } catch(IOException e){
            closeEverything();
        }
    }

    private void sendAListOfUsernamesExists(){
        ArrayList<String> users = databaseConnection.getAllUsers(this.clientUserName);
        String usersJson = gson.toJson(users);
        ChatMessage usersMessage = new ChatMessage("GET-USERS", "server", this.clientUserName, usersJson);
        try {
            String messageJson =  gson.toJson(usersMessage);

            this.out.write(messageJson);
            this.out.newLine();
            this.out.flush();
        } catch(IOException e){
            closeEverything();
        }
    }

    // sending the message to the required user/users(later on).
    private void sendMessageToRecipient(ClientHandler targetClient, String content) {
        // Use your user or session manager to get the recipient's connection information
        // and send the message
        ChatMessage message = new ChatMessage("SYSTEM", "server", targetClient.clientUserName, content);
        try {
            String messageJson =  gson.toJson(message);

            targetClient.out.write(messageJson);
            targetClient.out.newLine();
            targetClient.out.flush();
        } catch(IOException e){
           closeEverything();
        }
    }

    private void forwardChatMessage(ClientHandler targetClient, String content, String sender) {
        // Use your user or session manager to get the recipient's connection information
        // and send the message
        ChatMessage message = new ChatMessage("CHAT", sender, targetClient.clientUserName, content);
        if(!this.clientUserName.equals(targetClient.clientUserName)){
            try {
                String messageJson =  gson.toJson(message);

                targetClient.out.write(messageJson);
                targetClient.out.newLine();
                targetClient.out.flush();
            } catch(IOException e){
                closeEverything();
            }
        }
        databaseConnection.saveMessageInDatabase(message, this.clientUserName);
    }

    //closing the connection between the instance - the curr client, with the server.
    private void closeEverything(){
        removeClient();
        try{
            if(this.in != null){
                this.in.close();
            }
            if(this.out != null){
                this.out.close();
            }
            if(this.clientSocket != null){
                this.clientSocket.close();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("client is closing");
    }
    private void removeClient(){
        clients.remove(this);
    }

}