package com.geo.enterprises.utils;

import android.util.Log;
import com.geo.enterprises.api.ApiClient;
import com.geo.enterprises.api.ApiService;
import com.geo.enterprises.models.PaidServicesResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthTestHelper {
    private static final String TAG = "AuthTestHelper";
    
    public static void testPaidServicesAuth(PreferenceManager preferenceManager) {
        ApiService apiService = ApiClient.getInstance().getApiService();
        String authToken = preferenceManager.getAuthToken();
        
        Log.d(TAG, "=== AUTHENTICATION TEST ===");
        Log.d(TAG, "Auth Token: " + (authToken != null ? authToken.substring(0, Math.min(20, authToken.length())) + "..." : "NULL"));
        Log.d(TAG, "Token Length: " + (authToken != null ? authToken.length() : 0));
        
        if (authToken == null || authToken.isEmpty()) {
            Log.e(TAG, "❌ No authentication token found!");
            return;
        }
        
        String token = "Bearer " + authToken;
        Log.d(TAG, "Full Authorization Header: " + token.substring(0, Math.min(30, token.length())) + "...");
        
        // Test the paid services endpoint
        apiService.getPaidServices(token).enqueue(new Callback<PaidServicesResponse>() {
            @Override
            public void onResponse(Call<PaidServicesResponse> call, Response<PaidServicesResponse> response) {
                Log.d(TAG, "=== API RESPONSE ===");
                Log.d(TAG, "Response Code: " + response.code());
                Log.d(TAG, "Is Successful: " + response.isSuccessful());
                Log.d(TAG, "Request URL: " + call.request().url());
                Log.d(TAG, "Auth Header Present: " + (call.request().header("Authorization") != null));
                
                if (response.isSuccessful() && response.body() != null) {
                    PaidServicesResponse servicesResponse = response.body();
                    Log.d(TAG, "API Success: " + servicesResponse.isSuccess());
                    Log.d(TAG, "Services Count: " + (servicesResponse.getData() != null ? servicesResponse.getData().size() : 0));
                    
                    if (servicesResponse.getData() != null) {
                        for (int i = 0; i < servicesResponse.getData().size(); i++) {
                            var service = servicesResponse.getData().get(i);
                            Log.d(TAG, "Service " + (i + 1) + ":");
                            Log.d(TAG, "  - Title: " + service.getTitle());
                            Log.d(TAG, "  - Has Purchased: " + service.hasPurchased());
                            Log.d(TAG, "  - Show Buy Button: " + service.showBuyButton());
                            Log.d(TAG, "  - Golden Text: " + (service.getGoldenText() != null ? service.getGoldenText().substring(0, Math.min(50, service.getGoldenText().length())) + "..." : "NULL"));
                        }
                    }
                } else {
                    Log.e(TAG, "❌ API call failed");
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error Body: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body: " + e.getMessage());
                        }
                    }
                }
            }
            
            @Override
            public void onFailure(Call<PaidServicesResponse> call, Throwable t) {
                Log.e(TAG, "❌ Network error: " + t.getMessage());
                Log.e(TAG, "Request URL: " + call.request().url());
            }
        });
    }
}
