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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.prm392.assignment.productsale.chat.ChatUser;
import com.prm392.assignment.productsale.chat.FirebaseManager;
import com.prm392.assignment.productsale.chat.UserListAdapter;
import com.prm392.assignment.productsale.databinding.FragmentUserListBinding;
import com.prm392.assignment.productsale.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment implements UserListAdapter.OnUserClickListener {
    private static final String TAG = "UserListFragment";

    private FirebaseManager firebaseManager;
    private FragmentUserListBinding binding;
    private UserListAdapter adapter;
    private List<ChatUser> userList;
    private String currentUserId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        firebaseManager = FirebaseManager.getInstance(requireContext());
        currentUserId = firebaseManager.getCurrentUserId();

        initializeUIComponents();
        loadUsers();

        return view;
    }

    private void initializeUIComponents() {
        userList = new ArrayList<>();
        binding.userRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new UserListAdapter(requireContext(), userList, this);
        binding.userRecyclerView.setAdapter(adapter);
    }

    private void loadUsers() {
        binding.emptyView.setText("Loading users...");
        binding.emptyView.setVisibility(View.VISIBLE);

        firebaseManager.getAllUsers(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                if (snapshot.exists()) {
                    UserModel currentUser = firebaseManager.getCurrentUser();
                    String roleToShow = currentUser.hasStore() ? "buyer" : "seller";

                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        ChatUser user = userSnapshot.getValue(ChatUser.class);
                        if (user != null 
                            && !user.getId().equals(currentUserId) 
                            && user.getRole().equals(roleToShow)) {
                            userList.add(user);
                        }
                    }
                }

                updateUserListUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load users: " + error.getMessage());
                binding.emptyView.setText("Failed to load users: " + error.getMessage());
                binding.emptyView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateUserListUI() {
        adapter.notifyDataSetChanged();

        if (userList.isEmpty()) {
            binding.emptyView.setText(firebaseManager.isCurrentUserSeller() 
                ? "No buyers found" 
                : "No sellers found");
            binding.emptyView.setVisibility(View.VISIBLE);
        } else {
            binding.emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUserClick(ChatUser user) {
        // Open chat with selected user
        // You might want to use a navigation component or fragment transaction
        Toast.makeText(requireContext(), "Clicked on user: " + user.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}