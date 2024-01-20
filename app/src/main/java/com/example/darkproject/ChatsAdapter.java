package com.example.darkproject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharedmodule.ChatMessage;

import java.io.Console;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    private List<Chat> chats;
    Context context;

    public ChatsAdapter(Context context, List<Chat> chats) {
        this.chats = chats;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        String message = chat.getTitle();
        holder.bind(message);

        holder.itemView.setOnClickListener(view ->{
            String title = chat.getTitle();
            openIndividualChatActivity(title);

        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    private void openIndividualChatActivity(String title) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("CHAT_TITLE", title); // later change it to the id
        context.startActivity(intent);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView chatTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chatTextView = itemView.findViewById(R.id.chatTextView);
        }

        public void bind(String message) {
            chatTextView.setText(message);
            // Add any additional binding logic based on your message model
        }
    }
}
