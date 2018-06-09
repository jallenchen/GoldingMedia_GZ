package com.goldingmedia.sqlite;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jallen on 2017/7/25 0025 17:10.
 */

public class DataSharePreference {
    public static final String PREF_NAME = "pushVer";
    public static  void savePushVer(Context context,int version) {
        SharedPreferences sh = context.getSharedPreferences(
                PREF_NAME, Context.MODE_WORLD_WRITEABLE | Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor ed = sh.edit();
        ed.putInt("version", version);
        ed.apply();
    }
    public static int getPushVer(Context context,String name) {
        SharedPreferences share = context.getSharedPreferences(
                PREF_NAME, Context.MODE_WORLD_READABLE
                        | Context.MODE_WORLD_WRITEABLE);
        return share.getInt(name,0);
    }

    public static  void saveLanguage(Context context,String local) {
        SharedPreferences sh = context.getSharedPreferences(
                PREF_NAME, Context.MODE_WORLD_WRITEABLE | Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor ed = sh.edit();
        ed.putString("local", local);
        ed.apply();
    }
    public static String getLanguage(Context context,String name) {
        SharedPreferences share = context.getSharedPreferences(
                PREF_NAME, Context.MODE_WORLD_READABLE
                        | Context.MODE_WORLD_WRITEABLE);
        return share.getString(name,"CN");
    }

    public static  void saveFirstStart(Context context,boolean isFirst) {
        SharedPreferences sh = context.getSharedPreferences(
                PREF_NAME, Context.MODE_WORLD_WRITEABLE | Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor ed = sh.edit();
        ed.putBoolean("isFirst", isFirst);
        ed.apply();
    }
    public static boolean isFirstStart(Context context,String name) {
        SharedPreferences share = context.getSharedPreferences(
                PREF_NAME, Context.MODE_WORLD_READABLE
                        | Context.MODE_WORLD_WRITEABLE);
        return share.getBoolean(name,true);
    }

    public static  void saveDBCode(Context context,String code) {
        SharedPreferences sh = context.getSharedPreferences(
                PREF_NAME, Context.MODE_WORLD_WRITEABLE | Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor ed = sh.edit();
        ed.putString("DBCode", code);
        ed.apply();
    }
    public static String getDBCode(Context context) {
        SharedPreferences share = context.getSharedPreferences(
                PREF_NAME, Context.MODE_WORLD_READABLE
                        | Context.MODE_WORLD_WRITEABLE);
        return share.getString("DBCode","0");
    }


}
