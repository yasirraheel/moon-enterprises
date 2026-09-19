package com.geo.enterprises.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.geo.enterprises.R;

public class ConfirmationDialog {
    
    private Dialog dialog;
    private Context context;
    private OnConfirmListener listener;
    
    public interface OnConfirmListener {
        void onConfirm();
        void onCancel();
    }
    
    public ConfirmationDialog(Context context) {
        this.context = context;
        this.dialog = new Dialog(context);
        setupDialog();
    }
    
    private void setupDialog() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        
        // Setup click listeners
        dialog.findViewById(R.id.btn_dialog_cancel).setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancel();
            }
            dismiss();
        });
        
        dialog.findViewById(R.id.btn_dialog_confirm).setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirm();
            }
            dismiss();
        });
    }
    
    public ConfirmationDialog setTitle(String title) {
        TextView titleView = dialog.findViewById(R.id.tv_dialog_title);
        titleView.setText(title);
        return this;
    }
    
    public ConfirmationDialog setTitle(@StringRes int titleRes) {
        TextView titleView = dialog.findViewById(R.id.tv_dialog_title);
        titleView.setText(titleRes);
        return this;
    }
    
    public ConfirmationDialog setMessage(String message) {
        TextView messageView = dialog.findViewById(R.id.tv_dialog_message);
        messageView.setText(message);
        return this;
    }
    
    public ConfirmationDialog setMessage(@StringRes int messageRes) {
        TextView messageView = dialog.findViewById(R.id.tv_dialog_message);
        messageView.setText(messageRes);
        return this;
    }
    
    public ConfirmationDialog setIcon(@DrawableRes int iconRes) {
        ImageView iconView = dialog.findViewById(R.id.iv_dialog_icon);
        iconView.setImageResource(iconRes);
        return this;
    }
    
    public ConfirmationDialog setConfirmButtonText(String text) {
        TextView confirmButton = dialog.findViewById(R.id.btn_dialog_confirm);
        confirmButton.setText(text);
        return this;
    }
    
    public ConfirmationDialog setConfirmButtonText(@StringRes int textRes) {
        TextView confirmButton = dialog.findViewById(R.id.btn_dialog_confirm);
        confirmButton.setText(textRes);
        return this;
    }
    
    public ConfirmationDialog setCancelButtonText(String text) {
        TextView cancelButton = dialog.findViewById(R.id.btn_dialog_cancel);
        if (text == null || text.isEmpty()) {
            cancelButton.setVisibility(View.GONE);
        } else {
            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setText(text);
        }
        return this;
    }
    
    public ConfirmationDialog setCancelButtonText(@StringRes int textRes) {
        TextView cancelButton = dialog.findViewById(R.id.btn_dialog_cancel);
        cancelButton.setText(textRes);
        return this;
    }
    
    public ConfirmationDialog setOnConfirmListener(OnConfirmListener listener) {
        this.listener = listener;
        return this;
    }
    
    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }
    
    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
    
    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }
    
    // Static helper methods for common dialogs
    public static void showLogoutConfirmation(Context context, OnConfirmListener listener) {
        new ConfirmationDialog(context)
            .setTitle("Confirm Logout")
            .setMessage("Are you sure you want to logout from your account?")
            .setIcon(R.drawable.ic_logout)
            .setConfirmButtonText("Logout")
            .setCancelButtonText("Cancel")
            .setOnConfirmListener(listener)
            .show();
    }
    
    public static void showDeleteConfirmation(Context context, OnConfirmListener listener) {
        new ConfirmationDialog(context)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this item? This action cannot be undone.")
            .setIcon(R.drawable.ic_delete)
            .setConfirmButtonText("Delete")
            .setCancelButtonText("Cancel")
            .setOnConfirmListener(listener)
            .show();
    }
    
    public static void showExitConfirmation(Context context, OnConfirmListener listener) {
        new ConfirmationDialog(context)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit the application?")
            .setIcon(R.drawable.ic_exit)
            .setConfirmButtonText("Exit")
            .setCancelButtonText("Cancel")
            .setOnConfirmListener(listener)
            .show();
    }
}
