package com.cjt_pc.vehicleregulatoryestimate.entity;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by shuciqi on 15/7/13.
 * Email: shuciqi@gmail.com
 */
public class UploadPgrwInfo extends DataSupport implements KvmSerializable, Serializable {

    private boolean edit = false;

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    private String ppmc;

    public void setPpmc(String ppmc) {
        this.ppmc = ppmc;
    }

    public String getPpmc() {
        return this.ppmc;
    }

    private String pxmc;

    public void setPxmc(String pxmc) {
        this.pxmc = pxmc;
    }

    public String getPxmc() {
        return pxmc;
    }

    public String cxmc;

    public void setCxmc(String cxmc) {
        this.cxmc = cxmc;
    }

    public String getCxmc() {
        return cxmc;
    }

    // 自增id
    private int id;
    // 1对多，每一个评估任务下面的图片列表
    private List<UploadImageEntity> imageEntityList = new ArrayList<>();
    private String czfs;

    private String ccrq;
    private String isCheck;
    private String cjhm;
    private String cllx;
    private String clsyxz;
    private String cphm;
    private String cpxh;
    private String cx;
    private String cxdm;
    private String czmc;
    private String djrq;
    private String dkcs;
    private String escysjg;
    private String fdjh;
    private String gl;
    private String hbbz;
    private String olddjh;
    private String pgdh;
    private String pl;
    private String pp;
    private String px;
    private String syxz;
    private String tbrsj;
    private String tjrq;
    private String wjsl;
    private String xslc;
    private String zdr;
    private String zdrq;
    private String zt;
    private String zw;
    private String ztsm;


    public static UploadPgrwInfo getUploadPgrwInfo(SoapObject object) {
        UploadPgrwInfo pgrwInfo = new UploadPgrwInfo();
        pgrwInfo.setZt(object.getPrimitivePropertyAsString("zt"));
        pgrwInfo.setSyxz(object.getPrimitivePropertyAsString("syxz"));
        pgrwInfo.setPgdh(object.getPrimitivePropertyAsString("pgdh"));
        pgrwInfo.setCzmc(object.getPrimitivePropertyAsString("czmc"));
        pgrwInfo.setCphm(object.getPrimitivePropertyAsString("cphm"));
        pgrwInfo.setPl(object.getPrimitivePropertyAsString("pl"));
        pgrwInfo.setGl(object.getPrimitivePropertyAsString("gl"));
        pgrwInfo.setFdjh(object.getPrimitivePropertyAsString("fdjh"));
        pgrwInfo.setHbbz(object.getPrimitivePropertyAsString("hbbz"));
        pgrwInfo.setCjhm(object.getPrimitivePropertyAsString("cjhm"));
        pgrwInfo.setXslc(object.getPrimitivePropertyAsString("xslc"));
        pgrwInfo.setCcrq(object.getPrimitivePropertyAsString("ccrq"));
        pgrwInfo.setDjrq(object.getPrimitivePropertyAsString("djrq"));
        pgrwInfo.setCpxh(object.getPrimitivePropertyAsString("cpxh"));
        pgrwInfo.setCllx(object.getPrimitivePropertyAsString("cllx"));
        pgrwInfo.setPpmc(object.getPrimitivePropertyAsString("ppsm"));
        pgrwInfo.setPp(object.getPrimitivePropertyAsString("pp"));
        pgrwInfo.setPx(object.getPrimitivePropertyAsString("px"));
        pgrwInfo.setPxmc(object.getPrimitivePropertyAsString("pxsm"));
        pgrwInfo.setCx(object.getPrimitivePropertyAsString("cx"));
        pgrwInfo.setClsyxz(object.getPrimitivePropertyAsString("clsyxz"));
        pgrwInfo.setDkcs(object.getPrimitivePropertyAsString("dkcs"));
        pgrwInfo.setZw(object.getPrimitivePropertyAsString("zw"));
        pgrwInfo.setTbrsj(object.getPrimitivePropertyAsString("tbrsj"));
        pgrwInfo.setEscysjg(object.getPrimitivePropertyAsString("escysjg"));
        pgrwInfo.setZtsm(object.getPrimitivePropertyAsString("ztsm"));
        return pgrwInfo;
    }

