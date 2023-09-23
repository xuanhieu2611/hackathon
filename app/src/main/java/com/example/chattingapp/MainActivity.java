package com.example.chattingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText messageEditText;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageEditText = findViewById(R.id.messageEditText);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);

        // Initialize RecyclerView and Adapter
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(messageList);
        chatRecyclerView.setAdapter(chatAdapter);

        // Initialize Firebase Database reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("chat");

        // Listen for new messages
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                messageList.add(chatMessage);
                chatAdapter.notifyDataSetChanged();
            }

            // Handle other methods (onChildChanged, onChildRemoved, etc.) here

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    public void sendMessage(View view) {
        String message = messageEditText.getText().toString().trim();
        if (!message.isEmpty()) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("chat");
            String messageId = databaseReference.push().getKey();
            ChatMessage chatMessage = new ChatMessage(message, "User", messageId);
            databaseReference.child(messageId).setValue(chatMessage);
            messageEditText.setText("");
        }
    }

    public class ChatMessage {
        private String message;
        private String sender;
        private long timestamp;

        public ChatMessage() {
            // Default constructor required by Firebase
        }

        public ChatMessage(String message, String sender) {
            this.message = message;
            this.sender = sender;
            this.timestamp = System.currentTimeMillis(); // You can use a more precise timestamp mechanism
        }

        // Getters and setters
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
        private List<ChatMessage> messageList;

        public ChatAdapter(List<ChatMessage> messageList) {
            this.messageList = messageList;
        }

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            ChatMessage message = messageList.get(position);
            holder.bind(message);
        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }

        public class ChatViewHolder extends RecyclerView.ViewHolder {
            private TextView senderTextView;
            private TextView messageTextView;

            public ChatViewHolder(View itemView) {
                super(itemView);
                senderTextView = itemView.findViewById(R.id.senderTextView);
                messageTextView = itemView.findViewById(R.id.messageTextView);
            }

            public void bind(ChatMessage message) {
                senderTextView.setText(message.getSender());
                messageTextView.setText(message.getMessage());
            }
        }
    }
}
