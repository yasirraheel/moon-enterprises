package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class HelpVideo {
    @SerializedName("id")
    private int id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("video_type")
    private String videoType;
    
    @SerializedName("video_url")
    private String videoUrl;
    
    @SerializedName("embed_url")
    private String embedUrl;
    
    @SerializedName("view_count")
    private int viewCount;
    
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
    
    public String getVideoType() {
        return videoType;
    }
    
    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }
    
    public String getVideoUrl() {
        return videoUrl;
    }
    
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
    
    public String getEmbedUrl() {
        return embedUrl;
    }
    
    public void setEmbedUrl(String embedUrl) {
        this.embedUrl = embedUrl;
    }
    
    public int getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}
