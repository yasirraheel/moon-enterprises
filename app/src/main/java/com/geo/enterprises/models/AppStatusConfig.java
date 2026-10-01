package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class AppStatusConfig {
    @SerializedName("app_closed")
    private boolean appClosed;

    @SerializedName("app_closed_title")
    private String appClosedTitle;

    @SerializedName("app_closed_message")
    private String appClosedMessage;

    public AppStatusConfig() {}

    public boolean isAppClosed() {
        return appClosed;
    }

    public void setAppClosed(boolean appClosed) {
        this.appClosed = appClosed;
    }

    public String getAppClosedTitle() {
        return (appClosedTitle != null && !appClosedTitle.trim().isEmpty()) 
                ? appClosedTitle 
                : "App Temporarily Closed";
    }

    public void setAppClosedTitle(String appClosedTitle) {
        this.appClosedTitle = appClosedTitle;
    }

    public String getAppClosedMessage() {
        return (appClosedMessage != null && !appClosedMessage.trim().isEmpty()) 
                ? appClosedMessage 
                : "We are currently closed for bookings. Please check back later.";
    }

    public void setAppClosedMessage(String appClosedMessage) {
        this.appClosedMessage = appClosedMessage;
    }
}
