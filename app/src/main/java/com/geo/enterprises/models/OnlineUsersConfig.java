package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class OnlineUsersConfig {
    @SerializedName("enabled")
    private boolean enabled = true;

    @SerializedName("base_count")
    private int baseCount = 452;

    @SerializedName("current_count")
    private int currentCount = 0;

    @SerializedName("min_count")
    private int minCount = 420;

    @SerializedName("max_count")
    private int maxCount = 490;

    @SerializedName("interval_seconds")
    private int intervalSeconds = 6;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getBaseCount() {
        return baseCount > 0 ? baseCount : 452;
    }

    public void setBaseCount(int baseCount) {
        this.baseCount = baseCount;
    }

    public int getCurrentCount() {
        if (currentCount > 0) {
            return currentCount;
        }
        return getBaseCount();
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public int getMinCount() {
        return minCount > 0 ? minCount : 420;
    }

    public void setMinCount(int minCount) {
        this.minCount = minCount;
    }

    public int getMaxCount() {
        return maxCount >= minCount ? maxCount : 490;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getIntervalSeconds() {
        return intervalSeconds > 0 ? intervalSeconds : 6;
    }

    public void setIntervalSeconds(int intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }
}
