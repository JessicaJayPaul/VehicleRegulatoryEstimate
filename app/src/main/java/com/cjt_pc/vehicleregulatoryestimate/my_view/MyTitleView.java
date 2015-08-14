package com.cjt_pc.vehicleregulatoryestimate.my_view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjt_pc.vehicleregulatoryestimate.R;

/**
 * Created by cjt-pc on 2015/7/5.
 * Email:879309896@qq.com
 */
public class MyTitleView extends LinearLayout {

    private ImageButton ibtLeft, ibtRight;
    private TextView tvTitle;

    public ImageButton getIbtRight() {
        return ibtRight;
    }

    public MyTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.custom_title_view, this);
        ibtLeft = (ImageButton) findViewById(R.id.ibt_left);
        ibtRight = (ImageButton) findViewById(R.id.ibt_right);
        tvTitle = (TextView) findViewById(R.id.title_name);
    }

    public void setBg(String rgb) {
        this.setBackgroundColor(Color.parseColor(rgb));
    }

    public void setLeftButton(int id, OnClickListener listener) {
        ibtLeft.setVisibility(View.VISIBLE);
        ibtLeft.setImageResource(id);
        ibtLeft.setOnClickListener(listener);
    }

    public void setTitleName(String titleName) {
        tvTitle.setVisibility(VISIBLE);
        tvTitle.setText(titleName);
    }

    public void setRightButton(int id, OnClickListener listener) {
        ibtRight.setVisibility(VISIBLE);
        ibtRight.setImageResource(id);
        ibtRight.setOnClickListener(listener);
    }
}
