package com.ruaho.note.view;

public class PostilTag {
    int xPos;
    int yPos;
    String content;

    public PostilTag() {
        this.xPos = 0;
        this.yPos = 0;
        this.content = "";
    }

    public PostilTag(int xPos, int yPos, String content) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.content = content;
    }
}
