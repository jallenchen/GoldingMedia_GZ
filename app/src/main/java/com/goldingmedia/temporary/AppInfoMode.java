package com.goldingmedia.temporary;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class AppInfoMode {

	private int id;
	private boolean filtered;
	private String appLabel;
	private Drawable appIcon;
	private Intent intent;
	private String pkgName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAppLabel() {
		return appLabel;
	}

	public void setAppLabel(String appLabel) {
		this.appLabel = appLabel;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public boolean isFiltered() {
		return filtered;
	}

	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	}
}