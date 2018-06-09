package com.goldingmedia.temporary;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * Ӧ�ó�����Ϣ��ʵ����
 */
public class AppInfo {
	private Drawable icon;
	private String appName;
	private String pkgName;
	private int versionCode;
	private String versionName;
	private Intent intent;
	private boolean inRom;
	private boolean userApp;
	private int uid;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public Drawable getAppIcon() {
		return icon;
	}

	public void setAppIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	public boolean isInRom() {
		return inRom;
	}

	public void setInRom(boolean inRom) {
		this.inRom = inRom;
	}

	public boolean isUserApp() {
		return userApp;
	}

	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}

	@Override
	public String toString() {
		return "AppInfo [name=" + appName + ", pkgName=" + pkgName + ", inRom="
				+ inRom + ", userApp=" + userApp + "]";
	}
}
