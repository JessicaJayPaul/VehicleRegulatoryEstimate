package com.cjt_pc.vehicleregulatoryestimate.entity;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;
import java.util.Vector;

/**
 * 因为在WCF传输数据时需要序列化，就算传递的集合的元素已经序列化也不行，非得将传递的集合序列化
 * <p/>
 * Created by shuciqi on 15/7/15.
 * Email: shuciqi@gmail.com
 */
public class UploadImageList extends Vector<UploadImageEntity> implements KvmSerializable {

    String n1 = "http://schemas.datacontract.org/2004/07/SystemSettingModel";

    @Override
    public String getInnerText() {
        return null;
    }

    @Override
    public void setInnerText(String s) {

    }

    @Override
    public Object getProperty(int i) {
        return this.get(i);
    }

    @Override
    public int getPropertyCount() {
        return this.size();
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        propertyInfo.setName("UpLoadModel");
        propertyInfo.setNamespace(n1);
    }

    @Override
    public void setProperty(int i, Object o) {
        this.add((UploadImageEntity) o);
    }
}
