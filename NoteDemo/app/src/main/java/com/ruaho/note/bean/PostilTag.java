package com.ruaho.note.bean;

public class PostilTag {
    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public String getContent() {
        return content;
    }

    int offsetY;
    int xPos;
    int yPos;
    String content;

    public PostilTag() {
        this.offsetY = 0;
        this.xPos = 0;
        this.yPos = 0;
        this.content = "";
    }

    public PostilTag(int offsetY, int xPos, int yPos, String content) {
        this.offsetY = offsetY;
        this.xPos = xPos;
        this.yPos = yPos;
        this.content = content;
    }

    public void updatePos(int x, int y){
        xPos = x;
        yPos = y;
    }

    public void updateAll(int offsetY,int x,int y,String content){
        this.offsetY = offsetY;
        this.xPos = x;
        this.yPos = y;
        this.content = content;
    }

    public int getOffsetY() {
        return offsetY;
    }

    @Override
    public String toString() {
        return "PostilTag{" +
                "offsetY=" + offsetY +
                ", xPos=" + xPos +
                ", yPos=" + yPos +
                ", content='" + content + '\'' +
                '}';
    }
}
