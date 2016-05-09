package com.cjt_pc.vehicleregulatoryestimate.utils;

/**
 * Created by cjt-pc on 2015/7/8.
 * Email:879309896@qq.com
 *
 * 主要配置文件
 */
public interface SoapConfig {

    String NAMESPACE = "http://tempuri.org/";

    String BASE_URL = "http://61.183.41.211:8887/";

    String URL_1 = BASE_URL + "WCFService/SystemSettingService.svc";
    String URL_2 = BASE_URL + "WCFService/VRESystemService.svc";
    String URL_3 = BASE_URL + "WCFService/UpLoad/UpLoad.svc";

    String LOGININ_METHOD = "LoginIn";
    String LOGININ_ACTION = NAMESPACE + "ISystemSettingService/LoginIn";
    String GET_RW_LIST_METHOD = "getpgrwList";
    String GET_RW_LIST_ACTION = NAMESPACE + "IVRESystemService/getpgrwList";
    String GET_HBBZ_METHOD = "getDmzdList";
    String GET_HBBZ_ACTION = NAMESPACE + "ISystemSettingService/getDmzdList";
    String GET_PP_METHOD = "getC_ppzdList";
    String GET_PP_ACTION = NAMESPACE + "IVRESystemService/getC_ppzdList";
    String GET_CXI_METHOD = "getC_xlzdList";
    String GET_CXI_ACTION = NAMESPACE + "IVRESystemService/getC_xlzdList";
    String GET_CXING_METHOD = "getC_xhzdList";
    String GET_CXING_ACTION = NAMESPACE + "IVRESystemService/getC_xhzdList";
    String GET_UPLOAD_IMG_METHOD = "GetUpLoadListNew";
    String GET_UPLOAD_IMG_ACTION = NAMESPACE + "IUpLoad/GetUpLoadListNew";
    String UPLOAD_PHOTO_METHOD = "BeginUpload";
    String UPLOAD_PHOTO_ACTION = NAMESPACE + "IUpLoad/BeginUpload";
    String SAVE_PGRW_METHOD = "SavePgrw";
    String SAVE_PGRW_ACTION = NAMESPACE + "IVRESystemService/SavePgrw";
}
