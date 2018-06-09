package com.goldingmedia.temporary;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class SharedPreference {
	
	private static final String SERVICE_PARAMS_NAME = "ServiceParams";
	
	public static void writeStatus(Context context, String str, String settings) {
		SharedPreferences sh = context.getSharedPreferences(
				SERVICE_PARAMS_NAME, Context.MODE_WORLD_READABLE
						| Context.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor ed = sh.edit();
		ed.putString(str, settings);
		ed.apply();
	}
	
	public static String getStatus(Context context, String str){
		SharedPreferences share = context.getSharedPreferences(
				SERVICE_PARAMS_NAME, Context.MODE_WORLD_READABLE
						| Context.MODE_WORLD_WRITEABLE);
		return share.getString(str, null);
	}

	public static void setParamentString(Context context , String para, String data) {
		SharedPreferences sh = context.getSharedPreferences(
				SERVICE_PARAMS_NAME, Context.MODE_WORLD_WRITEABLE | Context.MODE_WORLD_READABLE);
		SharedPreferences.Editor ed = sh.edit();
		ed.putString(para, data);
		ed.apply();
	}
	
	public static String getParamentString(Context context, String para) {
		SharedPreferences share = context.getSharedPreferences(
				SERVICE_PARAMS_NAME, Context.MODE_WORLD_READABLE
						| Context.MODE_WORLD_WRITEABLE);
		return share.getString(para,"");
	}

	public static void setParamentInt(Context context , String para, int data) {
		SharedPreferences sh = context.getSharedPreferences(
				SERVICE_PARAMS_NAME, Context.MODE_WORLD_WRITEABLE | Context.MODE_WORLD_READABLE);
		SharedPreferences.Editor ed = sh.edit();
		ed.putInt(para, data);
		ed.apply();
	}

	public static int getParamentInt(Context context, String para) {
		SharedPreferences share = context.getSharedPreferences(
				SERVICE_PARAMS_NAME, Context.MODE_WORLD_READABLE
						| Context.MODE_WORLD_WRITEABLE);
		return share.getInt(para, 0);
	}

	public static String getParament(Context context, String para, boolean defvalue) {
		SharedPreferences share = context.getSharedPreferences(
				SERVICE_PARAMS_NAME, Context.MODE_WORLD_READABLE
						| Context.MODE_WORLD_WRITEABLE);
		return share.getString(para,String.valueOf(defvalue));
	}
}