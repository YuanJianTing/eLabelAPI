package com.elabel.api.mvp.model.entity;

import java.util.List;

public class BleData {
    private List<String> data;
    private String imageUrl;

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
