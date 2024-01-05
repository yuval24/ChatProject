package com.example.server;

public class Protocol {
    public static final String MESSAGE_TYPE_CHAT = "CHAT";
    public static final String MESSAGE_TYPE_COMMAND = "COMMAND";
    public static final String MESSAGE_TYPE_SYSTEM = "SYSTEM";
    public static final String MESSAGE_TYPE_LEAVE = "LEAVE";
    public static final String MESSAGE_TYPE_JOIN = "JOIN";

    public static String extractMessageType(String message){
        String[] parts = message.split(":", 2);
        if(parts.length == 2){
            return parts[0];
        } else {
            return null;
        }
    }
}
