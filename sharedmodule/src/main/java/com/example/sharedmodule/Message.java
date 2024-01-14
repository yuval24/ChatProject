package com.example.sharedmodule;

import com.google.gson.Gson;

public class Message{
    public static final String TYPE_CHAT = "CHAT";
    public static final String TYPE_SYSTEM = "SYSTEM";
    public static final String TYPE_JOIN = "JOIN";
    public static final String TYPE_COMMAND = "COMMAND";
    public static final String TYPE_LEAVE = "LEAVE";

    private String type;
    private String sender;
    private String recipient;

    //private long timestamp;


    public Message(String type, String sender, String recipient) {
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSender() {
        return sender;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public static Message fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Message.class);
    }

}