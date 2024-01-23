package com.example.darkproject.network;

import android.os.AsyncTask;

import com.example.sharedmodule.ChatMessage;
import com.example.sharedmodule.ControlMessage;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;

public class ServerManager {
    private static ServerManager serverManagerInstance = null;
    private static final int PORT = 3000;
    private static final String DEFAULT_IP = "127.0.0.1";

    public Socket getSocket() {
        return socket;
    }

    private Socket socket;
    private BufferedReader in;
    private String clientUsername;
    private final Gson gson = new Gson();
    private BufferedWriter out;
    private SocketCallback socketCallback;

    private final String validatedIP;
    private final int validatedPort;
    private ServerManager(String ip, int port) {
        this.socketCallback = null;
        this.clientUsername = null; // Set it to an appropriate default value if needed
        this.socket = null;
        this.in = null;
        this.out = null;
        // Validate and set the IP and PORT values
        this.validatedIP = (ip == null || ip.isEmpty()) ? DEFAULT_IP : ip;
        this.validatedPort = (port > 0) ? port : PORT;

    }

    public static synchronized ServerManager getInstance(String ip, int port) {
        if (serverManagerInstance == null) {
            serverManagerInstance = new ServerManager(ip, port);
        }
        return serverManagerInstance;
    }

    public void setSocketCallback(SocketCallback callback) {
        this.socketCallback = callback;
    }

    // gets a username and a password and sends it to the server.
    public void signUpToServer(String username, String password){
        this.clientUsername = username;
        ControlMessage logInMessage = new ControlMessage("SIGNUP", this.clientUsername, "server", username, password);
        String jsonMessage = gson.toJson(logInMessage);
        System.out.println(jsonMessage);
        sendMessageToServer(jsonMessage);
    }

    public void checkIfUserExists(String username){
        ControlMessage checkMessage = new ControlMessage("USEREXISTS", this.clientUsername, "server", username, "");
        String jsonMessage = gson.toJson(checkMessage);
        System.out.println(jsonMessage);
        sendMessageToServer(jsonMessage);
    }

    public void logInToServer(String username, String password){
        this.clientUsername = username;
        ControlMessage logInMessage = new ControlMessage("LOGIN", this.clientUsername, "server", username, password);
        String jsonMessage = gson.toJson(logInMessage);
        System.out.println(jsonMessage);
        sendMessageToServer(jsonMessage);
    }

    public void sendAMessageToSomeone(String message, String name){
        ChatMessage chatMessage = new ChatMessage("CHAT", this.clientUsername, name, message);
        String jsonMessage = gson.toJson(chatMessage);
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
            socketCallback.onError("Error sending message to server");
        }
    }

    public void getUsersFromServer(){
        ChatMessage chatMessage = new ChatMessage("GET-USERS", this.clientUsername, "server", "OK");
        String jsonMessage = gson.toJson(chatMessage);
        System.out.println(jsonMessage);
        sendMessageToServer(jsonMessage);
    }

    public void getMessagesFromServerForCertainUser(String chat_title){
        ChatMessage chatMessage = new ChatMessage("GET-MESSAGES", this.clientUsername, "server", chat_title);
        String jsonMessage = gson.toJson(chatMessage);
        System.out.println(jsonMessage);
        sendMessageToServer(jsonMessage);
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public interface SocketCallback {
        void onDataReceived(String data);
        void onError(String errorMessage);
    }
    public void connectToServerAsync() {
        new ConnectToServer(this).execute();
    }


    // will get here all the messages from the server. including - history of the chat, new chat messages, all server messages.
    private static class ConnectToServer extends AsyncTask<Void, Void ,Boolean> {
        private WeakReference<ServerManager> serverManagerReference;

        ConnectToServer(ServerManager serverManager) {
            this.serverManagerReference = new WeakReference<>(serverManager);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            ServerManager serverManager = serverManagerReference.get();
            if (serverManager == null) {
                return false;
            }

            try{
                serverManager.socket = new Socket(serverManager.validatedIP, serverManager.validatedPort);
                serverManager.in = new BufferedReader(new InputStreamReader(serverManager.socket.getInputStream()));
                serverManager.out = new BufferedWriter(new OutputStreamWriter(serverManager.socket.getOutputStream()));
                System.out.println("Connected");
                return true;
            } catch(IOException e){
                e.printStackTrace();
                return false;
            }
        }

        protected void onPostExecute(Boolean isConnected) {
            ServerManager serverManager = serverManagerReference.get();
            if (serverManager == null) {
                return;
            }

            if (isConnected) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                serverManager.startListening();
            } else {
                serverManager.socketCallback.onError("Failed to connect");
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

                socketCallback.onDataReceived(data);
                // Continue listening for incoming data
                startListening();
            } else {
                // Notify the callback about a read error
                socketCallback.onError("Error reading data");
            }
        }
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
