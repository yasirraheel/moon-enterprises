package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class LiveAlertsData {
    @SerializedName("online_users")
    private OnlineUsersConfig onlineUsers;

    @SerializedName("transaction_alerts")
    private TransactionAlertsConfig transactionAlerts;

    public OnlineUsersConfig getOnlineUsers() {
        return onlineUsers != null ? onlineUsers : new OnlineUsersConfig();
    }

    public void setOnlineUsers(OnlineUsersConfig onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    public TransactionAlertsConfig getTransactionAlerts() {
        return transactionAlerts != null ? transactionAlerts : new TransactionAlertsConfig();
    }

    public void setTransactionAlerts(TransactionAlertsConfig transactionAlerts) {
        this.transactionAlerts = transactionAlerts;
    }
}
