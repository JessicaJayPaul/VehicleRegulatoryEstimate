package com.cjt_pc.test;

import org.litepal.crud.DataSupport;

/**
 * Created by cjt-pc on 2015/8/15.
 * Email:879309896@qq.com
 */
public class User extends DataSupport{
    private int id;
    private String account;
    private String pwd;
    private String email;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
