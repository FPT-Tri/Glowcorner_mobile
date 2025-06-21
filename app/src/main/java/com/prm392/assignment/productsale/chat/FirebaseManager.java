package com.prm392.assignment.productsale.chat;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prm392.assignment.productsale.model.UserModel;
import com.prm392.assignment.productsale.util.UserAccountManager;
import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";
    private static final String USERS_REF = "users";
    private static final String MESSAGES_REF = "messages";
    private static final String CONVERSATIONS_REF = "conversations";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsersRef;
    private DatabaseReference mMessagesRef;
    private DatabaseReference mConversationsRef;
    private String currentUserId;
    private UserModel currentUser;
    private Context context;

    private static FirebaseManager instance;

    public static synchronized FirebaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseManager(context);
        }
        return instance;
    }

    private FirebaseManager(Context context) {
        this.context = context;
        initializeFirebase();
    }

    private void initializeFirebase() {
        try {
            mDatabase = FirebaseDatabase.getInstance();
            mUsersRef = mDatabase.getReference(USERS_REF);
            mMessagesRef = mDatabase.getReference(MESSAGES_REF);
            mConversationsRef = mDatabase.getReference(CONVERSATIONS_REF);

            // Get current user from UserAccountManager
            currentUser = UserAccountManager.getUser(context);
            if (currentUser != null) {
                // Ensure currentUserId is a string and valid for Firebase
                currentUserId = String.valueOf(currentUser.getId());

                if (isValidUserId(currentUserId)) {
                    saveUserToFirebase();
                } else {
                    Log.e(TAG, "Invalid user ID");
                }
            } else {
                Log.e(TAG, "No current user found");
            }

            debugFirebaseConnection();
        } catch (Exception e) {
            Log.e(TAG, "Firebase initialization error", e);
        }
    }

    private boolean isValidUserId(String userId) {
        return userId != null && !userId.isEmpty();
    }

    private void debugFirebaseConnection() {
        try {
            DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean connected = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                    Log.d(TAG, "Firebase Connection Status: " + connected);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Firebase Connection Error: " + error.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Debug connection error", e);
        }
    }

    private void saveUserToFirebase() {
        try {
            if (currentUser != null && isValidUserId(currentUserId)) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", currentUserId);
                userMap.put("name", currentUser.getUserName());
                userMap.put("email", currentUser.getEmail());
                userMap.put("role", currentUser.hasStore() ? "seller" : "buyer");
                userMap.put("profileImageUrl", currentUser.getImageLink());
                userMap.put("online", true);

                mUsersRef.child(currentUserId).setValue(userMap)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "User saved successfully"))
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to save user", e));
            } else {
                Log.e(TAG, "Cannot save user - null user or user ID");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in saveUserToFirebase", e);
        }
    }

    public void findChatPartner(String currentUserRole, ValueEventListener listener) {
        // Determine partner role based on current user's role
        String partnerRole = currentUserRole.equals("seller") ? "buyer" : "seller";

        // Query for users with the opposite role
        mUsersRef.orderByChild("role").equalTo(partnerRole)
                .addListenerForSingleValueEvent(listener);
    }

    public void sendMessage(String receiverId, String content, OnCompleteListener<Void> listener) {
        if (!isValidUserId(currentUserId) || !isValidUserId(receiverId)) {
            Log.e(TAG, "Invalid sender or receiver ID");
            return;
        }

        // Create a new message ID
        String messageId = mMessagesRef.push().getKey();
        if (messageId == null) return;

        // Create message object
        long timestamp = System.currentTimeMillis();
        Message message = new Message(
                messageId,
                currentUserId,
                receiverId,
                content,
                timestamp
        );

        // Save the message
        mMessagesRef.child(messageId).setValue(message)
                .addOnCompleteListener(listener);

        // Update conversations for both users
        updateConversation(currentUserId, receiverId, messageId, content, timestamp);

    }

    private void updateConversation(String senderId, String receiverId,
                                    String lastMessageId, String lastMessageContent,
                                    long timestamp) {
        // Update sender's conversation (reset unread count)
        Map<String, Object> senderConversation = new HashMap<>();
        senderConversation.put("lastMessageId", lastMessageId);
        senderConversation.put("lastMessageContent", lastMessageContent);
        senderConversation.put("timestamp", timestamp);
        senderConversation.put("unreadCount", 0);
        mConversationsRef.child(senderId).child(receiverId).updateChildren(senderConversation);

        // Update receiver's conversation (increment unread count)
        mConversationsRef.child(receiverId).child(senderId)
                .child("unreadCount")
                .runTransaction(new com.google.firebase.database.Transaction.Handler() {
                    @NonNull
                    @Override
                    public com.google.firebase.database.Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        Integer currentCount = currentData.getValue(Integer.class);
                        currentData.setValue((currentCount == null ? 0 : currentCount) + 1);
                        return com.google.firebase.database.Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                        if (error != null) {
                            Log.e(TAG, "Conversation update failed", error.toException());
                        }
                    }
                });

        // Update receiver's last message details
        Map<String, Object> receiverConversation = new HashMap<>();
        receiverConversation.put("lastMessageId", lastMessageId);
        receiverConversation.put("lastMessageContent", lastMessageContent);
        receiverConversation.put("timestamp", timestamp);
        mConversationsRef.child(receiverId).child(senderId).updateChildren(receiverConversation);
    }

    public void getMessages(String partnerId, ValueEventListener listener) {
        if (!isValidUserId(currentUserId) || !isValidUserId(partnerId)) return;

        // Get messages between current user and partner
        Query query = mMessagesRef.orderByChild("timestamp");
        query.addValueEventListener(listener);
    }

    public void markMessagesAsRead(String partnerId) {
        if (!isValidUserId(currentUserId) || !isValidUserId(partnerId)) return;

        // Reset unread count for this conversation
        mConversationsRef.child(currentUserId)
                .child(partnerId)
                .child("unreadCount")
                .setValue(0);
    }

    // Existing getters and other methods remain the same
    public String getCurrentUserId() {
        return currentUserId;
    }

    public UserModel getCurrentUser() {
        return currentUser;
    }

    public boolean isCurrentUserSeller() {
        return currentUser != null && currentUser.hasStore();
    }
    public void getUserProfile(String userId, ValueEventListener listener) {
        if (!isValidUserId(userId)) {
            Log.e(TAG, "Invalid user ID for profile retrieval");
            return;
        }

        // Query the users reference to get user profile
        mUsersRef.child(userId).addListenerForSingleValueEvent(listener);
    }

    public void getAllUsers(ValueEventListener listener) {
        // Query all users in the database
        mUsersRef.addListenerForSingleValueEvent(listener);
    }
    public void updateUserOnlineStatus(boolean isOnline) {
        // Ensure current user is valid
        if (currentUser == null || !isValidUserId(currentUserId)) {
            Log.e(TAG, "Cannot update online status - invalid user");
            return;
        }

        try {
            // Update the user's online status in Firebase
            mUsersRef.child(currentUserId).child("online").setValue(isOnline)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Online status updated to: " + isOnline))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to update online status", e));
        } catch (Exception e) {
            Log.e(TAG, "Error updating online status", e);
        }
    }
}