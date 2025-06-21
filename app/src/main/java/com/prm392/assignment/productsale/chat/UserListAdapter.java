package com.prm392.assignment.productsale.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prm392.assignment.productsale.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    
    private Context context;
    private List<ChatUser> userList;
    private OnUserClickListener listener;
    
    public interface OnUserClickListener {
        void onUserClick(ChatUser user);
    }
    
    public UserListAdapter(Context context, List<ChatUser> userList, OnUserClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatUser user = userList.get(position);
        
        // Set user name
        holder.userName.setText(user.getName());
        
        // Set user type (Buyer/Seller)
        holder.userType.setText(user.getUserType());
        
        // Set online status
        if (user.isOnline()) {
            holder.userStatus.setText("Online");
            holder.userStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            if (user.getLastSeen() != null && !user.getLastSeen().isEmpty()) {
                holder.userStatus.setText("Last seen: " + user.getLastSeen());
            } else {
                holder.userStatus.setText("Offline");
            }
            holder.userStatus.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }
        
        // Load profile image
        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(user.getProfileImageUrl())
                    .placeholder(R.drawable.user_icon)
                    .error(R.drawable.user_icon)
                    .into(holder.userImage);
        } else {
            holder.userImage.setImageResource(R.drawable.user_icon);
        }
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserClick(user);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return userList.size();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImage;
        TextView userName, userStatus, userType;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            userStatus = itemView.findViewById(R.id.userStatus);
            userType = itemView.findViewById(R.id.userType);
        }
    }
}