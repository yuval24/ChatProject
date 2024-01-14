package com.example.darkproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.darkproject.network.ServerManager;
import com.example.sharedmodule.ChatMessage;
import com.example.sharedmodule.Message;
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
    private Button btnConnectServer;
    private Button btnLogin;
    private EditText etUsername;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //----------------------------------------------------------------------

        serverManager = new ServerManager(this,"10.0.2.2", 3000);

        etUsername = (EditText)findViewById(R.id.userNameEditText);
        etPassword = (EditText)findViewById(R.id.passwordEditText);
        btnLogin = (Button) findViewById(R.id.logInButton);
        btnConnectServer = (Button) findViewById(R.id.connectServerBtn);

        btnConnectServer.setOnClickListener(v -> {
            // Perform login or other actions when the button is clicked
            serverManager.connectToServerAsync();
        });

        btnLogin.setOnClickListener(v -> {
            new Thread(new Runnable() {
                public void run() {
                    // Call your network operation here
                    serverManager.connectToServer(etUsername.getText().toString(), etPassword.getText().toString());                }
            }).start();
            // Perform login or other actions when the button is clicked

        });

    }

    @Override
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serverManager.disconnect();
    }

}