package com.geo.enterprises.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Utility class for creating shimmer loading effects
 */
public class ShimmerView {
    
    private static final int SHIMMER_DURATION = 1500; // 1.5 seconds
    private static final float SHIMMER_ALPHA_MIN = 0.3f;
    private static final float SHIMMER_ALPHA_MAX = 1.0f;
    
    /**
     * Apply shimmer effect to a view
     * @param view The view to apply shimmer effect to
     */
    public static void applyShimmer(View view) {
        if (view == null) return;
        
        // Create alpha animation
        ObjectAnimator shimmerAnimator = ObjectAnimator.ofFloat(view, "alpha", 
            SHIMMER_ALPHA_MIN, SHIMMER_ALPHA_MAX, SHIMMER_ALPHA_MIN);
        
        shimmerAnimator.setDuration(SHIMMER_DURATION);
        shimmerAnimator.setRepeatCount(ValueAnimator.INFINITE);
        shimmerAnimator.setRepeatMode(ValueAnimator.RESTART);
        shimmerAnimator.setInterpolator(new LinearInterpolator());
        
        shimmerAnimator.start();
    }
    
    /**
     * Apply shimmer effect to multiple views with staggered delay
     * @param views Array of views to apply shimmer effect to
     * @param delay Delay between each view animation in milliseconds
     */
    public static void applyShimmerStaggered(View[] views, long delay) {
        if (views == null || views.length == 0) return;
        
        for (int i = 0; i < views.length; i++) {
            if (views[i] != null) {
                final int index = i; // Create final variable for lambda
                final View view = views[i]; // Create final variable for lambda
                view.postDelayed(() -> applyShimmer(view), delay * index);
            }
        }
    }
    
    /**
     * Stop shimmer effect on a view
     * @param view The view to stop shimmer effect on
     */
    public static void stopShimmer(View view) {
        if (view == null) return;
        
        view.clearAnimation();
        view.setAlpha(1.0f);
    }
    
    /**
     * Stop shimmer effect on multiple views
     * @param views Array of views to stop shimmer effect on
     */
    public static void stopShimmer(View[] views) {
        if (views == null) return;
        
        for (View view : views) {
            stopShimmer(view);
        }
    }
}
