package com.geo.enterprises.utils;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Expands to its full content height when placed inside a parent scroll container.
 */
public class ExpandedRecyclerView extends RecyclerView {

    public ExpandedRecyclerView(@NonNull Context context) {
        super(context);
        init();
    }

    public ExpandedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExpandedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setNestedScrollingEnabled(false);
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int expandedHeightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, expandedHeightSpec);
    }
}
