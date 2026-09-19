package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class Subcategory {
    @SerializedName("id")
    private int id;
    
    @SerializedName("category_id")
    private int categoryId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("slug")
    private String slug;
    
    @SerializedName("mode")
    private String mode;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("keywords")
    private String keywords;
    
    @SerializedName("start_date")
    private String startDate;
    
    @SerializedName("start_time")
    private String startTime;
    
    @SerializedName("close_date")
    private String closeDate;
    
    @SerializedName("close_time")
    private String closeTime;
    
    // Constructors
    public Subcategory() {}
    
    public Subcategory(int id, int categoryId, String name, String slug, String mode, 
                      String description, String keywords, String startDate, String startTime, 
                      String closeDate, String closeTime) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.slug = slug;
        this.mode = mode;
        this.description = description;
        this.keywords = keywords;
        this.startDate = startDate;
        this.startTime = startTime;
        this.closeDate = closeDate;
        this.closeTime = closeTime;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSlug() {
        return slug;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
    }
    
    public String getMode() {
        return mode;
    }
    
    public void setMode(String mode) {
        this.mode = mode;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getKeywords() {
        return keywords;
    }
    
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    public String getCloseDate() {
        return closeDate;
    }
    
    public void setCloseDate(String closeDate) {
        this.closeDate = closeDate;
    }
    
    public String getCloseTime() {
        return closeTime;
    }
    
    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }
    
    // Helper methods
    public boolean isActive() {
        return "on".equalsIgnoreCase(mode);
    }
    
    public String getFormattedStartDateTime() {
        if (startDate != null && startTime != null) {
            return startDate + " " + startTime;
        }
        return "";
    }
    
    public String getFormattedCloseDateTime() {
        if (closeDate != null && closeTime != null) {
            return closeDate + " " + closeTime;
        }
        return "";
    }
    
    @Override
    public String toString() {
        return "Subcategory{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", name='" + name + '\'' +
                ", mode='" + mode + '\'' +
                ", startDate='" + startDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", closeDate='" + closeDate + '\'' +
                ", closeTime='" + closeTime + '\'' +
                '}';
    }
}
