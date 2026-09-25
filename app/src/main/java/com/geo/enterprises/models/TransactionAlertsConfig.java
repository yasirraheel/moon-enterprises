package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class TransactionAlertsConfig {
    @SerializedName("enabled")
    private boolean enabled = true;

    @SerializedName("interval_seconds")
    private int intervalSeconds = 10;

    @SerializedName("alerts")
    private List<TransactionAlertItem> alerts = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getIntervalSeconds() {
        return intervalSeconds > 0 ? intervalSeconds : 10;
    }

    public void setIntervalSeconds(int intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    public List<TransactionAlertItem> getAlerts() {
        return alerts != null ? alerts : new ArrayList<>();
    }

    public void setAlerts(List<TransactionAlertItem> alerts) {
        this.alerts = alerts;
    }
}
