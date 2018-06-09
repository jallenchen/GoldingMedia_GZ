package com.goldingmedia.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.goldingmedia.BaseActivity;
import com.goldingmedia.GDApplication;
import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.ethernet.IP;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.most.GlobalSettings;
import com.goldingmedia.most.fblock.FBlock;
import com.goldingmedia.most.ts_renderer.TsReceiver;
import com.goldingmedia.mvp.view.animations.Animations;
import com.goldingmedia.temporary.DataHelper;
import com.goldingmedia.temporary.Variables;
import com.goldingmedia.temporary.Variables.StatusItem;
import com.goldingmedia.utils.HandlerUtils;
import com.goldingmedia.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.goldingmedia.R.id.surfaceView;

public class WindowAdsPlayActivity extends BaseActivity implements HandlerUtils.OnReceiveMessageListener{

	private SurfaceView mMovieView;
	private Context mContext;
	private TsReceiver m_TsReceiver = new TsReceiver(GlobalSettings.GetAudioSink(), GlobalSettings.GetSource());

	private RelativeLayout lilplaybar;
	private Button mPriviousButton;
	private Button mPlayButton;
	private Button mNextButton;
	private Button close_btn1;

	private final static long SHOW_TIME = 800;
	private final static long DELAY_TIME = 6 * 1000;

	private Handler handler;
	private Runnable runnable;
	private boolean mStop = false;
	private List<TruckMediaProtos.CTruckMediaNode> mFidList;

	private Typeface localTypeface;
	private TextView mDurationTextView;
	private TextView mTotalTimeTextView;
	private int mTotalTime = 0;
	private int mPlayDuration = 0;
	private SeekBar mSeekBar;
	private SeekBar seekBarVolume;
	private LinearLayout relbody;
	private boolean mPause = false;
	private boolean isWidescreen = false;

	private int mProgress = 0;
	private int mProgressHold = 0;
	private int mPosition = 0;
	private int mOrient = 0;

	private FBlock mFBlock;
	private byte[] payloadMac = new byte[4];
	private static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

	private boolean mInitRunning = false;
	private boolean listIsVisible = false;
	private boolean lilplaybarIsVisible = true;
	private boolean saveProgress = false;
	private Boolean isExitActivity = false;
	private Boolean fileEnd = false;
	private AudioManager am;
	private HandlerUtils.HandlerHolder handlerHolder;

