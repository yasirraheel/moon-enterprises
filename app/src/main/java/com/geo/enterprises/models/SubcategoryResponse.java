package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SubcategoryResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private List<Subcategory> data;
    
    @SerializedName("category")
    private CategoryInfo category;
    
    // Constructors
    public SubcategoryResponse() {}
    
    public SubcategoryResponse(boolean success, List<Subcategory> data, CategoryInfo category) {
        this.success = success;
        this.data = data;
        this.category = category;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public List<Subcategory> getData() {
        return data;
    }
    
    public void setData(List<Subcategory> data) {
        this.data = data;
    }
    
    public CategoryInfo getCategory() {
        return category;
    }
    
    public void setCategory(CategoryInfo category) {
        this.category = category;
    }
    
    // Inner class for category info
    public static class CategoryInfo {
        @SerializedName("id")
        private int id;
        
        @SerializedName("name")
        private String name;
        
        // Constructors
        public CategoryInfo() {}
        
        public CategoryInfo(int id, String name) {
            this.id = id;
            this.name = name;
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
        
        @Override
        public String toString() {
            return "CategoryInfo{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
    
    @Override
    public String toString() {
        return "SubcategoryResponse{" +
                "success=" + success +
                ", data=" + data +
                ", category=" + category +
                '}';
    }
}
