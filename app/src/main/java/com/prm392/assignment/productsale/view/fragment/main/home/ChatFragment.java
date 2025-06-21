package com.prm392.assignment.productsale.view.fragment.main.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.prm392.assignment.productsale.R;
import com.prm392.assignment.productsale.chat.ChatAdapter;
import com.prm392.assignment.productsale.chat.Message;
import com.prm392.assignment.productsale.chat.FirebaseManager;
import com.prm392.assignment.productsale.chat.ChatUser;
import com.prm392.assignment.productsale.databinding.FragmentChatBinding;
import com.prm392.assignment.productsale.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";

    private FirebaseManager firebaseManager;
    private FragmentChatBinding binding;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private String currentUserId;
    private String partnerId;
    private ChatUser partnerUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        firebaseManager = FirebaseManager.getInstance(requireContext());
        UserModel currentUser = firebaseManager.getCurrentUser();
        currentUserId = firebaseManager.getCurrentUserId();
        initializeUIComponents();
        String currentUserRole = currentUser.hasStore() ? "seller" : "buyer";
        findChatPartner(currentUserRole);
        return view;
    }

    private void initializeUIComponents() {
        // Initialize message list and RecyclerView
        messageList = new ArrayList<>();
        binding.chattingRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        chatAdapter = new ChatAdapter(requireContext(), messageList, currentUserId);
        binding.chattingRecyclerView.setAdapter(chatAdapter);

        setupSendButton();
    }

    private void findChatPartner(String currentUserRole) {
        firebaseManager.findChatPartner(currentUserRole, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Chat partner search results: " + snapshot.getChildrenCount());

                if (!snapshot.exists()) {
                    Toast.makeText(requireContext(), "No chat partners available", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Find first available partner
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    partnerId = userSnapshot.getKey();
                    if (partnerId != null && !partnerId.equals(currentUserId)) {
                        Log.d(TAG, "Found chat partner: " + partnerId);
                        setupChatWithPartner();
                        return;
                    }
                }

                Toast.makeText(requireContext(), "Unable to find a chat partner", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error finding chat partner", error.toException());
                Toast.makeText(requireContext(), "Error finding chat partner", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupChatWithPartner() {
        if (partnerId == null) {
            Log.e(TAG, "Cannot setup chat - partner ID is null");
            return;
        }

        loadUserProfile();
        loadMessages();
        firebaseManager.markMessagesAsRead(partnerId);
    }

    private void loadUserProfile() {
        firebaseManager.getUserProfile(partnerId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    partnerUser = snapshot.getValue(ChatUser.class);
                    if (partnerUser != null) {
                        updateUserProfileUI(partnerUser);
                    }
                } else {
                    Log.e(TAG, "Partner user profile not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading user profile", error.toException());
                Toast.makeText(requireContext(), "Error loading user profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfileUI(ChatUser user) {
        binding.chatName.setText(user.getName());
        binding.statusText.setText(user.isOnline() ? "Online" : "Offline");

        Glide.with(requireContext())
                .load(user.getProfileImageUrl())
                .placeholder(R.drawable.user_icon)
                .into(binding.profilePic);
    }

    private void loadMessages() {
        firebaseManager.getMessages(partnerId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Ensure fragment is still active before processing data
                if (!isAdded() || getView() == null) {
                    Log.d(TAG, "Fragment not active, skipping message load");
                    return;
                }

                messageList.clear();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    if (message != null && isMessageBetweenUsers(message)) {
                        messageList.add(message);
                    }
                }

                updateMessageUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading messages", error.toException());
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Error loading messages", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isMessageBetweenUsers(Message message) {
        return (message.getSenderId().equals(currentUserId) && message.getReceiverId().equals(partnerId)) ||
                (message.getSenderId().equals(partnerId) && message.getReceiverId().equals(currentUserId));
    }

    private void updateMessageUI() {
        // Check if the fragment is attached and view is not destroyed
        if (!isAdded() || getView() == null || binding == null) {
            Log.d(TAG, "Cannot update message UI - fragment not active");
            return;
        }

        try {
            // Use getActivity().runOnUiThread to ensure UI updates happen on main thread
            getActivity().runOnUiThread(() -> {
                if (chatAdapter != null) {
                    chatAdapter.notifyDataSetChanged();

                    if (!messageList.isEmpty() && binding.chattingRecyclerView != null) {
                        binding.chattingRecyclerView.scrollToPosition(messageList.size() - 1);
                    }
                }

                // Mark messages as read
                if (partnerId != null) {
                    firebaseManager.markMessagesAsRead(partnerId);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error updating message UI", e);
        }
    }

    private void setupSendButton() {
        binding.sendBtn.setOnClickListener(v -> {
            String msg = binding.messageEditTxt.getText().toString().trim();

            // Validate message and chat partner
            if (msg.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
                return;
            }

            if (partnerId == null) {
                Toast.makeText(requireContext(), "No chat partner selected", Toast.LENGTH_SHORT).show();
                return;
            }

            // Send message
            sendMessage(msg);
        });
    }

    private void sendMessage(String content) {
        firebaseManager.sendMessage(partnerId, content, task -> {
            if (task.isSuccessful()) {
                binding.messageEditTxt.setText("");
                // Scroll to bottom after sending
                if (!messageList.isEmpty()) {
                    binding.chattingRecyclerView.scrollToPosition(messageList.size() - 1);
                }
            } else {
                Log.e(TAG, "Message send failed", task.getException());
                Toast.makeText(requireContext(), "Failed to send message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showErrorAndExit(String message) {
        Log.e(TAG, message);
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        requireActivity().onBackPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}