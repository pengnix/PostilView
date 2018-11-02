package com.ruaho.note.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.ruaho.note.bean.PostilTag;

public class ChooseColorLayout extends LinearLayout {

    Callback mCallback = null;

    public ChooseColorLayout(Context context) {
        super(context);
        init();
    }

    public ChooseColorLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChooseColorLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public interface Callback {
        void changePenColor(int color);
    }

    public void setCallback(Callback callback){
        mCallback = callback;
    }

    private void init(){
//        int count = getChildCount();
//        Log.i("getColor","count = " + count);
//        for(int i = 0;i<count;i++){
//            Log.i("getColor","i = " + i);
//            ColorBlockView childView = (ColorBlockView) getChildAt(i);
//            childView.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.i("getColor","color = " + ((ColorBlockView)view).getBlockColor());
//                }
//            });
//        }
    }

    public void lazyInit(){
        int count = getChildCount();
        Log.i("getColor","count = " + count);
        for(int i = 0;i<count;i++){
            Log.i("getColor","i = " + i);
            ColorBlockView childView = (ColorBlockView) getChildAt(i);
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mCallback != null){
                        mCallback.changePenColor(((ColorBlockView)view).getBlockColor());
                    }
                }
            });
        }
    }
}
