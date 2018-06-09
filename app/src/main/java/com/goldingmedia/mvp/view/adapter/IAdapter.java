package com.goldingmedia.mvp.view.adapter;

import android.view.View;

public interface IAdapter {
	 int getCount();
	 String getItem(int position);
	 long getItemId(int position);
	 View getView(int position);
}