	private Timer timer = new Timer( );
	private TimerTask task = new TimerTask( ) {
		public void run ( ) {
			Message message = new Message( );
			message.what = 1;
			handlerHolder.sendMessage(message);
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		Intent mIntentMsg = new Intent("com.goldingmedia.system.load.script");
		mIntentMsg.putExtra("scriptpath",Contant.SWITCH_HEADPHONE);
		mContext.sendBroadcast(mIntentMsg);

		IP.GetLocalIpData(payloadMac);
		am =(AudioManager)getSystemService(Context.AUDIO_SERVICE);
		getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		int width = mDisplayMetrics.widthPixels;
		if(width == 1280){
			isWidescreen = true;
		}
		localTypeface = Typeface.createFromAsset(getAssets(), "font/zfgy.otf");
		handlerHolder = new HandlerUtils.HandlerHolder(this);
		mFBlock = FBlock.GetInstance();

		Intent mIntent = getIntent();
		mProgressHold = mIntent.getIntExtra(StatusItem.percent, 0);
		mPosition = mIntent.getIntExtra(StatusItem.position, 0);
		mOrient = mIntent.getIntExtra("orient", 0);
		if(mOrient == 0) {
			mFidList = new ArrayList<>();
			mFidList.add((TruckMediaProtos.CTruckMediaNode) mIntent.getSerializableExtra("truck"));
		} else {
			mFidList = GDApplication.getmInstance().getTruckMedia().getcAds().getWindowOrientTrucksMap(mOrient);
		}

		setContentView(R.layout.activity_window_ads_play);
		initView();
		initListener();
		saveProgress = false;

		register();
		mInitRunning = true;
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				while (mInitRunning) {
					mInitRunning = false;
					mFBlock.TimePosition.Set(995);
					Play();
					timer.schedule(task, 100,1000);
				}
			}
		});
	}

	private void initView() {
		relbody = (LinearLayout) findViewById(R.id.relbody);
		mMovieView = (SurfaceView)findViewById(surfaceView);
		lilplaybar = (RelativeLayout) findViewById(R.id.lilplaybar);
		mDurationTextView = (TextView)findViewById(R.id.txtduration);
		mTotalTimeTextView = (TextView)findViewById(R.id.txttotal);
		mDurationTextView.setTypeface(localTypeface);
		mTotalTimeTextView.setTypeface(localTypeface);
		mSeekBar = (SeekBar)findViewById(R.id.seekBar);
		seekBarVolume = (SeekBar)findViewById(R.id.seekBarVolume);
		mPriviousButton = (Button) findViewById(R.id.btnprevious);
		mNextButton = (Button) findViewById(R.id.btnnext);
		mPlayButton = (Button) findViewById(R.id.btnstop);
		close_btn1 = (Button)findViewById(R.id.playback);
	}

	private void initListener() {
		close_btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mFBlock.TimePosition.Set(1000);
				ExitActivity();
			}
		});

		mMovieView.getHolder().addCallback(new Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder arg0) {
				if ( null != m_TsReceiver ) {
					m_TsReceiver.Stop();
				}
			}

			@Override
			public void surfaceCreated(SurfaceHolder arg0) {
			}

			@Override
			public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
				for (int j = 0; j < 50; j++) {
					if (mFBlock.MediaPid.Get()) {
						if (m_TsReceiver == null ) break;
						if ( !m_TsReceiver.GetIsRunning() )
							m_TsReceiver.Start(
									mMovieView.getHolder().getSurface(),
									mFBlock.MediaPid.GetAudioPid(),
									mFBlock.MediaPid.GetVideoPid());
					} else {
						try {
							Thread.sleep(100);
						} catch (InterruptedException ignored) {
						}
					}
				}
			}
		});

		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mFBlock.TimePosition.Set(mProgress * 10);//mTotalTime*progress/100);
				String mStr = getDurationString(mTotalTime*mProgress/100);
				mPlayDuration = mTotalTime*mProgress/100;
				mDurationTextView.setText(mStr);
				mStop = false;
				mFBlock.PlayMode.Set(mStop);
				if ( null != m_TsReceiver ) {
					m_TsReceiver.SetPause(mStop);
				}
				mPlayButton.setBackgroundResource(mStop?R.drawable.playedbtn_style:R.drawable.pausedbtn_style);
				mPause = false;
				hideBar();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				mFBlock.PlayMode.Set(true);
				if ( null != m_TsReceiver ) {
					m_TsReceiver.SetPause(true);
				}
				handler.removeCallbacks(runnable);
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
										  boolean fromUser) {
				mProgress = progress;
				if(fromUser){
//					mProgress = progress;
				}else{
					if( progress == 100 ){
						//PlayNextFile();
					}
				}
			}
		});

		lilplaybar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideMovieList();
			}
		});

		mPriviousButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PlayPreviousFile();
				handler.removeCallbacks(runnable);
				hideBar();
			}
		});

		mNextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PlayNextFile();
				handler.removeCallbacks(runnable);
				hideBar();
			}
		});

		mPlayButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideMovieList();
				mStop = !mStop;
				mFBlock.PlayMode.Set(mStop);
				if ( null != m_TsReceiver ) {
					m_TsReceiver.SetPause(mStop);
				}
				mPlayButton.setBackgroundResource(mStop?R.drawable.playedbtn_style:R.drawable.pausedbtn_style);
			}
		});

		if (!lilplaybarIsVisible) {
			lilplaybar.setVisibility(View.GONE);
		}
		hideBar();

		relbody.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				handler.removeCallbacks(runnable);

				if (lilplaybarIsVisible && lilplaybar.getVisibility() == View.GONE) {
					Log.i("", "listIsVisible|lilplaybarIsVisible2 = "+listIsVisible+"|"+lilplaybarIsVisible);
					if(isWidescreen){
						Animations.playbarOpenWide(lilplaybar, SHOW_TIME);
					}else{
						Animations.playbarOpenNormal(lilplaybar, SHOW_TIME);
					}
					lilplaybar.setVisibility(View.VISIBLE);

				} else if (!listIsVisible && lilplaybar.getVisibility() == View.VISIBLE) {
					Log.i("", "listIsVisible|lilplaybarIsVisible1 = "+listIsVisible+"|"+lilplaybarIsVisible);
					lilplaybar.setVisibility(View.GONE);
					if(isWidescreen){
						Animations.playbarHiddenWide(lilplaybar, SHOW_TIME);
					}else{
						Animations.playbarHiddenNormal(lilplaybar, SHOW_TIME);
					}
				}
				hideBar();
			}
		});

		seekBarVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				am.setStreamVolume(AudioManager.STREAM_MUSIC,progress, AudioManager.FLAG_PLAY_SOUND);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
					handler.removeCallbacks(runnable);

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				hideBar();
			}
		});
		seekBarVolume.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
	}


	@Override
	protected void onPause() {
		super.onPause();
		ExitActivity();
	}

	private void PlayPreviousFile(){
		hideMovieList();
		PlayOnUiThread();
	}


	private void PlayNextFile(){
		mPlayDuration = 0;
		hideMovieList();
		PlayOnUiThread();
	}

	private void PlayOnUiThread() {
		mFBlock.PlayMode.Set(true);
		if ( null != m_TsReceiver ) {
			m_TsReceiver.SetPause(true);
			mFBlock.TimePosition.Set(mTotalTime>5?((mTotalTime-3)*1000/mTotalTime):0);
			mFBlock.PlayMode.Set(false);
			m_TsReceiver.SetPause(false);
			new Thread() {
				public void run() {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Message message = new Message( );
					message.what = 2;
					handlerHolder.sendMessage(message);
				}
			}.start();
		}
	}

	private synchronized void Play() {
		if ( null != m_TsReceiver ) {
			m_TsReceiver.SetPause(true);
		}

		Utils.onDemandRecording(true, mOrient==0 ? Contant.PROPERTY_STATISTICS_MEDIA_ID:Contant.PROPERTY_STATISTICS_ADS_ID, mFidList.get(mPosition), mContext);
		String fid = mFidList.get(mPosition).getMediaInfo().getTruckMeta().getTruckFilename()+".ts";
		mTotalTime = mFidList.get(mPosition).getPlayInfo().getTotalTime();
		Log.e("","======time ====  "+mTotalTime);
		mPlayDuration = 0;

		FBlock.ResetStatus();
		if (null != mFBlock) {
			mFBlock.SelectFile.Set(Contant.ContentType.video, fid, "");
		}

		String mStr = getDurationString(0);
		mDurationTextView.setText(mStr);

		mTotalTimeTextView.setText(getDurationString(mTotalTime));
		if(mProgressHold != 0){
			try {
				Thread.sleep(350);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mPlayDuration = (mProgressHold*mTotalTime)/100;
		mSeekBar.setProgress(mProgressHold);
		mFBlock.TimePosition.Set(mProgressHold * 10);
		mProgressHold = 0;
		if (mStop) {
			mPlayButton.performClick();
		} else {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if ( null != m_TsReceiver ) {
				m_TsReceiver.SetPause(false);
			}
		}
		mFBlock.PlayStatus.Get();
	}

	private void register(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Contant.Actions.CLOSEACTIVITY);
		filter.addAction(Contant.Actions.PLAY_STATUS_REPORT);
		filter.addAction("com.golding.start.playads");
		filter.addAction("com.golding.nullTsExit");
		filter.addAction("com.golding.isWindowUpdate");
		registerReceiver(receiver, filter);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.e("","-----WindowAdsPlayActivity action = "+action);
			if("com.golding.nullTsExit".equals(action)){
				nullTsExitActivity();
			} else if("com.golding.start.playads".equals(action) || Contant.Actions.CLOSEACTIVITY.equals(action)){
				ExitActivity();
			} else if(Contant.Actions.PLAY_STATUS_REPORT.equals(action)){
				int status = intent.getIntExtra("status", -2);
				Log.e("","=====return status "+status);
				switch(status){
					case Contant.StatusCode.PLAY_STATUS_FAILURE_NO_SDCARD:
						ExitActivity();
						break;
					case Contant.StatusCode.PLAY_STATUS_FAILURE_NO_FILE:
						Toast.makeText(mContext,
								mContext.getResources().getString(R.string.nofile),
								Toast.LENGTH_LONG).show();
					case Contant.StatusCode.PLAY_STATUS_INVALID:
					case Contant.StatusCode.PLAY_STATUS_FAILURE:
					case Contant.StatusCode.PLAY_STATUS_FILEEND:
						if (saveProgress) {
							if (!fileEnd) {
								Log.i("", "--FILEEND--PlayNextFile");
								PlayNextFile();
							}
						} else {
							ExitActivity();
						}
						break;
					case Contant.StatusCode.PLAY_STATUS_SUCCESS:
						mPause = false;
					default:
						break;
				}
			} else if("com.marqueetextView.isWindowUpdate".equals(action)){
				boolean isWindowUpdate = intent.getBooleanExtra("isWindowUpdate", false);
				if (isWindowUpdate) {
					ExitActivity();
				}
			}
		}
	};

	private int mTimerCounter = 0;

	@Override
	public void handlerMessage(Message msg) {
		switch (msg.what) {
			case 1:

				if(!mPause && !mStop){

					if(mPlayDuration < mTotalTime){
						mPlayDuration++;
						String mStr = getDurationString(mPlayDuration);
						mDurationTextView.setText(mStr);
						mSeekBar.setProgress(mPlayDuration*100/mTotalTime);
					} else {
						mPlayDuration = 0;
						Log.i("", "--mTimerHandler--PlayNextFile");
						fileEnd = true;
						PlayNextFile();

						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								fileEnd = false;
							}
						}).start();
					}
				}

				if(mTimerCounter % 3 == 0){
					// 3秒更新点一次播时间
					DataHelper.updateStatistics(mContext, 3000, mFidList.get(mPosition), mOrient==0 ? Contant.PROPERTY_STATISTICS_MEDIA_ID:Contant.PROPERTY_STATISTICS_ADS_ID);
				}
				mTimerCounter++;
				break;

			case 2:
				Play();
				break;
		}
	}

	private String getDurationString(int counter){
		String mStr = "";
		if(counter >= 0){
			mStr = getResources().getString(R.string.time_string_movie, counter/3600,(counter%3600)/60, ((counter%3600)%60)%60);
		}
		return mStr;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mFBlock.TimePosition.Set(1000);
		ExitActivity();
	}

	private void hideMovieList(){
		handler.removeCallbacks(runnable);
		hideBar();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		timer.cancel();

		unregisterReceiver(receiver);
	}

	private void hideBar() {
		handler = new Handler();
		handler.postDelayed(runnable = new Runnable() {
			public void run() {
				if (lilplaybar.getVisibility() == View.VISIBLE) {
					if(isWidescreen){
						Animations.playbarHiddenWide(lilplaybar, SHOW_TIME);
					}else{
						Animations.playbarHiddenNormal(lilplaybar, SHOW_TIME);
					}
					lilplaybar.setVisibility(View.GONE);
				}
			}
		}, DELAY_TIME);
	}

	private synchronized void nullTsExitActivity(){
		if (!mStop) {
			mPlayButton.performClick();
		}
		mFBlock.StopStram.Set();
		if ( null != m_TsReceiver ) {
			m_TsReceiver.SetPause(true);
			new Thread(new Runnable() {
				@Override
				public void run() {
					mProgressHold = mProgress;
					Variables.tsStopAllowPlay = false;
					m_TsReceiver.Stop();
					m_TsReceiver = null;
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
					mFBlock = FBlock.GetInstance();
					m_TsReceiver = new TsReceiver(GlobalSettings.GetAudioSink(), GlobalSettings.GetSource());
					for (int j = 0; j < 50; j++) {
						if (mFBlock.MediaPid.Get()) {
							if ( !m_TsReceiver.GetIsRunning() )
								m_TsReceiver.Start(
										mMovieView.getHolder().getSurface(),
										mFBlock.MediaPid.GetAudioPid(),
										mFBlock.MediaPid.GetVideoPid());
						} else {
							try {
								Thread.sleep(100);
							} catch (InterruptedException ignored) {
							}
						}
					}

					Message message = new Message( );
					message.what = 2;
					handlerHolder.sendMessage(message);
				}
			}).start();
		}

		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Variables.tsStopAllowPlay = true;
		if ( null != m_TsReceiver ) {
			Variables.isTsStop = false;
			m_TsReceiver.Close();
		}
	}

	private synchronized void ExitActivity() {
		if (isExitActivity) return;
		isExitActivity = true;
		Utils.onDemandRecording(false, mOrient==0 ? Contant.PROPERTY_STATISTICS_MEDIA_ID:Contant.PROPERTY_STATISTICS_ADS_ID, null, mContext);
		mFBlock.StopStram.Set();
		if ( null != m_TsReceiver ) {
			m_TsReceiver.SetPause(true);
			m_TsReceiver.Stop();
			m_TsReceiver  = null;
		}
		finish();
	}
}
