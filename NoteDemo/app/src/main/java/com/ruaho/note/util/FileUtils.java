package com.ruaho.note.util;

import android.content.Context;
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
    
    private static File getAppDir(Context context) {
        File appDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ruahoPreview");
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        return appDir;
    }

    public static String saveImage(Context context, Bitmap bmp, int quality) {
        if (bmp == null || context == null) {
            return null;
        }
        String fileName = System.currentTimeMillis() + ".png";
        File appDir = getAppDir(context);
        File file = new File(appDir, fileName);
        Log.i("saveImage","file is" + fileName + " path=" + file.getAbsolutePath());
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

    public static Bitmap loadImage(Context context, String name){
        if (context == null || name == null) {
            return null;
        }
        Log.i("saveImage","loadImage="+ name);
        Bitmap bitmap = null;

        File appDir = getAppDir(context);
        String fileName = name;
        String uri = new File(appDir, fileName).getAbsolutePath();
        Log.i("saveImage","loadImage uri ="+ uri);
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_4444;
            bitmap = BitmapFactory.decodeFile(uri,options);
        } catch (OutOfMemoryError e){
            Log.i("saveImage","OutOfMemoryError");
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
