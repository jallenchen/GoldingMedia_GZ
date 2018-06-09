package com.goldingmedia.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.goldingmedia.BaseActivity;
import com.goldingmedia.R;

public class ImageActivity extends BaseActivity {

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		exitActivity();
	}
	
	private void register(){
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.golding.isWindowUpdate");
		registerReceiver(receiver, filter);
	}
		
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.e("","-----ImageActivity action = "+action);
			if("com.marqueetextView.isWindowUpdate".equals(action)){
				boolean isWindowUpdate = intent.getBooleanExtra("isWindowUpdate", false);
				if (isWindowUpdate) {
					exitActivity();
				}
			}
		}
	};   

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		register();
		int type = getIntent().getExtras().getInt("type");
		String filename = getIntent().getExtras().getString("filename");		
		Log.i("", "type|filename = " + type + "|" + filename);
		setContentView(R.layout.windowad);
		ImageView mImage = (ImageView)findViewById(R.id.imagead);
		mImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				exitActivity();
			}
		});

		if( 0 == type ){
		} else {
			mImage.setImageURI(Uri.parse(filename));
			mImage.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}


	private synchronized void exitActivity(){
		finish();
	}
}