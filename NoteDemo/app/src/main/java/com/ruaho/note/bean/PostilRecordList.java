package com.ruaho.note.bean;

import java.util.ArrayList;
import java.util.List;

public class PostilRecordList {

    List<Picture> picList;

    public PostilRecordList() {
        this.picList = new ArrayList<Picture>();
    }

    public PostilRecordList(List<Picture> picList) {
        this.picList = picList;
    }

    public List<Picture> getPicList() {
        return picList;
    }

    public void setPicList(List<Picture> picList) {
        this.picList = picList;
    }

    public boolean isEmpty(){
        return picList == null || picList.isEmpty();
    }

    @Override
    public String toString() {
        return "PostilRecordList{" +
                "picList=" + picList +
                '}';
    }
}
