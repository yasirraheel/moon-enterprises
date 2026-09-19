package com.geo.enterprises.models;

/**
 * Model class representing a draft order before submission.
 * Used in the bulk order system for previewing and managing multiple orders.
 */
public class DraftOrder {

    public enum State {
        PREVIEW,    // Yellow background - being typed
        DRAFTED,    // White background - confirmed, waiting to submit
        PLACING,    // Blue background - currently being submitted
        SUCCESS,    // Green - successfully placed
        FAILED      // Red - failed to place
    }

    private String rttp;
    private double first;
    private double second;
    private double total;
    private State state;
    private String errorMessage;
    private int orderId; // Set after successful placement

    public DraftOrder() {
        this.state = State.PREVIEW;
        this.first = 0;
        this.second = 0;
        this.total = 0;
    }

    public DraftOrder(String rttp, double first, double second) {
        this.rttp = rttp;
        this.first = first;
        this.second = second;
        this.total = first + second;
        this.state = State.PREVIEW;
    }

    // Getters and Setters
    public String getRttp() {
        return rttp;
    }

    public void setRttp(String rttp) {
        this.rttp = rttp;
    }

    public double getFirst() {
        return first;
    }

    public void setFirst(double first) {
        this.first = first;
        this.total = this.first + this.second;
    }

    public double getSecond() {
        return second;
    }

    public void setSecond(double second) {
        this.second = second;
        this.total = this.first + this.second;
    }

    public double getTotal() {
        return total;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public boolean isPreview() {
        return state == State.PREVIEW;
    }

    public boolean isDrafted() {
        return state == State.DRAFTED;
    }

    public boolean isPlacing() {
        return state == State.PLACING;
    }

    public boolean isSuccess() {
        return state == State.SUCCESS;
    }

    public boolean isFailed() {
        return state == State.FAILED;
    }

    /**
     * Get formatted first amount string
     */
    public String getFirstFormatted() {
        if (first == 0) return "-";
        return String.format("%.0f", first);
    }

    /**
     * Get formatted second amount string
     */
    public String getSecondFormatted() {
        if (second == 0) return "-";
        return String.format("%.0f", second);
    }

    /**
     * Get formatted total amount string
     */
    public String getTotalFormatted() {
        return String.format("%.0f", total);
    }

    @Override
    public String toString() {
        return "DraftOrder{" +
                "rttp='" + rttp + '\'' +
                ", first=" + first +
                ", second=" + second +
                ", total=" + total +
                ", state=" + state +
                '}';
    }
}
