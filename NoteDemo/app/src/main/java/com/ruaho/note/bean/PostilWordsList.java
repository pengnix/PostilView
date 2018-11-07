package com.ruaho.note.bean;

import java.util.ArrayList;
import java.util.List;

public class PostilWordsList {
    List<PostilWord> list;

    public PostilWordsList() {
        this.list = new ArrayList<PostilWord>();
    }

    public PostilWordsList(List<PostilWord> list) {
        this.list = list;
    }

    public List<PostilWord> getList() {
        return list;
    }
}
