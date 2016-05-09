package com.cjt_pc.vehicleregulatoryestimate.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.cjt_pc.vehicleregulatoryestimate.R;
import com.cjt_pc.vehicleregulatoryestimate.entity.User;
import com.cjt_pc.vehicleregulatoryestimate.utils.SoapUtil;
import com.cjt_pc.vehicleregulatoryestimate.utils.SystemUtil;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

import org.ksoap2.serialization.SoapObject;

import java.util.LinkedHashMap;

/**
 * Created by cjt-pc on 2015/8/8.
 * Email:879309896@qq.com
 */
public class LoginInActivity extends Activity implements View.OnClickListener {

    private EditText etAccount, etPwd;
    CheckBox cbRemember;
    Button btLogin;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_layout);
        preferences = getSharedPreferences("data", MODE_PRIVATE);
        initWidget();
        checkUpdate();
    }

    private void checkUpdate() {
        PgyUpdateManager.register(LoginInActivity.this, new UpdateManagerListener() {
            @Override
            public void onUpdateAvailable(final String result) {
                // 将新版本信息封装到AppBean中
                final AppBean appBean = getAppBeanFromString(result);
                new AlertDialog.Builder(LoginInActivity.this)
                        .setTitle("提示")
                        .setMessage("检测到有新的版本，是否立即更新？")
                        .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startDownloadTask(LoginInActivity.this, appBean.getDownloadURL());
                            }
                        })
                        .setNegativeButton("不了", null)
                        .show();
            }

            @Override
            public void onNoUpdateAvailable() {
            }
        });
    }


    private void initWidget() {
        etAccount = (EditText) findViewById(R.id.account);
        etAccount.setText(preferences.getString("account", ""));
        etPwd = (EditText) findViewById(R.id.pwd);
        if (preferences.getBoolean("is_remember", false)) {
            etPwd.setText(preferences.getString("pwd", ""));
        }
        cbRemember = (CheckBox) findViewById(R.id.remember);
        cbRemember.setChecked(preferences.getBoolean("is_remember", false));
        btLogin = (Button) findViewById(R.id.login);
        btLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String account = etAccount.getText().toString().trim();
        String pwd = etPwd.getText().toString();
        if (v.getId() == R.id.login) {
            if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(pwd)) {
                if (SystemUtil.isNetworkConnected(this)) {
                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(this);
                        progressDialog.setMessage("正在登陆，请稍后...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }
//                    callLoginIn(account, pwd);
                    new LoginInTask(account, pwd).execute();
                } else {
                    Toast.makeText(this, "请检查网络连接...", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "账号或者密码为空！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 登陆失败
    private void failedLogin() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        Toast.makeText(LoginInActivity.this,
                "账号或者密码错误，请仔细核对！", Toast.LENGTH_SHORT).show();
    }

    // 登陆成功
    private void succeedLogin() {
        editor = preferences.edit();
        if (cbRemember.isChecked()) {
            editor.putString("pwd", etPwd.getText().toString().trim());
            editor.putBoolean("is_remember", true);
        } else {
            editor.putBoolean("is_remember", false);
        }
        editor.apply();
        Intent intent = new Intent(LoginInActivity.this, MainActivity.class);
        startActivity(intent);
        progressDialog.dismiss();
        this.finish();
    }

    /**
     * 登陆异步任务
     */
    public class LoginInTask extends AsyncTask<Void, Void, SoapObject> {

        String account, pwd;

        public LoginInTask(String account, String pwd) {
            this.account = account;
            this.pwd = pwd;
        }

        @Override
        protected SoapObject doInBackground(Void... params) {
            editor = preferences.edit();
            editor.putString("account", account);
            editor.apply();
            LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
            properties.put("TrueName", account);
            properties.put("Pwd", pwd);
            return SoapUtil.postSoapRequest("LoginIn", properties);
        }

        @Override
        protected void onPostExecute(SoapObject soapObject) {
            if (soapObject.getPropertyCount() == 0) {
                failedLogin();
            } else {
                // 登陆成功
                User.getUserInstance().setZdr(soapObject.getPrimitivePropertyAsString("grdm"));
                succeedLogin();
            }
        }
    }
}
