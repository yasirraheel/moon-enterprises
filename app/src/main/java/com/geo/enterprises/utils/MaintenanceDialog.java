package com.geo.enterprises.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.geo.enterprises.MainActivity;
import com.geo.enterprises.R;

/**
 * Custom maintenance dialog with attractive design and animations
 */
public class MaintenanceDialog {
    
    private Dialog dialog;
    private Context context;
    private OnMaintenanceActionListener listener;
    private Handler handler;
    private Runnable retryRunnable;
    
    public interface OnMaintenanceActionListener {
        void onRetry();
        void onExit();
        void onClose();
    }
    
    public MaintenanceDialog(@NonNull Context context) {
        this.context = context;
        this.dialog = new Dialog(context);
        this.handler = new Handler(Looper.getMainLooper());
        setupDialog();
    }
    
    private void setupDialog() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_maintenance);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        
        // Setup views
        ImageView ivMaintenanceIcon = dialog.findViewById(R.id.iv_maintenance_icon);
        TextView tvTitle = dialog.findViewById(R.id.tv_maintenance_title);
        TextView tvSubtitle = dialog.findViewById(R.id.tv_maintenance_subtitle);
        TextView tvMessage = dialog.findViewById(R.id.tv_maintenance_message);
        ProgressBar progressBar = dialog.findViewById(R.id.progress_maintenance);
        TextView tvProgressText = dialog.findViewById(R.id.tv_progress_text);
        LinearLayout layoutEstimatedTime = dialog.findViewById(R.id.layout_estimated_time);
        TextView tvEstimatedTime = dialog.findViewById(R.id.tv_estimated_time);
        
        // Setup click listeners
        dialog.findViewById(R.id.btn_retry).setOnClickListener(v -> {
            if (listener != null) {
                listener.onRetry();
            }
            dismiss();
        });
        
        dialog.findViewById(R.id.btn_exit).setOnClickListener(v -> {
            if (listener != null) {
                listener.onExit();
            }
            dismiss();
        });
        
        // Start animations
        startIconAnimation(ivMaintenanceIcon);
        startProgressAnimation(progressBar, tvProgressText);
    }
    
    private void startIconAnimation(ImageView iconView) {
        // Rotation animation for the maintenance icon
        RotateAnimation rotateAnimation = new RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotateAnimation.setDuration(2000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        iconView.startAnimation(rotateAnimation);
        
        // Scale animation for pulsing effect
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(iconView, "scaleX", 1.0f, 1.1f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(iconView, "scaleY", 1.0f, 1.1f, 1.0f);
        scaleX.setDuration(1500);
        scaleY.setDuration(1500);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleX.start();
        scaleY.start();
    }
    
    private void startProgressAnimation(ProgressBar progressBar, TextView progressText) {
        String[] progressMessages = {
            "Please wait...",
            "Almost there...",
            "Just a moment...",
            "Almost done...",
            "Final touches..."
        };
        
        final int[] messageIndex = {0}; // Use array to make it effectively final
        retryRunnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    progressText.setText(progressMessages[messageIndex[0] % progressMessages.length]);
                    messageIndex[0]++;
                    handler.postDelayed(this, 2000);
                }
            }
        };
        handler.post(retryRunnable);
    }
    
    public MaintenanceDialog setTitle(String title) {
        TextView tvTitle = dialog.findViewById(R.id.tv_maintenance_title);
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
        return this;
    }
    
    public MaintenanceDialog setSubtitle(String subtitle) {
        TextView tvSubtitle = dialog.findViewById(R.id.tv_maintenance_subtitle);
        if (tvSubtitle != null) {
            tvSubtitle.setText(subtitle);
        }
        return this;
    }
    
    public MaintenanceDialog setMessage(String message) {
        TextView tvMessage = dialog.findViewById(R.id.tv_maintenance_message);
        if (tvMessage != null) {
            tvMessage.setText(message);
        }
        return this;
    }
    
    public MaintenanceDialog setEstimatedTime(String estimatedTime) {
        LinearLayout layoutEstimatedTime = dialog.findViewById(R.id.layout_estimated_time);
        TextView tvEstimatedTime = dialog.findViewById(R.id.tv_estimated_time);
        if (layoutEstimatedTime != null && tvEstimatedTime != null) {
            tvEstimatedTime.setText(estimatedTime);
            layoutEstimatedTime.setVisibility(View.VISIBLE);
        }
        return this;
    }
    
    public MaintenanceDialog setOnActionListener(OnMaintenanceActionListener listener) {
        this.listener = listener;
        return this;
    }
    
    public void show() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }
    
    public void dismiss() {
        if (dialog.isShowing()) {
            if (retryRunnable != null) {
                handler.removeCallbacks(retryRunnable);
            }
            dialog.dismiss();
        }
    }
    
    public boolean isShowing() {
        return dialog.isShowing();
    }
    
    /**
     * Show maintenance dialog with default actions
     */
    public static void showMaintenanceDialog(Context context, @Nullable Runnable onRetry) {
        new MaintenanceDialog(context)
            .setOnActionListener(new OnMaintenanceActionListener() {
                @Override
                public void onRetry() {
                    if (onRetry != null) {
                        onRetry.run();
                    }
                }
                
                @Override
                public void onExit() {
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).finishAffinity();
                        System.exit(0);
                    }
                }
                
                @Override
                public void onClose() {
                    // Do nothing for close
                }
            })
            .show();
    }
}
