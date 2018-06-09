package com.goldingmedia.mvp.view.activity;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goldingmedia.BaseActivity;
import com.goldingmedia.GDApplication;
import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.utils.Utils;

import java.util.List;

import uk.co.senab.photoview.PhotoView;

public class JmagazineActivity extends BaseActivity {

	private static final String PATH = "/mnt/sdcard/sysdata/joy/";
	private List<String> list;
	private ViewPager mViewPager;
	private TextView mPage, mTotalPage;
	private TruckMediaProtos.CTruckMediaNode truck;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photoview_layout);
		
		Intent intent = getIntent();
		int position = intent.getIntExtra("position", 1);
		truck = (TruckMediaProtos.CTruckMediaNode) intent.getSerializableExtra("truck");
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mPage = (TextView) findViewById(R.id.page);
		mTotalPage = (TextView) findViewById(R.id.totalpage);
		String folder = Contant.getTruckMetaNodePath(truck.getMediaInfo().getCategoryId(),truck.getMediaInfo().getCategorySubId(), truck.getMediaInfo().getTruckMeta().getTruckFilename()+"/"+truck.getMediaInfo().getTruckMeta().getTruckFilename(),true);
		list = Utils.getFolderMsgWithFileExtension(folder, "jpg", true);
		mViewPager.setAdapter(new PhotoViewAdapter(list));
		mTotalPage.setText(getListCount() + "");
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
	}
	
	public int getListCount(){
		return list.size() ;
	}
	
	public class PhotoViewAdapter extends PagerAdapter{
		
		private List<String> list;
		
		public PhotoViewAdapter(List<String> list) {
			this.list  =list;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			PhotoView photo = new PhotoView(container.getContext());
			Bitmap fBitmap = BitmapFactory.decodeFile(list.get(position));
			photo.setImageBitmap(fBitmap);

			container.addView(photo, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			return photo;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View)object);
		}

		@Override
		public int getCount() {
			
			return getListCount();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			
			return arg0 == arg1;
		}
		
	}

	public void onBack(View v){
		finish();
	}
}
