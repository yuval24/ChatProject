package com.example.sharedmodule;

import com.google.gson.Gson;

public class ChatMessage extends Message{
    private String content;
    public ChatMessage(String type, String sender, String recipient, String content) {
        super(type, sender, recipient);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static ChatMessage fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ChatMessage.class);
    }
}
