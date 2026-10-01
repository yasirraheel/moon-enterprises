package com.geo.enterprises.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.geo.enterprises.R;
import com.geo.enterprises.api.ApiClient;
import com.geo.enterprises.api.ApiService;
import com.geo.enterprises.models.ApiResponse;
import com.geo.enterprises.models.AppSettings;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Dedicated dialog for App Closed Mode (Server-side shut down).
 * Independent of Maintenance Mode (503).
 */
public class AppClosedDialog {

    private static AppClosedDialog activeInstance;

    private final Activity activity;
    private final Dialog dialog;
    private final TextView tvTitle;
    private final TextView tvMessage;
    private final LinearLayout layoutCheckingStatus;
    private final MaterialButton btnCheckStatus;
    private final MaterialButton btnExitApp;
    private Runnable onReopened;

    public AppClosedDialog(@NonNull Activity activity) {
        this.activity = activity;
        this.dialog = new Dialog(activity);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_app_closed);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        tvTitle = dialog.findViewById(R.id.tv_closed_title);
        tvMessage = dialog.findViewById(R.id.tv_closed_message);
        layoutCheckingStatus = dialog.findViewById(R.id.layout_checking_status);
        btnCheckStatus = dialog.findViewById(R.id.btn_check_status);
        btnExitApp = dialog.findViewById(R.id.btn_exit_app);

        setupListeners();
    }

    private void setupListeners() {
        btnCheckStatus.setOnClickListener(v -> checkServerStatus());

        btnExitApp.setOnClickListener(v -> {
            dismiss();
            if (activity != null && !activity.isFinishing()) {
                activity.finishAffinity();
            }
            System.exit(0);
        });
    }

    public AppClosedDialog setTitle(String title) {
        if (tvTitle != null && title != null && !title.trim().isEmpty()) {
            tvTitle.setText(title);
        }
        return this;
    }

    public AppClosedDialog setMessage(String message) {
        if (tvMessage != null && message != null && !message.trim().isEmpty()) {
            tvMessage.setText(message);
        }
        return this;
    }

    public AppClosedDialog setOnReopened(@Nullable Runnable onReopened) {
        this.onReopened = onReopened;
        return this;
    }

    public void show() {
        if (activity != null && !activity.isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (activeInstance == this) {
            activeInstance = null;
        }
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    private void checkServerStatus() {
        if (layoutCheckingStatus != null) {
            layoutCheckingStatus.setVisibility(View.VISIBLE);
        }
        btnCheckStatus.setEnabled(false);

        ApiService apiService = ApiClient.getInstance().getApiService();
        apiService.getSettings().enqueue(new Callback<ApiResponse<AppSettings>>() {
            @Override
            public void onResponse(Call<ApiResponse<AppSettings>> call, Response<ApiResponse<AppSettings>> response) {
                if (activity == null || activity.isFinishing()) {
                    return;
                }

                if (layoutCheckingStatus != null) {
                    layoutCheckingStatus.setVisibility(View.GONE);
                }
                btnCheckStatus.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AppSettings settings = response.body().getData();
                    if (settings != null) {
                        // Save latest settings
                        new PreferenceManager(activity).saveAppSettings(settings);

                        if (!settings.isAppClosed()) {
                            // Admin has reopened the app!
                            Toast.makeText(activity, "App is now open! Resuming services...", Toast.LENGTH_SHORT).show();
                            dismiss();
                            if (onReopened != null) {
                                onReopened.run();
                            }
                            return;
                        } else {
                            // App is still closed, refresh title and message
                            setTitle(settings.getAppClosedTitle());
                            setMessage(settings.getAppClosedMessage());
                            Toast.makeText(activity, "App is still currently closed. Please check back later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(activity, "Could not verify app status. Please check your connection.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AppSettings>> call, Throwable t) {
                if (activity == null || activity.isFinishing()) {
                    return;
                }
                if (layoutCheckingStatus != null) {
                    layoutCheckingStatus.setVisibility(View.GONE);
                }
                btnCheckStatus.setEnabled(true);
                Toast.makeText(activity, "Failed to connect to server. Please check internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Show app closed dialog, preventing duplicate dialog instances.
     */
    public static synchronized void showAppClosed(@NonNull Activity activity,
                                                  @Nullable String title,
                                                  @Nullable String message,
                                                  @Nullable Runnable onReopened) {
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        if (activeInstance != null && activeInstance.isShowing()) {
            // Update active dialog's content
            if (title != null) activeInstance.setTitle(title);
            if (message != null) activeInstance.setMessage(message);
            if (onReopened != null) activeInstance.setOnReopened(onReopened);
            return;
        }

        activeInstance = new AppClosedDialog(activity);
        if (title != null) activeInstance.setTitle(title);
        if (message != null) activeInstance.setMessage(message);
        activeInstance.setOnReopened(onReopened);
        activeInstance.show();
    }

    public static synchronized void dismissIfShowing() {
        if (activeInstance != null) {
            activeInstance.dismiss();
            activeInstance = null;
        }
    }
}
