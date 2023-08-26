package com.bscs.resellingapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bscs.resellingapp.Adaptor.ChatAdpater;
import com.bscs.resellingapp.databinding.ActivityChatScreenBinding;
import com.bscs.resellingapp.model.ChatMessage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.nio.charset.spi.CharsetProvider;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatScreenBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        // Get the receiverId and other details from the intent extras
        String receiverId = getIntent().getStringExtra("receiverId");
        String senderId = getIntent().getStringExtra("senderId");
        String userName = getIntent().getStringExtra("username");
        imageUrl = getIntent().getStringExtra("imageUrl");

        // Set the username in the TextView
        binding.tv1.setText(userName);

        // Load the image using Picasso into the ImageView
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.avatar)
                .into(binding.imageView);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        final ArrayList<ChatMessage> messageModels = new ArrayList<>();
        final ChatAdpater chatAdapter = new ChatAdpater(messageModels, this, receiverId);
        binding.chatRecyclerView.setAdapter(chatAdapter);

        final String senderRoom = senderId + receiverId;
        final String receiverRoom = receiverId + senderId;

        database.getReference().child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ChatMessage model = dataSnapshot.getValue(ChatMessage.class);
                            model.setMessageId(dataSnapshot.getKey());
                            messageModels.add(model);
                        }
                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatActivity.this, "error showing data in rv", Toast.LENGTH_SHORT).show();
                    }
                });
        binding.snd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.inputMessage.getText().toString();
                final ChatMessage model = new ChatMessage(senderId, message);
                model.setTimestamp(new Date().getTime());
                binding.inputMessage.setText("");
                database.getReference().child("chats")
                        .child(senderRoom)
                        .push()
                        .setValue(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("chats")
                                        .child(receiverRoom)
                                        .push()
                                        .setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                // Message sent successfully
                                            }
                                        });
                            }
                        });
            }
        });
    }
}
