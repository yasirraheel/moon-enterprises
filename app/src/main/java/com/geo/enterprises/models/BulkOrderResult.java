package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class BulkOrderResult {
    @SerializedName("index")
    private int index;
    
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private Order data;
    
    @SerializedName("message")
    private String message;

    public int getIndex() {
        return index;
    }

    public boolean isSuccess() {
        return success;
    }

    public Order getData() {
        return data;
    }
    
    public String getMessage() {
        return message;
    }
}
