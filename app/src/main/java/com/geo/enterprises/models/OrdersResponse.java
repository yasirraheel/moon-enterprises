package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrdersResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<Order> data;

    @SerializedName("pagination")
    private Pagination pagination;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Order> getData() {
        return data;
    }

    public void setData(List<Order> data) {
        this.data = data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public static class Pagination {
        @SerializedName("current_page")
        private int currentPage;

        @SerializedName("last_page")
        private int lastPage;

        @SerializedName("per_page")
        private int perPage;

        @SerializedName("total")
        private int total;

        @SerializedName("from")
        private int from;

        @SerializedName("to")
        private int to;

        @SerializedName("has_more_pages")
        private boolean hasMorePages;

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getLastPage() {
            return lastPage;
        }

        public void setLastPage(int lastPage) {
            this.lastPage = lastPage;
        }

        public int getPerPage() {
            return perPage;
        }

        public void setPerPage(int perPage) {
            this.perPage = perPage;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getFrom() {
            return from;
        }

        public void setFrom(int from) {
            this.from = from;
        }

        public int getTo() {
            return to;
        }

        public void setTo(int to) {
            this.to = to;
        }

        public boolean isHasMorePages() {
            return hasMorePages;
        }

        public void setHasMorePages(boolean hasMorePages) {
            this.hasMorePages = hasMorePages;
        }
    }
}
