package com.example.server;

import java.io.IOException;
import java.net.*;

public class ServerComms {

    //private User userConnected;
    private final ServerSocket serverSocket;
    private static final int PORT = 3000; // change it to the exact port later

    public ServerComms(ServerSocket serverSocket){ this.serverSocket = serverSocket; }

    public void startServer(){
        try{
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("a new client Connected");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch(IOException e){
            closeServer();
        }
    }

    public void closeServer() {
        try{
            this.serverSocket.close();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);

        System.out.println("Server listening on port " + PORT);
        ServerComms server = new ServerComms(serverSocket);
        server.startServer();
    }



}
