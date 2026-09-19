# Maintenance Mode Implementation Guide

## Overview
This document describes the maintenance mode handling implementation in the MOON ENTERPRISES Android app. When the Laravel backend is in maintenance mode, it returns HTTP status code `503 Service Unavailable`. The Android app now gracefully handles this response and shows appropriate user-friendly messages.

## Implementation Details

### 1. MaintenanceModeHandler Utility Class
**Location**: `app/src/main/java/com/geo/enterprises/utils/MaintenanceModeHandler.java`

**Features**:
- Detects 503 status code responses
- Shows user-friendly maintenance dialog
- Provides retry functionality
- Offers navigation options (Go to Home, Exit App)
- Thread-safe UI operations

**Key Methods**:
- `isMaintenanceMode(Response<?> response)` - Checks if response indicates maintenance mode
- `handleMaintenanceMode(Activity activity, Response<?> response, Runnable onRetry)` - Handles maintenance mode
- `checkAndHandleMaintenanceMode(Activity activity, Response<?> response, Runnable onRetry)` - One-stop method

### 2. BaseActivity Class
**Location**: `app/src/main/java/com/geo/enterprises/utils/BaseActivity.java`

**Features**:
- Extends AppCompatActivity
- Provides `createMaintenanceAwareCallback()` method
- Automatically wraps API callbacks with maintenance mode handling
- Abstract `MaintenanceAwareCallback` class for custom implementations

### 3. Maintenance Icon
**Location**: `app/src/main/res/drawable/ic_maintenance.xml`
- Vector drawable for maintenance mode dialogs
- Uses Material Design warning icon

## Usage Examples

### Method 1: Using BaseActivity (Recommended)
```java
public class YourActivity extends BaseActivity {
    
    private void makeApiCall() {
        apiService.getData().enqueue(createMaintenanceAwareCallback(new Callback<ApiResponse<Data>>() {
            @Override
            public void onResponse(Call<ApiResponse<Data>> call, Response<ApiResponse<Data>> response) {
                // Your normal response handling
                if (response.isSuccessful()) {
                    // Handle success
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Data>> call, Throwable t) {
                // Handle network errors
            }
        }));
    }
}
```

### Method 2: Manual Implementation
```java
public class YourActivity extends AppCompatActivity {
    
    private void makeApiCall() {
        apiService.getData().enqueue(new Callback<ApiResponse<Data>>() {
            @Override
            public void onResponse(Call<ApiResponse<Data>> call, Response<ApiResponse<Data>> response) {
                // Check for maintenance mode first
                if (MaintenanceModeHandler.checkAndHandleMaintenanceMode(YourActivity.this, response, () -> {
                    // Retry the same call
                    call.clone().enqueue(this);
                })) {
                    return; // Maintenance mode handled, don't proceed
                }
                
                // Your normal response handling
                if (response.isSuccessful()) {
                    // Handle success
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Data>> call, Throwable t) {
                // Handle network errors
            }
        });
    }
}
```

## Updated Activities

The following activities have been updated to use maintenance mode handling:

1. **DashboardActivity** - Profile fetching and balance refresh
2. **NotificationsActivity** - Notification loading and mark-as-read
3. **LoginActivity** - Login API calls

## Maintenance Mode Dialog Features

When maintenance mode is detected, the app shows a dialog with:

- **Title**: "Service Temporarily Unavailable"
- **Message**: User-friendly explanation about maintenance
- **Icon**: Maintenance/warning icon
- **Buttons**:
  - **Retry**: Retries the same API call
  - **Go to Home**: Navigates to MainActivity (if not already there)
  - **Exit App**: Closes the application

## Backend Integration

### Laravel Maintenance Mode
- **Status Code**: 503 Service Unavailable
- **Response**: HTML content (not JSON)
- **Trigger**: `php artisan down` command
- **Disable**: `php artisan up` command

### API Response Handling
The Android app specifically looks for HTTP status code `503` to detect maintenance mode. This is the standard status code returned by Laravel when maintenance mode is enabled.

## Testing

### To Test Maintenance Mode:
1. Enable maintenance mode on Laravel backend:
   ```bash
   php artisan down
   ```

2. Make any API call from the Android app

3. The app should show the maintenance mode dialog

4. Disable maintenance mode:
   ```bash
   php artisan up
   ```

## Benefits

1. **User Experience**: Users see friendly messages instead of technical errors
2. **Graceful Handling**: App doesn't crash or show confusing error messages
3. **Retry Functionality**: Users can retry after maintenance is complete
4. **Navigation Options**: Users can navigate to home or exit the app
5. **Consistent Behavior**: All API calls handle maintenance mode uniformly

## Future Enhancements

1. **Retry-After Header**: Parse and display estimated maintenance duration
2. **Custom Messages**: Allow backend to send custom maintenance messages
3. **Offline Mode**: Show cached data when possible during maintenance
4. **Push Notifications**: Notify users when maintenance is complete

## Files Modified

### New Files:
- `MaintenanceModeHandler.java` - Utility class for maintenance mode handling
- `BaseActivity.java` - Base activity with maintenance mode support
- `ic_maintenance.xml` - Maintenance mode icon
- `MAINTENANCE_MODE_IMPLEMENTATION.md` - This documentation

### Modified Files:
- `DashboardActivity.java` - Added maintenance mode handling to API calls
- `NotificationsActivity.java` - Added maintenance mode handling to API calls
- `LoginActivity.java` - Extended BaseActivity for maintenance mode support

## Conclusion

The maintenance mode implementation provides a robust, user-friendly way to handle server maintenance periods. The modular design allows easy integration into existing and new activities, ensuring consistent behavior across the entire application.
