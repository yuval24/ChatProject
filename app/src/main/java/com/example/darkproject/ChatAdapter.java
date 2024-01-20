package com.example.darkproject;

import android.content.Context;
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

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatMessage> messages;
    Context context;


    public ChatAdapter(Context context, List<ChatMessage> messages) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String message = messages.get(position).getContent();
        String name = messages.get(position).getSender();
        holder.bind(message, name);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView messageTextView;
        private TextView nameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }

        public void bind(String message, String name) {
            messageTextView.setText(message);
            nameTextView.setText(name);
            // Add any additional binding logic based on your message model
        }
    }
}
