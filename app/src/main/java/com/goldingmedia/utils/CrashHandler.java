package com.goldingmedia.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;


import com.goldingmedia.GDApplication;
import com.goldingmedia.WelcomeActivity;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.SystemDiagnoseProtos;
import com.goldingmedia.temporary.SystemInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/** 
 *  异常log捕捉
 * @author user 
 *  
 */
public class CrashHandler implements UncaughtExceptionHandler {
	private String getCrashFolder() {
        return Environment.getExternalStorageDirectory()+"/" + "crash"  + "/Launcher/";
    }

	private static final String TAG = "CrashHandler";

	private static CrashHandler INSTANCE = new CrashHandler();

	private Context mContext;

	private UncaughtExceptionHandler mDefaultHandler;

	private  final Map<String, String> infos = new HashMap<String, String>();

	@SuppressLint("SimpleDateFormat")
	private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


	private CrashHandler() {
	}

	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 *
	 * @param context
	 */
	public void init(Context context) {
		mContext = context.getApplicationContext();
	
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				NLog.e(TAG, "error : ", e);
			}
			
			Intent intent = new Intent(mContext,WelcomeActivity.class);
		    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    mContext.startActivity(intent);
			
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 
	 * @param ex
	 * @return 
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}

		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Looper.loop();
			}
		}.start();

		collectDeviceInfo(mContext);
		saveCrashInfo2File(ex);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}
	

	/**
	 * 
	 * @param ctx
	 */
	private void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);

			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			NLog.e(TAG, "an error occured when collect package info", e);
		}

		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				NLog.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				NLog.e(TAG, "an error occured when collect crash info", e);
			}
		}
		infos.put("masterId", Contant.MasterId);
		infos.put("seatNum", SystemInfo.getLocalSeat());
	}

	/**
	 * 
	*
	 * @param ex
	 * @return  
	 */
	private String saveCrashInfo2File(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();

		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName = "crash-" + time + "-" + timestamp + ".txt";
			
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				String path = getCrashFolder();
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
			}
			sendErrorMsg(time,sb.toString());
			return fileName;
		} catch (Exception e) {
			NLog.e(TAG, "an error occured while writing file...", e);
		}
		return null;
	}

	private void sendErrorMsg(String time,String desc){
		SystemDiagnoseProtos.CSystemDiagnose.Builder csBuilder = SystemDiagnoseProtos.CSystemDiagnose.newBuilder();
		csBuilder.setMasterId(Contant.MasterId);
		csBuilder.setDeviceId(Utils.getSerialID());
		csBuilder.setDeviceType(2);

		SystemDiagnoseProtos.CSystemDiagnoseMeta.Builder CSystemDiagnoseMeta = SystemDiagnoseProtos.CSystemDiagnoseMeta.newBuilder();
		CSystemDiagnoseMeta.setErrorFunction("GoldingMedia.apk");
		CSystemDiagnoseMeta.setErrorDesc(desc);
		CSystemDiagnoseMeta.setErrorTime(time);
		csBuilder.addSysobjList(CSystemDiagnoseMeta.build());

		GDApplication.getmInstance().getMostTcp().makeTCPMsgAndSend(Contant.CATEGORY_DIAGNOSE_ID,Contant.PROPERTY_DIAGNOSE_SYSTEM_ID,csBuilder.build().toByteArray());
	}
}