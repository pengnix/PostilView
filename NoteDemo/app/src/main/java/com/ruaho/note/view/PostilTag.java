package com.ruaho.note.view;

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

    public void updatePos(int x,int y){
        xPos = x;
        yPos = y;
    }

    @Override
    public String toString() {
        return "PostilTag{" +
                "xPos=" + xPos +
                ", yPos=" + yPos +
                ", content='" + content + '\'' +
                '}';
    }
}
