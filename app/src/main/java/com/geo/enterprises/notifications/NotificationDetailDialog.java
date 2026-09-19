package com.geo.enterprises.notifications;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.geo.enterprises.R;

public class NotificationDetailDialog extends Dialog {

    private NotificationItem notification;
    private OnNotificationActionListener listener;

    // UI Views
    private ImageView ivNotificationIcon, ivNotificationImage, ivClose;
    private TextView tvNotificationTitle, tvNotificationTime, tvNotificationMessage, tvNotificationType, tvNotificationAmount;
    private Button btnMarkRead, btnClose;

    public interface OnNotificationActionListener {
        void onMarkAsRead(NotificationItem notification);
        void onClose();
    }

    public NotificationDetailDialog(Context context, NotificationItem notification, OnNotificationActionListener listener) {
        super(context);
        this.notification = notification;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_notification_detail);
        
        // Make dialog background transparent and set rounded corners
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().getDecorView().setBackgroundResource(R.drawable.dialog_rounded_background);
            
            // Set dialog to be almost edge-to-edge with minimal margins
            android.view.WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
            getWindow().setAttributes(layoutParams);
            
            // Set minimal margins (8dp on each side)
            android.view.ViewGroup.MarginLayoutParams params = 
                (android.view.ViewGroup.MarginLayoutParams) getWindow().getDecorView().getLayoutParams();
            if (params != null) {
                int margin = (int) (8 * getContext().getResources().getDisplayMetrics().density);
                params.setMargins(margin, margin, margin, margin);
                getWindow().getDecorView().setLayoutParams(params);
            }
        }
        
        initializeViews();
        setupClickListeners();
        populateData();
    }

    private void initializeViews() {
        ivNotificationIcon = findViewById(R.id.iv_notification_icon);
        ivNotificationImage = findViewById(R.id.iv_notification_image);
        ivClose = findViewById(R.id.iv_close);
        tvNotificationTitle = findViewById(R.id.tv_notification_title);
        tvNotificationTime = findViewById(R.id.tv_notification_time);
        tvNotificationMessage = findViewById(R.id.tv_notification_message);
        tvNotificationType = findViewById(R.id.tv_notification_type);
        tvNotificationAmount = findViewById(R.id.tv_notification_amount);
        btnMarkRead = findViewById(R.id.btn_mark_read);
        btnClose = findViewById(R.id.btn_close);
    }

    private void setupClickListeners() {
        ivClose.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClose();
            }
            dismiss();
        });

        btnClose.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClose();
            }
            dismiss();
        });

        btnMarkRead.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMarkAsRead(notification);
            }
            dismiss();
        });
    }

    private void populateData() {
        // Set title
        tvNotificationTitle.setText(notification.getTitle());
        
        // Set time
        tvNotificationTime.setText(notification.getTimeAgo());
        
        // Set message
        tvNotificationMessage.setText(notification.getMessage());
        
        // Load notification icon based on type
        if (notification.getImage() != null && !notification.getImage().isEmpty()) {
            // For notifications with images, always try to load the image
            Glide.with(getContext())
                    .load(notification.getImage())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_notification_bell) // Simple placeholder
                            .error(R.drawable.ic_notification_bell) // Simple error fallback
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop())
                    .into(ivNotificationIcon);
            
            // Clear any color filter when showing images
            ivNotificationIcon.clearColorFilter();
        } else {
            // Use appropriate icon based on notification type for notifications without images
            ivNotificationIcon.setImageResource(getNotificationIcon(notification));
            ivNotificationIcon.setColorFilter(getNotificationIconColor(notification));
        }
        
        // Show notification type and amount for user-specific notifications
        if (notification.isUserSpecificNotification()) {
            if (tvNotificationType != null) {
                tvNotificationType.setVisibility(View.VISIBLE);
                String typeText = getNotificationTypeText(notification);
                tvNotificationType.setText(typeText);
            }
            
            if (tvNotificationAmount != null) {
                String amount = notification.getAmountFromMetadata();
                if (amount != null) {
                    tvNotificationAmount.setVisibility(View.VISIBLE);
                    tvNotificationAmount.setText(amount);
                } else {
                    tvNotificationAmount.setVisibility(View.GONE);
                }
            }
        } else {
            if (tvNotificationType != null) {
                tvNotificationType.setVisibility(View.GONE);
            }
            if (tvNotificationAmount != null) {
                tvNotificationAmount.setVisibility(View.GONE);
            }
        }
        
        // Load notification image if available
        if (notification.getImage() != null && !notification.getImage().isEmpty()) {
            ivNotificationImage.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .load(notification.getImage())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_notification_bell)
                            .error(R.drawable.ic_notification_bell)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop())
                    .into(ivNotificationImage);
        } else {
            ivNotificationImage.setVisibility(View.GONE);
        }
        
        // Show mark as read button only if notification is unread
        if (notification.isUnread()) {
            btnMarkRead.setVisibility(View.VISIBLE);
        } else {
            btnMarkRead.setVisibility(View.GONE);
        }
    }

    private int getNotificationIcon(NotificationItem notification) {
        if (notification.isPublicNotification()) {
            return R.drawable.ic_notification_bell;
        } else if (notification.isUserSpecificNotification()) {
            if (notification.isBalanceNotification()) {
                return R.drawable.ic_diamond;
            } else if (notification.isDepositNotification()) {
                return R.drawable.ic_deposit;
            } else if (notification.isWithdrawalNotification()) {
                return R.drawable.ic_transactions;
            } else if (notification.isOrderNotification()) {
                return R.drawable.ic_orders;
            } else if (notification.isServiceNotification()) {
                return R.drawable.ic_premium_star;
            } else if (notification.isRefundNotification()) {
                return R.drawable.ic_refresh;
            }
        }
        return R.drawable.ic_notification_bell;
    }

    private int getNotificationIconColor(NotificationItem notification) {
        if (notification.isPublicNotification()) {
            return getContext().getResources().getColor(R.color.primary_color);
        } else if (notification.isUserSpecificNotification()) {
            if (notification.isBalanceNotification()) {
                if ("balance_added".equals(notification.getActionType())) {
                    return getContext().getResources().getColor(R.color.success_color);
                } else {
                    return getContext().getResources().getColor(R.color.danger_color);
                }
            } else if (notification.isDepositNotification()) {
                if ("deposit_approved".equals(notification.getActionType())) {
                    return getContext().getResources().getColor(R.color.success_color);
                } else {
                    return getContext().getResources().getColor(R.color.danger_color);
                }
            } else if (notification.isWithdrawalNotification()) {
                if ("withdrawal_approved".equals(notification.getActionType())) {
                    return getContext().getResources().getColor(R.color.success_color);
                } else {
                    return getContext().getResources().getColor(R.color.danger_color);
                }
            } else if (notification.isOrderNotification()) {
                return getContext().getResources().getColor(R.color.primary_color);
            } else if (notification.isServiceNotification()) {
                return getContext().getResources().getColor(R.color.warning_color);
            } else if (notification.isRefundNotification()) {
                return getContext().getResources().getColor(R.color.success_color);
            }
        }
        return getContext().getResources().getColor(R.color.primary_color);
    }

    private String getNotificationTypeText(NotificationItem notification) {
        if (notification.getActionType() != null) {
            switch (notification.getActionType()) {
                case "balance_added":
                    return "Balance Added";
                case "balance_deducted":
                    return "Balance Deducted";
                case "deposit_approved":
                    return "Deposit Approved";
                case "deposit_rejected":
                    return "Deposit Rejected";
                case "withdrawal_approved":
                    return "Withdrawal Approved";
                case "withdrawal_rejected":
                    return "Withdrawal Rejected";
                case "order_placed":
                    return "Order Placed";
                case "paid_service_purchased":
                    return "Service Purchased";
                case "refund_processed":
                    return "Refund Processed";
                default:
                    return "System Notification";
            }
        }
        return "System Notification";
    }

    public static void show(Context context, NotificationItem notification, OnNotificationActionListener listener) {
        NotificationDetailDialog dialog = new NotificationDetailDialog(context, notification, listener);
        dialog.show();
    }
}
