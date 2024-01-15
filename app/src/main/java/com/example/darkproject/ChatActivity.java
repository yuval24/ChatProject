package com.example.darkproject;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import com.example.darkproject.network.ServerManager;
import com.example.sharedmodule.ChatMessage;


public class ChatActivity extends AppCompatActivity implements ServerManager.SocketCallback{

    Button btnSend;
    ServerManager serverManager;
    EditText chat;
    EditText nameToSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ServerManager serverManager = ServerManager.getInstance("your_ip", 3000);
        serverManager.setSocketCallback(this);

        chat = findViewById(R.id.chatEditText);
        btnSend = findViewById(R.id.sendBtn);
        nameToSend = findViewById(R.id.nameEditText);

        Toast.makeText(this, "WELCOME TO THE DARK CHAT!", Toast.LENGTH_SHORT).show();


        btnSend.setOnClickListener(v -> {


            String message = chat.getText().toString();
            String name = nameToSend.getText().toString();

            if(!message.equals("") && !name.equals("")) {
                Toast.makeText(this, "Aww jeez! " + serverManager.getClientUsername(), Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    public void run() {
                        serverManager.sendAMessageToSomeone(message, name);
                    }
                }).start();
                // Perform login or other actions when the button is clicked
            } else {
                Toast.makeText(this, "Sending nothing? I thought your Mother taught you better than That!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onDataReceived(String data) {
        // Handle the received data on the UI thread
        //change this later - just for checking
        if(data != null) {

            ChatMessage message = ChatMessage.fromJson(data);

            Toast.makeText(this, message.getContent(), Toast.LENGTH_SHORT).show();
        }

        runOnUiThread(() -> {

            // Update UI or perform actions based on the received data
        });
    }


    @Override
    public void onError(String errorMessage) {
        // Handle socket errors on the UI thread
        runOnUiThread(() -> {
            // Show error message or take appropriate action
        });
    }
}