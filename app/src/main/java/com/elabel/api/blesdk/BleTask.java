package com.elabel.api.blesdk;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.util.List;

public class BleTask {

    private String mac;
    private String key;
    private List<String> pack;
    private int sendCount=0;
    public BleTask(String mac) {
        this.mac = mac;
    }

    public BleTask() {
    }

    public String getTagType() {
        if(TextUtils.isEmpty(mac))
            return "";
        return mac.substring(0,2);
    }

    public int getSendCount() {
        return sendCount;
    }

    public void setSendCount() {
        this.sendCount++;
    }


    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getPack() {
        return pack;
    }

    public void setPack(List<String> pack) {
        this.pack = pack;
    }


}
