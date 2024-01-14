package com.example.darkproject.network;

import android.os.AsyncTask;

import com.example.sharedmodule.ChatMessage;
import com.example.sharedmodule.ControlMessage;
import com.example.sharedmodule.Message;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ServerManager {
    private static final int PORT = 3000;
    private static final String DEFAULT_IP = "127.0.0.1";

    private Socket socket;
    private BufferedReader in;
    private String clientUsername;
    private Gson gson = new Gson();
    private BufferedWriter out;
    private SocketCallback socketCallback;

    private String validatedIP;
    private int validatedPort;
    public ServerManager(SocketCallback callback, String ip, int port) {
        this.socketCallback = callback;
        this.clientUsername = null; // Set it to an appropriate default value if needed
        this.socket = null;
        this.in = null;
        this.out = null;
        // Validate and set the IP and PORT values
        this.validatedIP = (ip == null || ip.isEmpty()) ? DEFAULT_IP : ip;
        this.validatedPort = (port > 0) ? port : PORT;

    }

    // gets a username and a password and sends it to the server.
    public void connectToServer(String username, String password){
        this.clientUsername = username;
        ControlMessage logInMessage = new ControlMessage("JOIN", this.clientUsername, "server", username, password);
        String jsonMessage = gson.toJson(logInMessage);
        System.out.println(jsonMessage);
        sendMessageToServer(jsonMessage);
    }


    //send a json message to the server. could be a login message or a chat message.
    private void sendMessageToServer(String message) {
        try {
            this.out.write(message);
            this.out.newLine();
            this.out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            socketCallback.onError("Error sending message to server");
        }
    }

    public interface SocketCallback {
        void onDataReceived(String data);
        void onError(String errorMessage);
    }
    public void connectToServerAsync() {
        new ConnectToServer().execute();
    }


    // will get here all the messages from the server. including - history of the chat, new chat messages, all server messages.
    private class ConnectToServer extends AsyncTask<Void, Void ,Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                socket = new Socket(validatedIP, validatedPort);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                System.out.println("Connected");
                return true;
            } catch(IOException e){
                e.printStackTrace();
                return false;
            }
        }

        protected void onPostExecute(Boolean isConnected){
            if(isConnected){
                //socketCallback.onDataReceived("Connected to server");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                startListening();
            } else {
                socketCallback.onError("Failed to connect");
            }
        }
    }

    public void startListening() {
        new ReceiveDataTask().execute();
    }

    private class ReceiveDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Listen for incoming data
                return in.readLine();
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String data) {
            if (data != null) {
                // Notify the callback with the received data
                handleMessage(data);
                socketCallback.onDataReceived(data);
                // Continue listening for incoming data
                startListening();
            } else {
                // Notify the callback about a read error
                socketCallback.onError("Error reading data");
            }
        }
    }

    private Message handleMessage(String message){
        try{
            Message parsedMessage = Message.fromJson(message);
            if(parsedMessage instanceof ControlMessage){
                return (ControlMessage) parsedMessage;
            } else if(parsedMessage instanceof ChatMessage){
                return (ChatMessage) parsedMessage;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void closeResources() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        closeResources();
    }



}
