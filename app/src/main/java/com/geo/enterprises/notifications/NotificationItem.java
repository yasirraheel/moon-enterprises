package com.geo.enterprises.notifications;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class NotificationItem {
    @SerializedName("id")
    private int id;
    
    @SerializedName("type")
    private String type; // "public" or "user_specific"
    
    @SerializedName("action_type")
    private String actionType; // "balance_added", "deposit_approved", etc.
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("image")
    private String image;
    
    @SerializedName("metadata")
    private Map<String, Object> metadata; // Additional data like amounts, reference IDs
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("time_ago")
    private String timeAgo;
    
    @SerializedName("is_read")
    private boolean isRead = false; // Server-side read status
    
    @SerializedName("read_at")
    private String readAt; // When the notification was read
    
    private boolean isUnread = true; // Local unread status (for UI)

    public NotificationItem() {}

    public NotificationItem(int id, String title, String message, String image, String createdAt, String timeAgo) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.image = image;
        this.createdAt = createdAt;
        this.timeAgo = timeAgo;
        this.isUnread = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getReadAt() {
        return readAt;
    }

    public void setReadAt(String readAt) {
        this.readAt = readAt;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public void setUnread(boolean unread) {
        isUnread = unread;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    // Helper methods
    public boolean isPublicNotification() {
        return "public".equals(type);
    }

    public boolean isUserSpecificNotification() {
        return "user_specific".equals(type);
    }

    public boolean isBalanceNotification() {
        return "balance_added".equals(actionType) || "balance_deducted".equals(actionType);
    }

    public boolean isDepositNotification() {
        return "deposit_approved".equals(actionType) || "deposit_rejected".equals(actionType);
    }

    public boolean isWithdrawalNotification() {
        return "withdrawal_approved".equals(actionType) || "withdrawal_rejected".equals(actionType);
    }

    public boolean isOrderNotification() {
        return "order_placed".equals(actionType);
    }

    public boolean isServiceNotification() {
        return "paid_service_purchased".equals(actionType);
    }

    public boolean isRefundNotification() {
        return "refund_processed".equals(actionType);
    }

    // Get amount from metadata if available
    public String getAmountFromMetadata() {
        if (metadata != null && metadata.containsKey("amount")) {
            Object amount = metadata.get("amount");
            if (amount instanceof Number) {
                return String.format("Rs. %,.2f", ((Number) amount).doubleValue());
            }
        }
        return null;
    }

    // Get description from metadata if available
    public String getDescriptionFromMetadata() {
        if (metadata != null && metadata.containsKey("description")) {
            return (String) metadata.get("description");
        }
        return null;
    }
}
