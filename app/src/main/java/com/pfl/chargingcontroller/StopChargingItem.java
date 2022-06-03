package com.pfl.chargingcontroller;

public class StopChargingItem {

    private String path;
    private String onValue;
    private String offValue;

    public StopChargingItem() {

    }

    public StopChargingItem(String path, String onValue, String offValue) {
        this.path = path;
        this.onValue = onValue;
        this.offValue = offValue;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOnValue() {
        return onValue;
    }

    public void setOnValue(String onValue) {
        this.onValue = onValue;
    }

    public String getOffValue() {
        return offValue;
    }

    public void setOffValue(String offValue) {
        this.offValue = offValue;
    }
}
