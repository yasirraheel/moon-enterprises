package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class PaidService {
    @SerializedName("id")
    private int id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("price")
    private String price;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("image")
    private String image;
    
    @SerializedName("is_active")
    private boolean isActive;
    
    @SerializedName("has_purchased")
    private boolean hasPurchased;
    
    @SerializedName("show_buy_button")
    private boolean showBuyButton;
    
    @SerializedName("golden_text")
    private String goldenText;

    // Constructor
    public PaidService() {}

    public PaidService(int id, String title, String price, String description, String image, boolean isActive) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.description = description;
        this.image = image;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
    
    public boolean hasPurchased() {
        return hasPurchased;
    }
    
    public void setHasPurchased(boolean hasPurchased) {
        this.hasPurchased = hasPurchased;
    }
    
    public boolean showBuyButton() {
        return showBuyButton;
    }
    
    public void setShowBuyButton(boolean showBuyButton) {
        this.showBuyButton = showBuyButton;
    }
    
    public String getGoldenText() {
        return goldenText;
    }
    
    public void setGoldenText(String goldenText) {
        this.goldenText = goldenText;
    }
}
