package com.goldingmedia.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.goldingmedia.mvp.view.ui.HListView;
import com.goldingmedia.temporary.Variables;
import com.goldingmedia.temporary.Variables.StatusItem;
import com.goldingmedia.utils.HandlerUtils;
import com.goldingmedia.utils.NToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PreviewPlayActivity extends BaseActivity implements OnClickListener,HandlerUtils.OnReceiveMessageListener {
	private static final int HANDLER_TIMER = 1;
	private static final int HANDLER_MEDIA_PLAY = 2;
	private static final int HANDLER_ADAPTER_VIEW = 5;
	private static final int HANDLER_UPDATE_MEDIA_INFO = 6;
    private static final int HANDLER_SHOW_TOAST = 8;
	private static final int HANDLER_INIT_MEDIA_PLYT = 11;
	private LayoutInflater inflater;
	private SurfaceView mMovieView;
	private TextView mMovieInfomation;
	private TextView mMoviedetails;
	private ImageButton backBtn;
	private Button playBtn;
	private HListView gallery;
	private Context mContext;
	private TsReceiver m_TsReceiver;

	private ImageAdapter mAdapter;
	private TruckMediaProtos.CTruckMediaNode mTruck;
	private int position = 0;
	private int classId = 0;
	private int classSubId = 0;
	private int classMainId = 0;

	private LinearLayout relbody;

	private FBlock mFBlock;
	private byte[] payloadMac = new byte[4];
	private static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
	private HandlerUtils.HandlerHolder handlerHolder;
	private Boolean isPauseActivity = false;

	private Timer timer = new Timer( );
	private TimerTask task = new TimerTask( ) {
		public void run ( ) {
			Message message = new Message( );
			message.what = HANDLER_TIMER;
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

		getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);

		Intent intent = getIntent();
		position = intent.getIntExtra("position", 0);
		classId = intent.getIntExtra("classId", 0);
		classSubId = intent.getIntExtra("classSubId", 0);
		classMainId = intent.getIntExtra("classMainId", 0);
		Log.i("", "---classId|classSubId="+classId+"|"+classSubId);

		setContentView(R.layout.activity_preview_play);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initView();
		initListener();

		mAdapter = new ImageAdapter(this);
		gallery.setAdapter(mAdapter);

		mMovieView.setVisibility(View.VISIBLE);
		handlerHolder = new HandlerUtils.HandlerHolder(this);
		timer.schedule(task, 100,1000);
	}

	private void initView() {
		relbody = (LinearLayout) findViewById(R.id.relbody);
		mMovieView = (SurfaceView)findViewById(R.id.surfaceView);
		mMovieInfomation = (TextView) findViewById(R.id.infomation);
		mMoviedetails = (TextView) findViewById(R.id.details);
		backBtn = (ImageButton) findViewById(R.id.backBtn);
		playBtn = (Button) findViewById(R.id.playBtn);
		gallery = (HListView) findViewById(R.id.gallery);
	}

	private void initListener() {
		backBtn.setOnClickListener(this);
		playBtn.setOnClickListener(this);

		gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				mAdapter.setSelectedPosition(position);
				mAdapter.notifyDataSetChanged();
				mTruck = (TruckMediaProtos.CTruckMediaNode)mAdapter.getItem(mAdapter.getSelectedPosition());
				updateMediaInfoView();
			}
		});

		relbody.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

			}
		});

		mMovieView.getHolder().addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void surfaceCreated(SurfaceHolder arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
			}
		});
	}

	private class ImageAdapter extends BaseAdapter {
		Context context;
		int selectedPosition = 0;
		long selecteIndexId = 0;
		List<TruckMediaProtos.CTruckMediaNode> mTrucks = new ArrayList<>();

		public ImageAdapter(Context context) {
			this.context = context;
		}

		public void notifyDataSetChanged(List<TruckMediaProtos.CTruckMediaNode> trucks, int position) {
			this.selectedPosition = position;
			if (trucks != null && trucks.size() > 0) {
				this.mTrucks = trucks;
			}
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTrucks.size();
		}

		@Override
		public Object getItem(int position) {
			if (mTrucks == null) return null;
			else if (position >= mTrucks.size()) return null;
			return mTrucks.get(position);
		}

		public Object getTrucks() {
			return mTrucks;
		}

		public Object getSelecteItem() {
			return mTrucks.get(selectedPosition);
		}

		public long getSelecteItemId() {
			return selecteIndexId;
		}

		public void setSelectedPosition(int selectedPosition) {
			if(selectedPosition >= getCount()) {
				selectedPosition = 0;
			}
			this.selectedPosition = selectedPosition;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder ;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.gallery_item, parent,false);
				holder.icon = (ImageView)convertView.findViewById(R.id.icon);
				holder.name = (TextView)convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			TruckMediaProtos.CTruckMediaNode node = mTrucks.get(position);
			String imgPath = Contant.getTruckMetaNodePath(node.getMediaInfo().getCategoryId(),node.getMediaInfo().getCategorySubId(),node.getMediaInfo().getTruckMeta().getTruckFilename(),true);
			String imgName = node.getMediaInfo().getTruckMeta().getTruckImage();

			holder.icon.setImageBitmap(BitmapFactory.decodeFile(imgPath+"/"+imgName));
			holder.name.setText(node.getMediaInfo().getTruckMeta().getTruckTitle());

			if(position == selectedPosition) {
				convertView.setSelected(true);
				holder.name.setTextColor(0xffffff00);
			} else {
				convertView.setSelected(false);
				holder.name.setTextColor(0xaaffffff);
			}
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	private static class ViewHolder {
		public ImageView icon;
		public TextView name;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.backBtn:
				if(mFBlock != null){
					mFBlock.TimePosition.Set(1000);
				}
				ExitActivity();
				break;

			case R.id.playBtn:
				PauseActivity();
				Intent intent = new Intent(mContext, MediaPlayActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(StatusItem.position, mAdapter.getSelectedPosition());
				intent.putExtra(StatusItem.classId, mTruck.getCategoryId());
				intent.putExtra(StatusItem.classSubId, mTruck.getCategorySubId());
				intent.putExtra(StatusItem.classMainId, mTruck.getMediaInfo().getTruckMeta().getTruckMediaType());
				mContext.startActivity(intent);
				break;
		}
	}

	private void updateInfoView() {
		String paidAndFree="";
		if (mTruck.getPayInfo().getPayType()) {
			paidAndFree = String.format(getResources().getString(R.string.payAndFree),mTruck.getPayInfo().getPayValue(),mTruck.getPayInfo().getPayFreeTime());
		}
		String[] truckDesc = mTruck.getMediaInfo().getTruckMeta().getTruckDesc().split("&&");
		if(mTruck.getMediaInfo().getTruckMeta().getTruckMediaType()== Contant.MEDIA_TYPE_MTV) {
			String mtvInfo = String.format(getResources().getString(R.string.mtvinfo),mTruck.getMediaInfo().getTruckMeta().getTruckTitle(),(truckDesc.length>0?truckDesc[0]:""),(truckDesc.length>1?truckDesc[1]:""));
			mMovieInfomation.setText(mtvInfo);

			String mtvdetails =String.format(getResources().getString(R.string.details),(truckDesc.length>3?truckDesc[3]:""));
			mMoviedetails.setText(mtvdetails);
		} else {
			String infotxt = String.format(getResources().getString(R.string.movie_info),mTruck.getMediaInfo().getTruckMeta().getTruckTitle(),(truckDesc.length>0?truckDesc[0]:""),(truckDesc.length>1?truckDesc[1]:""),
					(truckDesc.length>2?truckDesc[2]:""),paidAndFree);
			mMovieInfomation.setText(infotxt);
			String details = String.format(getString(R.string.details),"\n"+(truckDesc.length>3?truckDesc[3]:""));
			mMoviedetails.setText(details);
		}
	}

	private void PlayNextFile() {
		mAdapter.setSelectedPosition(mAdapter.getSelectedPosition() + 1);
		mAdapter.notifyDataSetChanged();
		mTruck = (TruckMediaProtos.CTruckMediaNode)mAdapter.getItem(mAdapter.getSelectedPosition());
		updateMediaInfoView();
	}

	private void updateMediaInfoView() {
		Message message = new Message();
		message.what = HANDLER_UPDATE_MEDIA_INFO;
		handlerHolder.sendMessage(message);
	}

	private boolean isFirstInitMedia = true;
	private long playTime;
	private void InitMediaPlayerReady() {
		playTime = System.currentTimeMillis();
		if(!isFirstInitMedia) {
			new Thread(new Runnable() {
				@Override
				public void run() {

					try {
						Thread.sleep(1300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if (System.currentTimeMillis() - playTime > 1200 && !isPauseActivity) {
						Message message = new Message();
						message.what = HANDLER_INIT_MEDIA_PLYT;
						handlerHolder.sendMessage(message);
					}
				}
			}).start();

		} else {
			InitMediaPlayer();
			isFirstInitMedia = false;
		}
	}

	private synchronized void InitMediaPlayer() {
		if(m_TsReceiver == null) {
			m_TsReceiver = new TsReceiver(GlobalSettings.GetAudioSink(), GlobalSettings.GetSource());
		}
		if(mFBlock == null) {
			mFBlock = FBlock.GetInstance();
			new Thread(new Runnable() {
				@Override
				public void run() {
					int j = 0;
					for (; j < 50; j++) {
						if (isPauseActivity) return;
						if(mFBlock == null) break;
						if (mFBlock.MediaPid.Get()) {
							if(m_TsReceiver == null) break;
							if ( !m_TsReceiver.GetIsRunning() )
							{
								m_TsReceiver.Start(
										mMovieView.getHolder().getSurface(),
										mFBlock.MediaPid.GetAudioPid(),
										mFBlock.MediaPid.GetVideoPid());
								break;
							}
						} else {
							try {
								Thread.sleep(100);
							} catch (InterruptedException ignored) {
							}
						}
					}
					if (j == 50) {
						Message message = new Message( );
						message.what = HANDLER_SHOW_TOAST;
						message.obj = "不能获取 TS 通道！";
						handlerHolder.sendMessage(message);
					}
					if (mFBlock != null && m_TsReceiver != null) {
						mFBlock.PlayMode.Set(true);
						m_TsReceiver.SetPause(true);
						mFBlock.TimePosition.Set(0);
						mFBlock.PlayMode.Set(false);
						m_TsReceiver.SetPause(false);
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Message message = new Message( );
						message.what = HANDLER_MEDIA_PLAY;
						handlerHolder.sendMessage(message);
					}
				}
			}).start();
		} else {
			mFBlock.PlayMode.Set(true);
			if (m_TsReceiver != null) {
				m_TsReceiver.SetPause(true);
				mFBlock.TimePosition.Set(0);
				mFBlock.PlayMode.Set(false);
				m_TsReceiver.SetPause(false);
				new Thread() {
					public void run() {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Message message = new Message( );
						message.what = HANDLER_MEDIA_PLAY;
						handlerHolder.sendMessage(message);
					}
				}.start();
			}
		}

	}

	private synchronized void Play(){
		if (m_TsReceiver != null) {
			m_TsReceiver.SetPause(true);
		}

		mPlayDuration = 0;
		mTotalTime = mTruck.getPlayInfo().getTotalTime();

		FBlock.ResetStatus();
		mFBlock.SelectFile.Set(Contant.ContentType.video, mTruck.getMediaInfo().getTruckMeta().getTruckFilename()+".ts","");
		mFBlock.TimePosition.Set(0);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (m_TsReceiver != null) {
			m_TsReceiver.SetPause(false);
		}
		mFBlock.PlayStatus.Get();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if(mFBlock != null) mFBlock.TimePosition.Set(1000);
		ExitActivity();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isPauseActivity = false;

		try {
			initializeData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		register();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		PauseActivity();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		CancelDialog();
	}

	private void initializeData() {
		Log.i("", "-----getmSDLMediaNodes().size() = "+GDApplication.getmInstance().getTruckMedia().getcMoviesShow().getmSDLMediaNodes().size());
		(new Thread() {
			public void run() {
				List<TruckMediaProtos.CTruckMediaNode> trucks = new ArrayList<>();
				switch (classId) {
					case Contant.CATEGORY_MEDIA_ID:// 多媒体
						switch (classMainId){
							case Contant.MEDIA_TYPE_MOVIE:  // 取大类 Media 的第一个子类
								trucks = Variables.mTruckMediaMovieNodes.get(classSubId);
								break;

							default:// 大类 Media 各子类中遍历所有 子类 的描述类
								trucks = Variables.mTruckMediaTypeNodes.get(classMainId-1);
								break;
						}
						break;

					case Contant.CATEGORY_HOTZONE_ID:// 热门推荐类
						trucks = GDApplication.getmInstance().getTruckMedia().getcHotZone().getTruckMediaNodes();
						break;

					default:// 其他：
						break;
				}

				Log.i("","-----trucks.size() = " + trucks.size());
				if (trucks != null && trucks.size() != 0)
					mTruck = trucks.get(position);

				Message message = new Message();
				message.what = HANDLER_ADAPTER_VIEW;
				message.obj = trucks;
				handlerHolder.sendMessage(message);

				updateMediaInfoView();
			}
		}).start();
	}

	private AlertDialog mDialog = null;

	private void CancelDialog() {
		if (mDialog != null) {
			if (mDialog.isShowing()) {
				mDialog.cancel();
				mDialog = null;
			}
		}
	}

	private void register(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Contant.Actions.CLOSEACTIVITY);
		filter.addAction(Contant.Actions.PLAY_STATUS_REPORT);
		filter.addAction("com.golding.start.playads");
		filter.addAction("com.golding.getIsPayment");
		filter.addAction("com.golding.nullTsExit");
		registerReceiver(receiver, filter);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.e("","-----PreviewPlayActivity action = "+action);
			if("com.golding.nullTsExit".equals(action)){
				nullTsExitActivity();
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
						PlayNextFile();
						break;
					case Contant.StatusCode.PLAY_STATUS_SUCCESS:
					default:
						break;
				}
			}
		}
	};

	private int mTotalTime = 0;
	private int mPlayDuration = 0;

	@Override
	public void handlerMessage(Message msg) {
		switch (msg.what) {
			case HANDLER_TIMER:
				if(!isPauseActivity){
					if(mPlayDuration < mTotalTime){
						mPlayDuration++;
					} else if(mTotalTime != 0){
						mPlayDuration = 0;
						PlayNextFile();
					}

					boolean payType;
					int payFreeTime;
					payType = mTruck.getPayInfo().getPayType();
					payFreeTime = mTruck.getPayInfo().getPayFreeTime();
					if (!payType || Variables.isPaid || mPlayDuration/60 < payFreeTime) {
					} else {// 超过免费时长
						mPlayDuration = 0;
						PlayNextFile();
					}
				}
				break;

			case HANDLER_MEDIA_PLAY:
				if (m_TsReceiver != null && mFBlock != null && !isPauseActivity)
					if(mTruck != null) Play();
				break;

			case HANDLER_ADAPTER_VIEW:
				mAdapter.notifyDataSetChanged((List<TruckMediaProtos.CTruckMediaNode>) msg.obj, position);
				break;

			case HANDLER_UPDATE_MEDIA_INFO:
				if(mTruck != null) updateInfoView();
				else return;
				InitMediaPlayerReady();
				break;

			case HANDLER_SHOW_TOAST:
				NToast.shortToast(mContext, (String)msg.obj);
				break;

			case HANDLER_INIT_MEDIA_PLYT:
				InitMediaPlayer();
				break;

		}
	}

	private static int getScreenBrightness(Activity activity) {
		int nowBrightnessValue = 0;
		ContentResolver resolver = activity.getContentResolver();
		try {
			nowBrightnessValue = android.provider.Settings.System.getInt(
					resolver, Settings.System.SCREEN_BRIGHTNESS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nowBrightnessValue;
	}

	private static void setBrightness(Activity activity, int brightness) {
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.screenBrightness = brightness * (1f / 255f);
		activity.getWindow().setAttributes(lp);
	}


	private synchronized void nullTsExitActivity(){
		mFBlock.StopStram.Set();
		if ( null != m_TsReceiver ) {
			m_TsReceiver.SetPause(true);
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Variables.tsStopAllowPlay = false;
					m_TsReceiver.Stop();
					m_TsReceiver = null;
					Variables.isTsStop = true;
					IP.GetLocalIpData(payloadMac);
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
							if ( null == m_TsReceiver ) break;
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
					message.what = HANDLER_MEDIA_PLAY;
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

	private void PauseActivity() {
		if (isPauseActivity) return;
		isPauseActivity = true;
		if ( null != mFBlock ) {
			mFBlock.StopStram.Set();
			mFBlock  = null;
		}

		if ( null != m_TsReceiver ) {
			m_TsReceiver.SetPause(true);
			m_TsReceiver.Stop();
			m_TsReceiver  = null;
		}
	}

	private void ExitActivity() {
		Log.i("","-----PreviewPlayActivity ExitActivity ");
		PauseActivity();
		finish();
	}
}
