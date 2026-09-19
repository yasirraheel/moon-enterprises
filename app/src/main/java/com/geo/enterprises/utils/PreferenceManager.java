package com.geo.enterprises.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.geo.enterprises.models.AppSettings;
import com.geo.enterprises.models.DraftOrder;
import com.geo.enterprises.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class PreferenceManager {
    private static final String PREF_NAME = "geo_enterprises_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_USER_DATA = "user_data";
    private static final String KEY_APP_SETTINGS = "app_settings";
    private static final String KEY_JUST_LOGGED_IN = "just_logged_in";
    private static final String KEY_JUST_REGISTERED = "just_registered";
    private static final String KEY_FCM_TOKEN = "fcm_token";
    private static final String KEY_FCM_TOKEN_SENT = "fcm_token_sent";
    private static final String KEY_LAST_NOTIFICATION_PERMISSION_REQUEST = "last_notification_permission_request";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    
    public PreferenceManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    
    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }
    
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    public void saveAuthToken(String token) {
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply();
    }
    
    public String getAuthToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }
    
    public void saveUserData(User user) {
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER_DATA, userJson);
        editor.apply();
    }
    
    public User getUserData() {
        String userJson = sharedPreferences.getString(KEY_USER_DATA, null);
        if (userJson != null) {
            try {
                Gson gson = new Gson();
                return gson.fromJson(userJson, User.class);
            } catch (Exception e) {
                // If there's an error parsing the data (e.g. schema change), clear it
                android.util.Log.e("PreferenceManager", "Error parsing user data: " + e.getMessage());
                clearUserData();
                return null;
            }
        }
        return null;
    }
    
    public void saveAppSettings(AppSettings settings) {
        Gson gson = new Gson();
        String settingsJson = gson.toJson(settings);
        editor.putString(KEY_APP_SETTINGS, settingsJson);
        editor.apply();
    }
    
    public AppSettings getAppSettings() {
        String settingsJson = sharedPreferences.getString(KEY_APP_SETTINGS, null);
        if (settingsJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(settingsJson, AppSettings.class);
        }
        return null;
    }
    
    public void clearUserData() {
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_AUTH_TOKEN);
        editor.remove(KEY_USER_DATA);
        editor.apply();
    }
    
    public void logout() {
        clearUserData();
        clearFcmToken();
    }
    
    public void setJustLoggedIn(boolean justLoggedIn) {
        editor.putBoolean(KEY_JUST_LOGGED_IN, justLoggedIn);
        editor.apply();
    }
    
    public boolean isJustLoggedIn() {
        return sharedPreferences.getBoolean(KEY_JUST_LOGGED_IN, false);
    }
    
    public void setJustRegistered(boolean justRegistered) {
        editor.putBoolean(KEY_JUST_REGISTERED, justRegistered);
        editor.apply();
    }
    
    public boolean isJustRegistered() {
        return sharedPreferences.getBoolean(KEY_JUST_REGISTERED, false);
    }
    
    public void clearJustLoggedIn() {
        editor.remove(KEY_JUST_LOGGED_IN);
        editor.apply();
    }
    
    public void clearJustRegistered() {
        editor.remove(KEY_JUST_REGISTERED);
        editor.apply();
    }

    // Draft order persistence per game/bond
    public void saveDraftOrders(String key, List<DraftOrder> drafts) {
        Gson gson = new Gson();
        String json = gson.toJson(drafts);
        editor.putString(key, json);
        editor.apply();
    }

    public List<DraftOrder> getDraftOrders(String key) {
        String json = sharedPreferences.getString(key, null);
        if (json == null) return Collections.emptyList();
        try {
            Gson gson = new Gson();
            Type type = new TypeToken<List<DraftOrder>>() {}.getType();
            List<DraftOrder> list = gson.fromJson(json, type);
            return list != null ? list : Collections.emptyList();
        } catch (Exception e) {
            android.util.Log.e("PreferenceManager", "Error parsing draft orders: " + e.getMessage());
            clearDraftOrders(key);
            return Collections.emptyList();
        }
    }

    public void clearDraftOrders(String key) {
        editor.remove(key);
        editor.apply();
    }

    // FCM token management
    public void saveFcmToken(String token) {
        editor.putString(KEY_FCM_TOKEN, token);
        editor.apply();
    }

    public String getFcmToken() {
        return sharedPreferences.getString(KEY_FCM_TOKEN, null);
    }

    public void setFcmTokenSent(boolean sent) {
        editor.putBoolean(KEY_FCM_TOKEN_SENT, sent);
        editor.apply();
    }

    public boolean isFcmTokenSent() {
        return sharedPreferences.getBoolean(KEY_FCM_TOKEN_SENT, false);
    }

    public void clearFcmToken() {
        editor.remove(KEY_FCM_TOKEN);
        editor.remove(KEY_FCM_TOKEN_SENT);
        editor.apply();
    }

    // Notification permission request tracking
    public void saveLastNotificationPermissionRequestTime() {
        editor.putLong(KEY_LAST_NOTIFICATION_PERMISSION_REQUEST, System.currentTimeMillis());
        editor.apply();
    }

    public boolean shouldAskForNotificationPermission() {
        long lastRequestTime = sharedPreferences.getLong(KEY_LAST_NOTIFICATION_PERMISSION_REQUEST, 0);
        if (lastRequestTime == 0) {
            return true; // Never asked before
        }

        // Calculate time difference in days
        long currentTime = System.currentTimeMillis();
        long diffInMillis = currentTime - lastRequestTime;
        long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

        // Ask again if it's been more than 1 day
        return diffInDays >= 1;
    }
}
