package com.ruaho.note.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class NoteSharePreferenceUtils {
    private static SharedPreferences settings = null;

    public static void init(Context context){
        settings = PreferenceManager
                .getDefaultSharedPreferences(context);
    }

    /*
     * 根据传入的键得到字符串值
     */
    public  static String getPrefString(String key,
                                        final String defaultValue) {
        return settings.getString(key, defaultValue);
    }
    /*
     * 传入键值对的字符串，存入内存
     */
    public static void setPrefString(final String key,
                                     final String value) {
        settings.edit().putString(key, value).commit();
    }
    /*
     * 根据boolean键得到boolean值
     */
    public static boolean getPrefBoolean(final String key,
                                         final boolean defaultValue) {
        return settings.getBoolean(key, defaultValue);
    }
    /*
     * 查看是否有这个键对应的值，返回boolean值
     */
    public static boolean hasKey(final String key) {
        return settings.contains(
                key);
    }
    /*
     * 设置boolean值到内存中根据传入的键值对
     */
    public static void setPrefBoolean(final String key,
                                      final boolean value) {
        settings.edit().putBoolean(key, value).commit();
    }
    /*
     * 根据int型键值对写入值到内存
     */
    public static void setPrefInt(final String key,
                                  final int value) {
        settings.edit().putInt(key, value).commit();
    }
    /*
     * 根据键获取int型的值
     */
    public static int getPrefInt(final String key,
                                 final int defaultValue) {
        return settings.getInt(key, defaultValue);
    }
    /*
     * 根据传入的浮点型数据键值对存入内存
     */
    public static void setPrefFloat(final String key,
                                    final float value) {
        settings.edit().putFloat(key, value).commit();
    }

    /*
     * 根据键名获取对应的浮点型数据
     */
    public static float getPrefFloat(final String key,
                                     final float defaultValue) {
        return settings.getFloat(key, defaultValue);
    }
    /*
     * 存入一对长整型数据键值对
     */
    public static void setSettingLong(final String key,
                                      final long value) {
        settings.edit().putLong(key, value).commit();
    }
    /*
     * 根据键获取一个长整型数据
     */
    public static long getPrefLong(final String key,
                                   final long defaultValue) {
        return settings.getLong(key, defaultValue);
    }
    /*
     * 清空内存中的已有文件
     */
    public static void clearPreference(final SharedPreferences p){
        final SharedPreferences.Editor editor = p.edit();
        editor.clear();
        editor.commit();
    }
}
