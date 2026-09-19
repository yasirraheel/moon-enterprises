ckage com.geo.enterprises.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.geo.enterprises.R;
import com.geo.enterprises.api.ApiClient;
import com.geo.enterprises.api.ApiService;
import com.geo.enterprises.models.ApkVersionData;
import com.geo.enterprises.models.ApkVersionResponse;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppUpdateManager {
    
    private Context context;
    private ApiService apiService;
    
    public AppUpdateManager(Context context) {
        this.context = context;
        this.apiService = ApiClient.getInstance().getApiService();
    }
    
    /**
     * Get current app version code
     */
    public int getCurrentVersionCode() {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            android.util.Log.d("AppUpdateManager", "getCurrentVersionCode: " + versionCode);
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            android.util.Log.e("AppUpdateManager", "Failed to get version code, using default 1");
            return 1; // Default version if unable to get
        }
    }
    
    /**
     * Get current app version name
     */
    public String getCurrentVersionName() {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String versionName = packageInfo.versionName;
            android.util.Log.d("AppUpdateManager", "getCurrentVersionName: " + versionName);
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            android.util.Log.e("AppUpdateManager", "Failed to get version name, using default 1.0");
            return "1.0"; // Default version if unable to get
        }
    }
    
    /**
     * Check for app updates from server
     */
    public void checkForUpdates(boolean showShareDialog) {
        android.util.Log.d("AppUpdateManager", "checkForUpdates called with showShareDialog: " + showShareDialog);
        apiService.getApkVersion().enqueue(new Callback<ApkVersionResponse>() {
            @Override
            public void onResponse(Call<ApkVersionResponse> call, Response<ApkVersionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApkVersionResponse versionResponse = response.body();
                    android.util.Log.d("AppUpdateManager", "API Response successful: " + versionResponse.isSuccess());
                    
                    if (versionResponse.isSuccess() && versionResponse.getData() != null) {
                        ApkVersionData versionData = versionResponse.getData();
                        android.util.Log.d("AppUpdateManager", "API Data - Version Code: " + versionData.getVersionCode() + 
                            ", Version Name: " + versionData.getVersionName() + 
                            ", Force Update: " + versionData.isForceUpdate());
                        handleVersionCheck(versionData, showShareDialog);
                    } else {
                        // If API fails and showShareDialog is true, show share dialog anyway
                        if (showShareDialog) {
                            showShareAppDialog(null);
                        }
                    }
                } else {
                    // If API fails and showShareDialog is true, show share dialog anyway
                    if (showShareDialog) {
                        showShareAppDialog(null);
                    }
                }
            }
            
            @Override
            public void onFailure(Call<ApkVersionResponse> call, Throwable t) {
                // If API fails and showShareDialog is true, show share dialog anyway
                if (showShareDialog) {
                    showShareAppDialog(null);
                }
            }
        });
    }
    
    /**
     * Handle version check result
     */
    private void handleVersionCheck(ApkVersionData versionData, boolean showShareDialog) {
        int currentVersion = getCurrentVersionCode();
        int serverVersion = versionData.getVersionCode();
        
        android.util.Log.d("AppUpdateManager", "Current version: " + currentVersion);
        android.util.Log.d("AppUpdateManager", "Server version: " + serverVersion);
        android.util.Log.d("AppUpdateManager", "Force update: " + versionData.isForceUpdate());
        
        // Check if versions are different (server version should be higher than current)
        boolean updateAvailable = (serverVersion > currentVersion);
        boolean versionsMatch = (serverVersion == currentVersion);
        boolean forceUpdateEnabled = versionData.isForceUpdate();
        
        android.util.Log.d("AppUpdateManager", "=== VERSION CHECK DETAILS ===");
        android.util.Log.d("AppUpdateManager", "Server Version Code: " + serverVersion);
        android.util.Log.d("AppUpdateManager", "Current Version Code: " + currentVersion);
        android.util.Log.d("AppUpdateManager", "Update Available (server > current): " + updateAvailable);
        android.util.Log.d("AppUpdateManager", "Versions Match (server == current): " + versionsMatch);
        android.util.Log.d("AppUpdateManager", "Force Update Flag from API: " + forceUpdateEnabled);
        android.util.Log.d("AppUpdateManager", "Show Share Dialog Requested: " + showShareDialog);
        android.util.Log.d("AppUpdateManager", "=== END VERSION CHECK ===");
        
        // CRITICAL: If versions match exactly, NEVER show force update dialog
        if (versionsMatch) {
            android.util.Log.d("AppUpdateManager", "VERSIONS MATCH - No force update dialog will be shown");
            if (showShareDialog) {
                android.util.Log.d("AppUpdateManager", "Showing share dialog only (user requested)");
                showShareAppDialog(versionData);
            } else {
                android.util.Log.d("AppUpdateManager", "No action taken - versions match and no share requested");
            }
            return; // EXIT EARLY - no force update when versions match
        }
        
        // If we reach here, versions don't match (server is different from current)
        if (updateAvailable && forceUpdateEnabled) {
            // Server version is higher AND force update is enabled from API → Show force update dialog
            android.util.Log.d("AppUpdateManager", "SHOWING FORCE UPDATE DIALOG - Server higher + force enabled");
            showForceUpdateDialog(versionData);
        } else if (updateAvailable && !forceUpdateEnabled) {
            // Server version is higher BUT force update is disabled from API → Optional update, no force dialog
            android.util.Log.d("AppUpdateManager", "UPDATE AVAILABLE but force disabled - no force dialog");
            if (showShareDialog) {
                // Show share dialog with update info if user requested it
                showShareAppDialog(versionData);
            }
        } else if (showShareDialog) {
            // Server version is lower than current but user clicked share menu → Show share dialog
            android.util.Log.d("AppUpdateManager", "Server version lower than current - showing share dialog only");
            showShareAppDialog(versionData);
        } else {
            // Server version is lower and no share dialog requested → Do nothing
            android.util.Log.d("AppUpdateManager", "Server version lower - no action taken");
        }
    }
    
    /**
     * Show force update dialog
     */
    public void showForceUpdateDialog(ApkVersionData versionData) {
        android.util.Log.d("AppUpdateManager", "!!! FORCE UPDATE DIALOG CALLED - THIS SHOULD NOT HAPPEN IF VERSIONS MATCH !!!");
        android.util.Log.d("AppUpdateManager", "Dialog data - Version: " + (versionData != null ? versionData.getVersionName() : "null"));
        
        if (!(context instanceof Activity)) return;
        
        Activity activity = (Activity) context;
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_force_update, null);
        
        // Get views
        TextView tvCurrentVersion = dialogView.findViewById(R.id.tv_current_version);
        TextView tvNewVersion = dialogView.findViewById(R.id.tv_new_version);
        TextView tvReleaseNotes = dialogView.findViewById(R.id.tv_release_notes);
        CardView cardReleaseNotes = dialogView.findViewById(R.id.card_release_notes);
        MaterialButton btnDownload = dialogView.findViewById(R.id.btn_download);
        
        // Set current version
        tvCurrentVersion.setText(getCurrentVersionName());
        
        // Set new version info
        if (versionData != null) {
            tvNewVersion.setText(versionData.getVersionName());
            
            // Show release notes if available
            if (versionData.getReleaseNotes() != null && !versionData.getReleaseNotes().trim().isEmpty()) {
                tvReleaseNotes.setText(versionData.getReleaseNotes());
                cardReleaseNotes.setVisibility(View.VISIBLE);
            }
        }
        
        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setCancelable(false); // Force update - cannot be cancelled
        
        AlertDialog dialog = builder.create();
        // Remove window background to show our custom CardView properly
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        
        // Set download button click listener
        btnDownload.setOnClickListener(v -> {
            if (versionData != null && versionData.getDownloadLink() != null) {
                // Open download link
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(versionData.getDownloadLink()));
                context.startActivity(browserIntent);
                
                // Close app after opening download link (force update)
                if (context instanceof Activity) {
                    ((Activity) context).finishAffinity();
                }
            }
        });
        
        dialog.show();
    }
    
    /**
     * Show share app dialog
     */
    public void showShareAppDialog(ApkVersionData versionData) {
        if (!(context instanceof Activity)) return;
        
        Activity activity = (Activity) context;
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_share_app, null);
        
        // Get views
        TextView tvAppVersion = dialogView.findViewById(R.id.tv_app_version);
        TextView tvNewVersion = dialogView.findViewById(R.id.tv_new_version);
        TextView tvDownloadCount = dialogView.findViewById(R.id.tv_download_count);
        CardView cardUpdateInfo = dialogView.findViewById(R.id.card_update_info);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel);
        MaterialButton btnCopyLink = dialogView.findViewById(R.id.btn_copy_link);
        MaterialButton btnShare = dialogView.findViewById(R.id.btn_share);
        
        // Set current app version
        tvAppVersion.setText("Version " + getCurrentVersionName());
        
        // Show update info if new version available
        if (versionData != null) {
            int currentVersion = getCurrentVersionCode();
            int serverVersion = versionData.getVersionCode();
            
            if (serverVersion > currentVersion) {
                cardUpdateInfo.setVisibility(View.VISIBLE);
                tvNewVersion.setText("Version " + versionData.getVersionName() + " is now available");
                tvDownloadCount.setText("Downloads: " + versionData.getDownloadCount());
            }
            
            // Show/hide copy link button based on download link availability
            if (versionData.getDownloadLink() != null && !versionData.getDownloadLink().isEmpty()) {
                btnCopyLink.setVisibility(View.VISIBLE);
            } else {
                btnCopyLink.setVisibility(View.GONE);
            }
        } else {
            // No version data - hide copy link button
            btnCopyLink.setVisibility(View.GONE);
        }
        
        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        
        AlertDialog dialog = builder.create();
        // Remove window background to show our custom CardView properly
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        
        // Set button click listeners
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        // Copy link button functionality
        btnCopyLink.setOnClickListener(v -> {
            String linkToCopy = null;
            
            if (versionData != null && versionData.getDownloadLink() != null && !versionData.getDownloadLink().isEmpty()) {
                linkToCopy = versionData.getDownloadLink();
            } else {
                // Fallback to a default link or message
                linkToCopy = "https://geoenterprises.org"; // You can change this to your actual website
            }
            
            // Copy to clipboard
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Moon Enterprises Download Link", linkToCopy);
            clipboard.setPrimaryClip(clip);
            
            // Show confirmation toast
            Toast.makeText(context, "Download link copied to clipboard!", Toast.LENGTH_SHORT).show();
            
            // Optional: Add haptic feedback
            v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
        });
        

        
        btnShare.setOnClickListener(v -> {
            String shareText = "🚀 Check out Moon Enterprises App!\n\n" +
                    "📱 Prize Bond Booking & Management\n" +
                    "💰 Easy Deposits & Withdrawals\n" +
                    "🎯 Real-time Notifications\n\n";
            
            if (versionData != null && versionData.getDownloadLink() != null && !versionData.getDownloadLink().isEmpty()) {
                shareText += "Download now: " + versionData.getDownloadLink();
            } else {
                shareText += "Get the latest version from our official website!";
            }
            
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Moon Enterprises App");
            
            // Create chooser to show all available apps
            Intent chooserIntent = Intent.createChooser(shareIntent, "Share App via");
            context.startActivity(chooserIntent);
            dialog.dismiss();
        });

        dialog.show();
    }
}