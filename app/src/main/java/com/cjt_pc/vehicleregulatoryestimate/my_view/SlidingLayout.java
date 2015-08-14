package com.cjt_pc.vehicleregulatoryestimate.my_view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * Created by cjt-pc on 2015/7/27.
 * Email:879309896@qq.com
 */
public class SlidingLayout extends FrameLayout {

    // 滚动显示和隐藏menu时，手指滑动需要达到的速度
    public static final int SNAP_VELOCITY = 400;
    // 手指横向滑动临界距离，判断滑动类型
    public static final int SCROLL_DIS = 20;
    // 上下文
    private Context mContext;
    // 左中右三个layout
    private BaseSlideLayout leftSlideLayout, middleSlideLayout, rightSlideLayout;
    // 中间内容的“面罩”
    private BaseSlideLayout maskLayout;
    // 是否开启滑动渐变效果，默认为true
    private boolean isOnAlpha = true;
    // 渐变程度，1代表满足条件时完全不透明
    private float alphaRate = 0.5f;
    // 手指按下的坐标
    private int downX, downY;
    // 当前手指触摸屏幕的点
    private Point point;
    // 用于计算手指滑动的速度
    private VelocityTracker mVelocityTracker;
    // 滚动控制器
    public Scroller mScroller;
    // 手指移动类型是否为横向滑动
    private boolean isLeftRight = false;
    // 是否计算了滑动类型
    private boolean isCalTyped = false;
    // 是否屏蔽所有事件
    private boolean isIntercept = false;
    // 手指是否抬起
    private boolean fingerUp = true;
    // 视图移动的距离范围，注意这是偏移量，正负与坐标相反
    private int minX = 0, maxX = 0;
    // 侧边菜单的宽度比例，默认为主界面的0.8
    private double widthRate = 0.8;
    // 侧边菜单的宽度
    public int menuWidth = 0;

    public SlidingLayout(Context context) {
        super(context);
        mContext = context;
        mScroller = new Scroller(context, new DecelerateInterpolator());
        point = new Point();
        // 设置clickable可以使dispatchTouchEvent恒为true
        this.setClickable(true);
    }

    public SlidingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int finalX = 0;
        // 分别给存在的layout设置大小
        if (middleSlideLayout != null) {
            middleSlideLayout.measure(widthMeasureSpec, heightMeasureSpec);
            maskLayout.measure(widthMeasureSpec, heightMeasureSpec);
            menuWidth = (int) (middleSlideLayout.getMeasuredWidth() * widthRate);
            finalX = (MeasureSpec.makeMeasureSpec(menuWidth, MeasureSpec.EXACTLY));
        }

