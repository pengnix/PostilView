package com.ruaho.note.bean;

public class PostilWord {
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
    String address;
    String content;

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    boolean canMove;

    public boolean isCanMove() {
        return canMove;
    }

    public PostilWord() {
        this.offsetY = 0;
        this.xPos = 0;
        this.yPos = 0;
        this.content = "";
        this.address = "";
        this.canMove = true;
    }

    public PostilWord(int offsetY, int xPos, int yPos, String content) {
        this.offsetY = offsetY;
        this.xPos = xPos;
        this.yPos = yPos;
        this.content = content;
        this.canMove = true;
    }

    public PostilWord(int offsetY, int xPos, int yPos, String content, String address) {
        this.offsetY = offsetY;
        this.xPos = xPos;
        this.yPos = yPos;
        this.content = content;
        this.address = address;
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

    public String getAddress() {
        return address;
    }

    public int getOffsetY() {
        return offsetY;
    }

    @Override
    public String toString() {
        return "PostilWord{" +
                "offsetY=" + offsetY +
                ", xPos=" + xPos +
                ", yPos=" + yPos +
                ", content='" + content + '\'' +
                '}';
    }
}
