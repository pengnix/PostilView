package com.ruaho.note.bean;

public class Picture {

    int offsetY;
    int offsetX;
    //放大率，不是除以base的倍数
    float scale;

    String address;

    public float getScale() {
        return scale;
    }

    public Picture(int offsetY, String address) {
        this.offsetY = offsetY;
        this.address = address;
    }

    public Picture(int offsetX,int offsetY, String address, float scale) {
        this(offsetY,address);
        this.offsetX = offsetX;
        this.scale = scale;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Picture{" +
                "offsetY=" + offsetY +
                ", address='" + address + '\'' +
                '}';
    }
}
