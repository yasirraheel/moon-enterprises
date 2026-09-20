package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Order implements Serializable {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("user_id")
    private int userId;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("user_phone")
    private String userPhone;
    
    @SerializedName("game_name")
    private String gameName;
    
    @SerializedName("bond_name")
    private String bondName;
    
    @SerializedName("rttp")
    private String rttp;
    
    @SerializedName("first")
    private String first;
    
    @SerializedName("second")
    private String second;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("created_at")
    private String createdAt;
    
    // Constructors
    public Order() {
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
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getUserPhone() {
        return userPhone;
    }
    
    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
    
    public String getGameName() {
        return gameName;
    }
    
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
    
    public String getBondName() {
        return bondName;
    }
    
    public void setBondName(String bondName) {
        this.bondName = bondName;
    }
    
    public String getRttp() {
        return rttp;
    }
    
    public void setRttp(String rttp) {
        this.rttp = rttp;
    }
    
    public String getFirst() {
        return first;
    }
    
    public void setFirst(String first) {
        this.first = first;
    }
    
    public String getSecond() {
        return second;
    }
    
    public void setSecond(String second) {
        this.second = second;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    // Calculate total amount
    public double getTotalAmount() {
        try {
            double firstAmount = Double.parseDouble(first);
            double secondAmount = Double.parseDouble(second);
            return firstAmount + secondAmount;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", gameName='" + gameName + '\'' +
                ", bondName='" + bondName + '\'' +
                ", rttp='" + rttp + '\'' +
                ", first='" + first + '\'' +
                ", second='" + second + '\'' +
                ", status='" + status + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
