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

    int offsetX;
    int offsetY;
    float scale;
    int xPos;
    int yPos;
    //图片地址
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

    public PostilWord(int offsetX,int offsetY, int xPos, int yPos,float scale, String content, String address) {
        this(offsetX,offsetY,xPos,yPos,content,address);
        this.scale = scale;
    }

    public PostilWord(int offsetX,int offsetY, int xPos, int yPos,float scale, String content) {
        this(offsetY,xPos,yPos,content);
        this.scale = scale;
        this.offsetX = offsetX;
    }

    public PostilWord(int offsetX,int offsetY, int xPos, int yPos, String content, String address) {
        this(offsetY,xPos,yPos,content);
        this.offsetX = offsetX;
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

    public void updateAll(int offsetX,int offsetY,int x,int y,float scale,String content){
        updateAll(offsetY,x,y,content);
        this.offsetX = offsetX;
        this.scale = scale;
    }

    public String getAddress() {
        return address;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public float getScale() {
        return scale;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "PostilWord{" +
                "offsetX=" + offsetX +
                ", offsetY=" + offsetY +
                ", scale=" + scale +
                ", xPos=" + xPos +
                ", yPos=" + yPos +
                ", address='" + address + '\'' +
                ", content='" + content + '\'' +
                ", canMove=" + canMove +
                '}';
    }
}
