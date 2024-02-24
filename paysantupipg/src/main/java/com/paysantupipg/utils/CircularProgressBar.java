package com.paysantupipg.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;

import com.paysantupipg.R;


public class CircularProgressBar extends View {

    private static final float DEFAULT_SIZE_DP = 48f;
    private static final float DEFAULT_MAXIMUM = 100f;
    private static final float DEFAULT_PROGRESS = 0f;
    private static final float DEFAULT_FOREGROUND_STROKE_WIDTH_DP = 3f;
    private static final float DEFAULT_BACKGROUND_STROKE_WIDTH_DP = 1f;
    private static final float DEFAULT_START_ANGLE = 270f;
    private static final float DEFAULT_INDETERMINATE_MINIMUM_ANGLE = 60f;
    private static final int DEFAULT_FOREGROUND_STROKE_CAP = 0;
    private static final int DEFAULT_FOREGROUND_STROKE_COLOR = Color.BLUE;
    private static final int DEFAULT_BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final int DEFAULT_PROGRESS_ANIMATION_DURATION = 100;
    private static final int DEFAULT_INDETERMINATE_ROTATION_ANIMATION_DURATION = 1200;
    private static final int DEFAULT_INDETERMINATE_SWEEP_ANIMATION_DURATION = 600;
    private static final boolean DEFAULT_ANIMATE_PROGRESS = true;
    private static final boolean DEFAULT_DRAW_BACKGROUND_STROKE = false;
    private static final boolean DEFAULT_INDETERMINATE = false;
    private final Runnable mSweepRestartAction = new SweepRestartAction();
    private final RectF mDrawRect = new RectF();
    private final ValueAnimator mProgressAnimator = new ValueAnimator();
    private final ValueAnimator mIndeterminateStartAnimator = new ValueAnimator();
    private final ValueAnimator mIndeterminateSweepAnimator = new ValueAnimator();
    private final Paint mForegroundStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mBackgroundStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mDefaultSize = 0;
    private float mMaximum = 0f;
    private float mProgress = 0f;
    private float mStartAngle = 0f;
    private float mIndeterminateStartAngle = 0f;
    private float mIndeterminateSweepAngle = 0f;
    private float mIndeterminateOffsetAngle = 0f;
    private float mIndeterminateMinimumAngle = 0f;
    private float mForegroundStrokeCapAngle = 0f;
    private boolean mIndeterminate = false;
    private boolean mAnimateProgress = false;
    private boolean mDrawBackgroundStroke = false;
    private boolean mIndeterminateGrowMode = false;
    private boolean mVisible = false;

    public CircularProgressBar(@NonNull final Context context) {
        super(context);
        initialize(context, null, 0, 0);
    }

