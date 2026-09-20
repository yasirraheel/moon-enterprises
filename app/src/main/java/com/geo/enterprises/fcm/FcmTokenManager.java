package com.geo.enterprises.fcm;

import android.content.Context;
import android.util.Log;

import com.geo.enterprises.api.ApiClient;
import com.geo.enterprises.api.ApiService;
import com.geo.enterprises.models.ApiResponse;
import com.geo.enterprises.utils.PreferenceManager;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Manages FCM token registration and updates
 */
public class FcmTokenManager {

    private static final String TAG = "FcmTokenManager";

    /**
     * Initialize FCM and get token
     * Call this after user login
     */
    public static void initializeFcm(Context context) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);

                    // Save locally
                    PreferenceManager preferenceManager = new PreferenceManager(context);
                    preferenceManager.saveFcmToken(token);

                    // Send to server
                    sendTokenToServer(context, token);

                    // Subscribe to "all_users" topic for public notifications
                    subscribeToPublicNotifications();
                });
    }

    /**
     * Subscribe to "all_users" topic for public notifications
     */
    private static void subscribeToPublicNotifications() {
        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Subscribed to all_users topic");
                    } else {
                        Log.w(TAG, "Failed to subscribe to all_users topic", task.getException());
                    }
                });
    }

    /**
     * Send FCM token to backend server
     */
    public static void sendTokenToServer(Context context, String token) {
        PreferenceManager preferenceManager = new PreferenceManager(context);
        String authToken = preferenceManager.getAuthToken();

        if (authToken == null || authToken.isEmpty()) {
            Log.w(TAG, "Cannot send token: user not logged in");
            return;
        }

        ApiService apiService = ApiClient.getInstance().getApiService();
        String bearerToken = "Bearer " + authToken;

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("fcm_token", token);
        requestBody.put("device_type", "android");

        Call<ApiResponse<Void>> call = apiService.registerFcmToken(bearerToken, requestBody);

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d(TAG, "FCM token sent to server successfully");
                    preferenceManager.setFcmTokenSent(true);
                } else {
                    Log.e(TAG, "Failed to send FCM token to server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e(TAG, "Network error sending FCM token: " + t.getMessage());
            }
        });
    }

    /**
     * Clear FCM token on logout
     */
    public static void clearTokenOnLogout(Context context) {
        PreferenceManager preferenceManager = new PreferenceManager(context);
        String authToken = preferenceManager.getAuthToken();
        String fcmToken = preferenceManager.getFcmToken();

        if (authToken == null || fcmToken == null) {
            preferenceManager.clearFcmToken();
            return;
        }

        ApiService apiService = ApiClient.getInstance().getApiService();
        String bearerToken = "Bearer " + authToken;

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("fcm_token", fcmToken);

        Call<ApiResponse<Void>> call = apiService.unregisterFcmToken(bearerToken, requestBody);

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                Log.d(TAG, "FCM token cleared from server");
                preferenceManager.clearFcmToken();

                // Unsubscribe from topics
                FirebaseMessaging.getInstance().unsubscribeFromTopic("all_users")
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Unsubscribed from all_users topic");
                            }
                        });
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e(TAG, "Failed to clear FCM token: " + t.getMessage());
                preferenceManager.clearFcmToken();
            }
        });
    }
}
