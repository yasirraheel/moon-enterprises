package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private int id;
    
    @SerializedName("full_name")
    private String fullName;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("phone")
    private String phone;
    
    @SerializedName("city")
    private String city;
    
    @SerializedName("avatar")
    private String avatar;
    
    @SerializedName("balance")
    private double balance;
    
    @SerializedName("account_no")
    private String accountNo;
    
    @SerializedName("status")
    private String status;

    @SerializedName("is_admin")
    private boolean isAdmin;

    @SerializedName(value = "dealer_status", alternate = {"dealerStatus", "status_dealer", "dealer_request_status", "dealership_status"})
    private String dealerStatus;

    @SerializedName(value = "dealer_commission", alternate = {"dealerCommission", "commission_dealer", "commission"})
    private double dealerCommission;

    @SerializedName(value = "commission_earned", alternate = {"commissionEarned", "earned_commission"})
    private double commissionEarned;
    
    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("dealership_id")
    private String dealershipId;

    // Constructors
    public User() {}

    public User(int id, String fullName, String email, String phone, 
                String city, String avatar, double balance, String accountNo, String status, String createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.city = city;
        this.avatar = avatar;
        this.balance = balance;
        this.accountNo = accountNo;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isDealer() {
        return "approved".equalsIgnoreCase(dealerStatus);
    }

    public void setDealer(boolean dealer) {
        if (dealer) {
            this.dealerStatus = "approved";
        } else if ("approved".equalsIgnoreCase(this.dealerStatus)) {
            this.dealerStatus = "na";
        }
    }

    public String getDealerStatus() {
        return dealerStatus;
    }

    public void setDealerStatus(String dealerStatus) {
        this.dealerStatus = dealerStatus;
    }

    public double getDealerCommission() {
        return dealerCommission;
    }

    public void setDealerCommission(double dealerCommission) {
        this.dealerCommission = dealerCommission;
    }

    public double getCommissionEarned() {
        return commissionEarned;
    }

    public void setCommissionEarned(double commissionEarned) {
        this.commissionEarned = commissionEarned;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDealershipId() {
        return dealershipId;
    }

    public void setDealershipId(String dealershipId) {
        this.dealershipId = dealershipId;
    }

    public String getDealershipStatus() {
        return dealerStatus;
    }

    public void setDealershipStatus(String dealershipStatus) {
        this.dealerStatus = dealershipStatus;
    }

    public double getCommission() {
        return dealerCommission;
    }

    public void setCommission(double commission) {
        this.dealerCommission = commission;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", dealerStatus='" + dealerStatus + '\'' +
                ", dealerCommission=" + dealerCommission +
                ", commissionEarned=" + commissionEarned +
                '}';
    }
}
