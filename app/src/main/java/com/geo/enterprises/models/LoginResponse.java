package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse extends User {
    @SerializedName("token")
    private String token;
    
    @SerializedName("token_type")
    private String tokenType;

    public LoginResponse() {}

    public LoginResponse(String token, String tokenType) {
        this.token = token;
        this.tokenType = tokenType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
