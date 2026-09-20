package com.geo.enterprises.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.geo.enterprises.MainActivity;

/**
 * BroadcastReceiver to handle notification clicks
 * This approach works better on devices with aggressive background restrictions (Oppo/Realme/etc)
 */
public class NotificationClickReceiver extends BroadcastReceiver {
    private static final String TAG = "NotifClickReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "=== NOTIFICATION CLICK RECEIVED ===");
        Log.d(TAG, "Intent action: " + intent.getAction());
        Log.d(TAG, "Intent extras: " + (intent.getExtras() != null ? intent.getExtras().toString() : "null"));

        Intent launchIntent = new Intent(context, MainActivity.class);
        launchIntent.setAction("com.geo.enterprises.NOTIFICATION_CLICK");
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        if (intent.getExtras() != null) {
            launchIntent.putExtras(intent.getExtras());
            Log.d(TAG, "Copied notification extras to launch intent");
        }

        launchIntent.putExtra("FROM_NOTIFICATION", true);

        Log.d(TAG, "Launching MainActivity with notification data");
        context.startActivity(launchIntent);
        Log.d(TAG, "MainActivity launch requested");
    }
}
