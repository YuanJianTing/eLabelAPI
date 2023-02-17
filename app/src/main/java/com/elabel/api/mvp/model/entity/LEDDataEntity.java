package com.elabel.api.mvp.model.entity;

public class LEDDataEntity {
    private String mac;
    /**
     * The time should be between 1 and 65535
     */
    private short time;
    private boolean r;
    private boolean g;
    private boolean b;

    public LEDDataEntity() {
    }

    public LEDDataEntity(String mac, short time, boolean r, boolean g, boolean b) {
        this.mac = mac;
        this.time = time;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public short getTime() {
        return time;
    }

    public void setTime(short time) {
        this.time = time;
    }

    public boolean isR() {
        return r;
    }

    public void setR(boolean r) {
        this.r = r;
    }

    public boolean isG() {
        return g;
    }

    public void setG(boolean g) {
        this.g = g;
    }

    public boolean isB() {
        return b;
    }

    public void setB(boolean b) {
        this.b = b;
    }
}
