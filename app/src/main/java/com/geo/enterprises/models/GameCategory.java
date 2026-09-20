package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class GameCategory {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("image")
    private String image;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("date")
    private String date;
    
    @SerializedName("time")
    private String time;
    
    // Alternative field mappings in case API uses different names
    @SerializedName("start_date")
    private String startDate;
    
    @SerializedName("start_time")
    private String startTime;
    
    @SerializedName("end_date")
    private String endDate;
    
    @SerializedName("end_time")
    private String endTime;
    
    @SerializedName("datetime")
    private String datetime;
    
    // Constructors
    public GameCategory() {}
    
    public GameCategory(int id, String name, String image, String status, String createdAt, 
                       String date, String time) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.status = status;
        this.createdAt = createdAt;
        this.date = date;
        this.time = time;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
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
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    // Getters for alternative fields
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    
    public String getDatetime() { return datetime; }
    public void setDatetime(String datetime) { this.datetime = datetime; }
    
    // Helper methods - now using the new date/time fields as primary source
    public String getFormattedDateTime() {
        // Primary: Use the new date and time fields
        if (date != null && time != null) {
            return date + " " + time;
        }
        // Fallback to created_at if date/time not available
        if (createdAt != null) {
            return createdAt;
        }
        // Other fallbacks
        if (startDate != null && startTime != null) {
            return startDate + " " + startTime;
        }
        if (datetime != null) {
            return datetime;
        }
        return "Not Available";
    }
    
    public String getDateOnly() {
        // Primary: Use the new date field
        if (date != null) return date;
        
        // Fallback: Extract from created_at
        if (createdAt != null) {
            String[] parts = createdAt.split(" ");
            if (parts.length > 0) return parts[0];
        }
        
        // Other fallbacks
        if (startDate != null) return startDate;
        if (datetime != null) {
            String[] parts = datetime.split(" ");
            if (parts.length > 0) return parts[0];
        }
        return "Not Available";
    }
    
    public String getTimeOnly() {
        // Primary: Use the new time field
        if (time != null) return time;
        
        // Fallback: Extract from created_at
        if (createdAt != null) {
            String[] parts = createdAt.split(" ");
            if (parts.length > 1) return parts[1];
        }
        
        // Other fallbacks
        if (startTime != null) return startTime;
        if (datetime != null) {
            String[] parts = datetime.split(" ");
            if (parts.length > 1) return parts[1];
        }
        return "Not Available";
    }
}
