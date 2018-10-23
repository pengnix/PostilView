package com.ruaho.note.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class PostilView extends View{

    Paint paint;
    private final String TAG = "PostilView";
    private EditState editState = EditState.NOT_EDIT;

    public PostilView(Context context) {
        super(context);
        init();
    }

    public PostilView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PostilView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init(){
        paint = new Paint();
        paint.setColor(Color.RED);// 设置红色
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("画圆：", 10, 20, paint);// 画文本
        canvas.drawCircle(60, 20, 10, paint);// 小圆
        paint.setAntiAlias(true);// 设置画笔的锯齿效果。 true是去除，大家一看效果就明白了
        canvas.drawCircle(240, 240, 240, paint);// 大圆
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG,event.toString());
        if(editState == EditState.IS_EDIT){
            return true;
        } else{
            return super.onTouchEvent(event);
        }
    }
}
