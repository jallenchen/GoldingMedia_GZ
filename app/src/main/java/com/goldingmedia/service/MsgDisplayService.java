package com.goldingmedia.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import com.goldingmedia.R;
import com.goldingmedia.mvp.view.ui.MarqueeTextView;
import com.goldingmedia.temporary.Variables;

import java.util.ArrayList;
import java.util.List;

public class MsgDisplayService extends Service {
	private LinearLayout mFloatLayout;
	private WindowManager mWindowManager;
	private MarqueeTextView mTextView;

	private int type;
	private int txtcolor;
	private int backdrop;
	private int onOff;
	private float textX;
	private boolean move;

	public void onCreate() {
		super.onCreate();
		Log.i("", "@MsgDisplayService onCreate()");
		register();
		createFloatView();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	private void createFloatView() {
		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
		mWindowManager = (WindowManager) getApplication().getSystemService(
				getApplication().WINDOW_SERVICE);
		wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
		wmParams.format = PixelFormat.RGBA_8888;
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		wmParams.gravity = Gravity.START | Gravity.TOP;
		wmParams.x = 0;
		wmParams.y = 0;
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		LayoutInflater inflater = LayoutInflater.from(getApplication());
		mFloatLayout = (LinearLayout) inflater.inflate(R.layout.msg, null);
		mWindowManager.addView(mFloatLayout, wmParams);

		mTextView = (MarqueeTextView) mFloatLayout.findViewById(R.id.roll_titles_text);
		mTextView.setMaxLines(1);

		mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		wmParams.x = 0;// 800 - mFloatView.getMeasuredWidth();
		wmParams.y = 800 - mFloatLayout.getMeasuredHeight();// 390;//
		mWindowManager.updateViewLayout(mFloatLayout, wmParams);

		mFloatLayout.setVisibility(View.GONE);
	}

	public void onDestroy() {
		super.onDestroy();
		if (mFloatLayout != null) {
			mWindowManager.removeView(mFloatLayout);
		}
		unRegister();
	}

	private  void unRegister() {// 注销广播receiver
		unregisterReceiver(receiver);
	}

	private void register() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.marqueetextView.start");
		filter.addAction("com.marqueetextView.gone");
		filter.addAction("com.marqueetextView.next");
		registerReceiver(receiver, filter);
	}

	private List<String> list = new ArrayList<>();
	private int displayCount = 0;
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if("com.marqueetextView.start".equals(action)){
				displayCount = 0;
				list.clear();
				list = intent.getStringArrayListExtra("emgMsg");
				if (list.size() > displayCount) {
					type = intent.getIntExtra("type", 0);        //消息类型
					txtcolor = intent.getIntExtra("txtcolor", 0);//字体颜色
					backdrop = intent.getIntExtra("backdrop", 0);//背景颜色
					onOff = intent.getIntExtra("onOff", 0);      //消息显示与否
					textX = (float)intent.getIntExtra("textX", 800);//开始移动的位置（特殊情况：textX=1f 时则消息居中）
					move = intent.getBooleanExtra("move", true);  //是否移动
					if(type > 0) {
						Variables.isMarqueeShow = true;
						updateMarqueeText(list.get(displayCount), type, txtcolor, backdrop, onOff, textX, move);
					}
				}

			} else if ("com.marqueetextView.next".equals(action)) {
				displayCount++;
				if (list.size() > displayCount) {
					updateMarqueeText(list.get(displayCount), type, txtcolor, backdrop, onOff, textX, move);
				} else {
					Variables.isMarqueeShow = false;
					Intent mIntentViewGone = new Intent("com.marqueetextView.gone");
					mIntentViewGone.putExtra("view", 0);
					context.sendBroadcast(mIntentViewGone);
				}
			} else if ("com.marqueetextView.gone".equals(action)) {
				Log.i("", "@MarqueeTextView com.marqueetextView.gone");
				updateMarqueeText("", 0, 0, 1, 0, 800, false);
			}
		}
	};

	private final int[] colorMap = { Color.WHITE, Color.RED };
	private final int[] colorMapBackdrop = { 0xFF0000FF, 0x40000000 };

	private void updateMarqueeText(String str, int type, int txtcolor, int backdrop,
								  int onOff, float textX, boolean move) {
		try {
			mTextView.setText(str);
			if (txtcolor > 1 || txtcolor < 0) {
				txtcolor = 0;
			}
			if (backdrop > 1 || backdrop < 0) {
				backdrop = 0;
			}
			mTextView.setTextColor(colorMap[txtcolor]);
			mTextView.setBackgroundColor(colorMapBackdrop[backdrop]);
			mFloatLayout.setVisibility(onOff == 1 ? View.VISIBLE : View.GONE);
			if (onOff == 1) {
				Log.i("", "@MarqueeTextView VISIBLE");
				mTextView.controlMove(move);
				if (textX == 1f) {
					mTextView.setTextCenter();
				} else {
					mTextView.setX(textX);
				}
			} else {
				Log.i("", "@MarqueeTextView GONE");
				mTextView.controlMove(false);
			}

		} catch (NullPointerException e) {
			// TODO: handle exception
			Log.e("", "@NullPointerException");
		}
	}
}