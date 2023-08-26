package com.bscs.resellingapp.Adaptor;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bscs.resellingapp.ChatActivity;
import com.bscs.resellingapp.ProductChat;
import com.bscs.resellingapp.R;
import com.bscs.resellingapp.model.UserModel;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bscs.resellingapp.R;
import com.bscs.resellingapp.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<UserModel> userList;

    public UserAdapter(Context context, List<UserModel> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel user = userList.get(position);

        holder.userNameTextView.setText(user.getUsername());

        // Load the image using Picasso library into the ImageView
        Picasso.get()
                .load(user.getImageUrl())
                .placeholder(R.drawable.profileicon) // Placeholder image to show while loading
                .error(R.drawable.profileicon) // Error image to show if there's an error loading the image
                .into(holder.userImageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("receiverId",user.getUserId());
                intent.putExtra("senderId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                intent.putExtra("imageUrl",user.getImageUrl());
                intent.putExtra("username",user.getUsername());
                Toast.makeText(context.getApplicationContext(), "user Id is: " +user.getUserId() ,Toast.LENGTH_SHORT).show();

                context.startActivity(intent);

            }
        });
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImageView;
        TextView userNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.userp);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
        }
    }
}
