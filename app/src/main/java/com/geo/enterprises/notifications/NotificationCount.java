package com.geo.enterprises.notifications;

import com.google.gson.annotations.SerializedName;

public class NotificationCount {
    @SerializedName("count")
    private int count;

    public NotificationCount() {}

    public NotificationCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
