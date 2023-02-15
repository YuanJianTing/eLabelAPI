package com.elabel.api.blesdk;

import java.io.Serializable;

public class BleDeviceFeedback implements Serializable {
    private double power;
    private int temperature;

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
}
