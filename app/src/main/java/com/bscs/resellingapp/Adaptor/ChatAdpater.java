package com.bscs.resellingapp.Adaptor;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bscs.resellingapp.R;
import com.bscs.resellingapp.model.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatAdpater extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SENDER_VIEW_TYPE = 1;
    private static final int RECEIVER_VIEW_TYPE = 2;

    private ArrayList<ChatMessage> messageModels;
    private Context context;
    private String recId;

    public ChatAdpater(ArrayList<ChatMessage> messageModels, Context context, String recId) {
        this.messageModels = messageModels;
        this.context = context;
        this.recId = recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == SENDER_VIEW_TYPE) {
            view = LayoutInflater.from(context).inflate(R.layout.sender_item, parent, false);
            return new SenderViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.reciever_item, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messageModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageModels.get(position);
        if (holder instanceof SenderViewHolder) {
            ((SenderViewHolder) holder).senderMsg.setText(message.getMessage());
            ((SenderViewHolder) holder).senderTime.setText(formatTimestamp(message.getTimestamp()));
        } else if (holder instanceof ReceiverViewHolder) {
            ((ReceiverViewHolder) holder).receiverMsg.setText(message.getMessage());
            ((ReceiverViewHolder) holder).receiverTime.setText(formatTimestamp(message.getTimestamp()));
        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(new Date(timestamp));
    }

    static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg;
        TextView senderTime;

        SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderMessageTextView);
            senderTime = itemView.findViewById(R.id.senderMessageTimeTextView);
        }
    }

    static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMsg;
        TextView receiverTime;

        ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.receiverMessageTextView);
            receiverTime = itemView.findViewById(R.id.receiverMessageTimeTextView);
        }
    }
}
