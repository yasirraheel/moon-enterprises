package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PaidServicesResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private List<PaidService> data;

    // Constructor
    public PaidServicesResponse() {}

    public PaidServicesResponse(boolean success, List<PaidService> data) {
        this.success = success;
        this.data = data;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<PaidService> getData() {
        return data;
    }

    public void setData(List<PaidService> data) {
        this.data = data;
    }
}
