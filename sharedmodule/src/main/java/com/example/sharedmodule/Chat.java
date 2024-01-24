package com.example.sharedmodule;

import com.google.gson.Gson;

import java.util.ArrayList;

public class Chat {
    private final String title;

    private final String chatId;
    private final boolean isPrivateChat;


    public Chat(String title, String chatId, boolean isPrivateChat){
        this.title = title;
        this.chatId = chatId;
        this.isPrivateChat = isPrivateChat;
    }
    public boolean isPrivateChat() {
        return isPrivateChat;
    }
    public String getChatId() {
        return chatId;
    }

    public String getTitle() {
        return title;
    }

    public static boolean isChatAlreadyExists(String chat_title, ArrayList<Chat> chats){
        for (Chat chat: chats) {
            if(chat.getTitle().equals(chat_title)){
                return true;
            }
        }
        return false;
    }

    public static Chat fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Chat.class);
    }

}
