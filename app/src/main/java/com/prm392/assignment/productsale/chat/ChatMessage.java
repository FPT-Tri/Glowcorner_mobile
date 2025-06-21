package com.prm392.assignment.productsale.chat;

public class ChatMessage {
    private String message;
    private String timestamp;
    private boolean isMyMessage;

    public ChatMessage(String message, String timestamp, boolean isMyMessage) {
        this.message = message;
        this.timestamp = timestamp;
        this.isMyMessage = isMyMessage;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isMyMessage() {
        return isMyMessage;
    }
}