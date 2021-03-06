package com.cjt_pc.vehicleregulatoryestimate.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cjt_pc.vehicleregulatoryestimate.R;
import com.cjt_pc.vehicleregulatoryestimate.entity.UploadImageEntity;
import com.cjt_pc.vehicleregulatoryestimate.entity.UploadImageList;
import com.cjt_pc.vehicleregulatoryestimate.entity.UploadPgrwInfo;
import com.cjt_pc.vehicleregulatoryestimate.entity.User;
import com.cjt_pc.vehicleregulatoryestimate.my_view.MyTitleView;
import com.cjt_pc.vehicleregulatoryestimate.sortlistview.ChooseCarInfo;
import com.cjt_pc.vehicleregulatoryestimate.utils.SoapCallBackListener;
import com.cjt_pc.vehicleregulatoryestimate.utils.SoapConfig;
import com.cjt_pc.vehicleregulatoryestimate.utils.SoapUtil;
import com.cjt_pc.vehicleregulatoryestimate.utils.SystemUtil;

import org.kobjects.base64.Base64;
import org.ksoap2.serialization.SoapObject;
import org.litepal.crud.DataSupport;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by cjt-pc on 2015/8/9.
 * Email:879309896@qq.com
 */
public class TaskInfoActivity extends Activity implements View.OnClickListener {

