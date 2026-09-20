package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class ResetCommissionResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("commission_earned")
    private double commissionEarned;

    @SerializedName("previous_commission")
    private double previousCommission;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getCommissionEarned() {
        return commissionEarned;
    }

    public void setCommissionEarned(double commissionEarned) {
        this.commissionEarned = commissionEarned;
    }

    public double getPreviousCommission() {
        return previousCommission;
    }

    public void setPreviousCommission(double previousCommission) {
        this.previousCommission = previousCommission;
    }
}
