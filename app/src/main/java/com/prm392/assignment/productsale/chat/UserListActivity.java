package com.prm392.assignment.productsale.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.prm392.assignment.productsale.R;
import com.prm392.assignment.productsale.view.fragment.main.home.ChatFragment;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity implements UserListAdapter.OnUserClickListener {
    private static final String TAG = "UserListActivity";

    private RecyclerView recyclerView;
    private UserListAdapter adapter;
    private List<ChatUser> userList;
    private FirebaseManager firebaseManager;
    private TextView emptyView;
    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        // Initialize FirebaseManager
        firebaseManager = FirebaseManager.getInstance(this);

        // Initialize views
        recyclerView = findViewById(R.id.userRecyclerView);
        emptyView = findViewById(R.id.emptyView);
        titleText = findViewById(R.id.titleText);

        // Initialize user list
        userList = new ArrayList<>();

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserListAdapter(this, userList, this);
        recyclerView.setAdapter(adapter);

        // Load users based on current user's role
        loadUsers();
    }

    private void loadUsers() {
        // Show loading state
        emptyView.setText("Loading users...");
        emptyView.setVisibility(View.VISIBLE);

        firebaseManager.getAllUsers(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                if (snapshot.exists()) {
                    String currentUserId = firebaseManager.getCurrentUserId();
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        ChatUser user = userSnapshot.getValue(ChatUser.class);
                        if (user != null && !user.getId().equals(currentUserId)) {
                            userList.add(user);
                        }
                    }
                }

                adapter.notifyDataSetChanged();

                // Show empty view if no users found
                if (userList.isEmpty()) {
                    emptyView.setText("No users found");
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load users: " + error.getMessage());
                emptyView.setText("Failed to load users: " + error.getMessage());
                emptyView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onUserClick(ChatUser user) {
        // Open chat with selected user
        Intent intent = new Intent(this, ChatFragment.class);
        intent.putExtra("partner_id", user.getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update online status
        firebaseManager.updateUserOnlineStatus(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Update offline status
        firebaseManager.updateUserOnlineStatus(false);
    }
}