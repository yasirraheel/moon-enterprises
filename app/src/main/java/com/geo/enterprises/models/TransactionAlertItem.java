package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class TransactionAlertItem {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type; // "withdrawal" or "deposit"

    @SerializedName("amount")
    private double amount;

    @SerializedName("formatted_amount")
    private String formattedAmount;

    @SerializedName("currency")
    private String currency = "Rs.";

    @SerializedName("time_ago")
    private String timeAgo = "just now";

    @SerializedName("message")
    private String message;

    public TransactionAlertItem() {}

    public TransactionAlertItem(int id, String name, String type, double amount, String formattedAmount, String timeAgo, String message) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.formattedAmount = formattedAmount;
        this.timeAgo = timeAgo;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type != null ? type : "withdrawal";
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isWithdrawal() {
        return "withdrawal".equalsIgnoreCase(type);
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getFormattedAmount() {
        if (formattedAmount != null && !formattedAmount.isEmpty()) {
            return formattedAmount;
        }
        try {
            java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance(java.util.Locale.US);
            return nf.format((long) amount);
        } catch (Exception e) {
            return String.valueOf((long) amount);
        }
    }

    public void setFormattedAmount(String formattedAmount) {
        this.formattedAmount = formattedAmount;
    }

    public String getCurrency() {
        return currency != null ? currency : "Rs.";
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTimeAgo() {
        return (timeAgo != null && !timeAgo.isEmpty()) ? timeAgo : "just now";
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
