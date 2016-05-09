package com.cjt_pc.vehicleregulatoryestimate.entity;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.litepal.crud.DataSupport;

import java.util.Hashtable;

/**
 * Created by shuciqi on 15/7/14.
 * Email: shuciqi@gmail.com
 */
public class UploadImageEntity extends DataSupport implements KvmSerializable {

    //djz登记照，xsz行驶证，mp铭牌，cswg车身外观，ctgj车体骨架，clns车辆内饰，cybc差异化补充，ycbx原车保险

    // 自增id
    private int id;

    private String djh;
    private String exfile;
    private String filefl;
    private String filenames;
    private String filerealpath;
    private String filepath;
    private String filesize;
    private String filewz;
    private String flm;
    private String newname;
    private String scr;
    private String scrq;
    private String yxbz;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDjh() {
        return djh;
    }

    public void setDjh(String djh) {
        this.djh = djh;
    }

    public String getExfile() {
        return exfile;
    }

    public void setExfile(String exfile) {
        this.exfile = exfile;
    }

    public String getFilefl() {
        return filefl;
    }

    public void setFilefl(String filefl) {
        this.filefl = filefl;
    }

    public String getFilenames() {
        return filenames;
    }

    public void setFilenames(String filenames) {
        this.filenames = filenames;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFilerealpath() {
        return filerealpath;
    }

    public void setFilerealpath(String filerealpath) {
        this.filerealpath = filerealpath;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public String getFilewz() {
        return filewz;
    }

    public void setFilewz(String filewz) {
        this.filewz = filewz;
    }

    public String getFlm() {
        return flm;
    }

    public void setFlm(String flm) {
        this.flm = flm;
    }

    public String getNewname() {
        return newname;
    }

    public void setNewname(String newname) {
        this.newname = newname;
    }

    public String getScr() {
        return scr;
    }

    public void setScr(String scr) {
        this.scr = scr;
    }

    public String getScrq() {
        return scrq;
    }

    public void setScrq(String scrq) {
        this.scrq = scrq;
    }

    public String getYxbz() {
        return yxbz;
    }

    public void setYxbz(String yxbz) {
        this.yxbz = yxbz;
    }


    @Override
    public int getPropertyCount() {
        return 12;
    }

    @Override
    public Object getProperty(int i) {
        switch (i) {
            case 0:
                return djh;
            case 1:
                return exfile;
            case 2:
                return filefl;
            case 3:
                return filenames;
            case 4:
                return filepath;
            case 5:
                return filesize;
            case 6:
                return filewz;
            case 7:
                return flm;
            case 8:
                return newname;
            case 9:
                return scr;
            case 10:
                return scrq;
            case 11:
                return yxbz;
        }
        return null;
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        propertyInfo.setNamespace("http://schemas.datacontract.org/2004/07/SystemSettingModel");
        switch (i) {
            case 0:
                propertyInfo.name = "djh";
                break;
            case 1:
                propertyInfo.name = "exfile";
                break;
            case 2:
                propertyInfo.name = "filefl";
                break;
            case 3:
                propertyInfo.name = "filenames";
                break;
            case 4:
                propertyInfo.name = "filepath";
                break;
            case 5:
                propertyInfo.name = "filesize";
                break;
            case 6:
                propertyInfo.name = "filewz";
                break;
            case 7:
                propertyInfo.name = "flm";
                break;
            case 8:
                propertyInfo.name = "newname";
                break;
            case 9:
                propertyInfo.name = "scr";
                break;
            case 10:
                propertyInfo.name = "scrq";
                break;
            case 11:
                propertyInfo.name = "yxbz";
                break;
        }
    }

    @Override
    public void setProperty(int i, Object o) {

    }

    @Override
    public void setInnerText(String s) {

    }

    @Override
    public String getInnerText() {
        return null;
    }
}
