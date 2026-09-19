# Status Bar Overlap Fix Guide

## Problem
The app's top bar is being covered by the system status bar (battery, time, etc.) on some devices.

## Solution Implemented
Created `WindowInsetsHelper` utility class to handle system window insets properly.

## How to Apply to Each Activity

### Step 1: Import the Helper
Add this import to your activity:
```java
import com.geo.enterprises.utils.WindowInsetsHelper;
```

### Step 2: Add ID to Top Bar in Layout
In your activity's XML layout file, add an `android:id` to the top bar LinearLayout:
```xml
<LinearLayout
    android:id="@+id/top_bar"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@color/primary_color"
    ...>
```

### Step 3: Apply Padding in initializeViews()
In your activity's `initializeViews()` method, add this code:
```java
private void initializeViews() {
    // ... your existing view initialization code ...
    
    // Apply status bar padding to prevent overlap
    View topBar = findViewById(R.id.top_bar);
    if (topBar != null) {
        WindowInsetsHelper.applyStatusBarPaddingSimple(this, topBar);
    }
}
```

## Activities Already Fixed
- ✅ OrdersActivity

## Activities That Need This Fix
Apply the above steps to these activities:
- GameOrdersActivity
- YourOrdersActivity
- NotificationsActivity
- AccountActivity
- DashboardActivity
- Any other activity with a top bar

## Testing
Test on devices with:
- Different Android versions (especially Android 10+)
- Notched displays
- Different screen sizes
- Both portrait and landscape orientations

## Theme Changes
The `themes.xml` file has been updated with proper window attributes to support this fix.
