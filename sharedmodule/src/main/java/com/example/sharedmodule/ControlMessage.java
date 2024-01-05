package com.example.sharedmodule;

import com.google.gson.Gson;

public class ControlMessage extends Message {
    public static final String TYPE_JOIN = "JOIN";
    public static final String TYPE_COMMAND = "COMMAND";
    public static final String TYPE_LEAVE = "LEAVE";

    // Constructors, getters, setters, etc.

    public ControlMessage(String type, String sender, String recipient, String content){
        super(type, sender, recipient, content);
    }
    public static ControlMessage fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ControlMessage.class);
    }
}
