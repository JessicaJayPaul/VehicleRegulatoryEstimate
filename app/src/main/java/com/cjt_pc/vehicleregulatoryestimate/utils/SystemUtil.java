package com.cjt_pc.vehicleregulatoryestimate.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import org.kobjects.base64.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cjt-pc on 2015/8/8.
 * Email:879309896@qq.com
 */
public class SystemUtil {
    // 网络是否可用
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    // 在网络可用情况下，才会判断是否是WiFi环境
    public static boolean isWIFI(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        return info.getTypeName().equals("WIFI");
    }

    //图片转化成base64字符串
    public static String GetImageStr(String imgName) {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        if (!new File(imgName).exists()) {
            return null;
        }
        InputStream in;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(imgName);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        return data == null ? null : Base64.encode(data);//返回Base64编码过的字节数组字符串
    }

    // 检查是否有内存卡
    public static boolean isHasSdCard() {
        return Environment.getExternalStorageState().
                equals(android.os.Environment.MEDIA_MOUNTED);
    }
}
