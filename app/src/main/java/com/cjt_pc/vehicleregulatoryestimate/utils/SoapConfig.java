package com.cjt_pc.vehicleregulatoryestimate.utils;

/**
 * Created by cjt-pc on 2015/7/8.
 * Email:879309896@qq.com
 */
public interface SoapConfig {

    String NAMESPACE = "http://tempuri.org/";

    String URL_1 = "http://61.183.41.211:8883/WCFService/SystemSettingService.svc";
    String URL_2 = "http://61.183.41.211:8883/WCFService/VRESystemService.svc";
    String URL_3 = "http://61.183.41.211:8883/WCFService/UpLoad/UpLoad.svc";

    String LOGININ_METHOD = "LoginIn";
    String LOGININ_ACTION = "http://tempuri.org/ISystemSettingService/LoginIn";
    String GET_RW_LIST_METHOD = "getpgrwList";
    String GET_RW_LIST_ACTION = "http://tempuri.org/IVRESystemService/getpgrwList";
    String GET_HBBZ_METHOD = "getDmzdList";
    String GET_HBBZ_ACTION = "http://tempuri.org/ISystemSettingService/getDmzdList";
    String GET_PP_METHOD = "getC_ppzdList";
    String GET_PP_ACTION = "http://tempuri.org/IVRESystemService/getC_ppzdList";
    String GET_CXI_METHOD = "getC_xlzdList";
    String GET_CXI_ACTION = "http://tempuri.org/IVRESystemService/getC_xlzdList";
    String GET_CXING_METHOD = "getC_xhzdList";
    String GET_CXING_ACTION = "http://tempuri.org/IVRESystemService/getC_xhzdList";
    String GET_UPLOAD_IMG_METHOD = "GetUpLoadListNew";
    String GET_UPLOAD_IMG_ACTION = "http://tempuri.org/IUpLoad/GetUpLoadListNew";
    String UPLOAD_PHOTO_METHOD = "BeginUpload";
    String UPLOAD_PHOTO_ACTION = "http://tempuri.org/IUpLoad/BeginUpload";
    String SAVE_PGRW_METHOD = "SavePgrw";
    String SAVE_PGRW_ACTION = "http://tempuri.org/IVRESystemService/SavePgrw";
}
