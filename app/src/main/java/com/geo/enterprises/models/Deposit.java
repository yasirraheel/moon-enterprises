package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class Deposit {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("user_id")
    private int userId;
    
    @SerializedName("payment_method_id")
    private int paymentMethodId;
    
    @SerializedName("amount")
    private double amount;
    
    @SerializedName("transaction_id")
    private String transactionId;
    
    @SerializedName("payment_proof")
    private String paymentProof;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("date")
    private String date;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    // Constructors
    public Deposit() {
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getPaymentMethodId() {
        return paymentMethodId;
    }
    
    public void setPaymentMethodId(int paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getPaymentProof() {
        return paymentProof;
    }
    
    public void setPaymentProof(String paymentProof) {
        this.paymentProof = paymentProof;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Deposit{" +
                "id=" + id +
                ", userId=" + userId +
                ", paymentMethodId=" + paymentMethodId +
                ", amount=" + amount +
                ", transactionId='" + transactionId + '\'' +
                ", paymentProof='" + paymentProof + '\'' +
                ", status='" + status + '\'' +
                ", date='" + date + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
