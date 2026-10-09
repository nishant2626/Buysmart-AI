package com.buysmart.ai.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String profileImage;
    private Map<String, Object> preferences;
    private List<String> savedProducts;
    private List<String> searchHistory;
    private long createdAt;
    private long lastLogin;

    public User() {
        this.preferences = new HashMap<>();
        this.savedProducts = new ArrayList<>();
        this.searchHistory = new ArrayList<>();
    }

    public User(String userId, String name, String email) {
        this();
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.createdAt = System.currentTimeMillis();
        this.lastLogin = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public Map<String, Object> getPreferences() { return preferences; }
    public void setPreferences(Map<String, Object> preferences) { this.preferences = preferences; }

    public List<String> getSavedProducts() { return savedProducts; }
    public void setSavedProducts(List<String> savedProducts) { this.savedProducts = savedProducts; }

    public List<String> getSearchHistory() { return searchHistory; }
    public void setSearchHistory(List<String> searchHistory) { this.searchHistory = searchHistory; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getLastLogin() { return lastLogin; }
    public void setLastLogin(long lastLogin) { this.lastLogin = lastLogin; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("name", name);
        map.put("email", email);
        map.put("phone", phone);
        map.put("profileImage", profileImage);
        map.put("preferences", preferences);
        map.put("savedProducts", savedProducts);
        map.put("searchHistory", searchHistory);
        map.put("createdAt", createdAt);
        map.put("lastLogin", lastLogin);
        return map;
    }

    public static User fromMap(Map<String, Object> map) {
        User user = new User();
        user.setUserId((String) map.get("userId"));
        user.setName((String) map.get("name"));
        user.setEmail((String) map.get("email"));
        user.setPhone((String) map.get("phone"));
        user.setProfileImage((String) map.get("profileImage"));
        user.setPreferences((Map<String, Object>) map.get("preferences"));
        user.setSavedProducts((List<String>) map.get("savedProducts"));
        user.setSearchHistory((List<String>) map.get("searchHistory"));
        user.setCreatedAt(map.get("createdAt") != null ? (long) map.get("createdAt") : 0);
        user.setLastLogin(map.get("lastLogin") != null ? (long) map.get("lastLogin") : 0);
        return user;
    }
}
