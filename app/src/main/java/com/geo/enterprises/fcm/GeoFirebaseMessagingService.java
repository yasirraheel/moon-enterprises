package com.geo.enterprises.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.geo.enterprises.R;
import com.geo.enterprises.utils.PreferenceManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * FCM Service to handle incoming push notifications
 * This service receives FCM messages when app is in foreground, background, or killed
 */
public class GeoFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "GeoFCMService";
    private static final String CHANNEL_ID = "geo_notifications";
    private static final String CHANNEL_NAME = "GEO Notifications";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM token: " + token);

        // Save token locally
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.saveFcmToken(token);

        // Send token to server if user is logged in
        if (preferenceManager.isLoggedIn()) {
            FcmTokenManager.sendTokenToServer(this, token);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "Message received from: " + remoteMessage.getFrom());

        String title = null;
        String message = null;
        Map<String, String> data = null;

        // Check data payload first
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            data = remoteMessage.getData();
            title = data.get("title");
            message = data.get("message");
            if (message == null || message.trim().isEmpty() || "null".equalsIgnoreCase(message)) {
                message = data.get("body");
            }
            Log.d(TAG, "Title from data: " + title + ", Message from data: " + message);
        }

        boolean isTitleEmpty = (title == null || title.trim().isEmpty() || "null".equalsIgnoreCase(title));
        boolean isMessageEmpty = (message == null || message.trim().isEmpty() || "null".equalsIgnoreCase(message));

        // Fall back to notification payload if title/message not in data payload or empty
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Notification payload exists");
            if (isTitleEmpty) {
                title = remoteMessage.getNotification().getTitle();
                Log.d(TAG, "Using notification title: " + title);
            }
            if (isMessageEmpty) {
                message = remoteMessage.getNotification().getBody();
                Log.d(TAG, "Using notification body: " + message);
            }
        }

        // Show notification if we have content
        if (title != null && !title.isEmpty() && message != null && !message.isEmpty()) {
            Log.d(TAG, "Showing notification with title: " + title);
            showNotification(title, message, data);
        } else {
            Log.e(TAG, "Cannot show notification: title=" + title + ", message=" + message);
        }
    }

    private void showNotification(String title, String message, Map<String, String> data) {
        // Create notification channel (required for Android O+)
        createNotificationChannel();

        int requestCode = 0;
        if (data != null && data.containsKey("notification_id")) {
            try {
                requestCode = Integer.parseInt(data.get("notification_id"));
                Log.d(TAG, "Using notification_id as request code: " + requestCode);
            } catch (NumberFormatException e) {
                requestCode = (int) System.currentTimeMillis();
                Log.d(TAG, "Using timestamp as request code: " + requestCode);
            }
        } else {
            requestCode = (int) System.currentTimeMillis();
            Log.d(TAG, "Using timestamp as request code (no notification_id): " + requestCode);
        }

        Intent intent = new Intent(this, com.geo.enterprises.notifications.NotificationsActivity.class);
        intent.setAction("OPEN_ACTIVITY_1");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        if (data != null) {
            intent.putExtra("notification_id", data.get("notification_id"));
            intent.putExtra("action_type", data.get("action_type"));
            intent.putExtra("type", data.get("type"));
            for (Map.Entry<String, String> entry : data.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        }

        intent.putExtra("title", title);
        intent.putExtra("message", message);

        int flags = PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        Log.d(TAG, "Creating PendingIntent with flags: FLAG_CANCEL_CURRENT | FLAG_IMMUTABLE");
        Log.d(TAG, "Request code for PendingIntent: " + requestCode);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                requestCode,
                intent,
                flags
        );
        Log.d(TAG, "PendingIntent created: " + (pendingIntent != null ? "SUCCESS" : "FAILED"));

        // Verify intent has extras before creating notification
        if (intent.getExtras() != null) {
            Log.d(TAG, "Intent extras before PendingIntent: " + intent.getExtras().toString());
        } else {
            Log.w(TAG, "Intent has no extras before creating PendingIntent!");
        }

        Log.d(TAG, "Notification title: " + title);
        Log.d(TAG, "Notification text: " + message);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationIdInt = 0;
        if (data != null && data.containsKey("notification_id")) {
            try {
                notificationIdInt = Integer.parseInt(data.get("notification_id"));
                Log.d(TAG, "Using notification_id as display ID: " + notificationIdInt);
            } catch (NumberFormatException e) {
                notificationIdInt = (int) System.currentTimeMillis();
                Log.d(TAG, "Using timestamp as display ID (parse error): " + notificationIdInt);
            }
        } else {
            notificationIdInt = (int) System.currentTimeMillis();
            Log.d(TAG, "Using timestamp as display ID (no notification_id): " + notificationIdInt);
        }


        NotificationCompat.Builder baseBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_geo_notification)
                .setColor(getResources().getColor(R.color.primary_color))
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent);


        String imageUrl = null;
        if (data != null && data.containsKey("image")) {
            imageUrl = data.get("image");
            if (imageUrl != null) {
                imageUrl = imageUrl.replace("`", "").trim();
            }
            Log.d(TAG, "Image URL from notification data: " + imageUrl);
        }

        NotificationCompat.Builder notificationBuilder = baseBuilder.setStyle(
                new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(title)
                        .bigText(message)
        );

        Log.d(TAG, "Displaying notification with ID: " + notificationIdInt);
        notificationManager.notify(notificationIdInt, notificationBuilder.build());
        Log.d(TAG, "=== NOTIFICATION DISPLAYED SUCCESSFULLY ===");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            final int finalNotificationId = notificationIdInt;
            final String finalImageUrl = imageUrl;
            java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    Bitmap imageBitmap = Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(finalImageUrl)
                            .submit(128, 128)
                            .get();

                    NotificationCompat.Builder imageBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_stat_geo_notification)
                            .setColor(getResources().getColor(R.color.primary_color))
                            .setContentTitle(title)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setContentIntent(pendingIntent)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .setBigContentTitle(title)
                                    .bigText(message))
                            .setLargeIcon(imageBitmap);

                    notificationManager.notify(finalNotificationId, imageBuilder.build());
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Failed to load notification image: " + e.getMessage());
                }
            });
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for orders, deposits, withdrawals, and updates");
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setShowBadge(true);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
