package com.cjt_pc.vehicleregulatoryestimate.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cjt_pc.vehicleregulatoryestimate.my_view.LeftSlideLayout;
import com.cjt_pc.vehicleregulatoryestimate.my_view.MiddleSlideLayout;
import com.cjt_pc.vehicleregulatoryestimate.my_view.MultiListView;
import com.cjt_pc.vehicleregulatoryestimate.my_view.MyTitleView;
import com.cjt_pc.vehicleregulatoryestimate.my_view.SlidingLayout;

import org.litepal.tablemanager.Connector;

/**
 * Created by cjt-pc on 2015/8/8.
 * Email:879309896@qq.com
 */
public class MainActivity extends Activity {

    public final static int UPDATE_LIST = 0;
    public final static int SAVE = 1;
    public final static int UPLOAD = 2;

    private MiddleSlideLayout middleSlideLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SlidingLayout slidingLayout = new SlidingLayout(this);
        slidingLayout.setWidthRate(0.7);
        slidingLayout.setLeftSlideLayout(new LeftSlideLayout(this));
        middleSlideLayout = new MiddleSlideLayout(this, slidingLayout);
        slidingLayout.setMiddleSlideLayout(middleSlideLayout);
        setContentView(slidingLayout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPDATE_LIST) {
            switch (resultCode) {
                case SAVE:
                    middleSlideLayout.getPgrwList(true);
                    break;
                case UPLOAD:
                    middleSlideLayout.uploadList();
                    break;
                default:
                    break;
            }
        }
    }

    // 再按一次退出应用
    private Long exitTime = 0l;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
            return;
        }
        System.exit(0);
    }
}
