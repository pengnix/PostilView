package com.ruaho.note.bean;

public class Picture {

    int offsetY;
    String address;

    public Picture(int offset, String address) {
        this.offsetY = offset;
        this.address = address;
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
