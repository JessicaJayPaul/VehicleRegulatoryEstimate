package com.cjt_pc.vehicleregulatoryestimate.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.cjt_pc.vehicleregulatoryestimate.R;
import com.cjt_pc.vehicleregulatoryestimate.entity.User;
import com.cjt_pc.vehicleregulatoryestimate.utils.SoapCallBackListener;
import com.cjt_pc.vehicleregulatoryestimate.utils.SoapUtil;
import com.cjt_pc.vehicleregulatoryestimate.utils.SystemUtil;

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

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_layout);
        preferences = getSharedPreferences("data", MODE_PRIVATE);
        initWidget();
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
        String account = etAccount.getText().toString();
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
                    callLoginIn(account, pwd);
                } else {
                    Toast.makeText(this, "请检查网络连接...", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "账号或者密码为空！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void callLoginIn(final String account, final String pwd) {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("TrueName", account);
        properties.put("Pwd", pwd);
        SoapUtil.postSoapRequest(this, properties, "LoginIn", new SoapCallBackListener() {
            @Override
            public void onFinish(SoapObject soapObject) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("account", account);
                editor.apply();
                if (soapObject.getPropertyCount() == 0) {
                    failedLogin();
                } else {
                    succeedLogin(editor, pwd, soapObject);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("cjt-pc", e.toString());
            }
        });
    }

    // 登陆失败
    private void failedLogin() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Toast.makeText(LoginInActivity.this,
                        "账号或者密码错误，请仔细核对！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 登陆成功
    private void succeedLogin(SharedPreferences.Editor editor, String pwd, SoapObject soapObject) {
        if (cbRemember.isChecked()) {
            editor.putString("pwd", pwd);
            editor.putBoolean("is_remember", true);
        } else {
            editor.putBoolean("is_remember", false);
        }
        editor.apply();
        User.getUserInstance().setZdr(soapObject.getPrimitivePropertyAsString("grdm"));
        Intent intent = new Intent(LoginInActivity.this, MainActivity.class);
        startActivity(intent);
        progressDialog.dismiss();
        this.finish();
    }
}
