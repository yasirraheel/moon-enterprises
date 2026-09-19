package com.geo.enterprises.utils;

import android.app.Activity;
import android.content.Intent;

import com.geo.enterprises.R;

public class ActivityTransitionUtils {

    public static void slideInRight(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void slideInLeft(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public static void fadeIn(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void slideInRightAndFinish(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void slideInLeftAndFinish(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public static void fadeInAndFinish(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void slideOutRight(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public static void slideOutLeft(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void fadeOut(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
