package com.goldingmedia.mvp.view.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.goldingmedia.R;


public class BookAdapter implements IAdapter{
	private List<String> strList = new ArrayList<String>();
	
	private Context mContext;
	public BookAdapter(Context context) {
		super();
		this.mContext = context;
	}
	public void addItem(List<String> list){
		strList.addAll(list);
	}
	public int getCount() {
		return strList.size();
	}

	public String getItem(int position) {
		return strList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position) {
		TextView textView = new TextView(mContext);
		textView.setText(strList.get(position));
		textView.setTextColor(Color.WHITE);
		textView.setTextSize(22);
		textView.setBackgroundResource( R.mipmap.bg_1);
		textView.setPadding(10, 10, 10, 10);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		layoutParams.gravity = Gravity.CENTER;
		textView.setLayoutParams(layoutParams);
		return textView;
	}

}
