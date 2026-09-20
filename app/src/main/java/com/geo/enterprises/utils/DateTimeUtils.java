package com.geo.enterprises.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Utility class for handling date and time formatting
 * Ensures consistent timezone handling between server and client
 */
public class DateTimeUtils {
    
    /**
     * Format datetime string from server to Pakistan timezone (UTC+5)
     * Server sends datetime in UTC, we display it in Pakistan timezone
     * 
     * @param dateTimeStr Server datetime string (e.g., "2025-11-06 15:30:00")
     * @param outputPattern Output format pattern (e.g., "dd MMM yyyy, hh:mm a")
     * @return Formatted datetime string in Pakistan timezone (5 hours forward)
     */
    public static String formatServerDateTime(String dateTimeStr, String outputPattern) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return "";
        }
        
        try {
            SimpleDateFormat inputFormat;
            SimpleDateFormat outputFormat;
            
            // Pakistan timezone (UTC+5)
            TimeZone pakistanTimezone = TimeZone.getTimeZone("Asia/Karachi");
            
            // Handle different input formats
            if (dateTimeStr.contains("T")) {
                if (dateTimeStr.contains(".") && dateTimeStr.endsWith("Z")) {
                    // UTC format: "2025-11-06T15:30:00.000000Z"
                    inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US);
                    inputFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Server sends in UTC
                } else {
                    // ISO format: "2025-11-06T15:30:00" (assume UTC from server)
                    inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                    inputFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Treat as UTC
                }
            } else {
                // Standard format: "2025-11-06 15:30:00" (assume UTC from server)
                inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                inputFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Treat as UTC
            }
            
            // Parse the date from UTC
            Date date = inputFormat.parse(dateTimeStr);
            
            if (date != null) {
                // Format output in Pakistan timezone (UTC+5)
                outputFormat = new SimpleDateFormat(outputPattern, Locale.getDefault());
                outputFormat.setTimeZone(pakistanTimezone); // Display in Pakistan time
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            return dateTimeStr;
        }
        
        return dateTimeStr;
    }
    
    /**
     * Format datetime for withdrawal history display
     * @param dateTimeStr Server datetime string
     * @return Formatted string like "06 Nov 2025, 03:30 PM"
     */
    public static String formatWithdrawalDateTime(String dateTimeStr) {
        return formatServerDateTime(dateTimeStr, "dd MMM yyyy, hh:mm a");
    }
    
    /**
     * Format datetime for transaction history display
     * @param dateTimeStr Server datetime string
     * @return Formatted string like "Nov 06, 2025 at 03:30 PM"
     */
    public static String formatTransactionDateTime(String dateTimeStr) {
        return formatServerDateTime(dateTimeStr, "MMM dd, yyyy 'at' hh:mm a");
    }
    
    /**
     * Format datetime for deposit history display
     * @param dateTimeStr Server datetime string
     * @return Formatted string like "06 Nov 2025, 03:30 PM" in Pakistan timezone
     */
    public static String formatDepositDateTime(String dateTimeStr) {
        return formatServerDateTime(dateTimeStr, "dd MMM yyyy, hh:mm a");
    }
    
    /**
     * Convert UTC datetime to Pakistan timezone (UTC+5)
     * Example: "2025-11-06 10:30:00" UTC becomes "06 Nov 2025, 03:30 PM" Pakistan time
     * @param utcDateTimeStr UTC datetime string from server
     * @param outputPattern Output format pattern
     * @return Formatted datetime string 5 hours ahead (Pakistan timezone)
     */
    public static String convertUTCToPakistanTime(String utcDateTimeStr, String outputPattern) {
        return formatServerDateTime(utcDateTimeStr, outputPattern);
    }
    
    /**
     * Format datetime for orders (server already sends in Pakistan time)
     * Special handling for orders where server sends Pakistan time, not UTC
     * @param dateTimeStr Server datetime string (already in Pakistan timezone)
     * @param outputPattern Output format pattern
     * @return Formatted datetime string (no timezone conversion)
     */
    public static String formatOrderDateTime(String dateTimeStr, String outputPattern) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return "";
        }
        
        try {
            SimpleDateFormat inputFormat;
            SimpleDateFormat outputFormat;
            
            // Handle different input formats
            if (dateTimeStr.contains("T")) {
                if (dateTimeStr.contains(".") && dateTimeStr.endsWith("Z")) {
                    // UTC format: "2025-11-06T15:30:00.000000Z"
                    inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US);
                } else {
                    // ISO format: "2025-11-06T15:30:00"
                    inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                }
            } else {
                // Standard format: "2025-11-06 15:30:00" (server already in Pakistan time)
                inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            }
            
            // Parse the date (no timezone conversion - server already sends Pakistan time for orders)
            Date date = inputFormat.parse(dateTimeStr);
            
            if (date != null) {
                // Format output (no timezone conversion needed for orders)
                outputFormat = new SimpleDateFormat(outputPattern, Locale.getDefault());
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            return dateTimeStr;
        }
        
        return dateTimeStr;
    }
}