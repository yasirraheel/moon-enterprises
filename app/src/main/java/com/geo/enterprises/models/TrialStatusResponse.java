package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class TrialStatusResponse {
    @SerializedName("is_trial")
    private boolean isTrial;

    public boolean isTrial() {
        return isTrial;
    }

    public void setTrial(boolean trial) {
        isTrial = trial;
    }
}
