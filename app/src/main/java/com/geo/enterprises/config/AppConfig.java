package com.geo.enterprises.config;

/**
 * Application Configuration
 * Contains all server URLs and app-wide constants
 * Change BASE_URL here for different server deployments
 */
public class AppConfig {
    
    // ========== SERVER CONFIGURATION ==========
    
    /**
     * Main server domain
     * Change this for production/staging/development environments
     */
    private static final String SERVER_DOMAIN = "https://geoenterprises.org";
    
    /**
     * API Base URL
     * Used by Retrofit for all API calls
     */
    public static final String API_BASE_URL = SERVER_DOMAIN + "/api/";
    
    /**
     * Public assets base URL
     * Used for loading images, avatars, etc.
     */
    public static final String PUBLIC_BASE_URL = SERVER_DOMAIN + "/public";
    
    /**
     * Avatar directory URL
     * Used for loading user profile avatars
     */
    public static final String AVATAR_BASE_URL = PUBLIC_BASE_URL + "/avatar/";
    
    
    // ========== APP CONFIGURATION ==========
    
    /**
     * App version
     */
    public static final String APP_VERSION = "1.9";
    
    /**
     * User-Agent header for API requests
     */
    public static final String USER_AGENT = "GEO-ENTERPRISES-Android/" + APP_VERSION;
    
    
    // ========== HELPER METHODS ==========
    
    /**
     * Build complete avatar URL from filename
     * @param filename The avatar filename (e.g., "1760254831_1.png")
     * @return Complete avatar URL
     */
    public static String getAvatarUrl(String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        
        // If already a complete URL
        if (filename.startsWith("http")) {
            // Check if it's missing /public/ in the path
            if (filename.contains("/avatar/") && !filename.contains("/public/avatar/")) {
                // Fix backend URL by inserting /public/ before /avatar/
                return filename.replace("/avatar/", "/public/avatar/");
            }
            return filename;
        }
        
        return AVATAR_BASE_URL + filename;
    }
    
    /**
     * Extract filename from avatar URL or return filename as is
     * @param avatarUrl The avatar URL or filename
     * @return Just the filename
     */
    public static String extractAvatarFilename(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return null;
        }
        
        // If it's a URL, extract filename
        if (avatarUrl.contains("/")) {
            return avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
        }
        
        return avatarUrl;
    }
    
    /**
     * Fix service image URL to include /public/ if missing
     * Handles URLs like: https://domain.com/images/paid-services/file.png
     * Converts to: https://domain.com/public/images/paid-services/file.png
     * @param imageUrl The service image URL from API
     * @return Fixed URL with /public/ included
     */
    public static String getServiceImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        
        // If already a complete URL
        if (imageUrl.startsWith("http")) {
            // Check if it's missing /public/ in the path
            if (imageUrl.contains("/images/") && !imageUrl.contains("/public/images/")) {
                // Fix backend URL by inserting /public/ before /images/
                return imageUrl.replace("/images/", "/public/images/");
            }
            return imageUrl;
        }
        
        // If it's just a path, prepend the server domain
        return PUBLIC_BASE_URL + imageUrl;
    }
}
