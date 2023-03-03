package com.firstzoom.bluevisionlib.model;

import java.util.UUID;

public class User {
    UUID id;
    public String email,firstName,lastName,role;
    public String password;
    String fcmToken;
    Boolean isLoggedIn;
    String token;
    String subscriptionId;
    Boolean getNotify;
    String baseUrl;

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getLoggedIn() {
        return isLoggedIn;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRole() {
        return role;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Boolean getGetNotify() {
        return getNotify;
    }

    public void setGetNotify(Boolean getNotify) {
        this.getNotify = getNotify;
    }

    public User(String email, String password, String fcmToken) {
        this.email = email;
        this.password = password;
        this.fcmToken = fcmToken;
    }
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }
    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public String getToken() {
        return token;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void setEmail(String username) {
        email=username;
    }
}