        if (leftSlideLayout != null) {
            leftSlideLayout.measure(finalX, heightMeasureSpec);
        }
        if (rightSlideLayout != null) {
            rightSlideLayout.measure(finalX, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (middleSlideLayout != null) {
            middleSlideLayout.layout(l, t, r, b);
            maskLayout.layout(l, t, r, b);
        }
        if (leftSlideLayout != null) {
            leftSlideLayout.layout(l - leftSlideLayout.getMeasuredWidth(), t, l, b);
            minX = -leftSlideLayout.getMeasuredWidth();
        }
        if (rightSlideLayout != null) {
            rightSlideLayout.layout(r, t, r + rightSlideLayout.getMeasuredWidth(), b);
            maxX = rightSlideLayout.getMeasuredWidth();
        }
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        createVelocityTracker(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fingerUp = false;
                downX = (int) ev.getX();
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dX = (int) (ev.getX() - point.x);
                // 当滑动距离超过了计算滑动类型最小值，判断是否为左右滑动，只计算一次
                if (!isCalTyped && ((Math.abs((int) ev.getX() - downX) >= SCROLL_DIS) ||
                        Math.abs((int) ev.getY() - downY) >= SCROLL_DIS)) {
                    if (Math.abs(ev.getX() - downX) > Math.abs(ev.getY() - downY)) {
                        isLeftRight = true;
                    }
                    isCalTyped = true;
                }
                if (isLeftRight) {
                    isIntercept = true;
                    int expectX = getScrollX() - dX;
                    // 左右滑动的最小和最大值
                    if (expectX >= minX && expectX <= maxX) {
                        // 只有视图在滑动的时候让当前视图屏蔽掉所有控件事件
                        // 滚动视图到指定点
                        scrollBy(-dX, 0);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                fingerUp = true;
                isLeftRight = false;
                isCalTyped = false;
                beginStart();
                recycleVelocityTracker();
                break;
            default:
                break;
        }
        point.x = (int) ev.getX();
        point.y = (int) ev.getY();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        if (isOnAlpha) {
            int curScroX = Math.abs(getScrollX());
            float scale = curScroX / (float) menuWidth;
            if (middleSlideLayout != null) {
                maskLayout.setAlpha(scale * alphaRate);
            }
        }
    }

    // 手指抬起时判断情况滚动视图到指定点
    private void beginStart() {
        int curScroX = getScrollX();
        int cpX = menuWidth >> 1;
        int moveSp = getScrollVelocity();
        // 当手指移动速度满足要求时换一种判断方式
        if (Math.abs(moveSp) < SNAP_VELOCITY) {
            if (curScroX >= -cpX && curScroX < 0) {//左侧菜单缩进
                LToM();
            } else if (curScroX < -cpX) {//左侧菜单展出
                MToL();
            } else if (curScroX > 0 && curScroX <= cpX) {//右侧菜单缩进
                RToM();
            } else if (curScroX > cpX) {//右侧菜单展出
                MToR();
            }
        } else {
            if (moveSp < 0 && getScrollX() < 0 && getScrollX() > -menuWidth) {
                LToM();
            } else if (moveSp > 0 && getScrollX() < 0 && getScrollX() > -menuWidth) {
                MToL();
            } else if (moveSp > 0 && getScrollX() > 0 && getScrollX() < menuWidth) {
                RToM();
            } else if (moveSp < 0 && getScrollX() > 0 && getScrollX() < menuWidth) {
                MToR();
            }
        }
    }

    /**
     * 初始化VelocityTracker对象，并将触摸滑动事件加入到VelocityTracker当中
     *
     * @param event 触摸滑动事件
     */
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 获取手指在content界面滑动的速度
     *
     * @return 滑动速度，以每秒钟移动了多少像素值为单位
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        return (int) mVelocityTracker.getXVelocity();
    }

    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isIntercept;
    }

    // scrollTo就会触发该事件，scrollBy为scrollTo的重写方法
    @Override
    public void computeScroll() {// 动画绘制方法
        super.computeScroll();

        if (fingerUp) {
            if (!mScroller.computeScrollOffset()) {// 滑动完成
                isIntercept = false;
                // 滑动结束如若在两端就屏蔽掉中间layout事件
                middleSlideLayout.isIntercept =
                        (Math.abs(mScroller.getFinalX()) == menuWidth);
                return;
            }
            int tempX = mScroller.getCurrX();
            scrollTo(tempX, 0);
            postInvalidate();
        }
    }

    // 左侧菜单展出
    public void MToL() {
        mScroller.startScroll(getScrollX(), 0, -menuWidth - getScrollX(), 0);
        invalidate();
    }

    // 左侧菜单缩进
    public void LToM() {
        mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0);
        invalidate();
    }

    // 右侧菜单展出
    public void MToR() {
        mScroller.startScroll(getScrollX(), 0, menuWidth - getScrollX(), 0);
        invalidate();
    }

    // 右侧菜单缩进
    public void RToM() {
        mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0);
        invalidate();
    }

    public void setLeftSlideLayout(View view) {
        if (leftSlideLayout == null) {
            leftSlideLayout = new BaseSlideLayout(mContext);
            leftSlideLayout.addView(view);
            this.addView(leftSlideLayout);
        }
    }

    public void setMiddleSlideLayout(View view) {
        if (middleSlideLayout == null) {
            middleSlideLayout = new BaseSlideLayout(mContext);
            middleSlideLayout.addView(view);
            middleSlideLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int curScroX = getScrollX();
                    mScroller.startScroll(curScroX, 0, -curScroX, 0);
                }
            });
            this.addView(middleSlideLayout);
            maskLayout = new BaseSlideLayout(mContext);
            maskLayout.setBackgroundColor(Color.GRAY);
            // float类型，0-1，完全透明-完全不透明
            maskLayout.setAlpha(0.0f);
            this.addView(maskLayout);
        }
    }

    public void setRightSlideLayout(View view) {
        if (rightSlideLayout == null) {
            rightSlideLayout = new BaseSlideLayout(mContext);
            rightSlideLayout.addView(view);
            this.addView(rightSlideLayout);
        }
    }

    public void setOnAlpha(boolean onAlpha) {
        this.isOnAlpha = onAlpha;
    }

    public void setAlphaRate(float rate) {
        this.alphaRate = rate;
    }

    public void setWidthRate(double rate) {
        this.widthRate = rate;
    }

    private class BaseSlideLayout extends RelativeLayout {

        private boolean isIntercept = false;

        public BaseSlideLayout(Context context) {
            super(context);
        }

        public BaseSlideLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return isIntercept;
        }
    }
}