package com.prm392.assignment.productsale.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392.assignment.productsale.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    private List<Message> messageList;
    private String currentUserId;
    private SimpleDateFormat formatter;

    public ChatAdapter(Context context, List<Message> messageList, String currentUserId) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
        this.formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_adapter_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messageList.get(position);

        // Format timestamp
        String time = formatter.format(new Date(message.getTimestamp()));

        // Check if message is from current user
        boolean isMyMessage = message.getSenderId().equals(currentUserId);

        if (isMyMessage) {
            // Display current user's message
            holder.myLayout.setVisibility(View.VISIBLE);
            holder.oppoLayout.setVisibility(View.GONE);

            holder.myMessage.setText(message.getContent());
            holder.myMsgTime.setText(time);

            // Display read status
            if (message.isReadBy(message.getReceiverId())) {
                holder.myMsgStatus.setVisibility(View.VISIBLE);
                holder.myMsgStatus.setText("Đã xem");
            } else {
                holder.myMsgStatus.setVisibility(View.VISIBLE);
                holder.myMsgStatus.setText("Đã gửi");
            }
        } else {
            // Display partner's message
            holder.myLayout.setVisibility(View.GONE);
            holder.oppoLayout.setVisibility(View.VISIBLE);

            holder.oppoMessage.setText(message.getContent());
            holder.oppoMsgTime.setText(time);

            // Mark message as read when displayed
            if (!message.isReadBy(currentUserId)) {
                message.markAsReadBy(currentUserId);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout oppoLayout, myLayout;
        TextView oppoMessage, myMessage, oppoMsgTime, myMsgTime, myMsgStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            oppoLayout = itemView.findViewById(R.id.oppoLayout);
            myLayout = itemView.findViewById(R.id.myLayout);
            oppoMessage = itemView.findViewById(R.id.oppoMessage);
            myMessage = itemView.findViewById(R.id.myMessage);
            oppoMsgTime = itemView.findViewById(R.id.oppoMsgTime);
            myMsgTime = itemView.findViewById(R.id.myMsgTime);
            myMsgStatus = itemView.findViewById(R.id.myMsgStatus);
        }
    }
}