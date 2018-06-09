package com.goldingmedia.mvp.view.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.goldingmedia.GDApplication;
import com.goldingmedia.R;
import com.goldingmedia.activity.WindowAdsPlayActivity;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.mvp.view.activity.JmagazineActivity;
import com.goldingmedia.mvp.view.adapter.ImageAdapter;

public class AutoPlayGallery extends RelativeLayout {
	private int duration = 20000; // switch duration
	private MyGallery mGallery;
	private boolean flag = false; // switch for playing
	private Thread autoPlayThread;
	private Context mContext;

	public AutoPlayGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		setupContentView(context);
	}

	public AutoPlayGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setupContentView(context);
	}

	public AutoPlayGallery(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		setupContentView(context);
	}

	public void setPlayDuration(int sec){
		this.duration = sec*1000;
	}

	private void setupContentView(Context context) {
		mContext = context;

		mGallery = new MyGallery(context);
		Gallery.LayoutParams paramGallery = new Gallery.LayoutParams(
				Gallery.LayoutParams.MATCH_PARENT,
				Gallery.LayoutParams.MATCH_PARENT);
		mGallery.setLayoutParams(paramGallery);
		mGallery.setSpacing(1); // set the spacing between items
		mGallery.setUnselectedAlpha(1.0f); // make all the items light
		mGallery.setHorizontalFadingEdgeEnabled(false); // take off the fading
		addView(mGallery, paramGallery);
	}

	/**
	 * set adapter to start the gallery
	 * @param adapter
	 */
	public void setAdapter(ImageAdapter adapter,OnItemClickListener listener) {
		ArrayList<Bitmap> bitmaps = adapter.getBitmaps();
		if (bitmaps != null && bitmaps.size() > 0) {
			mGallery.setAdapter(adapter);
			mGallery.setOnItemClickListener(listener);
//			mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//				@Override
//				public void onItemSelected(AdapterView<?> arg0, View view,
//						int position, long arg3) {
//					// TODO Auto-generated method stub
//
//				}
//
//				@Override
//				public void onNothingSelected(AdapterView<?> arg0) {
//					// TODO Auto-generated method stub
//
//				}
//			});


			
//			mGallery.setOnItemClickListener(new OnItemClickListener() {
//
//				@Override
//				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//					// TODO Auto-generated method stub
//					View aView = (View) mGallery.getParent();
//					Log.d("AutoPlayGallery", "position:"+position);
//                	 if(aView.getId() == R.id.imgmid){
//						 startWindowAdActivity(position, Contant.ADS_WINDOW_ORIENT_MIDDLE);
//					}else  if(aView.getId() == R.id.imgbottom){
//						 startWindowAdActivity(position, Contant.ADS_WINDOW_ORIENT_BOTTOM);
//					 }
//
//				}
//			});
			flag = true;// make the switch true
			play(bitmaps.size());
		}
	}
	private List<TruckMediaProtos.CTruckMediaNode> getWindowList(int orient) {
		return GDApplication.getmInstance().getTruckMedia().getcAds().getWindowOrientTrucksMap(orient);
	}
//	private Boolean mRepeat = false;
//	public void startWindowAdActivity(int position, int orient) {
//		if (mRepeat) return;
//		mRepeat = true;
//		if (getWindowList(orient) != null && getWindowList(orient).size() > 0) {
//
//			TruckMediaProtos.CTruckMediaNode truck = GDApplication.getmInstance().getTruckMedia().getcAds().getWindowOrientTrucksMap(orient).get(position);
//			Intent mIntent ;
//			switch (truck.getCategorySubId()) {
//				case Contant.PROPERTY_ADS_MEDIA_ID:
//					mIntent = new Intent(mContext, WindowAdsPlayActivity.class);
//					break;
//
//				case Contant.PROPERTY_ADS_IMG_ID:
//					mIntent = new Intent(mContext, JmagazineActivity.class);
//					break;
//				default:
//					return;
//			}
//			mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			mIntent.putExtra("position", position);
//			mIntent.putExtra("orient", orient);
//			mIntent.putExtra("truck", truck);
//			mContext.startActivity(mIntent);
//		} else {
//			return;
//		}
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				mRepeat = false;
//			}
//		}).start();
//	}


	/**
	 * stop the play thread
	 */
	public void stop() {
		flag = false;
		autoPlayThread.interrupt();
		mGallery.setAdapter(new ImageAdapter(getContext(), new ArrayList<Bitmap>()));
	}

	private void indicateImage(int position) {
		mGallery.setSelection(position);
		imageViewInAniamtion(0);
	}

	/**
	 * setAnimation for auto play item
	 *
	 * @param position
	 */
	private void imageViewInAniamtion(int position) {
		LinearLayout img = (LinearLayout) mGallery.getChildAt(position);
		if(img!=null){
			img.startAnimation(getTranslateAnimation());
		}
	}

	/**
	 * a tween animation
	 * 
	 * @return
	 */
	private Animation getTranslateAnimation() {
		AnimationSet as = new AnimationSet(true);
		TranslateAnimation ta = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT,
				-1.0f, Animation.RELATIVE_TO_PARENT, 0,
				Animation.RELATIVE_TO_PARENT, 0);
		ta.setDuration(1000);
		as.addAnimation(ta);
		return as;
	}

	private int count;

	/**
	 * play by a thread
	 * 
	 * @param size
	 */
	private void play(final int size) {
		Log.d("AutoPlayGallery", "playsize:"+size);
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if (mGallery.isTouched()) {
					count = mGallery.getFirstVisiblePosition() + 1;
					mGallery.setTouched(false);
				} else {
					count++;
					if(count == size){
						count = 0;
					}
				}
				indicateImage(count % size);
			}
		};

		autoPlayThread = new Thread(new Runnable() {

			@Override
			public void run() {
				do{
					try {
						Thread.sleep(duration);
						handler.sendEmptyMessage(0);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
				}while(flag);
			}
		});
		autoPlayThread.start();
	}
}

class MyGallery extends Gallery {
	public MyGallery(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// Convert the dips to pixels
	float scale = getResources().getDisplayMetrics().density;
	int FLINGTHRESHOLD = (int) (20.0f * scale + 0.5f);
	int SPEED = 600;

	private boolean isTouched = false;

	public void setTouched(boolean isTouched) {
		this.isTouched = isTouched;
	}

	public boolean isTouched() {
		return isTouched;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// cap the velocityX to scroll only one page
		setTouched(true);
		if (velocityX > FLINGTHRESHOLD) {
			return super.onFling(e1, e2, SPEED, velocityY);
		} else if (velocityX < -FLINGTHRESHOLD) {
			return super.onFling(e1, e2, -SPEED, velocityY);
		} else {
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}
}