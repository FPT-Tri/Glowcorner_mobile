package com.prm392.assignment.productsale.chat;

import java.util.HashMap;
import java.util.Map;

public class Message {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String content;
    private long timestamp;
    private boolean read;
    private Map<String, Boolean> readBy;

    // Constructor trống cho Firebase
    public Message() {
    }

    public Message(String messageId, String senderId, String receiverId, String content, long timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
        this.read = false;
        this.readBy = new HashMap<>();
    }

    // Getters và Setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Map<String, Boolean> getReadBy() {
        if (readBy == null) {
            readBy = new HashMap<>();
        }
        return readBy;
    }

    public void setReadBy(Map<String, Boolean> readBy) {
        this.readBy = readBy;
    }

    public void markAsReadBy(String userId) {
        if (readBy == null) {
            readBy = new HashMap<>();
        }
        readBy.put(userId, true);
    }

    public boolean isReadBy(String userId) {
        return readBy != null && readBy.containsKey(userId) && readBy.get(userId);
    }
}