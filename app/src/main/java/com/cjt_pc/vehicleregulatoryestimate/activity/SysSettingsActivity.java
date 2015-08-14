package com.cjt_pc.vehicleregulatoryestimate.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

import com.cjt_pc.vehicleregulatoryestimate.R;
import com.cjt_pc.vehicleregulatoryestimate.my_view.MyTitleView;

/**
 * Created by cjt-pc on 2015/6/25.
 * Email:879309896@qq.com
 */
public class SysSettingsActivity extends Activity {

    MyTitleView titleView;
    private ToggleButton tbtCameraHelper;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = getSharedPreferences("data", MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity_layout);
        initWidget();
    }

    public void initWidget() {
        titleView = (MyTitleView) findViewById(R.id.system_settings_title);
        titleView.setLeftButton(R.mipmap.back_btn, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        titleView.setTitleName("系统设置");
        titleView.setBg("#303F9F");
        tbtCameraHelper = (ToggleButton) findViewById(R.id.take_picture_help);
        tbtCameraHelper.setChecked(preferences.getBoolean("isShowHelper", true));
    }

    @Override
    public void onBackPressed() {
        preferences = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isShowHelper", tbtCameraHelper.isChecked());
        editor.apply();
        super.onBackPressed();
    }
}