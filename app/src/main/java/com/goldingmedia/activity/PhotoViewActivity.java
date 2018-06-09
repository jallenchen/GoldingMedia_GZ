package com.goldingmedia.activity;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldingmedia.BaseActivity;
import com.goldingmedia.GDApplication;
import com.goldingmedia.R;
import com.goldingmedia.mvp.view.animations.Animations;
import com.goldingmedia.utils.Utils;

import java.util.List;

import uk.co.senab.photoview.PhotoView;

public class PhotoViewActivity extends BaseActivity {

	private final static long DELAY_TIME = 2 * 1000;
	private final static long AUTOTIME = 800;
	
	private ViewPager mViewPager;
	private TextView mPage, mTotalPage;
	private LinearLayout leftLayout, rightLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photoview_layout2);
		
		Intent intent = getIntent();
		String pathLast = intent.getStringExtra("pathLast");
		int position = intent.getIntExtra("position", 0);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mPage = (TextView) findViewById(R.id.page);
		mTotalPage = (TextView) findViewById(R.id.totalpage);
		leftLayout = (LinearLayout) findViewById(R.id.leftlayout);
		rightLayout= (LinearLayout)findViewById(R.id.rightlayout);
		Button toleft = (Button)findViewById(R.id.button_city_left);
		Button toright = (Button)findViewById(R.id.button_city_right);
		String path = pathLast;
		List<String> list = Utils.getFolderMsgWithFileExtension(path, "jpg", true);
		mViewPager.setAdapter(new PhotoViewAdapter(list));
		mTotalPage.setText(list.size() + "");
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				mPage.setText((position + 1) + ""); 
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		
		toleft.setOnClickListener(leftrightClickListener);
		toright.setOnClickListener(leftrightClickListener);
		Animations.leftOpen(leftLayout, AUTOTIME);
		Animations.rightOpen(rightLayout, AUTOTIME);
		autoSeek();
		autoSeek2(position);
	}
	
	public class PhotoViewAdapter extends PagerAdapter{
		
		private List<String> list;
		
		public PhotoViewAdapter(List<String> list) {
			this.list  =list;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			PhotoView photo = new PhotoView(container.getContext());
			Bitmap bitmap = BitmapFactory.decodeFile(list.get(position));
			photo.setImageBitmap(bitmap);
			photo.setOnTouchListener(touchListener);
			container.addView(photo, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			return photo;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View)object);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			
			return arg0 == arg1;
		}
	}

	private OnClickListener leftrightClickListener  = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_city_left:
				if (mViewPager.getCurrentItem() != 0) {
					mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
				}
				break;
			case R.id.button_city_right:
				if (mViewPager.getCurrentItem() != mViewPager.getAdapter().getCount()) {
					mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
				}
				break;
			}
		}
	};

	private OnTouchListener touchListener = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					leftLayout.setVisibility(View.VISIBLE);
					rightLayout.setVisibility(View.VISIBLE);
					autoSeek();
					break;		
			}			
			return true;
		}
	};

	private Handler	autoHandler;
	private Runnable autoRunnable;
	private void autoSeek() {
		if(autoHandler != null && autoRunnable != null)
			autoHandler.removeCallbacks(autoRunnable);
		autoHandler = new Handler();
		autoHandler.postDelayed(autoRunnable = new Runnable() {
			public void run() {
				Animations.leftHidden(leftLayout, AUTOTIME);
				Animations.rightHidden(rightLayout, AUTOTIME);
				leftLayout.setVisibility(View.GONE);
				rightLayout.setVisibility(View.GONE);
			}
		}, DELAY_TIME);
	}

	private Handler	autoHandler2;
	private Runnable autoRunnable2;
	private void autoSeek2(final int position) {
		if(autoHandler2 != null && autoRunnable2 != null)
			autoHandler2.removeCallbacks(autoRunnable2);
		autoHandler2 = new Handler();
		autoHandler2.postDelayed(autoRunnable2 = new Runnable() {
			public void run() {
				mViewPager.setCurrentItem(position);
			}
		}, 1);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(autoHandler != null && autoRunnable != null)
			autoHandler.removeCallbacks(autoRunnable);
		if(autoHandler2 != null && autoRunnable2 != null)
			autoHandler2.removeCallbacks(autoRunnable2);
	}

	public void onBack(View v){
		finish();
	}
}
