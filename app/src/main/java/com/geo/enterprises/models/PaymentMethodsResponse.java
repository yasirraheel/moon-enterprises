package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PaymentMethodsResponse {
    
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private List<PaymentMethod> data;
    
    @SerializedName("message")
    private String message;
    
    // Constructors
    public PaymentMethodsResponse() {
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public List<PaymentMethod> getData() {
        return data;
    }
    
    public void setData(List<PaymentMethod> data) {
        this.data = data;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
