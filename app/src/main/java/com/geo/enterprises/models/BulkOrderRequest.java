package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BulkOrderRequest {
    @SerializedName("orders")
    private List<BulkOrderItem> orders;

    public BulkOrderRequest(List<BulkOrderItem> orders) {
        this.orders = orders;
    }

    public static class BulkOrderItem {
        @SerializedName("game_name")
        private String gameName;
        @SerializedName("bond_name")
        private String bondName;
        @SerializedName("rttp")
        private String rttp;
        @SerializedName("first")
        private int first;
        @SerializedName("second")
        private int second;
        @SerializedName("user_phone")
        private String userPhone;

        public BulkOrderItem(String gameName, String bondName, String rttp, int first, int second, String userPhone) {
            this.gameName = gameName;
            this.bondName = bondName;
            this.rttp = rttp;
            this.first = first;
            this.second = second;
            this.userPhone = userPhone;
        }
    }
}
