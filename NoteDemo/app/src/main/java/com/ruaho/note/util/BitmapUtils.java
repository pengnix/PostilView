package com.ruaho.note.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapUtils {
    public static Bitmap bitMapScale(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale,scale); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }

    public static String getBitmapSize(Bitmap bitmap){
        int rowBytes = bitmap.getRowBytes();
        int height = bitmap.getHeight();
        long memorySize = rowBytes*height;
        return "" + (float)memorySize/1024 + "KB";
    }
}
