package com.cjt_pc.vehicleregulatoryestimate.utils;

import org.ksoap2.serialization.SoapObject;

/**
 * Created by cjt-pc on 2015/8/8.
 * Email:879309896@qq.com
 */
public interface SoapCallBackListener {
    void onFinish(SoapObject soapObject);
    void onError(Exception e);
}
