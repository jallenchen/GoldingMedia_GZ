package com.goldingmedia.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.goldingmedia.BaseActivity;
import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.temporary.Variables;

import java.io.File;

public class WebActivity extends BaseActivity {

	private Context mContext;
	private WebView mWebView;
	private AlertDialog mPDialog = null;
	private TruckMediaProtos.CTruckMediaNode mTruck;
	private String folderWeb;
	
	private void register(){
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.golding.web123");
		registerReceiver(receiver, filter);
	}
		
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.e("","-----WebActivity action = "+action);
			if("com.golding.web123".equals(action)){
			}
		}
	};   

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		register();
		setContentView(R.layout.activity_web);
		mWebView = (WebView)findViewById(R.id.webView1);

		boolean onLine =  getIntent().getBooleanExtra("online",false);
		if(onLine){
			String url = getIntent().getStringExtra("url");
			updateView(url);
		}else{
			mTruck = (TruckMediaProtos.CTruckMediaNode) getIntent().getSerializableExtra("truck");
			String folder = mTruck.getMediaInfo().getTruckMeta().getTruckFilename()+"/"+mTruck.getMediaInfo().getTruckMeta().getTruckFilename();
			folderWeb = Contant.getTruckMetaNodePath(mTruck.getCategoryId(),mTruck.getCategorySubId(),folder,true);
			if (new File(folderWeb+"/index.html").exists()) {
				Log.i("","----- exists{"+folderWeb+"/index.html"+"}");
				updateView("file://" + folderWeb+"/index.html");
			} else {
				Log.e("","----- no exists{"+folderWeb+"/index.html"+"}");
				finish();
			}
		}

	}

	@SuppressLint("JavascriptInterface")
	private void updateView(String uriPath) {
		mWebView.setScrollContainer(false);
		WebSettings wSet = mWebView.getSettings();
		wSet.setJavaScriptEnabled(true );
		mWebView.addJavascriptInterface(mContext,"android");
		if(mWebView != null)
		{
			mWebView.setWebViewClient(new WebViewClient()
			{
				@Override
				public void onPageFinished(WebView view,String url)
				{
					cancelDiglog();
				}
			});
		}
		mWebView.loadUrl(uriPath);
	    showWaitingDialog();
		mWebView.reload();
	}

	private void showWaitingDialog() {
		if (mPDialog == null) {
			AlertDialog.Builder b = new AlertDialog.Builder(WebActivity.this);
			mPDialog = b.create();
			mPDialog.setCancelable(false);// 设置ProgressDialog 是否可以按退回按键取消
			LayoutInflater inflater = (LayoutInflater) WebActivity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.progress_dialog, null);
			WindowManager.LayoutParams params = mPDialog.getWindow().getAttributes();
			if(params != null){
				params.x = -57;
				params.y = -97;
				mPDialog.getWindow().setAttributes(params);
			}

			mPDialog.setView(view, 0, 0, 0, 0);
		}
		mPDialog.show();
	}

	private void cancelDiglog() {
		if (mPDialog != null) {
			if (mPDialog.isShowing()) {
				mPDialog.cancel();
			}
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	public void onBack(View v){
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		cancelDiglog();
		unregisterReceiver(receiver);
	}
}