package com.example.darkproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import com.example.darkproject.adapters.ChatsAdapter;
import com.example.darkproject.network.ServerManager;
import com.example.sharedmodule.Chat;
import com.example.sharedmodule.ChatMessage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.util.*;


public class ChatsMenuActivity extends AppCompatActivity implements ServerManager.SocketCallback{

    private ServerManager serverManager;
    private final Gson gson = new Gson();
    private final ArrayList<Chat> chats = new ArrayList<>();
    private ChatsAdapter chatsAdapter;
    private ArrayList<String> currentUserNamesInDialog;
    private String currentNameToAdd;
    private TextView userListTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        currentUserNamesInDialog = new ArrayList<>();
        View dialogView = inflater.inflate(R.layout.new_chat_dialog, null);

        userListTextView = dialogView.findViewById(R.id.userListTextView);
        EditText recipientEditText = dialogView.findViewById(R.id.recipientEditText);
        EditText titleEditText = dialogView.findViewById(R.id.titleEditText);
        Button buttonStartChat = dialogView.findViewById(R.id.buttonStartChat);
        Button buttonAddUser = dialogView.findViewById(R.id.buttonAddUser);


        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        buttonAddUser.setOnClickListener(view -> {
            // Handle the button click event
            currentNameToAdd = recipientEditText.getText().toString();
            if(!currentNameToAdd.equals(serverManager.getClientUsername())){
                recipientEditText.setText("");
                addUser(currentNameToAdd);
            }
        });

        buttonStartChat.setOnClickListener(view -> {
            // Handle the button click event
            String title = titleEditText.getText().toString();
            if(currentUserNamesInDialog.size() != 0 && !title.isEmpty()){
                currentUserNamesInDialog.add(serverManager.getClientUsername()); // add to the chat yourself
                Chat chat = new Chat(title, UniqueIdGenerator.generateUniqueId(),isChatIsPrivate());
                new Thread(() -> serverManager.sendNewChatToServer(chat, currentUserNamesInDialog)).start();
                chats.add(chat);
                chatsAdapter.notifyDataSetChanged();
                dialog.dismiss();  // Dismiss the dialog
            } else if(currentUserNamesInDialog.size() == 1){
                Chat chat = new Chat(currentUserNamesInDialog.get(0), UniqueIdGenerator.generateUniqueId(), true);
            }
            else {
                Toast.makeText(this,"Something is missing", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    public void addUser(String chatName){
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
                        currentUserNamesInDialog.add(currentNameToAdd);
                        userListTextView.append(currentNameToAdd+ ", ");
                    }

                } else if(message.getType().equals("GET-CHATS")){
                    TypeToken<ArrayList<Chat>> token = new TypeToken<ArrayList<Chat>>() {};
                    ArrayList<Chat> chatsFromServer = gson.fromJson(message.getContent(), token.getType());
                    System.out.println("***" + chatsFromServer.get(0).getTitle());
                    System.out.println(chatsFromServer);
                    for(Chat chat : chatsFromServer){
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

    private boolean isChatIsPrivate(){
        return currentUserNamesInDialog.size() == 1;
    }


    @Override
    public void onError(String errorMessage) {
        // Handle socket errors on the UI thread
        runOnUiThread(() -> {
            // Show error message or take appropriate action
        });
    }
}