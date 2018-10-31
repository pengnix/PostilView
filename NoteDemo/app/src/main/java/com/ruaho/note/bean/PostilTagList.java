package com.ruaho.note.bean;

import java.util.ArrayList;
import java.util.List;

public class PostilTagList {
    List<PostilTag> list;

    public PostilTagList() {
        this.list = new ArrayList<PostilTag>();
    }

    public PostilTagList(List<PostilTag> list) {
        this.list = list;
    }

    public List<PostilTag> getList() {
        return list;
    }
}
