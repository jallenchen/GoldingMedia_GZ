package com.goldingmedia.temporary;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

public class AppInfoList {

	/**
	 * 获取所有的安装的应用程序信息。
	 * @param context 上下文
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context) {
		PackageManager pm = context.getPackageManager();
		//所有的安装在系统上的应用程序包信息。
		List<PackageInfo> pkgInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for(PackageInfo pkgInfo : pkgInfos) {
			AppInfo appInfo = new AppInfo();
			//packInfo  相当于一个应用程序apk包的清单文件
			Drawable icon = pkgInfo.applicationInfo.loadIcon(pm);
			String name = pkgInfo.applicationInfo.loadLabel(pm).toString();
			String pkgName = pkgInfo.packageName;
			int versionCode = pkgInfo.versionCode;
			String versionName = pkgInfo.versionName;
			Intent intent = pm.getLaunchIntentForPackage(pkgName);
			int flags = pkgInfo.applicationInfo.flags; // 应用程序信息的标记 相当于用户提交的答卷
			int uid = pkgInfo.applicationInfo.uid; // 操作系统分配给应用系统的一个固定的编号。一旦应用程序被装到手机 id就固定不变了。
//			File rcvfile = new File("/proc/uid_stat/"+uid+"/tcp_rcv");
//			File sndfILE = new File("/proc/uid_stat/"+uid+"/tcp_snd");
			appInfo.setUid(uid);
			if((flags&ApplicationInfo.FLAG_SYSTEM) == 0) {
				appInfo.setUserApp(true);// 用户程序
			} else {
				appInfo.setUserApp(false);// 系统程序
			}
			if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				appInfo.setInRom(true);// 手机的内存
			}else {
				appInfo.setInRom(false);// 手机外存储设备
			}
			appInfo.setAppIcon(icon);
			appInfo.setAppName(name);
			appInfo.setPkgName(pkgName);
			appInfo.setVersionCode(versionCode);
			appInfo.setVersionName(versionName);
			appInfo.setIntent(intent);
			appInfos.add(appInfo);
		}
		return appInfos;
	}

	public static List<AppInfo> getAppInfoSort(List<AppInfo> appInfos) {
		List<AppInfo> appInfosMain = new ArrayList<AppInfo>();
		for (int i = 0; i < mainApp.length; i++) {
			for (int j = 0; j < appInfos.size(); j++) {
				if (mainApp[i].equals(appInfos.get(j).getPkgName())) {
					appInfosMain.add(appInfos.get(j));
					appInfos.remove(j);
					break;
				}
			}
		}
		appInfosMain.addAll(appInfos);
		return appInfosMain;
	}

	private final static String[] mainApp={
			"com.goldingmedia",
			"com.golding.goldingchat",
			"com.golding.languageservice",
			"com.golding.systemservice",
			"com.golding.ebookservice",
			"com.android.camera2",
			"com.android.calculator2",
			"cn.etouch.ecalendar.pad",
			"com.reawake.game.llpoker",
			"com.lqs.kaisi.bill",
			"com.lemon.play.majiang",
			"com.flyfish.newgoldminer",
			"com.cgames.spider",
			"com.alarmclocksnoozers.runnershigh",
			"com.lemon.play.supertractor",
			"com.siondream.freegemas",
			"com.zhiwupkjiangshi.MM",
			"mobi.shoumeng.smtankHD",
	};
}