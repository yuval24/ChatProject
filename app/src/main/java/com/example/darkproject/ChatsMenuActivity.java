package com.example.darkproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.darkproject.databinding.ActivityChatsMenuBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatsMenuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton addChatButton;
    private EditText recipientEditText;
    private Button buttonStartChat;

    private ChatsAdapter chatsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_chats_menu);


        addChatButton = findViewById(R.id.addChatBtn);
        recyclerView = findViewById(R.id.recyclerViewChats);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);



        chatsAdapter = new ChatsAdapter(this, Chat.chats);

        recyclerView.setAdapter(chatsAdapter);



        addChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewChatDialog();
            }
        });
    }


    private void showNewChatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.new_chat_dialog, null);

        recipientEditText = dialogView.findViewById(R.id.recipientEditText);
        buttonStartChat = dialogView.findViewById(R.id.buttonStartChat);

        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        buttonStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the button click event
                String chatName = recipientEditText.getText().toString();
                Chat chat = new Chat(chatName, "123");
                chatsAdapter.notifyDataSetChanged();
                dialog.dismiss();  // Dismiss the dialog after handling the click
            }
        });

        dialog.show();
    }
}