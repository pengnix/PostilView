package com.ruaho.note.util;

import android.graphics.Bitmap;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public class BitmapCache {

    private final static boolean enableSoftReference = false;

    private static class BitmapCacheInstance{
        private static final BitmapCache instance=new BitmapCache();
    }

    private BitmapCache(){
        init();
    }

    public static BitmapCache getInstance(){
        return BitmapCacheInstance.instance;
    }

    public Map<String, SoftReference<Bitmap>> mImageCacheReferenceMap = null;
    public Map<String, Bitmap> mImageCacheMap = null;

    public void init(){
        if(enableSoftReference){
            mImageCacheReferenceMap = new HashMap<String,SoftReference<Bitmap>>();
        } else {
            mImageCacheMap = new HashMap<String,Bitmap>();
        }
    }

    public void put(String uri){
        Bitmap bmp = FileUtils.loadImage(uri);
        if(enableSoftReference){
            SoftReference<Bitmap> d = new SoftReference<Bitmap>(bmp);
            mImageCacheReferenceMap.put(uri, d);
        } else {
            mImageCacheMap.put(uri, bmp);
        }
    }

    public Bitmap getSafe(String uri){
        if(enableSoftReference){
            SoftReference<Bitmap> softReference = mImageCacheReferenceMap.get(uri);
            if (softReference != null && softReference.get() != null) {
                return softReference.get();
            } else {
                put(uri);
                Log.i("getSafeFail","1");
                return FileUtils.loadImage(uri);
            }
        } else {
            Bitmap softReference = mImageCacheMap.get(uri);
            if (softReference != null) {
                return softReference;
            } else {
                put(uri);
                Log.i("getSafeFail","1");
                return FileUtils.loadImage(uri);
            }
        }
    }

    public void clear(){
        if(enableSoftReference){
            if(mImageCacheReferenceMap != null){
                mImageCacheReferenceMap.clear();
                System.gc();
            }
        } else {
            if(mImageCacheMap != null){
                mImageCacheMap.clear();
                System.gc();
            }
        }
    }
}
