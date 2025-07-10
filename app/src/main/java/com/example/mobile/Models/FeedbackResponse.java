package com.example.mobile.Models;

import java.util.List;

public class FeedbackResponse {
    private int status;
    private String description;
    private List<com.example.mobile.Models.Feedback> data;
    private String role;
    private String redirectUrl;
    private boolean success;

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<com.example.mobile.Models.Feedback> getData() { return data; }
    public void setData(List<com.example.mobile.Models.Feedback> data) { this.data = data; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}