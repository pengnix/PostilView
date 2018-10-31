package com.ruaho.note.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    public static String saveImage(Bitmap bmp, int quality) {
        if (bmp == null) {
            return null;
        }
        File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (appDir == null) {
            return null;
        }
        String fileName = System.currentTimeMillis() + ".png";
        File file = new File(appDir, fileName);
        Log.i("saveImage","file is" + fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, quality, fos);
            fos.flush();
            return fileName;
        } catch (FileNotFoundException e) {
            fileName = null;
            Log.i("saveImage","FileNotFoundException");
            e.printStackTrace();
        } catch (IOException e) {
            fileName = null;
            Log.i("saveImage","IOException");
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileName;
    }

    public static Bitmap loadImage(String name){
        Log.i("saveImage","loadImage="+ name);
        File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String fileName = name;
        String uri = appDir + "/" + fileName;
        Log.i("saveImage","loadImage uri ="+ uri);
        Bitmap bitmap = null;
        //File file = new File(appDir, fileName);
        try{
            FileInputStream fis = new FileInputStream(uri);
            bitmap  = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e){
            Log.i("saveImage","FileNotFoundException");
        }
        return bitmap;
    }
}
