package com.prm392.assignment.productsale.chat;

import java.util.HashMap;
import java.util.Map;

public class ChatUser {
    private String id;
    private String name;
    private String email;
    private String role;
    private String profileImageUrl;
    private boolean online;
    private String lastSeen;

    // Empty constructor for Firebase
    public ChatUser() {
    }

    public ChatUser(String id, String name, String email, String role, String profileImageUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.profileImageUrl = profileImageUrl;
        this.online = false;
        this.lastSeen = "";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    // Friendly display of user type
    public String getUserType() {
        if ("seller".equals(role)) {
            return "Seller";
        } else {
            return "Buyer";
        }
    }
}