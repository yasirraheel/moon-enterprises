# FCM Testing Instructions

## Server Status: ✅ WORKING
Your server logs show FCM is already configured and sending notifications successfully to topic "all".

## App Status: ✅ READY
The Android app now has FCM implemented and will receive these notifications.

## Testing Steps

### 1. Install Updated App
- Build the app in Android Studio
- Install on your test device
- Clear old app data (optional but recommended for clean test)

### 2. Login & Check Logcat
Open Android Studio Logcat and filter by "FCM" or "GeoFCMService"

You should see:
```
D/GeoFCMService: New FCM token: eyJhbGc...
D/FcmTokenManager: FCM Token: eyJhbGc...
D/FcmTokenManager: Subscribed to all_users topic
D/FcmTokenManager: FCM token sent to server successfully
```

### 3. Test Public Notification
**Method A: Kill App Test**
1. Force stop the app completely
2. From your server admin panel, send a test public notification
3. Check your device - system notification should appear
4. Tap notification - app should open to NotificationsActivity

**Method B: Background Test**
1. Put app in background (press home button)
2. Send test notification from server
3. System notification should appear
4. Tap to open app

**Method C: Foreground Test**
1. Keep app open
2. Send test notification from server
3. System notification should still appear
4. Tap to navigate

### 4. Check Server Logs
After login, your server should log:
```
[timestamp] production.INFO: FCM token registered for user
```

## Expected Behavior

### When Server Sends to "all" Topic:
```php
// Your server code (already working)
$message = [
    'topic' => 'all',
    'notification' => [
        'title' => 'Test Notification',
        'body' => 'This is a test from server'
    ],
    'data' => [
        'notification_id' => '123',
        'type' => 'public',
        'action_type' => 'announcement',
        // ... other fields
    ]
];
```

### What Happens on Device:
1. FCM receives message (even if app is closed)
2. GeoFirebaseMessagingService.onMessageReceived() called
3. System notification displayed with title & body
4. User taps notification
5. App opens to NotificationsActivity
6. Notification appears in list via existing API

## Logcat Filters

In Android Studio Logcat, use these filters:
- `tag:GeoFCMService` - See FCM message handling
- `tag:FcmTokenManager` - See token management
- `package:com.geo.enterprises` - See all app logs

## Troubleshooting

### If no token appears in Logcat:
- Check google-services.json is in app/ folder
- Verify build succeeded without errors
- Check internet connection on device

### If token appears but "Subscribed to all_users topic" doesn't:
- Check Firebase console that project is active
- Verify server project ID matches: gen-lang-client-0212191941

### If notification not received:
1. Check notification permission granted (Android 13+)
2. Check app isn't in battery optimization/restricted mode
3. Verify server sent to topic "all" (your logs show it is)
4. Check Logcat for any errors in GeoFirebaseMessagingService

### If notification received but doesn't open app:
- Check NotificationsActivity not excluded in manifest
- Verify PendingIntent flags are correct (already done)

## User Rollout

Once testing is successful:
1. Increment app version in build.gradle (currently 1.8)
2. Build release APK
3. Distribute to users
4. As users update and login:
   - They auto-subscribe to "all" topic
   - They start receiving your existing server notifications
   - They can receive notifications when app is closed/background

## Notes

- **Existing notification system unchanged** - Pull-based API still works
- **Server already configured** - Your logs prove FCM is working
- **Users need app update** - Must install new version to receive push
- **No code changes needed on server** - Already sending to "all" topic correctly
