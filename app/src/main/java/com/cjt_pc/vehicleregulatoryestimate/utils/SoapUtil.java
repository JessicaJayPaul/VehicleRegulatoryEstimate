package com.cjt_pc.vehicleregulatoryestimate.utils;

import android.app.Activity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.LinkedHashMap;

/**
 * Created by cjt-pc on 2015/7/8.
 * Email:879309896@qq.com
 */
public class SoapUtil implements SoapConfig {

    /**
     * 根据传入的参数和访问方法名访问服务器，同时实现一个网络回调接口
     *
     * @param properties 参数
     * @param method     方法名
     * @param listener   回调接口
     */
    public static void postSoapRequest(final Activity activity, final LinkedHashMap<String, Object> properties,
                                       final String method, final SoapCallBackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 设置命名空间namespace访问地址url和访问方法action
                String nameSpace = NAMESPACE;
                String url = getUrl(method);
                String soapAction = getAction(method);
                // 初始化传入参数soapObject
                SoapObject soapObject = new SoapObject(nameSpace, method);
                for (LinkedHashMap.Entry<String, Object> entry : properties.entrySet()) {
                    soapObject.addProperty(entry.getKey(), entry.getValue());
                }
                // 设置版本和指定信封envelope的数据soapObject
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.bodyOut = soapObject;
                envelope.dotNet = true;
                // 采用webservice协议发送请求
                HttpTransportSE transport = new HttpTransportSE(url);
                transport.debug = true;
                try {
                    transport.call(soapAction, envelope);
                } catch (Exception e) {
                    listener.onError(e);
                }
                // 接受服务端返回的数据，取其child
                SoapObject object = (SoapObject) envelope.bodyIn;
                final SoapObject child = (SoapObject) object.getProperty(0);
                // 完成回调函数置于当前Activity的主线程中
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFinish(child);
                    }
                });
            }
        }).start();
    }

    // 重载上面方法，不需要传activity参数
    public static void postSoapRequest(final LinkedHashMap<String, Object> properties,
                                       final String method, final SoapCallBackListener listener) {
        // 设置命名空间namespace访问地址url和访问方法action
        String nameSpace = NAMESPACE;
        String url = getUrl(method);
        String soapAction = getAction(method);
        // 初始化传入参数soapObject
        SoapObject soapObject = new SoapObject(nameSpace, method);
        for (LinkedHashMap.Entry<String, Object> entry : properties.entrySet()) {
            soapObject.addProperty(entry.getKey(), entry.getValue());
        }
        // 设置版本和指定信封envelope的数据soapObject
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
        envelope.bodyOut = soapObject;
        envelope.dotNet = true;
        // 采用webservice协议发送请求
        HttpTransportSE transport = new HttpTransportSE(url);
        transport.debug = true;
        try {
            transport.call(soapAction, envelope);
        } catch (Exception e) {
            listener.onError(e);
        }
        // 接受服务端返回的数据，取其child
        SoapObject object = (SoapObject) envelope.bodyIn;
        SoapObject child = (SoapObject) object.getProperty(0);
        listener.onFinish(child);
    }

    /**
     * 根据传回的方法名返回指定的url
     *
     * @param methodName 方法名
     * @return 访问地址
     */
    private static String getUrl(String methodName) {
        switch (methodName) {
            case LOGININ_METHOD:
                return URL_1;
            case GET_RW_LIST_METHOD:
                return URL_2;
            case GET_HBBZ_METHOD:
                return URL_1;
            case GET_PP_METHOD:
                return URL_2;
            case GET_CXI_METHOD:
                return URL_2;
            case GET_CXING_METHOD:
                return URL_2;
            case GET_UPLOAD_IMG_METHOD:
                return URL_3;
            case UPLOAD_PHOTO_METHOD:
                return URL_3;
            case SAVE_PGRW_METHOD:
                return URL_2;
        }
        return null;
    }

    /**
     * 根据传回的方法名返回指定的action
     *
     * @param methodName 方法名
     * @return 访问action
     */
    private static String getAction(String methodName) {
        switch (methodName) {
            case LOGININ_METHOD:
                return LOGININ_ACTION;
            case GET_RW_LIST_METHOD:
                return GET_RW_LIST_ACTION;
            case GET_HBBZ_METHOD:
                return GET_HBBZ_ACTION;
            case GET_PP_METHOD:
                return GET_PP_ACTION;
            case GET_CXI_METHOD:
                return GET_CXI_ACTION;
            case GET_CXING_METHOD:
                return GET_CXING_ACTION;
            case GET_UPLOAD_IMG_METHOD:
                return GET_UPLOAD_IMG_ACTION;
            case UPLOAD_PHOTO_METHOD:
                return UPLOAD_PHOTO_ACTION;
            case SAVE_PGRW_METHOD:
                return SAVE_PGRW_ACTION;
        }
        return null;
    }
}
