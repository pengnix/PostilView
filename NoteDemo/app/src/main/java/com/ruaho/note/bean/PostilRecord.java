package com.ruaho.note.bean;

import java.util.List;

public class PostilRecord {

    List<Picture> picList;

    public PostilRecord(List<Picture> picList) {
        this.picList = picList;
    }

    @Override
    public String toString() {
        return "PostilRecord{" +
                "picList=" + picList +
                '}';
    }
}
