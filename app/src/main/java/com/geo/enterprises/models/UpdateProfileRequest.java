package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {
    
    @SerializedName("full_name")
    private String fullName;
    
    @SerializedName("phone")
    private String phone;
    
    @SerializedName("city")
    private String city;
    
    @SerializedName("password")
    private String password;
    
    // Constructors
    public UpdateProfileRequest() {}
    
    public UpdateProfileRequest(String fullName, String phone, String city, String password) {
        this.fullName = fullName;
        this.phone = phone;
        this.city = city;
        this.password = password;
    }
    
    // Getters and Setters
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
