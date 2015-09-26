// Copyright (c) kotemaru.org  (APL/2.0)
package org.kotemaru.android.slidelayout;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.FrameLayout;

public class SlideLayout extends FrameLayout {
    private View mInnerScrollView;
    private AbsListView mInnerScrollListView;
    private ViewGroup mSlideTarget;
    private int mMaxSlideSize;
    private float mLastY, mLastX;
    private float mOrgY, mOrgX;
    private boolean mIsSlidingMode = false;

    private int mAttrInnerScrollView = 0;
    private int mAttrSlideTarget = 0;

    enum SlideMode {
        NONE, HALF, PULL_DOWN, PULL_UP
    }


    public SlideLayout(Context context) {
        this(context, null, 0);
    }

    public SlideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideLayout);
        mAttrInnerScrollView = a.getResourceId(R.styleable.SlideLayout_innerScrollView,0);
        mAttrSlideTarget = a.getResourceId(R.styleable.SlideLayout_slideTarget,0);
        setMaxSlideSize((int) a.getDimension(R.styleable.SlideLayout_maxSlideSize, 50));
        a.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAttrInnerScrollView != 0) {
            setInnerScrollView(findViewById(mAttrInnerScrollView));
        }
        if (mAttrSlideTarget != 0) {
            Activity activity = (Activity) getContext();
            setSlideTarget((ViewGroup) activity.findViewById(mAttrSlideTarget));
        }
    }

    public void setInnerScrollView(View view) {
        mInnerScrollView = view;
        if (mInnerScrollView instanceof AbsListView) {
            mInnerScrollListView = (AbsListView) mInnerScrollView;
        }
    }

    public void setSlideTarget(ViewGroup viewGroup) {
        mSlideTarget = viewGroup;
    }

    public void setMaxSlideSize(int px) {
        mMaxSlideSize = px;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.e("DEBUG", "dispatchTouchEvent=" + ev.getRawY());
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //mLastX = ev.getRawX();
                //mLastY = ev.getRawY();
                mOrgX = ev.getRawX();
                mOrgY = ev.getRawY();
                //mIsSlidingMode = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = ev.getRawX() - mOrgX;
                mLastX = ev.getRawX();
                float deltaY = ev.getRawY() - mOrgY;
                mLastY = ev.getRawY();
                SlideMode mode = getSlideMode(deltaX, deltaY);
                if (mode == SlideMode.PULL_DOWN) {
                    MarginAnimation anim = new MarginAnimation(1);
                    mSlideTarget.startAnimation(anim);
                    mIsSlidingMode = true;
                } else if (mode == SlideMode.PULL_UP) {
                    MarginAnimation anim = new MarginAnimation(-1);
                    mSlideTarget.startAnimation(anim);
                    mIsSlidingMode = true;
                }
                //if (isSliding(deltaX, deltaY)) {
                //    doSlide(deltaY);
                //    mIsSlidingMode = true;
                //}
                //if (mIsSlidingMode) return true;
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        if (mIsSlidingMode) return true;
        return super.dispatchTouchEvent(ev);
    }



    private SlideMode getSlideMode(float deltaX, float deltaY) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mSlideTarget.getLayoutParams();
        int slideSize = params.topMargin;
        if (0 < slideSize && slideSize < mMaxSlideSize) return SlideMode.HALF;
        if (deltaY > mMaxSlideSize && !canChildScrollUp() && slideSize < mMaxSlideSize) return SlideMode.PULL_DOWN;
        if (deltaY < 0 && slideSize >= mMaxSlideSize) return SlideMode.PULL_UP;
        return SlideMode.NONE;
    }


    private int doSlide(float deltaY) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mSlideTarget.getLayoutParams();
        params.topMargin += deltaY;
        if (params.topMargin < 10) params.topMargin = 0;
        if (params.topMargin > mMaxSlideSize *0.8) params.topMargin = mMaxSlideSize;
        Log.e("DEBUG", "params.topMargin=" + deltaY + ":" + params.topMargin);
        mSlideTarget.setLayoutParams(params);
        return params.topMargin;
    }


    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mInnerScrollListView != null) {
                return mInnerScrollListView.getChildCount() > 0
                        && (mInnerScrollListView.getFirstVisiblePosition() > 0 || mInnerScrollListView.getChildAt(0).getTop() < mInnerScrollListView.getPaddingTop());
            } else {
                return mInnerScrollView.getScrollY() > 0;
            }
        } else {
            return mInnerScrollView.canScrollVertically(-1);
        }
    }

    private class MarginAnimation extends Animation implements Animation.AnimationListener {
        private int mDirection;

        public MarginAnimation(int direction) {
            mDirection = direction;
            setAnimationListener(this);
            setDuration(500);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mSlideTarget.getLayoutParams();
            if (mDirection>0) {
                params.topMargin = (int) (mMaxSlideSize * interpolatedTime);
            } else {
                params.topMargin = mMaxSlideSize - (int)(mMaxSlideSize * interpolatedTime);;
            }
            mSlideTarget.setLayoutParams(params);
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mSlideTarget.getLayoutParams();
            if (mDirection>0) {
                params.topMargin = mMaxSlideSize;
            } else {
                params.topMargin = 0;
            }
            mSlideTarget.setLayoutParams(params);
            mIsSlidingMode = false;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };
}
