package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class ApkVersionData {
    @SerializedName("version_name")
    private String versionName;
    
    @SerializedName("version_code")
    private int versionCode;
    
    @SerializedName("download_link")
    private String downloadLink;
    
    @SerializedName("release_notes")
    private String releaseNotes;
    
    @SerializedName("is_force_update")
    private boolean isForceUpdate;
    
    @SerializedName("download_count")
    private int downloadCount;
    
    // Constructors
    public ApkVersionData() {}
    
    public ApkVersionData(String versionName, int versionCode, String downloadLink, 
                         String releaseNotes, boolean isForceUpdate, int downloadCount) {
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.downloadLink = downloadLink;
        this.releaseNotes = releaseNotes;
        this.isForceUpdate = isForceUpdate;
        this.downloadCount = downloadCount;
    }
    
    // Getters and Setters
    public String getVersionName() {
        return versionName;
    }
    
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
    
    public int getVersionCode() {
        return versionCode;
    }
    
    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }
    
    public String getDownloadLink() {
        return downloadLink;
    }
    
    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }
    
    public String getReleaseNotes() {
        return releaseNotes;
    }
    
    public void setReleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }
    
    public boolean isForceUpdate() {
        return isForceUpdate;
    }
    
    public void setForceUpdate(boolean forceUpdate) {
        isForceUpdate = forceUpdate;
    }
    
    public int getDownloadCount() {
        return downloadCount;
    }
    
    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }
    
    @Override
    public String toString() {
        return "ApkVersionData{" +
                "versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                ", downloadLink='" + downloadLink + '\'' +
                ", releaseNotes='" + releaseNotes + '\'' +
                ", isForceUpdate=" + isForceUpdate +
                ", downloadCount=" + downloadCount +
                '}';
    }
}