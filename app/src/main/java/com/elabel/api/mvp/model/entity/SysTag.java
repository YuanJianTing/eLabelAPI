package com.elabel.api.mvp.model.entity;

public class SysTag {

    private int id;
    private int userId;
    private String mac;
    private boolean activate;
    private String secretKey;
    private String tagType;
    private String version;
    private boolean share;
    private double powerVal;
    private double temperature;
    private double rssi;
    private Integer templateId;
    private String displayName;
    private boolean assets;
    private Integer ownerUser;
    private String ownerName;
    private boolean isModify;
    private String displayImage;

    public String getDisplayImage() {
        return displayImage;
    }

    public void setDisplayImage(String displayImage) {
        this.displayImage = displayImage;
    }

    public boolean isModify() {
        return isModify;
    }

    public void setModify(boolean modify) {
        isModify = modify;
    }

    public SysTag() {
    }

    public SysTag(int id) {
        this.id = id;
    }

    public boolean isAssets() {
        return assets;
    }

    public void setAssets(boolean assets) {
        this.assets = assets;
    }

    public Integer getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(Integer ownerUser) {
        this.ownerUser = ownerUser;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public double getPowerVal() {
        return powerVal;
    }

    public void setPowerVal(double powerVal) {
        this.powerVal = powerVal;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getRssi() {
        return rssi;
    }

    public void setRssi(double rssi) {
        this.rssi = rssi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public boolean isActivate() {
        return activate;
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isShare() {
        return share;
    }

    public void setShare(boolean share) {
        this.share = share;
    }
}
