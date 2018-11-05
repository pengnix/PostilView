package com.ruaho.note.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
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
        String fileName = System.currentTimeMillis() + ".png";
        if(isSDCardMounted()){
            File appDir = new File(getSDCardBaseDir() + File.separator + "ruahoPreview");
            if (appDir == null) {
                return null;
            }
            if (!appDir.exists()) {
                appDir.mkdirs();// 递归创建自定义目录
            }
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
        } else {
            fileName = null;
        }

        return fileName;
    }

    public static Bitmap loadImage(String name){
        Log.i("saveImage","loadImage="+ name);
        Bitmap bitmap = null;

        if(isSDCardMounted()){
            File appDir = new File(getSDCardBaseDir() + File.separator + "ruahoPreview");
            String fileName = name;
            String uri = appDir + "/" + fileName;
            Log.i("saveImage","loadImage uri ="+ uri);
            try{
                FileInputStream fis = new FileInputStream(uri);
                bitmap  = BitmapFactory.decodeStream(fis);
            } catch (FileNotFoundException e){
                Log.i("saveImage","FileNotFoundException");
            }
        }

        return bitmap;
    }

    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    // 获取SD卡的根目录
    public static String getSDCardBaseDir() {
        if (isSDCardMounted()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }
}