    public String getZtsm() {
        return ztsm;
    }

    public void setZtsm(String ztsm) {
        this.ztsm = ztsm;
    }

    public String getClsyxz() {
        return clsyxz;
    }

    public void setClsyxz(String clsyxz) {
        this.clsyxz = clsyxz;
    }

    public String getDkcs() {
        return dkcs;
    }

    public void setDkcs(String dkcs) {
        this.dkcs = dkcs;
    }

    public String getCzfs() {
        return czfs;
    }

    public void setCzfs(String czfs) {
        this.czfs = czfs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<UploadImageEntity> getImageEntityList() {
        return imageEntityList;
    }

    public void setImageEntityList(List<UploadImageEntity> imageEntityList) {
        this.imageEntityList = imageEntityList;
    }

    public String getCcrq() {
        return ccrq;
    }

    public void setCcrq(String ccrq) {
        this.ccrq = ccrq;
    }

    public String getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(String isCheck) {
        this.isCheck = isCheck;
    }

    public String getCjhm() {
        return cjhm;
    }

    public void setCjhm(String cjhm) {
        this.cjhm = cjhm;
    }

    public String getCllx() {
        return cllx;
    }

    public void setCllx(String cllx) {
        this.cllx = cllx;
    }

    public String getCphm() {
        return cphm;
    }

    public void setCphm(String cphm) {
        this.cphm = cphm;
    }

    public String getCpxh() {
        return cpxh;
    }

    public void setCpxh(String cpxh) {
        this.cpxh = cpxh;
    }

    public String getCx() {
        return cx;
    }

    public void setCx(String cx) {
        this.cx = cx;
    }

    public String getCxdm() {
        return cxdm;
    }

    public void setCxdm(String cxdm) {
        this.cxdm = cxdm;
    }

    public String getCzmc() {
        return czmc;
    }

    public void setCzmc(String czmc) {
        this.czmc = czmc;
    }

    public String getDjrq() {
        return djrq;
    }

    public void setDjrq(String djrq) {
        this.djrq = djrq;
    }

    public String getEscysjg() {
        return escysjg;
    }

    public void setEscysjg(String escysjg) {
        this.escysjg = escysjg;
    }

    public String getFdjh() {
        return fdjh;
    }

    public void setFdjh(String fdjh) {
        this.fdjh = fdjh;
    }

    public String getGl() {
        return gl;
    }

    public void setGl(String gl) {
        this.gl = gl;
    }

    public String getHbbz() {
        return hbbz;
    }

    public void setHbbz(String hbbz) {
        this.hbbz = hbbz;
    }

    public String getOlddjh() {
        return olddjh;
    }

    public void setOlddjh(String olddjh) {
        this.olddjh = olddjh;
    }

    public String getPgdh() {
        return pgdh;
    }

    public void setPgdh(String pgdh) {
        this.pgdh = pgdh;
    }

    public String getPl() {
        return pl;
    }

    public void setPl(String pl) {
        this.pl = pl;
    }

    public String getPp() {
        return pp;
    }

    public void setPp(String pp) {
        this.pp = pp;
    }

    public String getPx() {
        return px;
    }

    public void setPx(String px) {
        this.px = px;
    }

    public String getSyxz() {
        return syxz;
    }

    public void setSyxz(String syxz) {
        this.syxz = syxz;
    }

    public String getTbrsj() {
        return tbrsj;
    }

    public void setTbrsj(String tbrsj) {
        this.tbrsj = tbrsj;
    }

    public String getTjrq() {
        return tjrq;
    }

    public void setTjrq(String tjrq) {
        this.tjrq = tjrq;
    }

    public String getWjsl() {
        return wjsl;
    }

    public void setWjsl(String wjsl) {
        this.wjsl = wjsl;
    }

    public String getXslc() {
        return xslc;
    }

    public void setXslc(String xslc) {
        this.xslc = xslc;
    }

    public String getZdr() {
        return zdr;
    }

    public void setZdr(String zdr) {
        this.zdr = zdr;
    }

    public String getZdrq() {
        return zdrq;
    }

    public void setZdrq(String zdrq) {
        this.zdrq = zdrq;
    }

    public String getZt() {
        return zt;
    }

    public void setZt(String zt) {
        this.zt = zt;
    }

    public String getZw() {
        return zw;
    }

    public void setZw(String zw) {
        this.zw = zw;
    }

    @Override
    public int getPropertyCount() {
        return 30;
    }

    @Override
    public Object getProperty(int i) {
        switch (i) {
            case 0:
                return ccrq;
            case 1:
                return isCheck;
            case 2:
                return cjhm;
            case 3:
                return cllx;
            case 4:
                return clsyxz;
            case 5:
                return cphm;
            case 6:
                return cpxh;
            case 7:
                return cx;
            case 8:
                return cxdm;
            case 9:
                return czmc;
            case 10:
                return djrq;
            case 11:
                return dkcs;
            case 12:
                return escysjg;
            case 13:
                return fdjh;
            case 14:
                return gl;
            case 15:
                return hbbz;
            case 16:
                return olddjh;
            case 17:
                return pgdh;
            case 18:
                return pl;
            case 19:
                return pp;
            case 20:
                return px;
            case 21:
                return syxz;
            case 22:
                return tbrsj;
            case 23:
                return tjrq;
            case 24:
                return wjsl;
            case 25:
                return xslc;
            case 26:
                return zdr;
            case 27:
                return zdrq;
            case 28:
                return zt;
            case 29:
                return zw;
        }
        return null;
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        propertyInfo.setNamespace("http://schemas.datacontract.org/2004/07/VRESystemModel");
        switch (i) {
            case 0:
                propertyInfo.name = "ccrq";
                break;
            case 1:
                propertyInfo.name = "check";
                break;
            case 2:
                propertyInfo.name = "cjhm";
                break;
            case 3:
                propertyInfo.name = "cllx";
                break;
            case 4:
                propertyInfo.name = "clsyxz";
                break;
            case 5:
                propertyInfo.name = "cphm";
                break;
            case 6:
                propertyInfo.name = "cpxh";
                break;
            case 7:
                propertyInfo.name = "cx";
                break;
            case 8:
                propertyInfo.name = "cxdm";
                break;
            case 9:
                propertyInfo.name = "czmc";
                break;
            case 10:
                propertyInfo.name = "djrq";
                break;
            case 11:
                propertyInfo.name = "dkcs";
                break;
            case 12:
                propertyInfo.name = "escysjg";
                break;
            case 13:
                propertyInfo.name = "fdjh";
                break;
            case 14:
                propertyInfo.name = "gl";
                break;
            case 15:
                propertyInfo.name = "hbbz";
                break;
            case 16:
                propertyInfo.name = "olddjh";
                break;
            case 17:
                propertyInfo.name = "pgdh";
                break;
            case 18:
                propertyInfo.name = "pl";
                break;
            case 19:
                propertyInfo.name = "pp";
                break;
            case 20:
                propertyInfo.name = "px";
                break;
            case 21:
                propertyInfo.name = "syxz";
                break;
            case 22:
                propertyInfo.name = "tbrsj";
                break;
            case 23:
                propertyInfo.name = "tjrq";
                break;
            case 24:
                propertyInfo.name = "wjsl";
                break;
            case 25:
                propertyInfo.name = "xslc";
                break;
            case 26:
                propertyInfo.name = "zdr";
                break;
            case 27:
                propertyInfo.name = "zdrq";
                break;
            case 28:
                propertyInfo.name = "zt";
                break;
            case 29:
                propertyInfo.name = "zw";
                break;
        }
    }

    public String getIndex(int index) {
        switch (index) {
            case 1:
                return getCzmc();
            case 2:
                return getCphm();
            case 3:
                return getPl();
            case 4:
                return getGl();
            case 5:
                return getFdjh();
            case 6:
                return getHbbz();
            case 7:
                return getCjhm();
            case 8:
                return getXslc();
            case 9:
                return getCcrq();
            case 10:
                return getDjrq();
            case 11:
                return getCpxh();
            case 12:
                return getCllx();
            case 13:
                return getPp();
            case 14:
                return getPx();
            case 15:
                return getCx();
            case 16:
                return getClsyxz();
            case 17:
                return getDkcs();
            case 18:
                return getZw();
            case 19:
                return getTbrsj();
            case 20:
                return getEscysjg();
        }
        return null;
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
