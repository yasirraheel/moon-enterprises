package com.geo.enterprises.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.geo.enterprises.MainActivity;

import retrofit2.Response;

/**
 * Utility class to handle maintenance mode responses from the API
 */
public class MaintenanceModeHandler {
    
    private static final String TAG = "MaintenanceModeHandler";
    private static final int MAINTENANCE_STATUS_CODE = 503;
    
    /**
     * Check if the response indicates maintenance mode
     */
    public static boolean isMaintenanceMode(Response<?> response) {
        return response.code() == MAINTENANCE_STATUS_CODE;
    }
    
    /**
     * Handle maintenance mode response
     * @param activity The current activity
     * @param response The API response
     * @param onRetry Callback for retry action (optional)
     */
    public static void handleMaintenanceMode(Activity activity, Response<?> response, Runnable onRetry) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        
        Log.d(TAG, "Maintenance mode detected - Status Code: " + response.code());
        
        // Run on main thread to ensure UI operations are safe
        new Handler(Looper.getMainLooper()).post(() -> {
            showCustomMaintenanceDialog(activity, onRetry);
        });
    }
    
    /**
     * Show custom maintenance mode dialog
     */
    private static void showCustomMaintenanceDialog(Activity activity, Runnable onRetry) {
        MaintenanceDialog dialog = new MaintenanceDialog(activity)
                .setTitle("Service Temporarily Unavailable")
                .setSubtitle("We're working to improve your experience")
                .setMessage("Our team is currently performing scheduled maintenance to enhance the app's performance and add new features. This process usually takes a few minutes.")
                .setEstimatedTime("Estimated completion: 5-10 minutes")
                .setOnActionListener(new MaintenanceDialog.OnMaintenanceActionListener() {
                    @Override
                    public void onRetry() {
                        if (onRetry != null) {
                            onRetry.run();
                        }
                    }
                    
                    @Override
                    public void onExit() {
                        activity.finishAffinity();
                        System.exit(0);
                    }
                    
                    @Override
                    public void onClose() {
                        // Do nothing for close
                    }
                });
        
        dialog.show();
    }
    
    /**
     * Handle maintenance mode with custom message
     */
    public static void handleMaintenanceMode(Activity activity, String customMessage, Runnable onRetry) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        
        new Handler(Looper.getMainLooper()).post(() -> {
            MaintenanceDialog dialog = new MaintenanceDialog(activity)
                    .setTitle("Service Temporarily Unavailable")
                    .setSubtitle("We're working to improve your experience")
                    .setMessage(customMessage != null ? customMessage : "Our team is currently performing scheduled maintenance to enhance the app's performance and add new features. This process usually takes a few minutes.")
                    .setEstimatedTime("Estimated completion: 5-10 minutes")
                    .setOnActionListener(new MaintenanceDialog.OnMaintenanceActionListener() {
                        @Override
                        public void onRetry() {
                            if (onRetry != null) {
                                onRetry.run();
                            }
                        }
                        
                        @Override
                        public void onExit() {
                            activity.finishAffinity();
                            System.exit(0);
                        }
                        
                        @Override
                        public void onClose() {
                            // Do nothing for close
                        }
                    });
            
            dialog.show();
        });
    }
    
    /**
     * Check and handle maintenance mode in API response
     * @param activity Current activity
     * @param response API response
     * @param onRetry Retry callback
     * @return true if maintenance mode was detected and handled, false otherwise
     */
    public static boolean checkAndHandleMaintenanceMode(Activity activity, Response<?> response, Runnable onRetry) {
        if (isMaintenanceMode(response)) {
            handleMaintenanceMode(activity, response, onRetry);
            return true;
        }
        return false;
    }
}
