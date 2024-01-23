package com.example.darkproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import com.example.darkproject.network.ServerManager;
import com.example.sharedmodule.ChatMessage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.util.*;


public class ChatsMenuActivity extends AppCompatActivity implements ServerManager.SocketCallback{

    private EditText recipientEditText;
    private ServerManager serverManager;
    private final Gson gson = new Gson();
    private final ArrayList<Chat> chats = new ArrayList<>();
    private ChatsAdapter chatsAdapter;
    private String currentName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //SQLiteDatabase db = dbHelper.getWritableDatabase();

        setContentView(R.layout.activity_chats_menu);

        serverManager = ServerManager.getInstance("127.0.0.1", 3000);
        serverManager.setSocketCallback(this);


        new Thread(() -> serverManager.getUsersFromServer()).start();

        FloatingActionButton addChatButton = findViewById(R.id.addChatBtn);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewChats);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);



        chatsAdapter = new ChatsAdapter(this, chats);

        recyclerView.setAdapter(chatsAdapter);



        addChatButton.setOnClickListener(view -> showNewChatDialog());
    }


    private void showNewChatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.new_chat_dialog, null);

        recipientEditText = dialogView.findViewById(R.id.recipientEditText);
        Button buttonStartChat = dialogView.findViewById(R.id.buttonStartChat);

        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        buttonStartChat.setOnClickListener(view -> {
            // Handle the button click event
            String chatName = recipientEditText.getText().toString();
            addChat(chatName);
            dialog.dismiss();  // Dismiss the dialog after handling the click
        });

        dialog.show();
    }

    public void addChat(String chatName){
        currentName = chatName;
        new Thread(() -> {
            serverManager.checkIfUserExists(chatName); // change it later to handle group with multiple users
        }).start();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onDataReceived(String data) {
        // Handle the received data on the UI thread
        //change this later - just for checking
        if(data != null) {
            try{
                ChatMessage message = ChatMessage.fromJson(data);
                if(message.getType().equals("SYSTEM")){
                    String res = message.getContent();
                    if(res.equals("OK")){
                        if(!Chat.isChatAlreadyExists(currentName, chats)) {
                            Chat chat = new Chat(currentName, "");
                            chats.add(chat);
                            chatsAdapter.notifyDataSetChanged();
                        }
                    }
                    currentName = "";
                } else if(message.getType().equals("GET-USERS")){
                    TypeToken<ArrayList<String>> token = new TypeToken<ArrayList<String>>() {};
                    ArrayList<String> users = gson.fromJson(message.getContent(), token.getType());
                    System.out.println(users);
                    for(int i = 0; i < users.size(); i++){
                        Chat chat = new Chat(users.get(i),"");
                        chats.add(chat);
                        chatsAdapter.notifyDataSetChanged();
                    }
                }
            } catch (Exception e){
                System.out.println("The Message is not parsable to the ChatMessage class");
                e.printStackTrace();
            }
        }

    }


    @Override
    public void onError(String errorMessage) {
        // Handle socket errors on the UI thread
        runOnUiThread(() -> {
            // Show error message or take appropriate action
        });
    }
}