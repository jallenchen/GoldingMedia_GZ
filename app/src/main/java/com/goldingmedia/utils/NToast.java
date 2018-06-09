package com.goldingmedia.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * [Toast工具类]
 *
 * @author Jallen
 * @version
 * @date 2017-07-20
 * 
 **/
public class NToast {
	public static void shortToast(Context context, int resId) {
		showToast(context, context.getString(resId), Toast.LENGTH_SHORT);
	}
	
	public static void shortToast(Context context, String text) {
		showToast(context, text, Toast.LENGTH_SHORT);
	}

	public static void longToast(Context context, int resId) {
		showToast(context, context.getString(resId), Toast.LENGTH_LONG);
	}
	
	public static void longToast(Context context, String text) {
		showToast(context, text, Toast.LENGTH_LONG);
	}

	private static void showToast(Context context, String text, int duration) {
		if(!TextUtils.isEmpty(text)){
			Toast.makeText(context, text, duration).show();
		}
	}
}
