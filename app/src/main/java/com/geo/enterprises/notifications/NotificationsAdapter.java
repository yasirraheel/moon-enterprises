package com.geo.enterprises.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.geo.enterprises.R;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    private List<NotificationItem> notificationList;
    private OnNotificationClickListener clickListener;

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationItem notification);
        void onMarkAsRead(NotificationItem notification);
    }

    public NotificationsAdapter(List<NotificationItem> notificationList, OnNotificationClickListener clickListener) {
        this.notificationList = notificationList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem notification = notificationList.get(position);
        
        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());
        holder.tvTime.setText(notification.getTimeAgo());
        
        // Load notification image or set appropriate icon based on type
        if (notification.getImage() != null && !notification.getImage().isEmpty()) {
            // For notifications with images, always try to load the image
            
            Glide.with(holder.itemView.getContext())
                    .load(notification.getImage())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_notification_bell) // Simple placeholder
                            .error(R.drawable.ic_notification_bell) // Simple error fallback
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop())
                    .into(holder.ivNotificationImage);
            
            // Clear any color filter when showing images
            holder.ivNotificationImage.clearColorFilter();
        } else {
            // Use appropriate icon based on notification type for notifications without images
            holder.ivNotificationImage.setImageResource(getNotificationIcon(notification));
            holder.ivNotificationImage.setColorFilter(getNotificationIconColor(holder.itemView.getContext(), notification));
        }
        
        // Set unread indicator
        if (notification.isUnread()) {
            holder.tvUnreadIndicator.setVisibility(View.VISIBLE);
            holder.tvTitle.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_primary));
        } else {
            holder.tvUnreadIndicator.setVisibility(View.GONE);
            holder.tvTitle.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_secondary));
        }
        
        // Set notification type badge
        if (notification.isUserSpecificNotification()) {
            holder.tvTypeBadge.setVisibility(View.VISIBLE);
            holder.tvTypeBadge.setText("System");
            holder.tvTypeBadge.setBackgroundResource(R.drawable.bg_status_ok);
        } else if (notification.isPublicNotification()) {
            holder.tvTypeBadge.setVisibility(View.VISIBLE);
            holder.tvTypeBadge.setText("Public");
            holder.tvTypeBadge.setBackgroundResource(R.drawable.bg_status_pending);
        } else {
            holder.tvTypeBadge.setVisibility(View.GONE);
        }
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onNotificationClick(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
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

    private int getNotificationIconColor(android.content.Context context, NotificationItem notification) {
        if (notification.isPublicNotification()) {
            return context.getResources().getColor(R.color.primary_color);
        } else if (notification.isUserSpecificNotification()) {
            if (notification.isBalanceNotification()) {
                if ("balance_added".equals(notification.getActionType())) {
                    return context.getResources().getColor(R.color.success_color);
                } else {
                    return context.getResources().getColor(R.color.danger_color);
                }
            } else if (notification.isDepositNotification()) {
                if ("deposit_approved".equals(notification.getActionType())) {
                    return context.getResources().getColor(R.color.success_color);
                } else {
                    return context.getResources().getColor(R.color.danger_color);
                }
            } else if (notification.isWithdrawalNotification()) {
                if ("withdrawal_approved".equals(notification.getActionType())) {
                    return context.getResources().getColor(R.color.success_color);
                } else {
                    return context.getResources().getColor(R.color.danger_color);
                }
            } else if (notification.isOrderNotification()) {
                return context.getResources().getColor(R.color.primary_color);
            } else if (notification.isServiceNotification()) {
                return context.getResources().getColor(R.color.warning_color);
            } else if (notification.isRefundNotification()) {
                return context.getResources().getColor(R.color.success_color);
            }
        }
        return context.getResources().getColor(R.color.primary_color);
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView ivNotificationImage;
        TextView tvTitle, tvMessage, tvTime, tvUnreadIndicator, tvTypeBadge;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivNotificationImage = itemView.findViewById(R.id.iv_notification_image);
            tvTitle = itemView.findViewById(R.id.tv_notification_title);
            tvMessage = itemView.findViewById(R.id.tv_notification_message);
            tvTime = itemView.findViewById(R.id.tv_notification_time);
            tvUnreadIndicator = itemView.findViewById(R.id.tv_unread_indicator);
            tvTypeBadge = itemView.findViewById(R.id.tv_notification_type_badge);
        }
    }
}
