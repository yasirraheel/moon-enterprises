package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DepositsResponse {
    
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private List<DepositItem> data;
    
    @SerializedName("pagination")
    private Pagination pagination;
    
    @SerializedName("message")
    private String message;
    
    // Constructors
    public DepositsResponse() {
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public List<DepositItem> getData() {
        return data;
    }
    
    public void setData(List<DepositItem> data) {
        this.data = data;
    }
    
    public Pagination getPagination() {
        return pagination;
    }
    
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    // Inner class for pagination
    public static class Pagination {
        @SerializedName("current_page")
        private int currentPage;
        
        @SerializedName("last_page")
        private int lastPage;
        
        @SerializedName("per_page")
        private int perPage;
        
        @SerializedName("total")
        private int total;
        
        @SerializedName("from")
        private int from;
        
        @SerializedName("to")
        private int to;
        
        @SerializedName("has_more_pages")
        private boolean hasMorePages;
        
        // Getters
        public int getCurrentPage() {
            return currentPage;
        }
        
        public int getLastPage() {
            return lastPage;
        }
        
        public int getPerPage() {
            return perPage;
        }
        
        public int getTotal() {
            return total;
        }
        
        public int getFrom() {
            return from;
        }
        
        public int getTo() {
            return to;
        }
        
        public boolean hasMorePages() {
            return hasMorePages;
        }
    }
}
