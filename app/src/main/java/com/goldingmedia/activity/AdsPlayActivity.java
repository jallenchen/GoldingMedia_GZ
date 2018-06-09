package com.goldingmedia.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.goldingmedia.BaseActivity;
import com.goldingmedia.GDApplication;
import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.ethernet.IP;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.most.GlobalSettings;
import com.goldingmedia.most.fblock.FBlock;
import com.goldingmedia.most.ts_renderer.TsReceiver;
import com.goldingmedia.temporary.Command;
import com.goldingmedia.temporary.DataHelper;
import com.goldingmedia.temporary.SharedPreference;
import com.goldingmedia.temporary.Variables;
import com.goldingmedia.utils.HandlerUtils;
import com.goldingmedia.utils.NLog;
import com.goldingmedia.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AdsPlayActivity extends BaseActivity implements HandlerUtils.OnReceiveMessageListener{

	private static final String TAG = "AdsPlayActivity";
	private static final int TIME_MAX_REGULAR = 120;
	private Context mContext;
	private ImageView mImageView;
	private SurfaceView mSurfaceView;
	private int mPos = 0;
	private int mEvenPos = 0;
	private TruckMediaProtos.CTruckMediaNode mTruck;
	private List<TruckMediaProtos.CTruckMediaNode> mDataList;
	private List<TruckMediaProtos.CTruckMediaNode> mEvenDataList;
	private List<TruckMediaProtos.CTruckMediaNode> mSecurityDataList;
	private TsReceiver m_TsReceiver = new TsReceiver(GlobalSettings.GetAudioSink(), GlobalSettings.GetSource());

	private int mImgTimeCounter = 0;
	private int ImgDisplayTime = 10;//显示图片时间(10秒)
	private boolean mIsImgAd = false;
	private boolean mIsVideoAd = false;
	private FBlock mFBlock;
	private int mCounter = 0;
	private boolean isServer = false;
	private final byte[] payloadMac = new byte[4];
	private boolean mRet = false;
	private AudioManager audioManager = null;
	public int mDefVolume = 2;
	public int mLimitCounter = 0;
	private Boolean isExitActivity = false;
	private Boolean mRegularFirst = true;// 标识界面启动后是否第一个定时广告
	private Boolean mRegularAdStart = false;
	private Boolean mEvenAdStart = false;
	private Boolean mSVStart = false;
	private HandlerUtils.HandlerHolder handlerHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mFBlock = FBlock.GetInstance();
		setContentView(R.layout.activity_ads_play);
		mRet = IP.GetLocalIpData(payloadMac);
		String mStr = SharedPreference.getParamentString(mContext, "adPos");
		if( !TextUtils.isEmpty(mStr) ){
			mPos = Integer.valueOf(mStr);
		}
		Variables.mLimitOutCounter = 0;

		audioManager=(AudioManager)getSystemService(Service.AUDIO_SERVICE);

		Intent intent = new Intent("com.goldingmedia.system.load.script");
		intent.putExtra("scriptpath",Contant.SWITCH_SPEAK);
		mContext.sendBroadcast(intent);

		mDefVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 4, 0 );

		mImageView = (ImageView)findViewById(R.id.img);
		mSurfaceView = (SurfaceView)findViewById(R.id.surfaceView);
		mSurfaceView.getHolder().addCallback( new Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder arg0) {
			}

			@Override
			public void surfaceCreated(SurfaceHolder arg0) {
			}

			@Override
			public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
				int ScreenWidth, ContentWidth;
				for (int j = 0; j < 50; j++) {
					if(mFBlock == null) break;
					if (mFBlock.MediaPid.Get()) {
						if (m_TsReceiver == null ) break;
						if ( !m_TsReceiver.GetIsRunning() ){
							m_TsReceiver.Start(
									mSurfaceView.getHolder().getSurface(),
									0x241,//mFBlock.MediaPid.GetAudioPid(),
									0x141);//mFBlock.MediaPid.GetVideoPid());
						}
					} else {
						try {
							Thread.sleep(100);
						} catch (InterruptedException ignored) {
						}
					}
				}
				//setAdsPid();
				ScreenWidth = mSurfaceView.getWidth();
				ContentWidth = mSurfaceView.getWidth();
				mSurfaceView.setScaleX(ScreenWidth/ContentWidth);
			}
		});
		register();
		handlerHolder = new HandlerUtils.HandlerHolder(this);
		mLimitCounter = 0;
		handlerHolder.sendEmptyMessageDelayed(1, 100);
		isServer = IP.isServerTerminal();

	}

	@Override
	protected void onResume() {
		super.onResume();

		Intent mIntent = getIntent();
		boolean isClick = mIntent.getBooleanExtra("onclick", false);
		if(isClick){
//			AdStruct mAdData = new AdStruct();
//			mAdData.name = mIntent.getStringExtra("name");
//			mAdData.fid = mIntent.getIntExtra("fid", 0);
//			mAdData.type = mIntent.getIntExtra("type", 0);
//			mAdData.detailName = mIntent.getStringExtra("detailName");
//			mAdData.detailText = mIntent.getStringExtra("detailText");
//			mDataList.add(mAdData);
		} else {
			mDataList  = GDApplication.getmInstance().getTruckMedia().getcAds().getExtendTypeTrucksMap(Contant.ADS_EXTEND_TYPE_ADS);
			initAdInexistence();
		}

		mCounter = 0;
		SwitchContent();
	}

	@Override
	public void handlerMessage(Message msg) {
		switch (msg.what) {
			case 1:
				if (isExitActivity) return;
				handlerHolder.sendEmptyMessageDelayed(1, 1000);
				if(mIsVideoAd && isServer){
					if(mCounter%2 == 0){
						mFBlock.AdsStatus.Get();
					} else {
						mCounter++;
					}
				}

				if(mIsImgAd){
					if (mImgTimeCounter < ImgDisplayTime){
						mImgTimeCounter++;
					} else {
						SwitchContent();
					}
				}

				if(mLimitCounter < Variables.mLimitOutCounter){
					mLimitCounter++;
				} else if(mLimitCounter >= Variables.mLimitOutCounter){// 时间到退出广告，最大播放时间Variables.mLimitOutCounter = 即时广告时间 + 最大定时广告时间TIME_MAX_REGULAR
					mContext.sendBroadcast(new Intent("com.golding.exitadplay"));
				}

				// 1秒更新点一次播时间
				if(mTruck != null) DataHelper.updateStatistics(mContext, 1000, mTruck, Contant.PROPERTY_STATISTICS_ADS_ID);
				break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return true;
		}

		if(keyCode == KeyEvent.KEYCODE_HOME){
			return true;
		}
		
		if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("TAG","Destroy()");
		unregisterReceiver(receiver);
	}

	private void SwitchContent(){// 广告处理器，管理 安全广告 即时广告 定时广告
		if (isExitActivity) return;
		TruckMediaProtos.CTruckMediaNode svStruct = null;
		if (Variables.mSVStart) {// 安全广告
			Variables.mSVStart = false;
			if (/*判断是否第一次开机*/true) {
				mSecurityDataList = GDApplication.getmInstance().getTruckMedia().getcAds().getExtendTypeTrucksMap(Contant.ADS_EXTEND_TYPE_SECURITY);
				if (mSecurityDataList.size()>0) {
					svStruct = mSecurityDataList.get(0);
					mSVStart = true;
					Variables.mLimitOutCounter = Variables.mLimitOutCounter + svStruct.getPlayInfo().getTotalTime();
				}
			}
		} else if (mSVStart) {
			mSVStart = false;
			if (mLimitCounter < 5) {
				if(svStruct == null || svStruct.getPlayInfo() == null){
					return;
				}
				Variables.mLimitOutCounter = Variables.mLimitOutCounter - svStruct.getPlayInfo().getTotalTime();
			}
		}

		if (Variables.mEvenAdStart) {// 即时广告
			Variables.mEvenAdStart = false;
			mEvenAdStart = true;
			mEvenPos = 0;
			mEvenDataList = GDApplication.getmInstance().getTruckMedia().getcAds().getExtendTypeTrucksMap(Contant.ADS_EXTEND_TYPE_EVEN);
			for (int i = 0; i < mEvenDataList.size(); i++) {
				Variables.mLimitOutCounter = Variables.mLimitOutCounter + mEvenDataList.get(i).getPlayInfo().getTotalTime();
			}
		}

		if (Variables.mRegularAdStart) {// 定时广告
			Variables.mRegularAdStart = false;
			mRegularAdStart = true;
			Variables.mLimitOutCounter = Variables.mLimitOutCounter + TIME_MAX_REGULAR;
		}

		if (mSVStart) {// 播放安全视频
			PlayContent(svStruct);
			return;
		}

		if (mEvenAdStart) {// 播放即时视频
			if(mEvenPos < mEvenDataList.size()){
				PlayContent(mEvenDataList.get(mEvenPos));
				mEvenPos++;
			} else {
				mEvenAdStart = false;
			}
		}

		Log.i("TAG","SwitchContent() mEvenAdStart+|mRegularAdStart|mLimitCounter|Variables.mLimitOutCounter = " + mEvenAdStart+"|"+mRegularAdStart+"|"+mLimitCounter+"|"+Variables.mLimitOutCounter+"|");
		if (mEvenAdStart || !mRegularAdStart || mLimitCounter >= Variables.mLimitOutCounter) {
			return;
		}
		Log.i("","SwitchContent() mPos+|mDataList.size() = " + mPos + "|" + mDataList.size());
		// 播放定时广告
		if(mPos < mDataList.size()){
		} else if(mDataList.size() > 0){
			mPos = 0;
		} else {
			if(isServer) Command.sendCommandString("com.golding.exitadplay");// 广播整个网络，退出广告
			return;
		}
		TruckMediaProtos.CTruckMediaNode mAdData = mDataList.get(mPos);
		if(mRegularFirst) {
			mRegularFirst = false;
		} else if(mAdData.getPlayInfo().getPlayInterval() != 0) {
			if(isServer) Command.sendCommandString("com.golding.exitadplay");// 广播整个网络，退出广告
			return;
		}
		PlayContent(mAdData);
		mPos++;
	}

	private void PlayContent(TruckMediaProtos.CTruckMediaNode mAdData){
		mTruck = mAdData;
		Utils.onDemandRecording(true, Contant.PROPERTY_STATISTICS_ADS_ID, mAdData, mContext);
		mImageView.setVisibility(View.GONE);
		mIsImgAd = false;
		mIsVideoAd = false;
		Log.i("TAG","CategorySubId = " + mAdData.getCategorySubId());
		if(mAdData.getCategorySubId() == Contant.PROPERTY_ADS_IMG_ID){ // 图片
			mImageView.setVisibility(View.VISIBLE);
			String imgPath = Contant.getTruckMetaNodePath(mAdData.getMediaInfo().getCategoryId(),mAdData.getMediaInfo().getCategorySubId(), mAdData.getMediaInfo().getTruckMeta().getTruckFilename()+"/"+mAdData.getMediaInfo().getTruckMeta().getTruckFilename()+".jpg",true);
			mImageView.setImageURI(Uri.parse(imgPath));
			mImgTimeCounter=0;
			ImgDisplayTime=mAdData.getPlayInfo().getTotalTime();
			mIsImgAd = true;

		} else if(mAdData.getCategorySubId() == Contant.PROPERTY_ADS_MEDIA_ID ){ // 视频
			if ( null != m_TsReceiver ) {
				m_TsReceiver.SetPause(false);
			}

			mIsVideoAd = true;
			if(isServer){
				FBlock.ResetStatus();
				if (null != mFBlock) {
					mFBlock.AdsFileSet.Set((byte)4, mAdData.getMediaInfo().getTruckMeta().getTruckFilename()+".ts");
					mFBlock.Repetition.Set(false);
				}

			}
		}

		String truckDesc = mAdData.getMediaInfo().getTruckMeta().getTruckDesc();// 附带的走马灯广告
		if(!truckDesc.equals("")) {
			ArrayList<String> emgMsg = new ArrayList<String>();
			emgMsg.add(truckDesc);
			if (emgMsg.size() > 0) {
				Intent mIntentEmg1 = new Intent("com.marqueetextView.start");
				mIntentEmg1.putExtra("type", 2);
				mIntentEmg1.putExtra("emgMsg", emgMsg);
				mIntentEmg1.putExtra("level", 1);
				mIntentEmg1.putExtra("backdrop", 1);
				mIntentEmg1.putExtra("onOff", 1);// 0:Off,1:On
				mIntentEmg1.putExtra("textX", 800);
				mIntentEmg1.putExtra("move", true);
				mContext.sendBroadcast(mIntentEmg1);
			}
		}
	}

	private void register(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Contant.Actions.PLAY_STATUS_REPORT);
		filter.addAction(Contant.Actions.ADS_SWITCH);
		filter.addAction("com.golding.exitadplay");
		registerReceiver(receiver, filter);
	}

	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(Contant.Actions.PLAY_STATUS_REPORT.equals(action)){
				int status = intent.getIntExtra("status", -1);
				Log.i("","BroadcastReceiver status = " + status);
				switch(status){
					case Contant.StatusCode.PLAY_STATUS_SUCCESS:
						break;
					case Contant.StatusCode.PLAY_STATUS_FAILURE_NO_FILE:
					case Contant.StatusCode.PLAY_STATUS_FAILURE_NO_SDCARD:
						Command.sendCommandString(Contant.Actions.ADS_SWITCH+"%#%noFile");
						break;
					case Contant.StatusCode.PLAY_STATUS_FILEEND:
					case Contant.StatusCode.PLAY_STATUS_FAILURE:
					case Contant.StatusCode.PLAY_STATUS_INVALID:
						Command.sendCommandString(Contant.Actions.ADS_SWITCH);
						break;
					default:
						break;
				}
			} else if(Contant.Actions.ADS_SWITCH.equals(action)){
				String status = intent.getStringExtra("status");
				if(status == null || "".equals(status)) {
					SwitchContent();
				} else if("noFile".equals(status)) {
					if(!mSVStart && !mEvenAdStart) {
						adInexistence[mPos-1] = 1;// 点播失败，标识1
					}
					int i ;
					for (i = 0; i < adInexistence.length; i++) {
						if(adInexistence[i] == 0) {
							break;
						}
					}
					if(i == adInexistence.length) {
						if(isServer) Command.sendCommandString("com.golding.exitadplay");// 广播整个网络，退出广告
					} else {
						SwitchContent();
					}
				} else {
					SwitchContent();
				}
			} else if("com.golding.exitadplay".equals(action)){
				SharedPreference.setParamentString(mContext, "adPos", String.valueOf(mPos));
				exitActivity();
			}
		}
	};

	private int adInexistence[];// 该数组初始化标识0，所有广告可以点播成功，如果在点播过程中，点播了所有文件都失败，则直接退出定时广告
	private void initAdInexistence() {
		if(mDataList.size() != 0) {
			adInexistence = new int[mDataList.size()];
			for (int i = 0; i < adInexistence.length; i++) {
				adInexistence[i] = 0;
			}
		}
	}

	private synchronized void exitActivity(){
		if (isExitActivity) return;
		isExitActivity = true;
		Utils.onDemandRecording(false, Contant.PROPERTY_STATISTICS_ADS_ID, null, mContext);
		Intent intent = new Intent("com.goldingmedia.system.load.script");
		intent.putExtra("scriptpath",Contant.SWITCH_HEADPHONE);
		mContext.sendBroadcast(intent);
		if ( null != m_TsReceiver ) {
			m_TsReceiver.SetPause(true);
		}

		if(isServer)
			mFBlock.StopAdsStram.Set();

		Log.i("TAG","exitActivity  Thread.sleep(3000+mCount*250);");

		if ( null != m_TsReceiver ) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Variables.tsStopAllowPlay = false;
					m_TsReceiver.Stop();
					m_TsReceiver  = null;
					Variables.isTsStop = true;
					int mCount = payloadMac[payloadMac.length-1];
					if(mCount > 50){
						mCount = 50;
					}
					try {
						Thread.sleep(3000+mCount*250);
					} catch (Exception e) {
						e.printStackTrace();
					}
					mContext.sendBroadcast(new Intent("com.golding.isTsStop"));
				}
			}).start();
		}
		if(payloadMac.length > 0){
			int mCount = payloadMac[payloadMac.length-1];
			if(mCount > 50){
				mCount = 50;
			}
			try {
				Thread.sleep(1000+mCount*250);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ( null != m_TsReceiver ) {
			Variables.isTsStop = false;
			m_TsReceiver.Close();
		}
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mDefVolume, 0 );
		Variables.tsStopAllowPlay = true;
		finish();
	}

}