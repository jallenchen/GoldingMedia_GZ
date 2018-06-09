package com.goldingmedia.mvp.view.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyViewPager(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		try {
			
			return super.onTouchEvent(arg0);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		try {
			return super.onInterceptTouchEvent(arg0);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return false;
	}
}
