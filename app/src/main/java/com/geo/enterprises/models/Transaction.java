package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;
import com.google.gson.JsonElement;

public class Transaction {
    @SerializedName("id")
    private int id;
    
    @SerializedName("type")
    private String type; // credit or debit
    
    @SerializedName("transaction_type")
    private String transactionType; // deposit, withdrawal, etc.
    
    @SerializedName("transaction_type_label")
    private String transactionTypeLabel;
    
    @SerializedName("amount")
    private String amount;
    
    @SerializedName("current_balance")
    private String currentBalance;
    
    @SerializedName("remaining_balance")
    private String remainingBalance;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("reference_id")
    private String referenceId;
    
    @SerializedName("reference_type")
    private String referenceType;
    
    @SerializedName("metadata")
    private JsonElement metadata;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("formatted_amount")
    private String formattedAmount;
    
    @SerializedName("formatted_current_balance")
    private String formattedCurrentBalance;
    
    @SerializedName("formatted_remaining_balance")
    private String formattedRemainingBalance;
    
    // Constructors
    public Transaction() {}
    
    // Getters
    public int getId() {
        return id;
    }
    
    public String getType() {
        return type;
    }
    
    public String getTransactionType() {
        return transactionType;
    }
    
    public String getTransactionTypeLabel() {
        return transactionTypeLabel;
    }
    
    public String getAmount() {
        return amount;
    }
    
    public String getCurrentBalance() {
        return currentBalance;
    }
    
    public String getRemainingBalance() {
        return remainingBalance;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getReferenceId() {
        return referenceId;
    }
    
    public String getReferenceType() {
        return referenceType;
    }
    
    public JsonElement getMetadata() {
        return metadata;
    }
    
    public String getMetadataAsString() {
        if (metadata == null || metadata.isJsonNull()) {
            return null;
        }
        if (metadata.isJsonObject()) {
            return metadata.toString();
        }
        return metadata.getAsString();
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public String getFormattedAmount() {
        return formattedAmount;
    }
    
    public String getFormattedCurrentBalance() {
        return formattedCurrentBalance;
    }
    
    public String getFormattedRemainingBalance() {
        return formattedRemainingBalance;
    }
    
    // Setters
    public void setId(int id) {
        this.id = id;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    
    public void setTransactionTypeLabel(String transactionTypeLabel) {
        this.transactionTypeLabel = transactionTypeLabel;
    }
    
    public void setAmount(String amount) {
        this.amount = amount;
    }
    
    public void setCurrentBalance(String currentBalance) {
        this.currentBalance = currentBalance;
    }
    
    public void setRemainingBalance(String remainingBalance) {
        this.remainingBalance = remainingBalance;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
    
    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }
    
    public void setMetadata(JsonElement metadata) {
        this.metadata = metadata;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setFormattedAmount(String formattedAmount) {
        this.formattedAmount = formattedAmount;
    }
    
    public void setFormattedCurrentBalance(String formattedCurrentBalance) {
        this.formattedCurrentBalance = formattedCurrentBalance;
    }
    
    public void setFormattedRemainingBalance(String formattedRemainingBalance) {
        this.formattedRemainingBalance = formattedRemainingBalance;
    }
    
    // Helper methods
    public boolean isCredit() {
        return "credit".equalsIgnoreCase(type);
    }
    
    public boolean isDebit() {
        return "debit".equalsIgnoreCase(type);
    }
}
