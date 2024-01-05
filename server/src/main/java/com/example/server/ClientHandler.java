package com.example.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import com.example.sharedmodule.ControlMessage;
import com.example.sharedmodule.Message;
import com.google.gson.Gson;

import javax.imageio.IIOException;

public class ClientHandler implements Runnable{
    private static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

    private String clientUserName;
    private Gson gson = new Gson();
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;

    //constructor
    public ClientHandler(Socket socket){
        try{
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
            if(this.clientUserName.equals("")){
                this.clientUserName = parsedMessage.getSender();
            }
            if(parsedMessage instanceof ControlMessage){
                handleControlMessage((ControlMessage) parsedMessage);
            } else {
                handleChatMessage(parsedMessage);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // handle the chat messages from the client
    private void handleChatMessage(Message message) {
        if(message.getType().equals("CHAT")){
            for (ClientHandler client: clients) {
                if(message.getRecipient().equals(client.clientUserName)){
                    String content = message.getContent();
                    sendMessageToRecipient(client, content);
                }
            }
        }
    }


    // handle the control messages from the clients according to the protocol.
    private void handleControlMessage(ControlMessage message) {
        if(message.getType().equals("JOIN")){
            //ADD THE CLIENT TO THE DATABASE
            String content = "WELCOME TO THE DARK CHAT!";
            sendMessageToRecipient(this, content);
        }
        else if(message.getType().equals("LEAVE")){
            //REMOVE THE CLIENT FROM THE DATABASE
        }
    }

    // sending the message to the required user/users(later on).
    private void sendMessageToRecipient(ClientHandler targetClient, String content) {
        // Use your user or session manager to get the recipient's connection information
        // and send the message
        Message message = new Message("SYSTEM", "server", targetClient.clientUserName, content);
        try {
            String messageJson =  gson.toJson(message);

            targetClient.out.write(messageJson);
            targetClient.out.newLine();
            targetClient.out.flush();
        } catch(IOException e){
           closeEverything();
        }
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