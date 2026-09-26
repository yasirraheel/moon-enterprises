package com.geo.enterprises.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Custom view that draws an animated illuminating border (running light effect)
 * around its bounds using a rotating SweepGradient stroke.
 */
public class AnimatedBorderView extends View {

    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF borderRect = new RectF();
    private final Matrix gradientMatrix = new Matrix();
    private float rotationAngle = 0f;
    private ValueAnimator rotationAnimator;
    private float cornerRadius;
    private float borderWidth;
    private int accentColor = 0xFF10B981;
    private SweepGradient sweepGradient;
    private float centerX, centerY;

    public AnimatedBorderView(Context context) { super(context); init(); }
    public AnimatedBorderView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public AnimatedBorderView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        float density = getResources().getDisplayMetrics().density;
        borderWidth = 2f * density;
        cornerRadius = (12f * density) - (borderWidth / 2f);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidth);
        setClickable(false);
        setFocusable(false);
    }

    public void setCornerRadius(float radiusDp) {
        float density = getResources().getDisplayMetrics().density;
        this.cornerRadius = (radiusDp * density) - (borderWidth / 2f);
        invalidate();
    }

    public void setAccentColor(int color) {
        this.accentColor = color;
        buildGradient();
        invalidate();
    }

    private void buildGradient() {
        if (centerX <= 0 || centerY <= 0) return;

        int bright = accentColor;
        int glow = adjustAlpha(bright, 140);
        int faint = adjustAlpha(bright, 50);
        int dim = adjustAlpha(bright, 14);

        // Single bright arc (~25% of circumference) with subtle base border elsewhere
        int[] colors = { bright, glow, faint, dim, dim, dim, faint, glow, bright };
        float[] positions = { 0f, 0.05f, 0.13f, 0.22f, 0.42f, 0.72f, 0.87f, 0.95f, 1f };

        sweepGradient = new SweepGradient(centerX, centerY, colors, positions);
        borderPaint.setShader(sweepGradient);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float half = borderWidth / 2f;
        borderRect.set(half, half, w - half, h - half);
        centerX = w / 2f;
        centerY = h / 2f;
        buildGradient();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (sweepGradient == null || borderRect.isEmpty()) return;
        gradientMatrix.setRotate(rotationAngle, centerX, centerY);
        sweepGradient.setLocalMatrix(gradientMatrix);
        canvas.drawRoundRect(borderRect, cornerRadius, cornerRadius, borderPaint);
    }

    private int adjustAlpha(int color, int alpha) {
        return Color.argb(Math.min(alpha, 255), Color.red(color), Color.green(color), Color.blue(color));
    }

    public void startAnimation() {
        if (rotationAnimator != null && rotationAnimator.isRunning()) return;
        rotationAnimator = ValueAnimator.ofFloat(0f, 360f);
        rotationAnimator.setDuration(3000);
        rotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnimator.setInterpolator(new LinearInterpolator());
        rotationAnimator.addUpdateListener(a -> {
            rotationAngle = (float) a.getAnimatedValue();
            invalidate();
        });
        rotationAnimator.start();
    }

    public void stopAnimation() {
        if (rotationAnimator != null) {
            rotationAnimator.cancel();
            rotationAnimator = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }
}
