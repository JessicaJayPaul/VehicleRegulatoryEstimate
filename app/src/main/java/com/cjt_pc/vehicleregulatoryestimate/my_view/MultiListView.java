package com.cjt_pc.vehicleregulatoryestimate.my_view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by cjt-pc on 2015/8/16.
 * Email:879309896@qq.com
 */


public class MultiListView extends LinearLayout implements View.OnTouchListener {

    // 刷新的各个状态，分别是 下拉可以刷新、释放立即刷新、正在刷新、刷新完成。默认为刷新完成
    public static final int STATUS_PULL_TO_REFRESH = 0;
    public static final int STATUS_RELEASE_TO_REFRESH = 1;
    public static final int STATUS_REFRESHING = 2;
    public static final int STATUS_REFRESH_FINISHED = 3;
    // 当前状态，随着手指移动的过程中会改变
    private int curStatus = STATUS_REFRESH_FINISHED;
    // 上一状态，用作状态码的缓存，若与当前状态不同则更新下拉头，然后lastStatus = curStatus，否则不做任何处理
    private int lastStatus = curStatus;
    // 时间单位的初始化，毫秒为最小计时单位
    public static final long ONE_MINUTE = 60 * 1000;
    public static final long ONE_HOUR = 60 * ONE_MINUTE;
    public static final long ONE_DAY = 24 * ONE_HOUR;
    public static final long ONE_MONTH = 30 * ONE_DAY;
    public static final long ONE_YEAR = 12 * ONE_MONTH;
    private static final String UPDATED_AT = "updated_at";
    // 存储上次更新时间
    private SharedPreferences preferences;
    // 实例化各个控件所必须的上下文参数
    private Context mContext;
    // 下拉刷新布局
    private BaseRefreshLayout header;
    // 上拉加载布局
    private BaseRefreshLayout footer;
    // 所需设置附加功能的ListView
    private ListView listView;
    // dp、px互转工具
    private DensityUtil util;
    // 手指触摸的坐标，在onTouch的最后赋值，用作判断当前手势
    private Point point;
    // 控制视图滚动
    private Scroller mScroller;
    // 刷新完成回调方法，用于耗时操作
    private PullToRefreshListener refreshListener;
    // 底部自动加载回调方法
    private AutoRefreshListener autoListener;
    // listView的适配器adapter
    private ArrayAdapter adapter;
    // 是否没有更多数据（上拉加载）
    private boolean isNoMoreData;
    // 刷新箭头的id，默认采用系统的
    private int refreshArrowId = android.R.drawable.arrow_down_float;
    // 刷新完成图片的id，默认也是采用系统的
    private int refreshFinishedImgId = android.R.drawable.checkbox_on_background;
    // 上拉加载的高度，默认是40dp
    private int footerHeight = 40;

    private void initBasicValue(Context context) {
        setOrientation(VERTICAL);
        setBackgroundColor(Color.GRAY);
        // 初始化所需各个变量
        mContext = context;
        util = new DensityUtil(context);
        point = new Point();
        mScroller = new Scroller(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        // 默认FinalY为0，手动设为-1判断是否开启动画的标志
        mScroller.setFinalY(-1);
        setListView();
    }

    // 代码加载布局所需
    public MultiListView(Context context) {
        super(context);
        initBasicValue(context);
    }

    // xml加载布局所需
    public MultiListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBasicValue(context);
    }

    // 设置下拉刷新布局
    private void setHeader() {
        header = new BaseRefreshLayout(mContext);
        // 设置下拉布局的宽高参数
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        header.setLayoutParams(params);
        header.arrow.setImageResource(refreshArrowId);
        header.progressBar.setVisibility(GONE);
        this.addView(header);
    }

