package com.example.sharedmodule;

import com.google.gson.Gson;

import java.util.ArrayList;

public class ChatMessageMultiple extends ChatMessage{
    private final ArrayList<String> users;

    public ChatMessageMultiple(String type, String sender, String recipient, String content, ArrayList<String> users) {
        super(type, sender, recipient, content);
        this.users = users;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public static ChatMessageMultiple fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ChatMessageMultiple.class);
    }
}
