package com.bscs.resellingapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Myviewholder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView nameView,emailView;
    public Myviewholder(@NonNull View itemView) {
        super(itemView);
        imageView=itemView.findViewById(R.id.imageviewchat);
        nameView=itemView.findViewById(R.id.namechat);
        emailView=itemView.findViewById((R.id.namechat2));

    }
}
