package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TransactionResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private List<Transaction> data;
    
    @SerializedName("pagination")
    private Pagination pagination;
    
    @SerializedName("filters")
    private Filters filters;
    
    @SerializedName("message")
    private String message;
    
    // Constructors
    public TransactionResponse() {}
    
    // Getters
    public boolean isSuccess() {
        return success;
    }
    
    public List<Transaction> getData() {
        return data;
    }
    
    public Pagination getPagination() {
        return pagination;
    }
    
    public Filters getFilters() {
        return filters;
    }
    
    public String getMessage() {
        return message;
    }
    
    // Setters
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public void setData(List<Transaction> data) {
        this.data = data;
    }
    
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
    
    public void setFilters(Filters filters) {
        this.filters = filters;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    // Pagination class
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
        
        public int getCurrentPage() { return currentPage; }
        public int getLastPage() { return lastPage; }
        public int getPerPage() { return perPage; }
        public int getTotal() { return total; }
        public int getFrom() { return from; }
        public int getTo() { return to; }
    }
    
    // Filters class
    public static class Filters {
        @SerializedName("type")
        private String type;
        
        @SerializedName("transaction_type")
        private String transactionType;
        
        @SerializedName("date_from")
        private String dateFrom;
        
        @SerializedName("date_to")
        private String dateTo;
        
        public String getType() { return type; }
        public String getTransactionType() { return transactionType; }
        public String getDateFrom() { return dateFrom; }
        public String getDateTo() { return dateTo; }
    }
}
