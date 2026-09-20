package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class DepositItem {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("user_id")
    private int userId;
    
    @SerializedName("amount")
    private String amount;
    
    @SerializedName("transaction_id")
    private String transactionId;
    
    @SerializedName("payment_proof")
    private String paymentProof;
    
    @SerializedName("status")
    private String status; // pending, approved, rejected
    
    @SerializedName("admin_notes")
    private String adminNotes;
    
    @SerializedName("date")
    private String date;
    
    @SerializedName("payment_method")
    private PaymentMethod paymentMethod;
    
    // Constructors
    public DepositItem() {
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
    
    public String getAmount() {
        return amount;
    }
    
    public void setAmount(String amount) {
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
    
    public String getAdminNotes() {
        return adminNotes;
    }
    
    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
