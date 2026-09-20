package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HelpVideosResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("videos")
    private List<HelpVideo> videos;
    
    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<HelpVideo> getVideos() {
        return videos;
    }

    public void setVideos(List<HelpVideo> videos) {
        this.videos = videos;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