    // 设置上拉加载布局
    private void setFooter() {
        footer = new BaseRefreshLayout(mContext, footerHeight);
        footer.txtLlLayout.setVisibility(GONE);
        footer.progressBar.setVisibility(GONE);
        footer.txtRlLayout.setVisibility(VISIBLE);
        initFooter();
        listView.addFooterView(footer);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // SCROLL_STATE_FLING，这个参数表示你手离开后ListView还在“飞”中，理解？
                // SCROLL_STATE_IDLE，这个参数表示ListView停下不动了
                // SCROLL_STATE_TOUCH_SCROLL，这个参数表示你手还在ListView上
                switch (scrollState) {
                    case SCROLL_STATE_FLING:
                        break;
                    case SCROLL_STATE_IDLE:
                        if (view.getBottom() != 0) {
                            // 当还存在更多数据的时候并且滑动到了listView的最底部，执行加载方法
                            if (!isNoMoreData && (view.getAdapter().getView(view.getAdapter().getCount() - 1,
                                    null, MultiListView.this).getBottom() == view.getBottom())) {
                                footer.info.setText("正在加载...");
                                footer.progressBar.setVisibility(VISIBLE);
                                footer.arrow.setVisibility(GONE);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        autoListener.onAutoLoad();
                                    }
                                }).start();
                            }
                        }
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void initFooter() {
        footer.arrow.setImageResource(refreshArrowId);
        isNoMoreData = false;
        footer.info.setText("上拉加载");
    }

    private void setListView() {
        setHeader();
        listView = new ListView(mContext);
        // 自定义视图一定要注意设置宽高参数，除非onMeasure
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        listView.setLayoutParams(params);
        listView.setBackgroundColor(Color.WHITE);
        this.addView(listView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(sizeWidth, sizeHeight);
    }

    // 重点关注changed
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cCount = getChildCount();
        int cWidth;
        int cHeight;
        for (int i = 0; i < cCount; i++) {
            View childView = getChildAt(i);
            cWidth = childView.getMeasuredWidth();
            cHeight = childView.getMeasuredHeight();
            int cl = 0, ct = 0, cr, cb;
            switch (i) {
                case 0:
                    cl = 0;
                    ct = -cHeight;
                    break;
                case 1:
                    cl = 0;
                    ct = 0;
                    break;
            }
            cr = cWidth + cl;
            cb = cHeight + ct;
            childView.layout(cl, ct, cr, cb);
        }
    }

    // ListView的滑动监听方法，返回true代表屏蔽滚动事件
    @Override
    public boolean onTouch(View view, @NonNull MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                int dY = (int) ev.getY() - point.y;
                // 如果刷新头已经显示出来了，或者是列表首元素置顶并且手势为向下滑动滚动视图且屏蔽滚动事件
                if (refreshListener != null && (getScrollY() < 0 || (isOnTop() && dY > 0))) {
                    // 在每次移动刷新刷新布局，并且没在刷新的时候设置状态
                    if (curStatus != STATUS_REFRESHING) {
                        if (getScrollY() > -header.getMeasuredHeight()) {
                            curStatus = STATUS_PULL_TO_REFRESH;
                        } else {
                            curStatus = STATUS_RELEASE_TO_REFRESH;
                        }
                        // 记得更新下拉头中的信息
                        updateHeaderView();
                    }
                    int tempY = dY / 2 - getScrollY();
                    // 上滑过程中越界处理
                    dY = tempY < 0 ? -getScrollY() : -dY / 2;
                    scrollBy(0, dY);
                    break;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (getScrollY() < 0) {
                    if (-getScrollY() < header.getMeasuredHeight()) {
                        // 继续下拉可以刷新，手指抬起
                        mScroller.startScroll(0, getScrollY(), 0, -getScrollY());
                    } else {
                        // 松开即可刷新，手指抬起
                        mScroller.startScroll(0, getScrollY(), 0, -header.getMeasuredHeight() - getScrollY());
                    }
                    invalidate();
                }
                break;
        }
        point.x = (int) ev.getX();
        point.y = (int) ev.getY();
        if (getScrollY() < 0) {
            // 在每一次onTouch事件中，若刷新头已经显现（哪怕是一点），就让List不被选中，并且返回true，屏蔽滚动点击事件
            listView.setPressed(false);
            listView.setFocusable(false);
            listView.setFocusableInTouchMode(false);
            return true;
        }
        return false;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        // 是否执行动画
        if (mScroller.getFinalY() != -1) {
            // 动画结束
            if (!mScroller.computeScrollOffset()) {
                // 若执行的是释放刷新动画，动画结束后应该设置控件属性，执行回调方法，耗时操作应另开线程
                if (curStatus == STATUS_RELEASE_TO_REFRESH) {
                    curStatus = STATUS_REFRESHING;
                    updateHeaderView();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            refreshListener.onRefresh();
                        }
                    }).start();
                }
                mScroller.setFinalY(-1);
                return;
            }
            int tempY = mScroller.getCurrY();
            scrollTo(0, tempY);
            postInvalidate();
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        if (footer != null) {
            footer.setBackgroundColor(color);
        }
    }

    private boolean isOnTop() {
        View firstChild = listView.getChildAt(0);
        // 注意listView.getCount()和listView.getChildCount()的区别
        return (firstChild == null || firstChild.getTop() == 0) && listView.getFirstVisiblePosition() == 0;
    }

    public void setOnPullToRefreshListener(PullToRefreshListener listener) {
        this.refreshListener = listener;
        // 尤其注意添加子view的顺序
        listView.setOnTouchListener(this);
    }

    public void setAutoRefreshListener(AutoRefreshListener listener) {
        this.autoListener = listener;
        setFooter();
    }

    public void onFinishPullToRefresh() {
        preferences.edit().putLong(UPDATED_AT, System.currentTimeMillis()).apply();
        curStatus = STATUS_REFRESH_FINISHED;
        // 刷新完成后回到主线程更新UI
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (footer != null) {
                    initFooter();
                }
                adapter.notifyDataSetChanged();
                updateHeaderView();
            }
        });
        Sleep(800);
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY());
        postInvalidate();
    }

    public void onFinishAutoRefresh() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 若加载完成之后数据源的大小没有发生改变，则断定没有更多数据
                // listView比adapter多了一个footer，因此count大1
                if (adapter.getCount() + 1 == listView.getCount()) {
                    isNoMoreData = true;
                    Toast.makeText(mContext, "没有更多数据了~\\(≧▽≦)/~", Toast.LENGTH_SHORT).show();
                    footer.progressBar.setVisibility(GONE);
                    footer.arrow.setVisibility(VISIBLE);
                    footer.info.setText("已经到底了...");
                    return;
                }
                // adapter的count随着数据源的改变而改变，但是listView的count必须在初始化或者是notifyDataSetChanged之后才会改变
                adapter.notifyDataSetChanged();
                footer.progressBar.setVisibility(GONE);
                footer.arrow.setVisibility(VISIBLE);
                footer.info.setText("上拉加载");
            }
        });
    }

    private void updateHeaderView() {
        if (lastStatus != curStatus) {
            if (curStatus == STATUS_PULL_TO_REFRESH) {
                header.description.setText("下拉可以刷新");
                header.arrow.setVisibility(View.VISIBLE);
                header.progressBar.setVisibility(View.GONE);
                header.arrow.setImageResource(refreshArrowId);
                rotateArrow();
            } else if (curStatus == STATUS_RELEASE_TO_REFRESH) {
                header.description.setText("松开立即刷新");
                header.arrow.setVisibility(View.VISIBLE);
                header.progressBar.setVisibility(View.GONE);
                rotateArrow();
            } else if (curStatus == STATUS_REFRESHING) {
                header.description.setText("正在刷新...");
                header.arrow.clearAnimation();
                header.arrow.setVisibility(View.GONE);
                header.progressBar.setVisibility(View.VISIBLE);
            } else if (curStatus == STATUS_REFRESH_FINISHED) {
                header.description.setText("刷新完成");
                header.arrow.setVisibility(View.VISIBLE);
                header.progressBar.setVisibility(View.GONE);
                header.arrow.clearAnimation();
                header.arrow.setImageResource(refreshFinishedImgId);
            }
            refreshUpdatedAtValue();
            lastStatus = curStatus;
        }
    }

    private void rotateArrow() {
        float pivotX = header.arrow.getWidth() / 2f;
        float pivotY = header.arrow.getHeight() / 2f;
        float fromDegrees;
        float toDegrees;
        if (lastStatus == STATUS_PULL_TO_REFRESH) {
            fromDegrees = 0f;
            toDegrees = 180f;
        } else {
            fromDegrees = 180f;
            toDegrees = 360f;
        }
        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        animation.setDuration(100);
        animation.setFillAfter(true);
        header.arrow.startAnimation(animation);
    }

    private void refreshUpdatedAtValue() {
        Long lastUpdateTime = preferences.getLong(UPDATED_AT, -1);
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - lastUpdateTime;
        long timeIntoFormat;
        String updateAtValue;
        if (lastUpdateTime == -1) {
            updateAtValue = "暂未更新过";
        } else if (timePassed < 0) {
            updateAtValue = "事件错误";
        } else if (timePassed < ONE_MINUTE) {
            updateAtValue = "刚刚更新";
        } else if (timePassed < ONE_HOUR) {
            timeIntoFormat = timePassed / ONE_MINUTE;
            String value = timeIntoFormat + "分钟";
            updateAtValue = String.format("上次更新于%1$s前", value);
        } else if (timePassed < ONE_DAY) {
            timeIntoFormat = timePassed / ONE_HOUR;
            String value = timeIntoFormat + "小时";
            updateAtValue = String.format("上次更新于%1$s前", value);
        } else if (timePassed < ONE_MONTH) {
            timeIntoFormat = timePassed / ONE_DAY;
            String value = timeIntoFormat + "天";
            updateAtValue = String.format("上次更新于%1$s前", value);
        } else if (timePassed < ONE_YEAR) {
            timeIntoFormat = timePassed / ONE_MONTH;
            String value = timeIntoFormat + "个月";
            updateAtValue = String.format("上次更新于%1$s前", value);
        } else {
            timeIntoFormat = timePassed / ONE_YEAR;
            String value = timeIntoFormat + "年";
            updateAtValue = String.format("上次更新于%1$s前", value);
        }
        header.updateAt.setText(updateAtValue);
    }

    public void Sleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 设置listView的适配器
    public void setAdapter(ArrayAdapter adapter) {
        listView.setAdapter(adapter);
        this.adapter = adapter;
    }

    // 设置listView的背景色
    public void setListViewBgColor(int intColor) {
        listView.setBackgroundColor(intColor);
    }

    // 设置刷新完成图片
    public void setFinishedImgId(int id) {
        refreshFinishedImgId = id;
    }

    public void setFooterHeight(int height) {
        footerHeight = height;
    }

    private class BaseRefreshLayout extends RelativeLayout {

        // 中间提示文本图片布局的参数
        private int dpWidth = 200;
        private int dpHeight = 60;
        // 左边提示图片控件
        public ProgressBar progressBar;
        public ImageView arrow;
        // 下拉刷新右边文字布局控件
        public LinearLayout txtLlLayout;
        public TextView description;
        public TextView updateAt;
        // 上拉加载右边文字布局控件
        public RelativeLayout txtRlLayout;
        public TextView info;

        private LinearLayout linearLayout;

        public BaseRefreshLayout(Context context) {
            super(context);
            setLayout();
        }

        // 重载的构造方法，用于设置高度
        public BaseRefreshLayout(Context context, int height) {
            super(context);
            dpHeight = height;
            setLayout();
        }

        public void setLayout() {
            // 初始化整个布局
            linearLayout = new LinearLayout(mContext);
            linearLayout.setOrientation(HORIZONTAL);
            int width = util.dpTopx(dpWidth);
            int height = util.dpTopx(dpHeight);
            LayoutParams params = new LayoutParams(width, height);
            params.addRule(CENTER_IN_PARENT);
            linearLayout.setLayoutParams(params);
            this.addView(linearLayout);
            // 初始化图片布局
            RelativeLayout imgLayout = new RelativeLayout(mContext);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(0, height);
            imgParams.weight = 1;
            imgLayout.setLayoutParams(imgParams);
            linearLayout.addView(imgLayout);
            // 设置提示文字布局
            txtLlLayout = new LinearLayout(mContext);
            LinearLayout.LayoutParams txtLlParams = new LinearLayout.LayoutParams(0, height);
            txtLlParams.weight = 4;
            txtLlLayout.setLayoutParams(txtLlParams);
            txtLlLayout.setOrientation(VERTICAL);
            linearLayout.addView(txtLlLayout);
            // 设置进度条
            progressBar = new ProgressBar(mContext);
            LayoutParams pbParams = new LayoutParams(height / 2, height / 2);
            pbParams.addRule(CENTER_IN_PARENT);
            progressBar.setLayoutParams(pbParams);
            imgLayout.addView(progressBar);
            // 设置刷新提示箭头
            arrow = new ImageView(mContext);
            LayoutParams arrowParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            arrowParams.addRule(CENTER_IN_PARENT);
            arrow.setLayoutParams(arrowParams);
            imgLayout.addView(arrow);
            // 设置提示刷新文字
            description = new TextView(mContext);
            LinearLayout.LayoutParams topTxtParams = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, 0);
            topTxtParams.weight = 1;
            description.setLayoutParams(topTxtParams);
            description.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            txtLlLayout.addView(description);
            // 设置提示刷新时间
            updateAt = new TextView(mContext);
            LinearLayout.LayoutParams bottomTxtParams = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, 0);
            bottomTxtParams.weight = 1;
            updateAt.setLayoutParams(bottomTxtParams);
            updateAt.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            txtLlLayout.addView(updateAt);
            // 设置提示文字布局
            txtRlLayout = new RelativeLayout(mContext);
            LinearLayout.LayoutParams txtRlParams = new LinearLayout.LayoutParams(0, height);
            txtRlParams.weight = 4;
            txtRlLayout.setLayoutParams(txtRlParams);
            linearLayout.addView(txtRlLayout);
            txtRlLayout.setVisibility(GONE);
            // 设置提示文字
            info = new TextView(mContext);
            LayoutParams infoParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            infoParams.addRule(CENTER_IN_PARENT);
            info.setLayoutParams(infoParams);
            txtRlLayout.addView(info);
        }
    }

    public class DensityUtil {

        private Context context;

        public DensityUtil(Context context) {
            this.context = context;
        }

        public int dpTopx(float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }

        public int pxTodip(float pxValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        }
    }

    // 下拉刷新完成回调接口
    public interface PullToRefreshListener {
        void onRefresh();
    }

    // 上拉加载完成回调接口
    public interface AutoRefreshListener {
        void onAutoLoad();
    }
}
