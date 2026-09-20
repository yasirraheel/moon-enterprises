package com.geo.enterprises.utils;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Helper class to handle window insets and prevent UI from being covered by system bars
 */
public class WindowInsetsHelper {

    /**
     * Apply status bar padding to a view to prevent it from being covered
     * @param activity The activity
     * @param topView The top view that needs padding (usually the top bar/toolbar)
     */
    public static void applyStatusBarPadding(Activity activity, View topView) {
        if (activity == null || topView == null) return;
        
        ViewCompat.setOnApplyWindowInsetsListener(topView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            
            // Apply top padding equal to status bar height
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.topMargin = insets.top;
            v.setLayoutParams(params);
            
            return windowInsets;
        });
        
        // Request to apply window insets
        ViewCompat.requestApplyInsets(topView);
    }

    /**
     * Apply padding to a view including status bar and navigation bar insets
     * @param view The view to apply insets to
     */
    public static void applySystemBarInsets(View view) {
        if (view == null) return;
        
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            
            v.setPadding(
                v.getPaddingLeft() + insets.left,
                v.getPaddingTop() + insets.top,
                v.getPaddingRight() + insets.right,
                v.getPaddingBottom() + insets.bottom
            );
            
            return WindowInsetsCompat.CONSUMED;
        });
    }

    /**
     * Make the app draw behind the status bar (edge-to-edge)
     * @param activity The activity
     */
    public static void enableEdgeToEdge(Activity activity) {
        if (activity == null || activity.getWindow() == null) return;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 and above
            activity.getWindow().setDecorFitsSystemWindows(false);
        } else {
            // Android 10 and below
            activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }

    /**
     * Get the status bar height
     * @param activity The activity
     * @return Status bar height in pixels
     */
    public static int getStatusBarHeight(Activity activity) {
        if (activity == null) return 0;
        
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return activity.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * Apply status bar padding using a simpler approach (for activities without complex layouts)
     * 
     * @param activity The activity
     * @param topView The top view (toolbar/top bar)
     */
    public static void applyStatusBarPaddingSimple(Activity activity, View topView) {
        if (activity == null || topView == null) return;

        // Capture base padding once and apply dynamic status-bar inset on top.
        // This keeps top bars consistent across devices and avoids cumulative padding.
        final int baseLeft = topView.getPaddingLeft();
        final int baseTop = topView.getPaddingTop();
        final int baseRight = topView.getPaddingRight();
        final int baseBottom = topView.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(topView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
            int topInset = Math.max(0, insets.top);

            v.setPadding(
                baseLeft,
                baseTop + topInset,
                baseRight,
                baseBottom
            );

            return windowInsets;
        });

        ViewCompat.requestApplyInsets(topView);
    }

    /**
     * Apply responsive bottom navigation padding that adapts to different devices
     * Only adds padding when system navigation bar is actually present and overlapping
     * @param bottomView The bottom navigation view
     */
    public static void applyResponsiveBottomNavigationInsets(View bottomView) {
        if (bottomView == null) return;
        
        ViewCompat.setOnApplyWindowInsetsListener(bottomView, (v, windowInsets) -> {
            // Get actual navigation bar insets (this will be 0 if no nav bar or gesture navigation)
            Insets navBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());
            
            // Only apply padding if there's actually a navigation bar that could overlap
            int bottomPadding = navBarInsets.bottom;
            
            // Apply bottom margin only if needed
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.bottomMargin = bottomPadding;
            v.setLayoutParams(params);
            
            return windowInsets;
        });
        
        // Request to apply window insets
        ViewCompat.requestApplyInsets(bottomView);
    }

    /**
     * Check if device has system navigation bar (not gesture navigation)
     * @param activity The activity
     * @return true if device has navigation bar buttons
     */
    public static boolean hasSystemNavigationBar(Activity activity) {
        if (activity == null) return false;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ - Check if navigation bar is visible
            WindowInsets insets = activity.getWindow().getDecorView().getRootWindowInsets();
            if (insets != null) {
                return insets.getInsets(WindowInsets.Type.navigationBars()).bottom > 0;
            }
        }
        
        // Fallback for older versions
        int resourceId = activity.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            int navBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
            return navBarHeight > 0;
        }
        
        return false;
    }

    /**
     * Apply smart bottom navigation padding that only adds padding when necessary
     * @param activity The activity
     * @param bottomView The bottom navigation view
     */
    public static void applySmartBottomNavigationPadding(Activity activity, View bottomView) {
        if (activity == null || bottomView == null) return;
        
        // Use the responsive insets approach for dynamic handling
        // This will automatically detect if padding is needed based on actual system insets
        applyResponsiveBottomNavigationInsets(bottomView);
    }

    /**
     * Apply minimal bottom navigation padding (fallback for problematic devices)
     * Only adds a small padding if system nav bar is detected
     * @param activity The activity
     * @param bottomView The bottom navigation view
     */
    public static void applyMinimalBottomNavigationPadding(Activity activity, View bottomView) {
        if (activity == null || bottomView == null) return;
        
        ViewCompat.setOnApplyWindowInsetsListener(bottomView, (v, windowInsets) -> {
            Insets navBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());
            
            // Only apply minimal padding (max 16dp) if navigation bar is present
            int maxPadding = (int) (16 * activity.getResources().getDisplayMetrics().density); // 16dp in pixels
            int bottomPadding = Math.min(navBarInsets.bottom, maxPadding);
            
            if (bottomPadding > 0) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                params.bottomMargin = bottomPadding;
                v.setLayoutParams(params);
            }
            
            return windowInsets;
        });
        
        ViewCompat.requestApplyInsets(bottomView);
    }

    /**
     * Professional bottom navigation fix - handles all edge cases
     * Ensures bottom navigation is never hidden under system navigation bar
     * @param activity The activity
     * @param bottomView The bottom navigation view
     */
    public static void applyProfessionalBottomNavigationFix(Activity activity, View bottomView) {
        if (activity == null || bottomView == null) return;
        
        // Enable edge-to-edge for proper inset handling
        enableEdgeToEdge(activity);
        
        ViewCompat.setOnApplyWindowInsetsListener(bottomView, (v, windowInsets) -> {
            // Get navigation bar insets
            Insets navBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());
            Insets systemBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            
            // Calculate appropriate bottom padding
            int bottomPadding = Math.max(navBarInsets.bottom, systemBarInsets.bottom);
            
            // Apply padding using both margin and padding for maximum compatibility
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            
            // Use margin for layout positioning
            params.bottomMargin = bottomPadding;
            v.setLayoutParams(params);
            
            // Also apply internal padding for content spacing
            v.setPadding(
                v.getPaddingLeft(),
                v.getPaddingTop(),
                v.getPaddingRight(),
                Math.max(v.getPaddingBottom(), bottomPadding / 2) // Half padding internally
            );
            
            return windowInsets;
        });
        
        // Request to apply window insets immediately
        ViewCompat.requestApplyInsets(bottomView);
        
        // Force layout update
        bottomView.post(() -> {
            bottomView.requestLayout();
            bottomView.invalidate();
        });
    }
}
