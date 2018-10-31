package com.ruaho.note.bean;

public class Picture {

    int offset;
    String address;

    public Picture(int offset, String address) {
        this.offset = offset;
        this.address = address;
    }

    @Override
    public String toString() {
        return "Picture{" +
                "offset=" + offset +
                ", address='" + address + '\'' +
                '}';
    }
}
