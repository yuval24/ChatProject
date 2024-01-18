package com.example.darkproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.darkproject.network.ServerManager;
import com.example.sharedmodule.ChatMessage;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.darkproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements ServerManager.SocketCallback{

    private ActivityMainBinding binding;
    private ServerManager serverManager;
    private Button btnSignUp;
    private Button btnLogin;
    private EditText etUsername;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);

        //----------------------------------------------------------------------
        setContentView(R.layout.activity_main);
        serverManager = ServerManager.getInstance("10.0.2.2", 3000);
        serverManager.setSocketCallback(this);

        etUsername = findViewById(R.id.userNameEditText);
        etPassword = findViewById(R.id.passwordEditText);
        btnLogin = findViewById(R.id.logInButton);
        btnSignUp = findViewById(R.id.sighUpButton);

        btnSignUp.setOnClickListener(v -> {
            // Perform login or other actions when the button is clicked
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            if(!username.equals("") && !password.equals("")) {
                new Thread(new Runnable() {
                    public void run() {
                        if(serverManager.getSocket() == null){
                            serverManager.connectToServerAsync();
                        }

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
                new Thread(new Runnable() {
                    public void run() {
                        if(serverManager.getSocket() == null){
                            serverManager.connectToServerAsync();
                        }

                        serverManager.logInToServer(username, password);
                    }
                }).start();
            } else {

            }

            // Perform login or other actions when the button is clicked

        });

    }


    private void navigateToChatActivity() {
        Intent intent = new Intent(this, ChatActivity.class);
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