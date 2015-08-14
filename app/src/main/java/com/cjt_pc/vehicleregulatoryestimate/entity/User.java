package com.cjt_pc.vehicleregulatoryestimate.entity;

/**
 * Created by cjt-pc on 2015/8/8.
 * Email:879309896@qq.com
 */
public class User {
    private static User user = null;
    private String zdr = "";

    public String getZdr() {
        return zdr;
    }

    public void setZdr(String zdr) {
        this.zdr = zdr;
    }

    public static User getUserInstance() {
        if (user == null) {
            user = new User();
        }
        return user;
    }
}
