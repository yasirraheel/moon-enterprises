package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class DealershipStatusResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private DealershipData data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public DealershipData getData() {
        return data;
    }

    public void setData(DealershipData data) {
        this.data = data;
    }

    public static class DealershipData {
        @SerializedName("is_dealer")
        private int isDealer;
        
        @SerializedName("has_request")
        private boolean hasRequest;
        
        @SerializedName("request")
        private DealershipRequest request;
        
        @SerializedName("can_apply")
        private boolean canApply;

        public boolean isDealer() {
            return isDealer == 1;
        }

        public void setDealer(boolean dealer) {
            isDealer = dealer ? 1 : 0;
        }

        public boolean hasRequest() {
            return hasRequest;
        }

        public void setHasRequest(boolean hasRequest) {
            this.hasRequest = hasRequest;
        }

        public DealershipRequest getRequest() {
            return request;
        }

        public void setRequest(DealershipRequest request) {
            this.request = request;
        }

        public boolean canApply() {
            return canApply;
        }

        public void setCanApply(boolean canApply) {
            this.canApply = canApply;
        }
    }

    public static class DealershipRequest {
        @SerializedName("id")
        private int id;
        
        @SerializedName("status")
        private String status;
        
        @SerializedName("submitted_at")
        private String submittedAt;
        
        @SerializedName("processed_at")
        private String processedAt;
        
        @SerializedName("admin_notes")
        private String adminNotes;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getSubmittedAt() {
            return submittedAt;
        }

        public void setSubmittedAt(String submittedAt) {
            this.submittedAt = submittedAt;
        }

        public String getProcessedAt() {
            return processedAt;
        }

        public void setProcessedAt(String processedAt) {
            this.processedAt = processedAt;
        }

        public String getAdminNotes() {
            return adminNotes;
        }

        public void setAdminNotes(String adminNotes) {
            this.adminNotes = adminNotes;
        }
    }
}
