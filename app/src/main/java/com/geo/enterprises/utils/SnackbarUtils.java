ckage com.geo.enterprises.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.geo.enterprises.R;
import com.google.android.material.snackbar.Snackbar;

public class SnackbarUtils {
    
    public enum SnackbarType {
        SUCCESS,
        WARNING,
        DANGER,
        INFO
    }
    
    private static Snackbar currentSnackbar;
    private static LoadingDialog currentLoadingDialog;
    
    /**
     * Show success Snackbar
     */
    public static void showSuccess(View parent, String message) {
        showSnackbar(parent, message, SnackbarType.SUCCESS, Snackbar.LENGTH_LONG);
    }
    
    public static void showSuccess(View parent, @StringRes int messageRes) {
        showSnackbar(parent, parent.getContext().getString(messageRes), SnackbarType.SUCCESS, Snackbar.LENGTH_LONG);
    }
    
    /**
     * Show warning Snackbar
     */
    public static void showWarning(View parent, String message) {
        showSnackbar(parent, message, SnackbarType.WARNING, Snackbar.LENGTH_LONG);
    }
    
    public static void showWarning(View parent, @StringRes int messageRes) {
        showSnackbar(parent, parent.getContext().getString(messageRes), SnackbarType.WARNING, Snackbar.LENGTH_LONG);
    }
    
    /**
     * Show danger/error Snackbar
     */
    public static void showDanger(View parent, String message) {
        showSnackbar(parent, message, SnackbarType.DANGER, Snackbar.LENGTH_LONG);
    }
    
    public static void showDanger(View parent, @StringRes int messageRes) {
        showSnackbar(parent, parent.getContext().getString(messageRes), SnackbarType.DANGER, Snackbar.LENGTH_LONG);
    }
    
    /**
     * Show info Snackbar
     */
    public static void showInfo(View parent, String message) {
        showSnackbar(parent, message, SnackbarType.INFO, Snackbar.LENGTH_LONG);
    }
    
    public static void showInfo(View parent, @StringRes int messageRes) {
        showSnackbar(parent, parent.getContext().getString(messageRes), SnackbarType.INFO, Snackbar.LENGTH_LONG);
    }
    
    /**
     * Show custom Snackbar with duration
     */
    public static void showSnackbar(View parent, String message, SnackbarType type, int duration) {
        // Dismiss current snackbar if showing
        dismissCurrent();
        
        // Create custom Snackbar
        Snackbar snackbar = Snackbar.make(parent, "", duration);
        
        // Get the Snackbar's layout
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        
        // Remove default content
        layout.removeAllViews();
        layout.setPadding(0, 0, 0, 0);
        layout.setBackground(null);
        
        // Inflate custom layout based on type
        View customView = getCustomView(parent, message, type);
        layout.addView(customView);
        
        // Set up close button
        View closeButton = customView.findViewById(R.id.iv_snackbar_close);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> snackbar.dismiss());
        }
        
        // Show the Snackbar
        snackbar.show();
        currentSnackbar = snackbar;
    }
    
    /**
     * Get custom view based on Snackbar type
     */
    private static View getCustomView(View parent, String message, SnackbarType type) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View customView;
        
        switch (type) {
            case SUCCESS:
                customView = inflater.inflate(R.layout.snackbar_success, null);
                break;
            case WARNING:
                customView = inflater.inflate(R.layout.snackbar_warning, null);
                break;
            case DANGER:
                customView = inflater.inflate(R.layout.snackbar_danger, null);
                break;
            case INFO:
            default:
                customView = inflater.inflate(R.layout.snackbar_success, null); // Use success layout for info
                break;
        }
        
        // Set message text
        TextView messageView = customView.findViewById(R.id.tv_snackbar_message);
        if (messageView != null) {
            messageView.setText(message);
        }
        
        return customView;
    }
    
    /**
     * Dismiss current Snackbar
     */
    public static void dismissCurrent() {
        if (currentSnackbar != null && currentSnackbar.isShown()) {
            currentSnackbar.dismiss();
            currentSnackbar = null;
        }
    }
    
    /**
     * Show Snackbar with action button
     */
    public static void showWithAction(View parent, String message, SnackbarType type, 
                                    String actionText, View.OnClickListener actionListener) {
        showSnackbar(parent, message, type, Snackbar.LENGTH_INDEFINITE);
        
        // Add action button to current snackbar
        if (currentSnackbar != null) {
            currentSnackbar.setAction(actionText, actionListener);
            currentSnackbar.setActionTextColor(parent.getContext().getResources().getColor(R.color.white));
        }
    }
    
    /**
     * Show loading Snackbar
     */
    public static void showLoading(View parent, String message) {
        showSnackbar(parent, message, SnackbarType.INFO, Snackbar.LENGTH_INDEFINITE);
    }
    
    /**
     * Show network error Snackbar
     */
    public static void showNetworkError(View parent) {
        showDanger(parent, "Network error. Please check your connection.");
    }
    
    /**
     * Show login success Snackbar
     */
    public static void showLoginSuccess(View parent) {
        showSuccess(parent, "Login successful! Welcome back.");
    }
    
    /**
     * Show registration success Snackbar
     */
    public static void showRegistrationSuccess(View parent) {
        showSuccess(parent, "Registration successful! Welcome to MOON ENTERPRISES.");
    }
    
    /**
     * Show logout success Snackbar
     */
    public static void showLogoutSuccess(View parent) {
        showSuccess(parent, "Logged out successfully.");
    }
    
    /**
     * Show validation error Snackbar
     */
    public static void showValidationError(View parent, String field) {
        showDanger(parent, "Please enter a valid " + field + ".");
    }
    
    /**
     * Show API error Snackbar
     */
    public static void showApiError(View parent, String message) {
        showDanger(parent, "Error: " + message);
    }
    
    /**
     * Show generic error Snackbar
     */
    public static void showError(View parent, String message) {
        showDanger(parent, "Error: " + message);
    }

    // ==================== LOADING DIALOG METHODS ====================

    /**
     * Show loading dialog
     */
    public static void showLoading(Activity activity) {
        showLoading(activity, "Loading...");
    }

    /**
     * Show loading dialog with custom text
     */
    public static void showLoading(Activity activity, String message) {
        dismissCurrentLoading();
        currentLoadingDialog = LoadingDialog.show(activity, message);
    }

    /**
     * Update loading dialog text
     */
    public static void updateLoadingText(String message) {
        if (currentLoadingDialog != null) {
            currentLoadingDialog.setLoadingText(message);
        }
    }

    /**
     * Dismiss current loading dialog
     */
    public static void dismissCurrentLoading() {
        if (currentLoadingDialog != null && currentLoadingDialog.isShowing()) {
            currentLoadingDialog.dismiss();
            currentLoadingDialog = null;
        }
    }

    /**
     * Check if loading dialog is showing
     */
    public static boolean isLoadingShowing() {
        return currentLoadingDialog != null && currentLoadingDialog.isShowing();
    }
}
