package com.cjt_pc.vehicleregulatoryestimate.my_view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.cjt_pc.vehicleregulatoryestimate.R;
import com.cjt_pc.vehicleregulatoryestimate.activity.SysSettingsActivity;
import com.cjt_pc.vehicleregulatoryestimate.activity.TaskInfoActivity;

/**
 * Created by cjt-pc on 2015/8/12.
 * Email:879309896@qq.com
 */
public class LeftSlideLayout extends LinearLayout implements View.OnClickListener {

    LinearLayout llNewTask, llSysSettings, llExit;

    public LeftSlideLayout(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.left_slide_layout, this);
        llNewTask = (LinearLayout) findViewById(R.id.new_task);
        llNewTask.setOnClickListener(this);
        llSysSettings = (LinearLayout) findViewById(R.id.system_settings);
        llSysSettings.setOnClickListener(this);
        llExit = (LinearLayout) findViewById(R.id.exit_app);
        llExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.new_task:
                intent = new Intent(getContext(), TaskInfoActivity.class);
                getContext().startActivity(intent);
                break;
            case R.id.system_settings:
                intent = new Intent(getContext(), SysSettingsActivity.class);
                getContext().startActivity(intent);
                break;
            case R.id.exit_app:
                System.exit(0);
                break;
        }
    }
}
