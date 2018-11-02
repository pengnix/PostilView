package com.ruaho.note.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ruaho.note.activity.R;

public class ColorBlockView extends View {
    Paint p;
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
        array.recycle();
        init();
    }

    public ColorBlockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.previewcolor);
        color=array.getColor(R.styleable.previewcolor_mColor,Color.RED);
        array.recycle();
        init();
    }

    void init(){
        p = new Paint();
        p.setColor(color);// 设置红色
        p.setStyle(Paint.Style.FILL);//设置填满
        height = mContext.getResources().getDimension(R.dimen.preview_color_block_height);
        width = height;
        blockWidth = mContext.getResources().getDimension(R.dimen.preview_color_block_inner_height);
        blockHeight = blockWidth;
        float padding = (width - blockWidth)/2;
        left = padding;
        right = left + blockWidth;
        top = padding;
        bottom = top + blockHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(left, top, right, bottom, p);// 正方形
    }
}
