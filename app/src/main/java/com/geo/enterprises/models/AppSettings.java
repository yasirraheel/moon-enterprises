package com.geo.enterprises.models;

import com.google.gson.annotations.SerializedName;

public class AppSettings {
    @SerializedName("app_name")
    private String appName;
    
    @SerializedName("app_logo")
    private String appLogo;
    
    @SerializedName("app_logo_light")
    private String appLogoLight;
    
    @SerializedName("app_tagline")
    private String appTagline;
    
    @SerializedName("primary_color")
    private String primaryColor;
    
    @SerializedName("registration_active")
    private boolean registrationActive;
    
    @SerializedName("email_verification")
    private boolean emailVerification;
    
    @SerializedName("signup_bonus_credits")
    private int signupBonusCredits;
    
    @SerializedName("facebook_login")
    private boolean facebookLogin;
    
    @SerializedName("google_login")
    private boolean googleLogin;
    
    @SerializedName("twitter_login")
    private boolean twitterLogin;
    
    @SerializedName("captcha_enabled")
    private boolean captchaEnabled;
    
    @SerializedName("theme")
    private String theme;
    
    @SerializedName("currency_symbol")
    private String currencySymbol;
    
    @SerializedName("currency_code")
    private String currencyCode;
    
    @SerializedName("currency_position")
    private String currencyPosition;
    
    @SerializedName("whatsapp_number")
    private String whatsappNumber;

    @SerializedName("whatsapp_group_link")
    private String whatsappGroupLink;

    public AppSettings() {}

    // Getters and Setters
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppLogo() {
        return appLogo;
    }

    public void setAppLogo(String appLogo) {
        this.appLogo = appLogo;
    }

    public String getAppLogoLight() {
        return appLogoLight;
    }

    public void setAppLogoLight(String appLogoLight) {
        this.appLogoLight = appLogoLight;
    }

    public String getAppTagline() {
        return appTagline;
    }

    public void setAppTagline(String appTagline) {
        this.appTagline = appTagline;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public boolean isRegistrationActive() {
        return registrationActive;
    }

    public void setRegistrationActive(boolean registrationActive) {
        this.registrationActive = registrationActive;
    }

    public boolean isEmailVerification() {
        return emailVerification;
    }

    public void setEmailVerification(boolean emailVerification) {
        this.emailVerification = emailVerification;
    }

    public int getSignupBonusCredits() {
        return signupBonusCredits;
    }

    public void setSignupBonusCredits(int signupBonusCredits) {
        this.signupBonusCredits = signupBonusCredits;
    }

    public boolean isFacebookLogin() {
        return facebookLogin;
    }

    public void setFacebookLogin(boolean facebookLogin) {
        this.facebookLogin = facebookLogin;
    }

    public boolean isGoogleLogin() {
        return googleLogin;
    }

    public void setGoogleLogin(boolean googleLogin) {
        this.googleLogin = googleLogin;
    }

    public boolean isTwitterLogin() {
        return twitterLogin;
    }

    public void setTwitterLogin(boolean twitterLogin) {
        this.twitterLogin = twitterLogin;
    }

    public boolean isCaptchaEnabled() {
        return captchaEnabled;
    }

    public void setCaptchaEnabled(boolean captchaEnabled) {
        this.captchaEnabled = captchaEnabled;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getCurrencySymbol() {
        return currencySymbol != null ? currencySymbol : "$";
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getCurrencyCode() {
        return currencyCode != null ? currencyCode : "USD";
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyPosition() {
        return currencyPosition != null ? currencyPosition : "before";
    }

    public void setCurrencyPosition(String currencyPosition) {
        this.currencyPosition = currencyPosition;
    }
    
    public String getWhatsappNumber() {
        return whatsappNumber;
    }

    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    public String getWhatsappGroupLink() {
        return whatsappGroupLink;
    }

    public void setWhatsappGroupLink(String whatsappGroupLink) {
        this.whatsappGroupLink = whatsappGroupLink;
    }
    
    // Helper method to format currency
    public String formatCurrency(double amount) {
        String code = getCurrencyCode();
        String symbol = getCurrencySymbol();
        String formattedAmount = String.format("%.2f", amount);
        
        // Format: PKR 10000 Rs (code + amount + symbol)
        return code + " " + formattedAmount + " " + symbol;
    }
}