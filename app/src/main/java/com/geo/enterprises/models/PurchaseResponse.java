package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class PurchaseResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private PurchaseData data;

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

    public PurchaseData getData() {
        return data;
    }

    public void setData(PurchaseData data) {
        this.data = data;
    }

    public static class PurchaseData {
        @SerializedName("purchase_id")
        private int purchaseId;

        @SerializedName("remaining_balance")
        private double remainingBalance;

        public int getPurchaseId() {
            return purchaseId;
        }

        public void setPurchaseId(int purchaseId) {
            this.purchaseId = purchaseId;
        }

        public double getRemainingBalance() {
            return remainingBalance;
        }

        public void setRemainingBalance(double remainingBalance) {
            this.remainingBalance = remainingBalance;
        }
    }
}
