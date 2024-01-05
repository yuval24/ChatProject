package com.example.server;

import java.io.IOException;
import java.net.*;

public class ServerComms {

    //private User userConnected;
    private ServerSocket serverSocket;
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
//    public User initiate(String userName, String password){
//        /*TODO
//            asks the server if userName exists
//         */
//        if(!doesUserNameExist(userName)){
//
//        }
//
//
//
//
//
//        return null;
//    }



    public boolean doesUserNameExist(String userName){



        return false;
    }


    /**
     *  userName must exists in the server.
     * @param userName
     * @param password
     * @return
     */
    public boolean doesPasswordMuchUserName(String userName,String password){



        return false;
    }



}
