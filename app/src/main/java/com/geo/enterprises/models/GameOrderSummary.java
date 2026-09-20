package com.geo.enterprises.models;

public class GameOrderSummary {
    private String gameName;
    private int orderCount;
    private String gameImage;

    public GameOrderSummary(String gameName, int orderCount, String gameImage) {
        this.gameName = gameName;
        this.orderCount = orderCount;
        this.gameImage = gameImage;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public String getGameImage() {
        return gameImage;
    }

    public void setGameImage(String gameImage) {
        this.gameImage = gameImage;
    }
}
