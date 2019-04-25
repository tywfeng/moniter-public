package com.tyw.moniter.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;

import moniter.tyw.com.moniterlibrary.common.Tag;


public class Options {
    // 选项首选项
    private static final String PREF_OPTIONS = "Pref_OPTIONS";
    // 配置文件路径
    private static final String PREF_OPTIONS_CONFIG_PATH = "Pref_ConfigPath";
    // 保留小数位
    private static final String PREF_OPTIONS_MaximumFractionDigits = "Pref_MaximumFractionDigits";

    // 初始化app配置
    public static void InitAppConfig(Context context)
    {
        // 小数点位数
        int nMaximumFractionDigits=getMaximumFractionDigits(context);
        Tag.MaximumFractionDigits = nMaximumFractionDigits;
    }

    // 提取路径
    public static String getDevicesConfigPath(Context context) {
        String strSDPath=getSDPath();
        if(strSDPath==null) return null;
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_OPTIONS
                , Context.MODE_PRIVATE);
        String strDefault =strSDPath+"/DeviceMoniter/Config/default.conf";
        return sharedPref.getString(PREF_OPTIONS_CONFIG_PATH, strDefault);
    }

    // 保存路径
    public static void SaveDevicesConfigPath(Context context, String _path) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_OPTIONS
                , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREF_OPTIONS_CONFIG_PATH, _path);
        editor.commit();
    }

    public static String getSDPath() {
        File sdDir = null;
        //判断sd卡是否存在
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            //获取根目录 Environment.getExternalStorageDirectory().getAbsolutePath()
            sdDir = Environment.getExternalStorageDirectory();
            if(sdDir!=null) return sdDir.getAbsolutePath();
        }
        return null;
    }
    // 提取小数位
    public static int getMaximumFractionDigits(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_OPTIONS
                , Context.MODE_PRIVATE);
        int nDefault = Tag.MaximumFractionDigits;
        return sharedPref.getInt(PREF_OPTIONS_MaximumFractionDigits, nDefault);
    }
    // 保存小数位
    public static int SaveMaximumFractionDigits(Context context, int _param) {
        if(_param<0) _param=0;
        else if(_param>10)_param=10;
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_OPTIONS
                , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(PREF_OPTIONS_MaximumFractionDigits, _param);
        editor.commit();
        Tag.MaximumFractionDigits = _param;
        return Tag.MaximumFractionDigits;
    }
}
