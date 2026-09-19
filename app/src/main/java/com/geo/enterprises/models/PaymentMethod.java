package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class PaymentMethod {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("bank_or_account_name")
    private String bankOrAccountName;
    
    @SerializedName("account_title")
    private String accountTitle;
    
    @SerializedName("account_no")
    private String accountNo;
    
    @SerializedName("bank_image")
    private String bankImage;
    
    @SerializedName("is_active")
    private boolean isActive;
    
    @SerializedName("sort_order")
    private int sortOrder;
    
    @SerializedName("minimum_limit")
    private double minimumLimit;
    
    @SerializedName("maximum_limit")
    private double maximumLimit;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    // Constructors
    public PaymentMethod() {
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getBankOrAccountName() {
        return bankOrAccountName;
    }
    
    public void setBankOrAccountName(String bankOrAccountName) {
        this.bankOrAccountName = bankOrAccountName;
    }
    
    public String getAccountTitle() {
        return accountTitle;
    }
    
    public void setAccountTitle(String accountTitle) {
        this.accountTitle = accountTitle;
    }
    
    public String getAccountNo() {
        return accountNo;
    }
    
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
    
    public String getBankImage() {
        return bankImage;
    }
    
    public void setBankImage(String bankImage) {
        this.bankImage = bankImage;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public int getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public double getMinimumLimit() {
        return minimumLimit;
    }
    
    public void setMinimumLimit(double minimumLimit) {
        this.minimumLimit = minimumLimit;
    }
    
    public double getMaximumLimit() {
        return maximumLimit;
    }
    
    public void setMaximumLimit(double maximumLimit) {
        this.maximumLimit = maximumLimit;
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
        return "PaymentMethod{" +
                "id=" + id +
                ", bankOrAccountName='" + bankOrAccountName + '\'' +
                ", accountTitle='" + accountTitle + '\'' +
                ", accountNo='" + accountNo + '\'' +
                ", bankImage='" + bankImage + '\'' +
                ", isActive=" + isActive +
                ", sortOrder=" + sortOrder +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