    // 启动选择车辆信息的Activity所传入的requestCode
    public final static int GET_CPP = 0;
    public final static int GET_CX = 1;
    public final static int GET_CXING = 2;
    // 进入此页面大体状态有三个：新建任务、本地读取、来自网络。
    public final static int NEW_TASK = 0;
    public final static int LOCAL_TASK = 1;
    public final static int ONLINE_TASK = 2;
    // 定义当前进入此页面的大体状态
    private int curTaskStatus;
    // 传入的pgrwInfo（如若有的话）
    private UploadPgrwInfo pgrwInfo;
    // 当前状态是否可编辑，默认false
    private boolean isEditable = false;
    // 是否正在编辑，默认是true
    private boolean isEditing = true;
    // 评估任务唯一标示符，taskId;
    int taskId;
    // 自定义标题栏
    MyTitleView titleView;
    // 编辑按钮
    private ImageButton ibtEdit;
    // 填写基本信息的控件
    EditText et_1, et_2, et_3, et_4, et_5;
    private Spinner sp_6;
    private TextView tv_6;
    EditText et_7, et_8;
    private TextView tv_9, tv_10;
    EditText et_11, et_12;
    private TextView tv_13, tv_14, tv_15;
    private Spinner sp_16;
    private TextView tv_16;
    EditText et_17, et_18, et_19, et_20;
    private View[] views;
    // 上传与保存的popupWindow
    private PopupWindow popupWindow;
    // 出厂日期和登记日期
    private int ccYear, ccMonth, ccDay;
    private int djYear, djMonth, djDay;
    // 环保标准的list以及adapter
    private List<String> hbbzList = new ArrayList<>();
    private ArrayAdapter<String> hbbzAdapter;
    // 车辆品牌的list以及adapter
    private ArrayList<String> cppList = new ArrayList<>();
    private List<String> cppIdList = new ArrayList<>();
    // 车系的list以及adapter
    private ArrayList<String> cxList = new ArrayList<>();
    private List<String> cxIdList = new ArrayList<>();
    // 车型的list以及adapter
    private ArrayList<String> cxingList = new ArrayList<>();
    private ArrayList<String> cxingIdList = new ArrayList<>();
    // 使用性质的list以及adapter
    private List<String> syxzList = new ArrayList<>();
    ArrayAdapter<String> syxzAdapter;
    // 图像采集相关8项linearLayout
    LinearLayout ll_1;
    LinearLayout ll_2;
    LinearLayout ll_3;
    LinearLayout ll_4;
    LinearLayout ll_5;
    LinearLayout ll_6;
    LinearLayout ll_7;
    LinearLayout ll_8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_info_layout);
        setCurTaskStatus();
        initTitleView();
        initWidget();
    }

    // 设置当前任务的大体状态，同时初始化了传入的pgrwInfo
    private void setCurTaskStatus() {
        pgrwInfo = (UploadPgrwInfo) getIntent().getSerializableExtra("pgrwInfo");
        if (pgrwInfo == null) {
            curTaskStatus = NEW_TASK;
            // 新建状态时初始化任务详情
            pgrwInfo = new UploadPgrwInfo();
            pgrwInfo.setOlddjh(UUID.randomUUID().toString());
            pgrwInfo.setPgdh(UUID.randomUUID().toString());
            pgrwInfo.setZdr(User.getUserInstance().getZdr());
            pgrwInfo.save();
            taskId = pgrwInfo.getId();
        } else {
            if (pgrwInfo.getZt().equals("-1")) {
                curTaskStatus = LOCAL_TASK;
                isEditable = true;
                isEditing = false;
                // 本地直接读取task_id
                taskId = pgrwInfo.getId();
                // I don't konw why I have to find it from db again if you don't want taskId add 1.
                pgrwInfo = DataSupport.find(UploadPgrwInfo.class, taskId);
            } else {
                // 网络状态先存数据库，获取自增taskId
                pgrwInfo.save();
                taskId = pgrwInfo.getId();
                getUpLoadListNew();
                curTaskStatus = ONLINE_TASK;
                // 若返回的zt为""则任务可以编辑
                if (pgrwInfo.getZt().equals("")) {
                    isEditable = true;
                } else {
                    isEditable = false;
                }
                isEditing = false;
            }
        }
    }

    private void initWidget() {
        initIbtEdit();
        initBasicInfo();
        initLLImgCap();
    }

    private void initLLImgCap() {
        ll_1 = (LinearLayout) findViewById(R.id.ll_1);
        ll_1.setOnClickListener(this);
        ll_2 = (LinearLayout) findViewById(R.id.ll_2);
        ll_2.setOnClickListener(this);
        ll_3 = (LinearLayout) findViewById(R.id.ll_3);
        ll_3.setOnClickListener(this);
        ll_4 = (LinearLayout) findViewById(R.id.ll_4);
        ll_4.setOnClickListener(this);
        ll_5 = (LinearLayout) findViewById(R.id.ll_5);
        ll_5.setOnClickListener(this);
        ll_6 = (LinearLayout) findViewById(R.id.ll_6);
        ll_6.setOnClickListener(this);
        ll_7 = (LinearLayout) findViewById(R.id.ll_7);
        ll_7.setOnClickListener(this);
        ll_8 = (LinearLayout) findViewById(R.id.ll_8);
        ll_8.setOnClickListener(this);
    }

    private void initIbtEdit() {
        ibtEdit = (ImageButton) findViewById(R.id.edit_task);
        if (isEditable) {
            ibtEdit.setImageResource(R.mipmap.edit_btn_yellow);
            ibtEdit.setOnClickListener(this);
        }
    }

    private void initBasicInfo() {
        et_1 = (EditText) findViewById(R.id.task_info_1);
        et_2 = (EditText) findViewById(R.id.task_info_2);
        et_3 = (EditText) findViewById(R.id.task_info_3);
        et_4 = (EditText) findViewById(R.id.task_info_4);
        et_5 = (EditText) findViewById(R.id.task_info_5);
        sp_6 = (Spinner) findViewById(R.id.task_info_6);
        tv_6 = (TextView) findViewById(R.id.task_info_6_ii);
        et_7 = (EditText) findViewById(R.id.task_info_7);
        et_8 = (EditText) findViewById(R.id.task_info_8);
        tv_9 = (TextView) findViewById(R.id.task_info_9);
        tv_9.setOnClickListener(this);
        tv_10 = (TextView) findViewById(R.id.task_info_10);
        tv_10.setOnClickListener(this);
        et_11 = (EditText) findViewById(R.id.task_info_11);
        et_12 = (EditText) findViewById(R.id.task_info_12);
        tv_13 = (TextView) findViewById(R.id.task_info_13);
        tv_13.setOnClickListener(this);
        tv_14 = (TextView) findViewById(R.id.task_info_14);
        tv_14.setOnClickListener(this);
        tv_15 = (TextView) findViewById(R.id.task_info_15);
        tv_15.setOnClickListener(this);
        sp_16 = (Spinner) findViewById(R.id.task_info_16);
        tv_16 = (TextView) findViewById(R.id.task_info_16_ii);
        et_17 = (EditText) findViewById(R.id.task_info_17);
        et_18 = (EditText) findViewById(R.id.task_info_18);
        et_19 = (EditText) findViewById(R.id.task_info_19);
        et_20 = (EditText) findViewById(R.id.task_info_20);
        views = new View[]{et_1, et_2, et_3, et_4, et_5, sp_6, et_7, et_8, tv_9, tv_10, et_11,
                et_12, tv_13, tv_14, tv_15, sp_16, et_17, et_18, et_19, et_20};
        if (curTaskStatus == NEW_TASK) {
            // 如若为新建应当初始化时间显示、下拉控件
            initTvTime();
            initSp();
        } else {
            initValue();
        }
    }

    // 初始化环保标准、车辆性质的下拉框
    private void initSp() {
        hbbzAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, hbbzList);
        sp_6.setAdapter(hbbzAdapter);
        if (SystemUtil.isNetworkConnected(this)) {
            getHbbzList();
        } else {
            Toast.makeText(this, "网络异常，请退出检查后重试...", Toast.LENGTH_SHORT).show();
        }

        syxzList.add("营运");
        syxzList.add("非营运");
        syxzAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, syxzList);
        sp_16.setAdapter(syxzAdapter);
        // 非编辑状态调用说明 点击编辑按钮加载数据
        if (!isEditing) {
            sp_16.setSelection(syxzList.indexOf(pgrwInfo.getSyxz()));
        }
    }

    private void getHbbzList() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("正在加载环保标准...");
        dialog.show();

        final LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("flm", "hbbz");
        properties.put("where", "");
        SoapUtil.postSoapRequest(this, properties, "getDmzdList", new SoapCallBackListener() {
            @Override
            public void onFinish(SoapObject soapObject) {
                for (int i = 0; i < soapObject.getPropertyCount(); i++) {
                    SoapObject object = (SoapObject) soapObject.getProperty(i);
                    hbbzList.add(object.getPrimitivePropertyAsString("sm"));
                }
                dialog.dismiss();
                hbbzAdapter.notifyDataSetChanged();
                // 非编辑状态调用说明 点击编辑按钮加载数据
                if (!isEditing) {
                    sp_6.setSelection(hbbzList.indexOf(pgrwInfo.getHbbz()));
                }
            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();
                Toast.makeText(TaskInfoActivity.this, "未知错误！", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private void getCppList() {
        if (SystemUtil.isNetworkConnected(this)) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("正在加载车辆品牌...");
            dialog.show();

            final LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
            properties.put("where", "");
            SoapUtil.postSoapRequest(this, properties, "getC_ppzdList", new SoapCallBackListener() {
                @Override
                public void onFinish(SoapObject soapObject) {
                    for (int i = 0; i < soapObject.getPropertyCount(); i++) {
                        SoapObject object = (SoapObject) soapObject.getProperty(i);
                        cppList.add(object.getPrimitivePropertyAsString("name"));
                        cppIdList.add(object.getPrimitivePropertyAsString("id"));
                    }
                    dialog.dismiss();
                    // 调用getCpp()方法无非两种状况：新建；编辑
                    // 编辑不会启动选择排序Activity
                    if (isEditing) {
                        startChooseCarInfoAty(cppList, GET_CPP);
                    } else {
                        getcxList(cppList.indexOf(pgrwInfo.getPpmc()));
                    }
                }

                @Override
                public void onError(Exception e) {
                    dialog.dismiss();
                    Toast.makeText(TaskInfoActivity.this, "请检查网络连接...", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "请检查网络连接...", Toast.LENGTH_SHORT).show();
        }
    }

    private void getcxList(int index) {
        if (SystemUtil.isNetworkConnected(this)) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("正在加载车辆系别...");
            dialog.show();

            cxList.clear();
            cxIdList.clear();
            String id = cppIdList.get(index);
            final LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
            properties.put("where", "father_id = '" + id + "'");
            SoapUtil.postSoapRequest(this, properties, "getC_xlzdList", new SoapCallBackListener() {
                @Override
                public void onFinish(SoapObject soapObject) {
                    for (int i = 0; i < soapObject.getPropertyCount(); i++) {
                        SoapObject object = (SoapObject) soapObject.getProperty(i);
                        cxList.add(object.getPrimitivePropertyAsString("name"));
                        cxIdList.add(object.getPrimitivePropertyAsString("id"));
                    }
                    dialog.dismiss();
                    if (isEditing) {
                        tv_14.setText(cxList.get(0));
                        getcxingList(0);
                    } else {
                        getcxingList(cxList.indexOf(pgrwInfo.getPxmc()));
                    }
                }

                @Override
                public void onError(Exception e) {
                    dialog.dismiss();
                    Toast.makeText(TaskInfoActivity.this, "请检查网络连接...", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "请检查网络连接...", Toast.LENGTH_SHORT).show();
        }
    }

    private void getcxingList(int index) {
        if (SystemUtil.isNetworkConnected(this)) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("正在加载车辆型号...");
            dialog.show();

            cxingList.clear();
            cxingIdList.clear();
            String id = cxIdList.get(index);
            final LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
            properties.put("where", "c.father_id = '" + id + "'");
            SoapUtil.postSoapRequest(this, properties, "getC_xhzdList", new SoapCallBackListener() {
                @Override
                public void onFinish(SoapObject soapObject) {
                    for (int i = 0; i < soapObject.getPropertyCount(); i++) {
                        SoapObject object = (SoapObject) soapObject.getProperty(i);
                        cxingList.add(object.getPrimitivePropertyAsString("name"));
                        cxingIdList.add(object.getPrimitivePropertyAsString("id"));
                    }
                    dialog.dismiss();
                    if (isEditing) {
                        tv_15.setText(cxingList.get(0));
                    } else {
                        isEditing = true;
                    }
                }

                @Override
                public void onError(Exception e) {
                    dialog.dismiss();
                    Toast.makeText(TaskInfoActivity.this, "请检查网络连接...", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "请检查网络连接...", Toast.LENGTH_SHORT).show();
        }
    }

    public void getUpLoadListNew() {
        if (SystemUtil.isNetworkConnected(this)) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("正在加载图片列表...");
            dialog.show();

            LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
            properties.put("strWhere", "djh = '" + pgrwInfo.getPgdh() + "'");
            SoapUtil.postSoapRequest(this, properties, "GetUpLoadListNew", new SoapCallBackListener() {
                @Override
                public void onFinish(SoapObject soapObject) {
                    for (int i = 0; i < soapObject.getPropertyCount(); i++) {
                        UploadImageEntity uploadImageEntity = new UploadImageEntity();
                        SoapObject object = (SoapObject) soapObject.getProperty(i);
                        uploadImageEntity.setDjh(object.getPrimitivePropertyAsString("djh").trim());
                        String filefl = object.getPrimitivePropertyAsString("filefl").trim();
                        uploadImageEntity.setFilefl(filefl);
                        uploadImageEntity.setScr(object.getPrimitivePropertyAsString("scr"));
                        uploadImageEntity.setFilesize(object.getPrimitivePropertyAsString("filesize"));
                        // 供编辑上传之用
                        uploadImageEntity.setFilepath(object.getPrimitivePropertyAsString("filepath"));
                        // 缩略图路径，供显示之用
                        String filepath = SoapConfig.BASE_URL + returnPath(object.getPrimitivePropertyAsString("filepath"));
                        uploadImageEntity.setFilerealpath(filepath);
                        String filewz = object.getPrimitivePropertyAsString("filewz");
                        uploadImageEntity.setFilewz(filewz);
                        uploadImageEntity.setScrq("2015-07-14");
                        uploadImageEntity.save();
                        pgrwInfo.getImageEntityList().add(uploadImageEntity);
                    }
                    pgrwInfo.save();
                    dialog.dismiss();
                }

                @Override
                public void onError(Exception e) {
                    dialog.dismiss();
                    Toast.makeText(TaskInfoActivity.this, "请检查网络连接...", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "请检查网络连接...", Toast.LENGTH_SHORT).show();
        }
    }

    private String returnPath(String filepath) {
        StringBuilder builder = new StringBuilder();
        String path[] = filepath.split("/");
        for (int i = 0; i < path.length; i++) {
            builder.append(path[i]);
            if (i == (path.length - 2)) {
                builder.append("/MicroPic/");
            } else if (i != path.length - 1) {
                builder.append("/");
            }
        }
        return builder.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String name;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GET_CPP:
                    name = data.getStringExtra("name");
                    tv_13.setText(name);
                    getcxList(cppList.indexOf(name));
                    break;
                case GET_CX:
                    name = data.getStringExtra("name");
                    tv_14.setText(name);
                    getcxingList(cxList.indexOf(name));
                    break;
                case GET_CXING:
                    name = data.getStringExtra("name");
                    tv_15.setText(name);
                    break;
            }
        }
    }

    // 初始化选择时间显示
    private void initTvTime() {
        Calendar d = Calendar.getInstance(Locale.CHINA);
        Date myDate = new Date();
        d.setTime(myDate);
        int i = ccYear = djYear = d.get(Calendar.YEAR);
        int i1 = ccMonth = djMonth = d.get(Calendar.MONTH);
        int i2 = ccDay = djDay = d.get(Calendar.DAY_OF_MONTH);
        String year = i + "";
        String month = ++i1 < 10 ? "0" + i1 : i1 + "";
        String day = i2 < 10 ? "0" + i2 : i2 + "";
        tv_9.setText(year + "-" + month + "-" + day);
        tv_10.setText(year + "-" + month + "-" + day);
    }

    // 点击日期tv弹出一个datePickerDialog供选择
    private void setTime(final TextView tvDisplay) {
        int year = (tvDisplay.getId() == R.id.task_info_9) ? ccYear : djYear;
        int month = (tvDisplay.getId() == R.id.task_info_9) ? ccMonth : djMonth;
        int day = (tvDisplay.getId() == R.id.task_info_9) ? ccDay : djDay;
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                if (tvDisplay.getId() == R.id.task_info_9) {
                    ccYear = i;
                    ccMonth = i1;
                    ccDay = i2;
                } else {
                    djYear = i;
                    djMonth = i1;
                    djDay = i2;
                }
                String year = i + "";
                // month从0开始
                String month = ++i1 < 10 ? "0" + i1 : i1 + "";
                String day = i2 < 10 ? "0" + i2 : i2 + "";
                tvDisplay.setText(year + "-" + month + "-" + day);
            }
        }, year, month, day).show();
    }

    // 若不是新建任务当初始化各个控件初始值，且使每个控件都不可点击
    private void initValue() {
        if (pgrwInfo.getCx().equals("请选择车型")) {
            pgrwInfo.setPpmc("请选择品牌");
            pgrwInfo.setPxmc("请选择车系");
        }
        for (int i = 0; i < views.length; i++) {
            switch (i + 1) {
                // 日期
                case 9:
                    tv_9.setText(pgrwInfo.getCcrq().split("T")[0]);
                    break;
                case 10:
                    tv_10.setText(pgrwInfo.getDjrq().split("T")[0]);
                    break;
                // 车辆品牌、车系、车型
                case 13:
                    tv_13.setText(pgrwInfo.getPpmc());
                    break;
                case 14:
                    tv_14.setText(pgrwInfo.getPxmc());
                    break;
                case 15:
                    tv_15.setText(pgrwInfo.getCx());
                    break;
                // 下拉框
                case 6:
                    sp_6.setVisibility(View.GONE);
                    tv_6.setVisibility(View.VISIBLE);
                    tv_6.setText(pgrwInfo.getHbbz() + "");
                    break;
                case 16:
                    sp_16.setVisibility(View.GONE);
                    tv_16.setVisibility(View.VISIBLE);
                    tv_16.setText(pgrwInfo.getSyxz() + "");
                    break;
                default:
                    ((EditText) views[i]).setText(pgrwInfo.getIndex(i + 1));
                    break;
            }
            views[i].setFocusable(false);
            views[i].setFocusableInTouchMode(false);
            views[i].setEnabled(false);
        }
    }

    private void editTask() {
        for (int i = 0; i < views.length; i++) {
            switch (i + 1) {
                // 下拉框
                case 6:
                    tv_6.setVisibility(View.GONE);
                    sp_6.setVisibility(View.VISIBLE);
                    break;
                case 16:
                    tv_16.setVisibility(View.GONE);
                    sp_16.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
            views[i].setFocusable(true);
            views[i].setFocusableInTouchMode(true);
            views[i].setEnabled(true);
        }
        initTime();
        initSp();
        if (!pgrwInfo.getPpmc().equals("请选择品牌")) {
            // 牵一发而动全身
            getCppList();
        } else {
            isEditing = true;
        }
    }

    // 点击编辑按钮后初始化出厂日期和登记日期
    private void initTime() {
        String ccrq[] = tv_9.getText().toString().split("-");
        ccYear = Integer.parseInt(ccrq[0]);
        ccMonth = Integer.parseInt(ccrq[1]) - 1;
        ccDay = Integer.parseInt(ccrq[2]);

        String djrq[] = tv_10.getText().toString().split("-");
        djYear = Integer.parseInt(djrq[0]);
        djMonth = Integer.parseInt(djrq[1]) - 1;
        djDay = Integer.parseInt(djrq[2]);
    }

    private void initTitleView() {
        titleView = (MyTitleView) findViewById(R.id.title_view);
        titleView.setLeftButton(R.mipmap.back_btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (curTaskStatus == NEW_TASK) {
            titleView.setTitleName("新建任务");
        } else {
            titleView.setTitleName("单号：" + pgrwInfo.getPgdh());
        }
        titleView.setRightButton(R.mipmap.menu_right, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(v);
            }
        });
        titleView.setBg("#303F9F");
    }

    @Override
    public void onBackPressed() {
        if (isEditing) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setMessage("当前任务已经编辑过，是否保存至本地？");
            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    savePgrwInfo();
                }
            });
            builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
        } else {
            finish();
        }
    }

    @Override
    public void finish() {
        if (curTaskStatus != LOCAL_TASK) {
            DataSupport.delete(UploadPgrwInfo.class, taskId);
            // 若当前是新建任务，且没有保存就删掉存储到本地的图片文件夹
            String imgListPath = Environment.getExternalStorageDirectory().getPath()
                    + "/jiuche" + "/imgDir" + taskId;
            deleteDir(new File(imgListPath));
        }
        super.finish();
    }

    // 删除当前文件夹及其子目录所有文件
    // 若当前文件夹存在子目录，则直接删除失败，所以采用下述递归删除
    public static boolean deleteDir(File dir) {
        if (dir.exists()) {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                //递归删除目录中的子目录下
                for (String name : children) {
                    if (!deleteDir(new File(dir, name))) {
                        return false;
                    }
                }
            }
            // 目录此时为空，可以删除
            return dir.delete();
        } else {
            return false;
        }
    }

    private void showPopupWindow(View view) {
        if (popupWindow == null) {
            View popupView = LayoutInflater.from(this).inflate(R.layout.info_popup, null, false);
            // 创建一个PopupWidow对象
            popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setAnimationStyle(R.style.Animation_AppCompat_Dialog);
            TextView tvSave = (TextView) popupView.findViewById(R.id.save_task);
            tvSave.setOnClickListener(this);
            TextView tvUpload = (TextView) popupView.findViewById(R.id.upload_task);
            tvUpload.setOnClickListener(this);
        }
        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_task:
                if (!isEditing) {
                    if (curTaskStatus == ONLINE_TASK) {
                        pgrwInfo.setEdit(true);
                    }
                    ibtEdit.setImageResource(R.mipmap.edit_btn_gray);
                    // 将isEditing = true;写到getCXing的onFinish中，避免线程的干扰
                    editTask();
                }
                break;
            case R.id.save_task:
                popupWindow.dismiss();
                if (isEditing) {
                    savePgrwInfo();
                } else {
                    Toast.makeText(this, "当前状态不可保存", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.upload_task:
                popupWindow.dismiss();
                if (isEditing) {
                    setPgrwInfo();
                    if (checkTaskBeforeUpload()) {
                        checkUploadNet();
                    }
                } else {
                    if (curTaskStatus == LOCAL_TASK) {
                        if (checkTaskBeforeUpload()) {
                            checkUploadNet();
                        }
                    } else {
                        Toast.makeText(this, "当前状态不可上传", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            // 出厂日期
            case R.id.task_info_9:
                setTime(tv_9);
                break;
            // 登记日期
            case R.id.task_info_10:
                setTime(tv_10);
                break;
            // 选择品牌
            case R.id.task_info_13:
                if (cppList.isEmpty()) {
                    if (SystemUtil.isNetworkConnected(this)) {
                        getCppList();
                    }
                } else {
                    startChooseCarInfoAty(cppList, GET_CPP);
                }
                break;
            // 选择车系
            case R.id.task_info_14:
                if (cxList.isEmpty()) {
                    Toast.makeText(this, "请先选择车辆品牌！", Toast.LENGTH_SHORT).show();
                } else {
                    startChooseCarInfoAty(cxList, GET_CX);
                }
                break;
            // 选择车型
            case R.id.task_info_15:
                if (cxingList.isEmpty()) {
                    Toast.makeText(this, "请先选择车辆品牌！", Toast.LENGTH_SHORT).show();
                } else {
                    startChooseCarInfoAty(cxingList, GET_CXING);
                }
                break;
            // 图像采集相关8个ll的点击事件处理
            case R.id.ll_1:
                startDisImgAty(1);
                break;
            case R.id.ll_2:
                startDisImgAty(2);
                break;
            case R.id.ll_3:
                startDisImgAty(3);
                break;
            case R.id.ll_4:
                startDisImgAty(4);
                break;
            case R.id.ll_5:
                startDisImgAty(5);
                break;
            case R.id.ll_6:
                startDisImgAty(6);
                break;
            case R.id.ll_7:
                startDisImgAty(7);
                break;
            case R.id.ll_8:
                startDisImgAty(8);
                break;
            default:
                break;
        }
    }

    // 检查上传网络环境
    private void checkUploadNet() {
        if (SystemUtil.isNetworkConnected(this)) {
            if (SystemUtil.isWIFI(this)) {
                new UploadTask().execute();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage("非WIFI环境，是否继续上传？");
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new UploadTask().execute();
                    }
                });
                builder.setNegativeButton("否", null);
                builder.show();
            }
        } else {
            Toast.makeText(this, "请检查网络连接...", Toast.LENGTH_SHORT).show();
        }
    }

    private void startDisImgAty(int llIndex) {
        Intent intent = new Intent(this, DisImgActivity.class);
        intent.putExtra("isEditing", isEditing);
        intent.putExtra("llIndex", llIndex);
        intent.putExtra("taskId", taskId);
        startActivity(intent);
    }

    private void startChooseCarInfoAty(ArrayList names, int requestCode) {
        Intent intent = new Intent(this, ChooseCarInfo.class);
        intent.putExtra("names", names);
        startActivityForResult(intent, requestCode);
    }

    // 上传之前，若没有编辑检查各约束
    private boolean checkTaskBeforeUpload() {
//        String[] strTemp = {pgrwInfo.getCzmc(), pgrwInfo.getCphm(), pgrwInfo.getPl(), pgrwInfo.getGl(), pgrwInfo.getFdjh(),
//                pgrwInfo.getCjhm(), pgrwInfo.getXslc(), pgrwInfo.getCpxh(), pgrwInfo.getCllx(),
//                pgrwInfo.getDkcs(), pgrwInfo.getZw(), pgrwInfo.getTbrsj(), pgrwInfo.getEscysjg()};
//        for (String temp : strTemp) {
//            if (TextUtils.isEmpty(temp)) {
//                Toast.makeText(this, "相关信息不能为空！", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        }
        // 车排量限制
//        try {
//            Double pl = Double.parseDouble(pgrwInfo.getPl());
//            if (pl > 10) {
//                Toast.makeText(this, "排量必须小于10！", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        } catch (NumberFormatException e) {
//            Toast.makeText(this, "排量必须为数字！", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        // 功率限制
//        try {
//            int temp = Integer.parseInt(pgrwInfo.getGl());
//        } catch (NumberFormatException e) {
//            Toast.makeText(this, "功率必须为数字！", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        // 车架号限制
//        if (pgrwInfo.getCjhm().length() > 17) {
//            Toast.makeText(this, "车架号码必须不大于17位！", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        // 公里数限制
//        try {
//            int temp = Integer.parseInt(pgrwInfo.getXslc());
//        } catch (NumberFormatException e) {
//            Toast.makeText(this, "公里数必须为数字！", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        // 电话号码限制
        if (TextUtils.isEmpty(pgrwInfo.getTbrsj())) {
            Toast.makeText(this, "填报人手机不能为空！", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 日期限制
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date ccrq = sdf.parse(pgrwInfo.getCcrq());
            Date djrq = sdf.parse(pgrwInfo.getDjrq());
            if (ccrq.after(djrq)) {
                Toast.makeText(this, "出厂日期一定不在登记日期之后！", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(this, "日期转换出了问题！", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 座位限制
//        try {
//            int temp = Integer.parseInt(pgrwInfo.getZw());
//        } catch (NumberFormatException e) {
//            Toast.makeText(this, "座位必须为数字！", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        // 车辆相关信息限制
        if (TextUtils.isEmpty(pgrwInfo.getPp())) {
            pgrwInfo.setPp("1");
            pgrwInfo.setPx("1");
            pgrwInfo.setCx("1");
//            Toast.makeText(this, "没有选择车辆相关信息！", Toast.LENGTH_SHORT).show();
//            return false;
        }
        // 预售价格限制
        try {
            double pl = Double.parseDouble(pgrwInfo.getEscysjg());
            if (pl < 1000d) {
                Toast.makeText(this, "预售价格不能低于1000！", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "预售价格必须为数字！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void savePgrwInfo() {
        setPgrwInfo();
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        setResult(MainActivity.SAVE, intent);
        super.finish();
    }

    private void setPgrwInfo() {
        // 我也不知道为毛非要重新冲数据库找一次才不会新增数据，直接覆盖
        pgrwInfo.setCzmc(TextUtils.isEmpty(et_1.getText().toString()) ? "1" : et_1.getText().toString());
        pgrwInfo.setCphm(TextUtils.isEmpty(et_2.getText().toString()) ? "1" : et_2.getText().toString());
        pgrwInfo.setPl(TextUtils.isEmpty(et_3.getText().toString()) ? "1" : et_3.getText().toString());
        pgrwInfo.setGl(TextUtils.isEmpty(et_4.getText().toString()) ? "1" : et_4.getText().toString());
        pgrwInfo.setFdjh(TextUtils.isEmpty(et_5.getText().toString()) ? "1" : et_5.getText().toString());
        pgrwInfo.setHbbz(sp_6.getSelectedItem().toString());
        pgrwInfo.setCjhm(TextUtils.isEmpty(et_7.getText().toString()) ? "1" : et_7.getText().toString());
        pgrwInfo.setXslc(TextUtils.isEmpty(et_8.getText().toString()) ? "1" : et_8.getText().toString());
        pgrwInfo.setCcrq(tv_9.getText().toString());
        pgrwInfo.setDjrq(tv_10.getText().toString());
        pgrwInfo.setCpxh(TextUtils.isEmpty(et_11.getText().toString()) ? "1" : et_11.getText().toString());
        pgrwInfo.setCllx(TextUtils.isEmpty(et_12.getText().toString()) ? "1" : et_12.getText().toString());
        pgrwInfo.setPpmc(tv_13.getText().toString());
        pgrwInfo.setPxmc(tv_14.getText().toString());
        pgrwInfo.setCx(tv_15.getText().toString());
        if (!cppList.isEmpty()) {
            pgrwInfo.setPp(cppIdList.get(cppList.indexOf(tv_13.getText().toString())));
            pgrwInfo.setPx(cxIdList.get(cxList.indexOf(tv_14.getText().toString())));
            pgrwInfo.setCxdm(cxingIdList.get(cxingList.indexOf(tv_15.getText().toString())));
        }
        pgrwInfo.setSyxz(sp_16.getSelectedItem().toString());
        pgrwInfo.setDkcs(TextUtils.isEmpty(et_17.getText().toString()) ? "1" : et_17.getText().toString());
        pgrwInfo.setZw(TextUtils.isEmpty(et_18.getText().toString()) ? "1" : et_18.getText().toString());
        pgrwInfo.setTbrsj(et_19.getText().toString());
        pgrwInfo.setEscysjg(et_20.getText().toString());
        pgrwInfo.setZdr(User.getUserInstance().getZdr());
        pgrwInfo.setIsCheck("0");
        pgrwInfo.setWjsl("0");
        pgrwInfo.setZt("-1");
        // 虽然在添加照片页面已经更新了pgrw，但是在当前Activity中pgrwInfo的imgList还是没变，重新获取一次赋值
        List<UploadImageEntity> imgList = DataSupport.where("uploadpgrwinfo_id = ?", taskId + "")
                .find(UploadImageEntity.class);
        pgrwInfo.setImageEntityList(imgList);
        pgrwInfo.save();
    }

    private class UploadTask extends AsyncTask<Void, Void, Boolean> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(TaskInfoActivity.this);
            dialog.setMessage("正在上传，请耐心等待...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                uploadPgrwImgList();
                uploadPgrwInfo();
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean isSucceed) {
            if (isSucceed) {
                Toast.makeText(TaskInfoActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();
                // 如果是本地任务，或者是刚编辑在线的任务保存直接上传，则
                // 从本地数据库中删除数据，并且删掉当前任务在本地存储图片的文件夹
                if (curTaskStatus == LOCAL_TASK || pgrwInfo.isEdit()) {
                    DataSupport.delete(UploadPgrwInfo.class, taskId);
                    // 若当前是新建任务，且没有保存就删掉存储到本地的图片文件夹
                    String imgListPath = Environment.getExternalStorageDirectory().getPath()
                            + "/jiuche" + "/imgDir" + taskId;
                    deleteDir(new File(imgListPath));
                }
                dialog.dismiss();
                Intent intent = new Intent();
                setResult(MainActivity.UPLOAD, intent);
                TaskInfoActivity.this.finish();
            } else {
                dialog.dismiss();
                Toast.makeText(TaskInfoActivity.this, "网络异常，上传失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs;
        if (src.length - begin > count) {
            bs = new byte[count];
        } else {
            bs = new byte[src.length - begin];
        }
        for (int i = begin; i < begin + count && i < src.length; i++)
            bs[i - begin] = src[i];
        return bs;
    }

    // 将文件转为字节数组
    public byte[] getByteArr(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = fis.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        fis.close();
        return data;
    }

    private void uploadPgrwImgList() throws Exception {
        List<UploadImageEntity> imgList = DataSupport.where("uploadpgrwinfo_id = ?", taskId + "")
                .find(UploadImageEntity.class);
        UploadPgrwInfo uploadPgrwInfo = DataSupport.find(UploadPgrwInfo.class, taskId);
        for (int i = 0; i < imgList.size(); i++) {
            UploadImageEntity uploadImageEntity = imgList.get(i);
            uploadImageEntity.setDjh(uploadPgrwInfo.getOlddjh());
            File imgFile = new File(uploadImageEntity.getFilerealpath());
            // 如果文件不存在说明读自网络，不用上传
            if (!imgFile.exists()) {
                continue;
            }
            byte[] bytes = getByteArr(imgFile);
            // 以块状上传，单位是32kb，不足一块补1
            int blockSize = 1024 * 1024;
            int bCount = bytes.length / blockSize + 1;
            String dateString = new SimpleDateFormat("yyyyMM", Locale.CHINA).format(new Date());
            String imgName = uploadPgrwInfo.getOlddjh() + "_" + uploadImageEntity.getFilefl() + uploadImageEntity.getFilewz() + ".jpg";
            String fullPath = "Upload/IMG/" + dateString + "/" + imgName;
            for (int j = 0; j < bCount; j++) {
                LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
                byte[] buffer;
                buffer = subBytes(bytes, j * blockSize, blockSize);
                String blockS = "00000000" + j + "-" + bCount;
                String blockStr = blockS.substring(blockS.length() - 11, blockS.length());
                properties.put("FullPath", fullPath + "." + blockStr);
                String data = "";
                if (buffer.length > 0) {
                    data = Base64.encode(buffer);
                }
                properties.put("FileContext", data);
                String overWrite = j == 0 ? "true" : "false";
                properties.put("Overwrite", overWrite);
                // 开始上传
                SoapUtil.postSoapRequest(properties, "BeginUpload", null);
            }
            uploadImageEntity.setFilepath(fullPath);
            uploadImageEntity.setScr(User.getUserInstance().getZdr());
            uploadImageEntity.setScrq("2015-07-14");
            uploadImageEntity.setFilesize(bytes.length + "");
            uploadImageEntity.setFilenames(imgName);
            uploadImageEntity.setNewname(imgName);
            uploadImageEntity.save();
        }
    }

    private void uploadPgrwInfo() throws IOException, XmlPullParserException {
        if (isEditing) {
            setPgrwInfo();
        }
        if (cppList.isEmpty()) {
            pgrwInfo.setPp("1");
            pgrwInfo.setPx("1");
            pgrwInfo.setCxdm("1");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        pgrwInfo.setTjrq(sdf.format(new Date()));
        if (TextUtils.isEmpty(pgrwInfo.getZdrq())) {
            pgrwInfo.setZdrq(sdf.format(new Date()));
        }
        pgrwInfo.setZt("");
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("pgrwmodel", pgrwInfo);
        List<UploadImageEntity> imgList = DataSupport.where("uploadpgrwinfo_id = ?", taskId + "")
                .find(UploadImageEntity.class);
        UploadImageList uploadImageList = new UploadImageList();
        for (UploadImageEntity entity : imgList) {
            uploadImageList.add(entity);
        }
        properties.put("list", uploadImageList);
        if (pgrwInfo.isEdit()) {
            properties.put("czfs", "edit");
        } else {
            properties.put("czfs", "creat");
        }
        SoapUtil.postSoapRequest(properties, "SavePgrw", null);
    }
}
