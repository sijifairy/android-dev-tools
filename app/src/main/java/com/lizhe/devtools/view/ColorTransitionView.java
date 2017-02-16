package com.lizhe.devtools.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import com.lizhe.devtools.utils.CommonUtils;

public class ColorTransitionView extends View {

    private int[] colorArr = new int[]{
            0xff3F51B5,
            0xff008577,
            0xff9a7970
    };
    private int mTransitionDuration = 325;
    private int mIndex = 0;
    private ValueAnimator mAnimator;
    private int mRadius;

    private int mWidth;
    private int mHeight;
    private int mPaddingBottom;
    private RectF mRectF;
    private Paint mPaint;
    private Paint mPaintTransition;

    public ColorTransitionView(Context context) {
        this(context, null);
    }

    public ColorTransitionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorTransitionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // cache view width and height.
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);

                mWidth = getMeasuredWidth();
                mHeight = getMeasuredHeight();
                mRectF = new RectF(0, 0, mWidth, mHeight);
                mPaddingBottom = CommonUtils.hasNavBar(getContext()) ?
                        CommonUtils.getNavigationBarHeight(getContext()) : 0;
            }
        });

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(colorArr[0]);
        mPaintTransition = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    }

    /**
     * Do color transition animation.
     *
     * @param index view index which is newly activited.
     */
    public void startColorTransition(int index) {
        if (index == mIndex) {
            return;
        }

        mPaintTransition.setColor(colorArr[index]);
        mIndex = index;

        mAnimator = ValueAnimator.ofInt(0, Math.max(mWidth, mHeight));
        mAnimator.setDuration(mTransitionDuration);
        mAnimator.setInterpolator(new AccelerateInterpolator(1.5f));
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRadius = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPaint.setColor(colorArr[mIndex]);
                mAnimator = null;
            }
        });
        mAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw bg color
        canvas.drawRect(mRectF, mPaint);
        //draw color transition
        if (mAnimator != null && mAnimator.isRunning()) {
            canvas.drawCircle(mWidth / colorArr.length * mIndex + mWidth / colorArr.length / 2,
                    (mHeight - mPaddingBottom) / 2, mRadius, mPaintTransition);
        }
    }
}
