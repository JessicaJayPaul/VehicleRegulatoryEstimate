package com.cjt_pc.vehicleregulatoryestimate.my_view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cjt_pc.vehicleregulatoryestimate.R;
import com.cjt_pc.vehicleregulatoryestimate.activity.MainActivity;
import com.cjt_pc.vehicleregulatoryestimate.activity.TaskInfoActivity;
import com.cjt_pc.vehicleregulatoryestimate.adapter.PgrwInfoListAdapter;
import com.cjt_pc.vehicleregulatoryestimate.entity.UploadPgrwInfo;
import com.cjt_pc.vehicleregulatoryestimate.entity.User;
import com.cjt_pc.vehicleregulatoryestimate.utils.SoapCallBackListener;
import com.cjt_pc.vehicleregulatoryestimate.utils.SoapUtil;
import com.cjt_pc.vehicleregulatoryestimate.utils.SystemUtil;

import org.ksoap2.serialization.SoapObject;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by cjt-pc on 2015/8/8.
 * Email:879309896@qq.com
 */
public class MiddleSlideLayout extends LinearLayout implements OnClickListener, AdapterView.OnItemClickListener {

    MyTitleView titleView;
    private PopupWindow popupWindow;
    private MultiListView listView;
    private Context mContext;

    // 防止出现刷新闪屏情况，设置任务列表缓存
    private List<UploadPgrwInfo> bufferInfoList = new ArrayList<>();
    private List<UploadPgrwInfo> pgrwInfoList = new ArrayList<>();
    public PgrwInfoListAdapter listAdapter;
    // double buffer
    private List<UploadPgrwInfo> bufferOLList = new ArrayList<>();

    private SlidingLayout slidingLayout;

    public MiddleSlideLayout(Context context, SlidingLayout slidingLayout) {
        super(context);
        mContext = context;
        this.slidingLayout = slidingLayout;
        LayoutInflater.from(context).inflate(R.layout.middle_slide_layout, this);
        initTitleView();
        initListView();
        uploadList();
    }

    public void getPgrwList(boolean onlyLocal) {
        // 我也不懂为毛在这里clear()就会自动通知适配器更新，只有加一个缓存list了
        bufferInfoList.clear();
        loadLocalTasks();
        if (onlyLocal) {
            bufferInfoList.addAll(bufferOLList);
            pgrwInfoList.clear();
            pgrwInfoList.addAll(bufferInfoList);
            listAdapter.notifyDataSetChanged();
            return;
        }
        if (SystemUtil.isNetworkConnected(mContext)) {
            loadOnlineTasks();
        } else {
            // 回到主线程更新UI
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "请检查网络连接...", Toast.LENGTH_SHORT).show();
                    pgrwInfoList.clear();
                    pgrwInfoList.addAll(bufferInfoList);
                    listAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void loadLocalTasks() {
        List<UploadPgrwInfo> localTasks = DataSupport.findAll(UploadPgrwInfo.class);
        // 按照taskId排序的话应该倒过来去
        Collections.reverse(localTasks);
        bufferInfoList.addAll(localTasks);
    }

    // 采用重载的不另开线程的 方法，便于刷新完成触发回调
    private void loadOnlineTasks() {
        long spaceTime = 24 * 60 * 60 * 1000 * 10;
        long currentTime = System.currentTimeMillis() + spaceTime / 10;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String currentStr = sf.format(new Date(currentTime));
        String strWhere;
        strWhere = "zdrq<='" + currentStr + "' and zdr='" + User.getUserInstance().getZdr() + "'";
        final LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("where", strWhere);
        SoapUtil.postSoapRequest(properties, "getpgrwList", new SoapCallBackListener() {
            @Override
            public void onFinish(SoapObject soapObject) {
                List<UploadPgrwInfo> infos = new ArrayList<>();
                int count = soapObject.getPropertyCount();
                for (int i = count - 1; i >= 0; i--) {
                    SoapObject object = (SoapObject) soapObject.getProperty(i);
                    infos.add(UploadPgrwInfo.getUploadPgrwInfo(object));
                }
                bufferOLList.clear();
                bufferOLList.addAll(infos);

                bufferInfoList.addAll(infos);
                pgrwInfoList.clear();
                pgrwInfoList.addAll(bufferInfoList);
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }


    public MiddleSlideLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.middle_slide_layout, this);
        initTitleView();
        initListView();
    }

    private void initTitleView() {
        titleView = (MyTitleView) findViewById(R.id.title_view);
        titleView.setBg("#263238");
        titleView.setLeftButton(R.mipmap.menu_left, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingLayout.getScrollX() == 0) {
                    slidingLayout.MToL();
                }
            }
        });
        titleView.setTitleName("任务列表");
        titleView.setRightButton(R.mipmap.menu_right, new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(v);
            }
        });
    }

    private void showPopupWindow(View view) {
        if (popupWindow == null) {
            View popupView = LayoutInflater.from(mContext).inflate(R.layout.list_popup, this, false);
            // 创建一个PopupWidow对象
            popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setAnimationStyle(R.style.Animation_AppCompat_Dialog);

            TextView tvNew = (TextView) popupView.findViewById(R.id.new_task);
            tvNew.setOnClickListener(this);
            TextView tvUpload = (TextView) popupView.findViewById(R.id.upload_task);
            tvUpload.setOnClickListener(this);
            TextView tvDelete = (TextView) popupView.findViewById(R.id.delete_task);
            tvDelete.setOnClickListener(this);
        }
        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(view);
    }

    private void initListView() {
        listAdapter = new PgrwInfoListAdapter(mContext, R.layout.pgrw_list_item, pgrwInfoList);
        listView = (MultiListView) findViewById(R.id.list_view);
        listView.setAdapter(listAdapter);
        listView.setOnPullToRefreshListener(new MultiListView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                getPgrwList(false);
                listView.onFinishPullToRefresh();
            }
        });
        listView.setListViewBgColor(Color.parseColor("#37474F"));
        // 之所以不直接添加个onItemClickListener是因为本地任务长按有监听，为了用户体验，放在adapter中
//        listView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // popupWidow下面3个子view的点击事件
            case R.id.new_task:
                Intent intent = new Intent(mContext, TaskInfoActivity.class);
                ((Activity) getContext()).startActivityForResult(intent, MainActivity.UPDATE_LIST);
                popupWindow.dismiss();
                break;
            case R.id.upload_task:
                popupWindow.dismiss();
                break;
            case R.id.delete_task:
                popupWindow.dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(mContext, TaskInfoActivity.class);
        intent.putExtra("pgrwInfo", pgrwInfoList.get(position));
        mContext.startActivity(intent);
    }

    public void uploadList() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("正在更新列表数据...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPgrwList(false);
                progressDialog.dismiss();
            }
        }).start();
    }
}
