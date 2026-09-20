package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class WithdrawalMethod {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("bank_or_account_name")
    private String bankOrAccountName;
    
    @SerializedName("account_title")
    private String accountTitle;
    
    @SerializedName("account_no")
    private String accountNo;
    
    @SerializedName(value = "bank_image", alternate = {"image"})
    private String bankImage;
    
    @SerializedName("is_active")
    private boolean isActive;
    
    @SerializedName("sort_order")
    private int sortOrder;
    
    @SerializedName("minimum_limit")
    private String minimumLimit;
    
    @SerializedName("maximum_limit")
    private String maximumLimit;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    // Legacy fields for backward compatibility
    @SerializedName("name")
    private String name;
    
    @SerializedName("min_amount")
    private String minAmount;
    
    @SerializedName("max_amount")
    private String maxAmount;
    
    // Constructors
    public WithdrawalMethod() {
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getBankOrAccountName() {
        return bankOrAccountName != null ? bankOrAccountName : name;
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
    
    public String getName() {
        return name != null ? name : bankOrAccountName;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public String getMinimumLimit() {
        return minimumLimit != null ? minimumLimit : minAmount;
    }
    
    public void setMinimumLimit(String minimumLimit) {
        this.minimumLimit = minimumLimit;
    }
    
    public double getMinimumLimitDouble() {
        try {
            String limit = getMinimumLimit();
            if (limit != null) {
                return Double.parseDouble(limit);
            }
        } catch (Exception e) {
            // Ignore
        }
        return 0;
    }
    
    public String getMaximumLimit() {
        return maximumLimit != null ? maximumLimit : maxAmount;
    }
    
    public void setMaximumLimit(String maximumLimit) {
        this.maximumLimit = maximumLimit;
    }
    
    public double getMaximumLimitDouble() {
        try {
            String limit = getMaximumLimit();
            if (limit != null) {
                return Double.parseDouble(limit);
            }
        } catch (Exception e) {
            // Ignore
        }
        return 0;
    }
    
    // Legacy methods for backward compatibility
    public String getMinAmount() {
        return minAmount != null ? minAmount : minimumLimit;
    }
    
    public void setMinAmount(String minAmount) {
        this.minAmount = minAmount;
    }
    
    public double getMinAmountDouble() {
        return getMinimumLimitDouble();
    }
    
    public String getMaxAmount() {
        return maxAmount != null ? maxAmount : maximumLimit;
    }
    
    public void setMaxAmount(String maxAmount) {
        this.maxAmount = maxAmount;
    }
    
    public double getMaxAmountDouble() {
        return getMaximumLimitDouble();
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
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
        return getBankOrAccountName(); // For spinner display
    }
}
