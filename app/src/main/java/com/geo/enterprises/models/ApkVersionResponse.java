package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class ApkVersionResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private ApkVersionData data;
    
    @SerializedName("message")
    private String message;
    
    // Constructors
    public ApkVersionResponse() {}
    
    public ApkVersionResponse(boolean success, ApkVersionData data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public ApkVersionData getData() {
        return data;
    }
    
    public void setData(ApkVersionData data) {
        this.data = data;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "ApkVersionResponse{" +
                "success=" + success +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}