    public CircularProgressBar(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0, 0);
    }

    public CircularProgressBar(@NonNull final Context context, @Nullable final AttributeSet attrs,
                               final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public CircularProgressBar(@NonNull final Context context, @Nullable final AttributeSet attrs,
                               final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Indeterminate mode
     */
    public boolean isIndeterminate() {
        return mIndeterminate;
    }

    /**
     * Indeterminate mode, disabled by default
     */
    public void setIndeterminate(final boolean indeterminate) {
        stopIndeterminateAnimations();
        mIndeterminate = indeterminate;
        invalidate();
        if (mVisible && indeterminate) {
            startIndeterminateAnimations();
        }
    }

    /**
     * Get current progress value for non-indeterminate mode
     */
    public float getProgress() {
        return mProgress;
    }

    /**
     * Set current progress value for non-indeterminate mode
     */
    public void setProgress(final float progress) {
        if (mIndeterminate) {
            mProgress = progress;
        } else {
            stopProgressAnimation();
            if (mVisible && mAnimateProgress) {
                setProgressAnimated(progress);
            } else {
                setProgressInternal(progress);
            }
        }
    }

    /**
     * Maximum progress for non-indeterminate mode
     */
    public float getMaximum() {
        return mMaximum;
    }

    /**
     * Maximum progress for non-indeterminate mode
     */
    public void setMaximum(final float maximum) {
        mMaximum = maximum;
        invalidate();
    }

    /**
     * Start angle for non-indeterminate mode, between -360 and 360 degrees
     */
    @FloatRange(from = -360f, to = 360f)
    public float getStartAngle() {
        return mStartAngle;
    }

    /**
     * Start angle for non-indeterminate mode, between -360 and 360 degrees
     */
    public void setStartAngle(@FloatRange(from = -360f, to = 360f) final float angle) {
        checkStartAngle(angle);
        mStartAngle = angle;
        invalidate();
    }

    /**
     * Whether to animate progress for non-indeterminate mode
     */
    public boolean isAnimateProgress() {
        return mAnimateProgress;
    }

    /**
     * Whether to animate progress for non-indeterminate mode
     */
    public void setAnimateProgress(final boolean animate) {
        mAnimateProgress = animate;
    }

    /**
     * Progress animation duration for non-indeterminate mode (in milliseconds)
     */
    @IntRange(from = 0)
    public long getProgressAnimationDuration() {
        return mProgressAnimator.getDuration();
    }

    /**
     * Progress animation duration for non-indeterminate mode (in milliseconds)
     */
    public void setProgressAnimationDuration(@IntRange(from = 0) final long duration) {
        checkAnimationDuration(duration);
        if (mVisible) {
            if (mProgressAnimator.isRunning()) {
                mProgressAnimator.end();
            }
        }
        mProgressAnimator.setDuration(duration);
    }

    /**
     * Minimum angle for indeterminate mode, between 0 and 180 degrees
     */
    @FloatRange(from = 0f, to = 180f)
    public float getIndeterminateMinimumAngle() {
        return mIndeterminateMinimumAngle;
    }

    /**
     * Minimum angle for indeterminate mode, between 0 and 180 degrees
     */
    public void setIndeterminateMinimumAngle(@FloatRange(from = 0f, to = 180f) final float angle) {
        checkIndeterminateMinimumAngle(angle);
        stopIndeterminateAnimations();
        mIndeterminateMinimumAngle = angle;
        mIndeterminateSweepAnimator.setFloatValues(360f - angle * 2f);
        invalidate();
        if (mVisible && mIndeterminate) {
            startIndeterminateAnimations();
        }
    }

    /**
     * Rotation animation duration for indeterminate mode (in milliseconds)
     */
    @IntRange(from = 0)
    public long getIndeterminateRotationAnimationDuration() {
        return mIndeterminateStartAnimator.getDuration();
    }

    /**
     * Rotation animation duration for indeterminate mode (in milliseconds)
     */
    public void setIndeterminateRotationAnimationDuration(@IntRange(from = 0) final long duration) {
        checkAnimationDuration(duration);
        stopIndeterminateAnimations();
        mIndeterminateStartAnimator.setDuration(duration);
        invalidate();
        if (mVisible && mIndeterminate) {
            startIndeterminateAnimations();
        }
    }

    /**
     * Sweep animation duration for indeterminate mode (in milliseconds)
     */
    @IntRange(from = 0)
    public long getIndeterminateSweepAnimationDuration() {
        return mIndeterminateSweepAnimator.getDuration();
    }

    /**
     * Sweep animation duration for indeterminate mode (in milliseconds)
     */
    public void setIndeterminateSweepAnimationDuration(@IntRange(from = 0) final long duration) {
        checkAnimationDuration(duration);
        stopIndeterminateAnimations();
        mIndeterminateSweepAnimator.setDuration(duration);
        invalidate();
        if (mVisible && mIndeterminate) {
            startIndeterminateAnimations();
        }
    }

    /**
     * Foreground stroke cap
     */
    @NonNull
    public Paint.Cap getForegroundStrokeCap() {
        return mBackgroundStrokePaint.getStrokeCap();
    }

    /**
     * Foreground stroke cap
     */
    public void setForegroundStrokeCap(@NonNull final Paint.Cap cap) {
        mForegroundStrokePaint.setStrokeCap(cap);
        invalidateForegroundStrokeCapAngle();
        invalidate();
    }

    /**
     * Foreground stroke color
     */
    @ColorInt
    public int getForegroundStrokeColor() {
        return mForegroundStrokePaint.getColor();
    }

    /**
     * Foreground stroke color
     */
    public void setForegroundStrokeColor(@ColorInt final int color) {
        mForegroundStrokePaint.setColor(color);
        invalidate();
    }

    /**
     * Foreground stroke width (in pixels)
     */
    @FloatRange(from = 0f, to = Float.MAX_VALUE)
    public float getForegroundStrokeWidth() {
        return mForegroundStrokePaint.getStrokeWidth();
    }

    /**
     * Foreground stroke width (in pixels)
     */
    public void setForegroundStrokeWidth(@FloatRange(from = 0f, to = Float.MAX_VALUE) final float width) {
        checkWidth(width);
        mForegroundStrokePaint.setStrokeWidth(width);
        invalidateDrawRect();
        invalidate();
    }

    /**
     * Background stroke color
     */
    @ColorInt
    public int getBackgroundStrokeColor() {
        return mBackgroundStrokePaint.getColor();
    }

    /**
     * Background stroke color
     */
    public void setBackgroundStrokeColor(@ColorInt final int color) {
        mBackgroundStrokePaint.setColor(color);
        invalidate();
    }

    /**
     * Background stroke width (in pixels)
     */
    @FloatRange(from = 0f, to = Float.MAX_VALUE)
    public float getBackgroundStrokeWidth() {
        return mBackgroundStrokePaint.getStrokeWidth();
    }

    /**
     * Background stroke width (in pixels)
     */
    public void setBackgroundStrokeWidth(@FloatRange(from = 0f, to = Float.MAX_VALUE) final float width) {
        checkWidth(width);
        mBackgroundStrokePaint.setStrokeWidth(width);
        invalidateDrawRect();
        invalidate();
    }

    /**
     * Whether to draw background stroke
     */
    public boolean isDrawBackgroundStroke() {
        return mDrawBackgroundStroke;
    }

    /**
     * Whether to draw background stroke
     */
    public void setDrawBackgroundStroke(final boolean draw) {
        mDrawBackgroundStroke = draw;
        invalidateDrawRect();
        invalidate();
    }

    @Override
    public void onVisibilityAggregated(final boolean visible) {
        super.onVisibilityAggregated(visible);
        mVisible = visible;
        if (mIndeterminate) {
            if (visible) {
                startIndeterminateAnimations();
            } else {
                stopIndeterminateAnimations();
            }
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (mDrawBackgroundStroke) {
            canvas.drawOval(mDrawRect, mBackgroundStrokePaint);
        }
        float start;
        float sweep;
        if (mIndeterminate) {
            final float startAngle = mIndeterminateStartAngle;
            final float sweepAngle = mIndeterminateSweepAngle;
            final float offsetAngle = mIndeterminateOffsetAngle;
            final float minimumAngle = mIndeterminateMinimumAngle;
            if (mIndeterminateGrowMode) {
                start = startAngle - offsetAngle;
                sweep = sweepAngle + minimumAngle;
            } else {
                start = startAngle + sweepAngle - offsetAngle;
                sweep = 360f - sweepAngle - minimumAngle;
            }
        } else {
            final float maximum = mMaximum;
            final float progress = mProgress;
            start = mStartAngle;
            if (Math.abs(progress) < Math.abs(maximum)) {
                sweep = progress / maximum * 360f;
            } else {
                sweep = 360f;
            }
        }
        final float capAngle = mForegroundStrokeCapAngle;
        if (capAngle != 0f && Math.abs(sweep) != 360f) {
            if (sweep > 0) {
                start += capAngle;
                sweep -= capAngle * 2f;
                if (sweep < 0.0001f) {
                    sweep = 0.0001f;
                }
            } else if (sweep < 0) {
                start -= capAngle;
                sweep += capAngle * 2f;
                if (sweep > -0.0001f) {
                    sweep = -0.0001f;
                }
            }
        }
        canvas.drawArc(mDrawRect, start, sweep, false, mForegroundStrokePaint);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int defaultSize = mDefaultSize;
        final int defaultWidth = Math.max(getSuggestedMinimumWidth(), defaultSize);
        final int defaultHeight = Math.max(getSuggestedMinimumHeight(), defaultSize);
        final int width;
        final int height;
        switch (widthMode) {
            case MeasureSpec.EXACTLY: {
                width = widthSize;
                break;
            }
            case MeasureSpec.AT_MOST: {
                width = Math.min(defaultWidth, widthSize);
                break;
            }
            case MeasureSpec.UNSPECIFIED:
            default: {
                width = defaultWidth;
                break;
            }
        }
        switch (heightMode) {
            case MeasureSpec.EXACTLY: {
                height = heightSize;
                break;
            }
            case MeasureSpec.AT_MOST: {
                height = Math.min(defaultHeight, heightSize);
                break;
            }
            case MeasureSpec.UNSPECIFIED:
            default: {
                height = defaultHeight;
                break;
            }
        }
        setMeasuredDimension(width, height);
        invalidateDrawRect(width, height);
    }

    @Override
    protected void onSizeChanged(final int width, final int height, final int oldWidth, final int oldHeight) {
        invalidateDrawRect(width, height);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mVisible = true;
        if (mIndeterminate) {
            startIndeterminateAnimations();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVisible = false;
        stopIndeterminateAnimations();
        stopProgressAnimation();
    }

    private void initialize(@NonNull final Context context, @Nullable final AttributeSet attributeSet,
                            @AttrRes final int defStyleAttr, @StyleRes final int defStyleRes) {
        mForegroundStrokePaint.setStyle(Paint.Style.STROKE);
        mBackgroundStrokePaint.setStyle(Paint.Style.STROKE);
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mDefaultSize = Math.round(DEFAULT_SIZE_DP * displayMetrics.density);
        if (attributeSet == null) {
            mMaximum = DEFAULT_MAXIMUM;
            mProgress = DEFAULT_PROGRESS;
            mStartAngle = DEFAULT_START_ANGLE;
            mIndeterminateMinimumAngle = DEFAULT_INDETERMINATE_MINIMUM_ANGLE;
            mProgressAnimator.setDuration(DEFAULT_PROGRESS_ANIMATION_DURATION);
            mIndeterminate = DEFAULT_INDETERMINATE;
            mAnimateProgress = DEFAULT_ANIMATE_PROGRESS;
            mDrawBackgroundStroke = DEFAULT_DRAW_BACKGROUND_STROKE;
            mForegroundStrokePaint.setColor(DEFAULT_FOREGROUND_STROKE_COLOR);
            mForegroundStrokePaint
                    .setStrokeWidth(Math.round(DEFAULT_FOREGROUND_STROKE_WIDTH_DP * displayMetrics.density));
            mForegroundStrokePaint.setStrokeCap(getStrokeCap(DEFAULT_FOREGROUND_STROKE_CAP));
            mBackgroundStrokePaint.setColor(DEFAULT_BACKGROUND_STROKE_COLOR);
            mBackgroundStrokePaint
                    .setStrokeWidth(Math.round(DEFAULT_BACKGROUND_STROKE_WIDTH_DP * displayMetrics.density));
            mIndeterminateStartAnimator.setDuration(DEFAULT_INDETERMINATE_ROTATION_ANIMATION_DURATION);
            mIndeterminateSweepAnimator.setDuration(DEFAULT_INDETERMINATE_SWEEP_ANIMATION_DURATION);
        } else {
            TypedArray attributes = null;
            try {
                attributes = context.getTheme()
                        .obtainStyledAttributes(attributeSet, R.styleable.CircularProgressBar, defStyleAttr,
                                defStyleRes);
                mMaximum = attributes.getFloat(R.styleable.CircularProgressBar_maximum, DEFAULT_MAXIMUM);
                mProgress = attributes.getFloat(R.styleable.CircularProgressBar_progress, DEFAULT_PROGRESS);
                final float startAngle =
                        attributes.getFloat(R.styleable.CircularProgressBar_startAngle, DEFAULT_START_ANGLE);
                checkStartAngle(startAngle);
                mStartAngle = startAngle;
                final float minimumAngle = attributes
                        .getFloat(R.styleable.CircularProgressBar_indeterminateMinimumAngle,
                                DEFAULT_INDETERMINATE_MINIMUM_ANGLE);
                checkIndeterminateMinimumAngle(minimumAngle);
                mIndeterminateMinimumAngle = minimumAngle;
                final long progressDuration = attributes
                        .getInteger(R.styleable.CircularProgressBar_progressAnimationDuration,
                                DEFAULT_PROGRESS_ANIMATION_DURATION);
                checkAnimationDuration(progressDuration);
                mProgressAnimator.setDuration(progressDuration);
                final long rotationDuration = attributes
                        .getInteger(R.styleable.CircularProgressBar_indeterminateRotationAnimationDuration,
                                DEFAULT_INDETERMINATE_ROTATION_ANIMATION_DURATION);
                checkAnimationDuration(rotationDuration);
                mIndeterminateStartAnimator.setDuration(rotationDuration);
                final long sweepDuration = attributes
                        .getInteger(R.styleable.CircularProgressBar_indeterminateSweepAnimationDuration,
                                DEFAULT_INDETERMINATE_SWEEP_ANIMATION_DURATION);
                checkAnimationDuration(sweepDuration);
                mIndeterminateSweepAnimator.setDuration(sweepDuration);
                mForegroundStrokePaint.setColor(attributes
                        .getColor(R.styleable.CircularProgressBar_foregroundStrokeColor,
                                DEFAULT_FOREGROUND_STROKE_COLOR));
                mBackgroundStrokePaint.setColor(attributes
                        .getColor(R.styleable.CircularProgressBar_backgroundStrokeColor,
                                DEFAULT_BACKGROUND_STROKE_COLOR));
                final float foregroundWidth = attributes
                        .getDimension(R.styleable.CircularProgressBar_foregroundStrokeWidth,
                                Math.round(DEFAULT_FOREGROUND_STROKE_WIDTH_DP * displayMetrics.density));
                checkWidth(foregroundWidth);
                mForegroundStrokePaint.setStrokeWidth(foregroundWidth);
                mForegroundStrokePaint.setStrokeCap(getStrokeCap(attributes
                        .getInt(R.styleable.CircularProgressBar_foregroundStrokeCap, DEFAULT_FOREGROUND_STROKE_CAP)));
                final float backgroundWidth = attributes
                        .getDimension(R.styleable.CircularProgressBar_backgroundStrokeWidth,
                                Math.round(DEFAULT_BACKGROUND_STROKE_WIDTH_DP * displayMetrics.density));
                checkWidth(backgroundWidth);
                mBackgroundStrokePaint.setStrokeWidth(backgroundWidth);
                mAnimateProgress = attributes
                        .getBoolean(R.styleable.CircularProgressBar_animateProgress, DEFAULT_ANIMATE_PROGRESS);
                mDrawBackgroundStroke = attributes.getBoolean(R.styleable.CircularProgressBar_drawBackgroundStroke,
                        DEFAULT_DRAW_BACKGROUND_STROKE);
                mIndeterminate =
                        attributes.getBoolean(R.styleable.CircularProgressBar_indeterminate, DEFAULT_INDETERMINATE);
            } finally {
                if (attributes != null) {
                    attributes.recycle();
                }
            }
        }
        mProgressAnimator.setInterpolator(new DecelerateInterpolator());
        mProgressAnimator.addUpdateListener(new ProgressUpdateListener());
        mIndeterminateStartAnimator.setFloatValues(360f);
        mIndeterminateStartAnimator.setRepeatMode(ValueAnimator.RESTART);
        mIndeterminateStartAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mIndeterminateStartAnimator.setInterpolator(new LinearInterpolator());
        mIndeterminateStartAnimator.addUpdateListener(new StartUpdateListener());
        mIndeterminateSweepAnimator.setFloatValues(360f - mIndeterminateMinimumAngle * 2f);
        mIndeterminateSweepAnimator.setInterpolator(new DecelerateInterpolator());
        mIndeterminateSweepAnimator.addUpdateListener(new SweepUpdateListener());
        mIndeterminateSweepAnimator.addListener(new SweepAnimatorListener());
    }

    private void invalidateDrawRect() {
        final int width = getWidth();
        final int height = getHeight();
        if (width > 0 && height > 0) {
            invalidateDrawRect(width, height);
        }
    }

    private void invalidateDrawRect(final int width, final int height) {
        final float thickness;
        if (mDrawBackgroundStroke) {
            thickness = Math.max(mForegroundStrokePaint.getStrokeWidth(), mBackgroundStrokePaint.getStrokeWidth());
        } else {
            thickness = mForegroundStrokePaint.getStrokeWidth();
        }
        if (width > height) {
            final float offset = (width - height) / 2f;
            mDrawRect.set(offset + thickness / 2f + 1f, thickness / 2f + 1f, width - offset - thickness / 2f - 1f,
                    height - thickness / 2f - 1f);
        } else if (width < height) {
            final float offset = (height - width) / 2f;
            mDrawRect.set(thickness / 2f + 1f, offset + thickness / 2f + 1f, width - thickness / 2f - 1f,
                    height - offset - thickness / 2f - 1f);
        } else {
            mDrawRect.set(thickness / 2f + 1f, thickness / 2f + 1f, width - thickness / 2f - 1f,
                    height - thickness / 2f - 1f);
        }
        invalidateForegroundStrokeCapAngle();
    }

    private void invalidateForegroundStrokeCapAngle() {
        final Paint.Cap strokeCap = mForegroundStrokePaint.getStrokeCap();
        if (strokeCap == null) {
            mForegroundStrokeCapAngle = 0f;
            return;
        }
        switch (strokeCap) {
            case SQUARE:
            case ROUND: {
                final float r = mDrawRect.width() / 2f;
                if (r != 0) {
                    mForegroundStrokeCapAngle = 90f * mForegroundStrokePaint.getStrokeWidth() / (float) Math.PI / r;
                } else {
                    mForegroundStrokeCapAngle = 0f;
                }
                break;
            }
            case BUTT:
            default: {
                mForegroundStrokeCapAngle = 0f;
                break;
            }
        }
    }

    private void setProgressInternal(final float progress) {
        mProgress = progress;
        invalidate();
    }

    private void setProgressAnimated(final float progress) {
        mProgressAnimator.setFloatValues(mProgress, progress);
        mProgressAnimator.start();
    }

    private void stopProgressAnimation() {
        if (mProgressAnimator.isRunning()) {
            mProgressAnimator.cancel();
        }
    }

    private void stopIndeterminateAnimations() {
        if (mIndeterminateStartAnimator.isRunning()) {
            mIndeterminateStartAnimator.cancel();
        }
        if (mIndeterminateSweepAnimator.isRunning()) {
            mIndeterminateSweepAnimator.cancel();
        }
    }

    private void startIndeterminateAnimations() {
        if (!mIndeterminateStartAnimator.isRunning()) {
            mIndeterminateStartAnimator.start();
        }
        if (!mIndeterminateSweepAnimator.isRunning()) {
            mIndeterminateSweepAnimator.start();
        }
    }

    private static void checkStartAngle(final float angle) {
        if (angle < -360f || angle > 360f) {
            throw new IllegalArgumentException("Start angle value should be between -360 and 360 degrees (inclusive)");
        }
    }

    private static void checkIndeterminateMinimumAngle(final float angle) {
        if (angle < 0f || angle > 180f) {
            throw new IllegalArgumentException(
                    "Indeterminate minimum angle value should be between 0 and 180 degrees (inclusive)");
        }
    }

    private static void checkAnimationDuration(final long duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("Animation duration can't be negative");
        }
    }

    private static void checkWidth(final float width) {
        if (width < 0f) {
            throw new IllegalArgumentException("Width can't be negative");
        }
    }

    @NonNull
    private static Paint.Cap getStrokeCap(final int value) {
        switch (value) {
            case 2: {
                return Paint.Cap.SQUARE;
            }
            case 1: {
                return Paint.Cap.ROUND;
            }
            case 0:
            default: {
                return Paint.Cap.BUTT;
            }
        }
    }

    private final class ProgressUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(final ValueAnimator animation) {
            setProgressInternal(((Number) animation.getAnimatedValue()).floatValue());
        }
    }

    private final class StartUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(final ValueAnimator animation) {
            mIndeterminateStartAngle = ((Number) animation.getAnimatedValue()).floatValue();
            invalidate();
        }
    }

    private final class SweepUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(final ValueAnimator animation) {
            mIndeterminateSweepAngle = ((Number) animation.getAnimatedValue()).floatValue();
        }
    }

    private final class SweepAnimatorListener implements ValueAnimator.AnimatorListener {
        private boolean mCancelled;

        @Override
        public void onAnimationStart(final Animator animation) {
            mCancelled = false;
        }

        @Override
        public void onAnimationEnd(final Animator animation) {
            if (!mCancelled) {
                post(mSweepRestartAction);
            }
        }

        @Override
        public void onAnimationCancel(final Animator animation) {
            mCancelled = true;
        }

        @Override
        public void onAnimationRepeat(final Animator animation) {
            // Do nothing
        }
    }

    private final class SweepRestartAction implements Runnable {
        @Override
        public void run() {
            mIndeterminateGrowMode = !mIndeterminateGrowMode;
            if (mIndeterminateGrowMode) {
                mIndeterminateOffsetAngle = (mIndeterminateOffsetAngle + mIndeterminateMinimumAngle * 2f) % 360f;
            }
            if (mIndeterminateSweepAnimator.isRunning()) {
                mIndeterminateSweepAnimator.cancel();
            }
            if (mVisible) {
                mIndeterminateSweepAnimator.start();
            }
        }
    }
}