package com.example.darkproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import com.example.darkproject.network.ServerManager;
import com.example.sharedmodule.ChatMessage;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends AppCompatActivity implements ServerManager.SocketCallback{

    Button btnSend;
    ServerManager serverManager;
    EditText chat;

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();

        String chat_title = intent.getStringExtra("CHAT_TITLE");

        serverManager = ServerManager.getInstance("127.0.0.1", 3000);
        serverManager.setSocketCallback(this);

        recyclerView = findViewById(R.id.recyclerViewChat);
        chat = findViewById(R.id.chatEditText);
        btnSend = findViewById(R.id.sendBtn);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        chatMessages = new ArrayList<>();


        chatAdapter = new ChatAdapter(this, chatMessages);
        recyclerView.setAdapter(chatAdapter);

        // Notify the adapter after adding messages to the list






        Toast.makeText(this, "WELCOME TO THE DARK CHAT!", Toast.LENGTH_SHORT).show();


        btnSend.setOnClickListener(v -> {


            String message = chat.getText().toString();


            if(!message.equals("") && chat_title != null) {

                updateUIMessage(message,  serverManager.getClientUsername(), chat_title); // might change it later for a group for more then one

                new Thread(new Runnable() {
                    public void run() {
                        serverManager.sendAMessageToSomeone(message, chat_title);
                    }
                }).start();
                // Perform login or other actions when the button is clicked
            } else {
                Toast.makeText(this, "Sending nothing? I thought your Mother taught you better than That!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //adapt it later to the real sender and receiver when I am adding it to the UI
    public void updateUIMessage(String message, String sender, String recipient){
        chatMessages.add(new ChatMessage("CHAT", sender, recipient, message));
        chatAdapter.notifyDataSetChanged();
    }



    public void onDataReceived(String data) {
        // Handle the received data on the UI thread
        //change this later - just for checking
        if(data != null) {

            ChatMessage message = ChatMessage.fromJson(data);

            updateUIMessage(message.getContent(), message.getSender(), message.getRecipient());
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