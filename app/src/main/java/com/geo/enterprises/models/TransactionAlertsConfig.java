package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class TransactionAlertsConfig {
    @SerializedName("enabled")
    private boolean enabled = true;

    @SerializedName("interval_seconds")
    private int intervalSeconds = 10;

    @SerializedName("withdrawal_min_amount")
    private double withdrawalMinAmount = 2000;

    @SerializedName("withdrawal_max_amount")
    private double withdrawalMaxAmount = 25000;

    @SerializedName("deposit_min_amount")
    private double depositMinAmount = 1000;

    @SerializedName("deposit_max_amount")
    private double depositMaxAmount = 20000;

    @SerializedName("alerts")
    private List<TransactionAlertItem> alerts = new ArrayList<>();

    public double getWithdrawalMinAmount() {
        return withdrawalMinAmount > 0 ? withdrawalMinAmount : 2000;
    }

    public void setWithdrawalMinAmount(double withdrawalMinAmount) {
        this.withdrawalMinAmount = withdrawalMinAmount;
    }

    public double getWithdrawalMaxAmount() {
        return withdrawalMaxAmount >= getWithdrawalMinAmount() ? withdrawalMaxAmount : 25000;
    }

    public void setWithdrawalMaxAmount(double withdrawalMaxAmount) {
        this.withdrawalMaxAmount = withdrawalMaxAmount;
    }

    public double getDepositMinAmount() {
        return depositMinAmount > 0 ? depositMinAmount : 1000;
    }

    public void setDepositMinAmount(double depositMinAmount) {
        this.depositMinAmount = depositMinAmount;
    }

    public double getDepositMaxAmount() {
        return depositMaxAmount >= getDepositMinAmount() ? depositMaxAmount : 20000;
    }

    public void setDepositMaxAmount(double depositMaxAmount) {
        this.depositMaxAmount = depositMaxAmount;
    }

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
