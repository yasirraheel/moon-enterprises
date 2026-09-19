package com.geo.enterprises.utils;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Base activity class that provides maintenance mode handling for all activities
 */
public abstract class BaseActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    /**
     * Enhanced callback that automatically handles maintenance mode
     */
    protected abstract class MaintenanceAwareCallback<T> implements Callback<T> {
        
        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            // Check for maintenance mode first
            if (MaintenanceModeHandler.checkAndHandleMaintenanceMode(BaseActivity.this, response, () -> {
                // Retry the same call
                call.clone().enqueue(this);
            })) {
                return; // Maintenance mode handled, don't proceed with normal response handling
            }
            
            // Call the activity-specific response handler
            onResponseHandled(call, response);
        }
        
        @Override
        public void onFailure(Call<T> call, Throwable t) {
            onFailureHandled(call, t);
        }
        
        /**
         * Handle successful response (after maintenance mode check)
         */
        protected abstract void onResponseHandled(Call<T> call, Response<T> response);
        
        /**
         * Handle failure response
         */
        protected abstract void onFailureHandled(Call<T> call, Throwable t);
    }
    
    /**
     * Simple callback wrapper for maintenance mode handling
     */
    protected <T> Callback<T> createMaintenanceAwareCallback(Callback<T> originalCallback) {
        return new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                // Check for maintenance mode first
                if (MaintenanceModeHandler.checkAndHandleMaintenanceMode(BaseActivity.this, response, () -> {
                    // Retry the same call
                    call.clone().enqueue(this);
                })) {
                    return; // Maintenance mode handled, don't proceed with normal response handling
                }
                
                // Call original callback
                originalCallback.onResponse(call, response);
            }
            
            @Override
            public void onFailure(Call<T> call, Throwable t) {
                originalCallback.onFailure(call, t);
            }
        };
    }
}
