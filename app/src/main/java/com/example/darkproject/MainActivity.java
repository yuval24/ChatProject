package com.example.darkproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.darkproject.network.ServerManager;
import com.example.sharedmodule.ChatMessage;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements ServerManager.SocketCallback{

    private ServerManager serverManager;
    private EditText etUsername;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        serverManager = ServerManager.getInstance("10.0.2.2", 3000);
        serverManager.setSocketCallback(this);

        etUsername = findViewById(R.id.userNameEditText);
        etPassword = findViewById(R.id.passwordEditText);
        Button btnLogin = findViewById(R.id.logInButton);
        Button btnSignUp = findViewById(R.id.sighUpButton);

        btnSignUp.setOnClickListener(v -> {
            // Perform login or other actions when the button is clicked
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            if(!username.equals("") && !password.equals("")) {
                new Thread(() -> {
                    if(serverManager.getSocket() == null){
                        serverManager.connectToServerAsync();
                        serverManager.signUpToServer(username, password);
                    } else{
                        serverManager.signUpToServer(username, password);
                    }
                }).start();
            } else {
                Toast.makeText(this,"There is a empty field, moron!", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            if(!username.equals("") && !password.equals("")) {
                new Thread(() -> {
                    if(serverManager.getSocket() == null) {
                        serverManager.connectToServerAsync();
                        serverManager.logInToServer(username, password);
                    } else{
                        serverManager.logInToServer(username, password);
                    }
                }).start();
            } else {
                Toast.makeText(this,"There is a empty field, moron!", Toast.LENGTH_SHORT).show();
            }

            // Perform login or other actions when the button is clicked

        });

    }


    private void navigateToChatActivity() {
        Intent intent = new Intent(this, ChatsMenuActivity.class);
        startActivity(intent);
        finish(); // Optional: Finish the login activity so the user can't go back to it
    }
    @Override
    public void onDataReceived(String data) {
        // Handle the received data on the UI thread
        //change this later - just for checking
        if(data != null) {
            try{
                ChatMessage message = ChatMessage.fromJson(data);
                if(message.getContent().equals("OK")) {
                    navigateToChatActivity();
                } else if(message.getContent().equals("FAILED")){
                    Toast.makeText(this,"Try another Username", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        runOnUiThread(() -> {

            // Update UI or perform actions based on the received data
        });
    }


    @Override
    public void onError(String errorMessage) {
        // Handle socket errors on the UI thread
        runOnUiThread(() -> {
            Toast.makeText(this,"Wasn't able to connect to the server", Toast.LENGTH_SHORT).show();
            // Show error message or take appropriate action
        });
    }



}