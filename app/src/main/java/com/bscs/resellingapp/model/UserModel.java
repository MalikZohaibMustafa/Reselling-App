package com.bscs.resellingapp.model;

public class UserModel {
    private String userId;
    private String email;
    private String username;
    private String imageUrl;

    public UserModel(String userId, String email, String username) {
        this.userId = userId;
        this.email = email;
        this.username = username;
    }

    public UserModel(String userId, String email, String username, String imageUrl) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.imageUrl = imageUrl;
    }

    public UserModel(int i, String john) {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
