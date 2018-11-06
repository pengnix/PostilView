package com.ruaho.note.bean;

import java.util.ArrayList;
import java.util.List;

public class PostilRecord {

    List<Picture> picList;

    public PostilRecord() {
        this.picList = new ArrayList<Picture>();
    }

    public PostilRecord(List<Picture> picList) {
        this.picList = picList;
    }

    public List<Picture> getPicList() {
        return picList;
    }

    public void setPicList(List<Picture> picList) {
        this.picList = picList;
    }

    @Override
    public String toString() {
        return "PostilRecord{" +
                "picList=" + picList +
                '}';
    }
}
