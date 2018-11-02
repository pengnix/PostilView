package com.ruaho.note.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ruaho.note.activity.R;

public class ColorBlockView extends View {
    Paint p;
    Paint marginPaint;
    Paint selectPaint;
    Context mContext;
    float height;
    float width;
    float blockWidth;
    float blockHeight;
    float left;
    float right;
    float top;
    float bottom;
    int color = Color.RED;
    boolean select;
    static final float CORNOR_SIZE = 5.0f;
    float colorMargin;

    public ColorBlockView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ColorBlockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.previewcolor);
        color=array.getColor(R.styleable.previewcolor_mColor,Color.RED);
        select = array.getBoolean(R.styleable.previewcolor_mSelect,false);
        array.recycle();
        init();
    }

    public ColorBlockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.previewcolor);
        color=array.getColor(R.styleable.previewcolor_mColor,Color.RED);
        select = array.getBoolean(R.styleable.previewcolor_mSelect,false);
        array.recycle();
        init();
    }

    void init(){
        p = new Paint();
        p.setColor(color);
        p.setStyle(Paint.Style.FILL);
        marginPaint = new Paint();
        marginPaint.setColor(mContext.getResources().getColor(R.color.dark_gray));
        marginPaint.setStyle(Paint.Style.FILL);
        selectPaint = new Paint();
        selectPaint.setColor(mContext.getResources().getColor(R.color.dark_purple));
        selectPaint.setStyle(Paint.Style.FILL);
        height = mContext.getResources().getDimension(R.dimen.preview_color_block_height);
        width = height;
        blockWidth = mContext.getResources().getDimension(R.dimen.preview_color_block_inner_height);
        blockHeight = blockWidth;
        float padding = (width - blockWidth)/2;
        left = padding;
        right = left + blockWidth;
        top = padding;
        bottom = top + blockHeight;
        colorMargin = mContext.getResources().getDimension(R.dimen.preview_color_margin);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            if(select){
                canvas.drawRoundRect(left - colorMargin, top - colorMargin, right + colorMargin, bottom + colorMargin,CORNOR_SIZE,CORNOR_SIZE, selectPaint);
                canvas.drawRoundRect(left + colorMargin, top + colorMargin, right - colorMargin, bottom - colorMargin,CORNOR_SIZE,CORNOR_SIZE, p);
            } else {
                canvas.drawRoundRect(left - colorMargin, top - colorMargin, right + colorMargin, bottom + colorMargin,CORNOR_SIZE,CORNOR_SIZE, marginPaint);
                canvas.drawRoundRect(left, top, right, bottom,CORNOR_SIZE,CORNOR_SIZE, p);
            }
        } else {
            if(select){
                canvas.drawRect(left - colorMargin, top - colorMargin, right + colorMargin, bottom + colorMargin, selectPaint);
                canvas.drawRect(left + colorMargin, top + colorMargin, right - colorMargin, bottom - colorMargin, p);// 正方形
            } else{
                canvas.drawRect(left - colorMargin, top - colorMargin, right + colorMargin, bottom + colorMargin, p);
                canvas.drawRect(left, top, right, bottom, p);// 正方形
            }
        }
    }

    public int getBlockColor(){
        return this.color;
    }
}
