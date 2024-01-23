package com.example.darkproject;

import java.util.ArrayList;

public class Chat {
    private final String title;

    private final String chatId;

    public Chat(String title, String chatId){
        this.title = title;
        this.chatId = chatId;
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

}
