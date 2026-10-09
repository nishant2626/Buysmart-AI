package com.buysmart.ai.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.buysmart.ai.models.User;
import com.google.gson.Gson;

public class SessionManager {

    private static final String PREF_NAME = "BuySmartSession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PHONE = "userPhone";
    private static final String KEY_USER_IMAGE = "userImage";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_JSON = "userJson";
    private static final String KEY_FIRST_TIME = "firstTime";
    private static final String KEY_NOTIFICATION_ENABLED = "notificationsEnabled";
    private static final String KEY_DARK_MODE = "darkMode";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        gson = new Gson();
    }

    public void createSession(User user) {
        editor.putString(KEY_USER_ID, user.getUserId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_PHONE, user.getPhone());
        editor.putString(KEY_USER_IMAGE, user.getProfileImage());
        editor.putString(KEY_USER_JSON, gson.toJson(user));
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public User getUser() {
        String userJson = pref.getString(KEY_USER_JSON, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    public void updateUser(User user) {
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_PHONE, user.getPhone());
        editor.putString(KEY_USER_IMAGE, user.getProfileImage());
        editor.putString(KEY_USER_JSON, gson.toJson(user));
        editor.apply();
    }

    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    public String getUserPhone() {
        return pref.getString(KEY_USER_PHONE, "");
    }

    public String getUserImage() {
        return pref.getString(KEY_USER_IMAGE, null);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    public boolean isFirstTime() {
        return pref.getBoolean(KEY_FIRST_TIME, true);
    }

    public void setFirstTime(boolean firstTime) {
        editor.putBoolean(KEY_FIRST_TIME, firstTime);
        editor.apply();
    }

    public boolean isNotificationsEnabled() {
        return pref.getBoolean(KEY_NOTIFICATION_ENABLED, true);
    }

    public void setNotificationsEnabled(boolean enabled) {
        editor.putBoolean(KEY_NOTIFICATION_ENABLED, enabled);
        editor.apply();
    }

    public boolean isDarkMode() {
        return pref.getBoolean(KEY_DARK_MODE, false);
    }

    public void setDarkMode(boolean darkMode) {
        editor.putBoolean(KEY_DARK_MODE, darkMode);
        editor.apply();
    }
}
