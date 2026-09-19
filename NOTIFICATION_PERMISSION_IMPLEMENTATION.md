# Notification Permission Implementation

## How It Works Now

### Flow
1. **User logs in** → LoginActivity saves credentials and initializes FCM
2. **User navigates to Dashboard** → DashboardActivity opens
3. **Dashboard onResume()** → Checks notification permission
4. **If permission not granted + 24 hours passed** → Shows beautiful dialog
5. **User clicks "Enable"** → Requests Android permission
6. **User clicks "Not Now"** → Saves timestamp, will ask again in 24 hours

### Implementation Details

#### DashboardActivity (Main Implementation)
- **onResume()** - Checks permission every time dashboard appears
- **checkAndRequestNotificationPermission()** - Logic to decide if we should ask
  - Android 13+: Checks if POST_NOTIFICATIONS permission granted
  - Android < 13: Shows dialog once to educate (notifications work automatically)
  - Uses 24-hour cooldown via PreferenceManager
- **showNotificationPermissionDialog()** - Shows the beautiful custom dialog

#### PreferenceManager (Tracking)
- **saveLastNotificationPermissionRequestTime()** - Saves current timestamp
- **shouldAskForNotificationPermission()** - Returns true if:
  - Never asked before (timestamp = 0), OR
  - More than 24 hours (1 day) have passed since last request

#### Permission Dialog (dialog_notification_permission.xml)
- Beautiful UI with bell icon
- Shows 4 key benefits:
  - ✓ Deposit & Withdrawal approvals
  - ✓ Order status updates
  - ✓ Special offers & announcements
  - ✓ Balance updates in real-time
- Two buttons: "Not Now" and "Enable"

### When Dialog Shows

**Every login / dashboard resume:**
1. Check if permission is denied
2. Check if 24 hours have passed since last request
3. If both true → Show dialog

**After clicking "Not Now":**
- Timestamp saved
- Won't show again for 24 hours
- Next day (after 24 hours) → Shows again

**After clicking "Enable":**
- Timestamp saved
- Android permission dialog shows (Android 13+)
- If granted → Won't show again
- If denied → Will show again in 24 hours

### Benefits

✅ **Persistent** - Asks every day until permission granted
✅ **Not Annoying** - Maximum once per 24 hours
✅ **Educates Users** - Shows clear benefits in dialog
✅ **Works on All Android Versions** - Adapts to Android 13+ and older
✅ **Robust** - Shows in Dashboard onResume, not just on login
✅ **Logged** - Debug logs to track behavior

## Testing

### Test Scenario 1: First Login
1. Login to app
2. Dashboard opens
3. Dialog shows immediately (first time)
4. Click "Not Now"
5. Timestamp saved

### Test Scenario 2: Daily Reminder
1. Login next day (24+ hours later)
2. Dashboard opens
3. Dialog shows again
4. Click "Enable"
5. Android permission dialog shows
6. Grant permission
7. Won't be asked again

### Test Scenario 3: Multiple Logins Same Day
1. Login to app
2. Dashboard shows dialog
3. Click "Not Now"
4. Logout
5. Login again (within 24 hours)
6. Dashboard opens
7. Dialog does NOT show (cooldown period)

### Test Scenario 4: Permission Already Granted
1. User already granted permission
2. Login to app
3. Dashboard opens
4. Dialog does NOT show (permission exists)

## Debug Logs

Enable Logcat filter for `DashboardActivity` to see:
```
D/DashboardActivity: Checking notification permission...
D/DashboardActivity: Android 13+, has permission: false
D/DashboardActivity: Should ask: true
D/DashboardActivity: Showing notification permission dialog
D/DashboardActivity: User clicked 'Not Now', saved timestamp
```

Or:
```
D/DashboardActivity: Checking notification permission...
D/DashboardActivity: Android 13+, has permission: false
D/DashboardActivity: Should ask: false
```

## Files Modified

1. **DashboardActivity.java**
   - Added imports for permission checking
   - Added checkAndRequestNotificationPermission() in onResume()
   - Added showNotificationPermissionDialog() method

2. **PreferenceManager.java**
   - Added KEY_LAST_NOTIFICATION_PERMISSION_REQUEST
   - Added saveLastNotificationPermissionRequestTime()
   - Added shouldAskForNotificationPermission()

3. **dialog_notification_permission.xml**
   - Beautiful permission request dialog layout

4. **bg_dialog.xml**
   - Rounded white background for dialog

## Server Side (No Changes Needed)

The server-side FCM implementation remains unchanged. This only affects when and how users are asked to enable notifications on the Android client.

## User Experience

**Before:**
- Permission never requested properly
- Users might miss notifications

**After:**
- Beautiful dialog explains benefits
- Asks politely every day until granted
- Clear call-to-action buttons
- Professional UI matching app design
- Works reliably on all Android versions
