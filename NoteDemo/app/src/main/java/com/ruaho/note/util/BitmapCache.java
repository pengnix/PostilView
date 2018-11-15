package com.ruaho.note.util;

import android.graphics.Bitmap;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public class BitmapCache {
    private static class BitmapCacheInstance{
        private static final BitmapCache instance=new BitmapCache();
    }

    private BitmapCache(){
        init();
    }

    public static BitmapCache getInstance(){
        return BitmapCacheInstance.instance;
    }

//    public Map<String, SoftReference<Bitmap>> mImageCacheMap = null;
    public Map<String, Bitmap> mImageCacheMap = null;

    public void init(){
//        mImageCacheMap = new HashMap<String,SoftReference<Bitmap>>();
        mImageCacheMap = new HashMap<String,Bitmap>();
    }

    public void put(String uri){
        Bitmap bmp = FileUtils.loadImage(uri);
//        SoftReference<Bitmap> d = new SoftReference<Bitmap>(bmp);
        mImageCacheMap.put(uri, bmp);
    }

    public Bitmap getSafe(String uri){
//        SoftReference<Bitmap> softReference = mImageCacheMap.get(uri);
        Bitmap softReference = mImageCacheMap.get(uri);

        if (softReference != null/* && softReference.get() != null*/) {
//            return softReference.get();
            return softReference;
        } else {
            put(uri);
            Log.i("getSafeFail","1");
            return FileUtils.loadImage(uri);
        }
    }

    public void clear(){
        if(mImageCacheMap != null){
            mImageCacheMap.clear();
            System.gc();
        }
    }
}
