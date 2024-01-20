package com.example.darkproject;

import java.util.ArrayList;

public class Chat {
    public static ArrayList<Chat> chats = new ArrayList<>();
    private final String title;

    private final String chatId;

    public Chat(String title, String chatId){
        this.title = title;
        this.chatId = chatId;
        chats.add(this);
    }

    public String getChatId() {
        return chatId;
    }

    public String getTitle() {
        return title;
    }

//    public static String getChatById(String chat_Id){
//
//    }
}
