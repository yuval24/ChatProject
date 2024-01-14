package com.example.sharedmodule;

import com.google.gson.Gson;

public class ControlMessage extends Message {

    // Constructors, getters, setters, etc.
    private String password;
    private String username;

    public ControlMessage(String type, String sender, String recipient, String username, String password){
        super(type, sender, recipient);
        this.password = password;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static ControlMessage fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ControlMessage.class);
    }
}
