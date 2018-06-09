package com.goldingmedia.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.goldingmedia.GDApplication;
import com.goldingmedia.R;
import com.goldingmedia.activity.AdsPlayActivity;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.ethernet.HtmlRequest;
import com.goldingmedia.ethernet.IP;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.most.fblock.FBlock;
import com.goldingmedia.mvp.mode.EventBusCMD;
import com.goldingmedia.mvp.view.activity.SettingActivity;
import com.goldingmedia.sqlite.DataSharePreference;
import com.goldingmedia.temporary.Command;
import com.goldingmedia.temporary.DataHelper;
import com.goldingmedia.temporary.Modes;
import com.goldingmedia.temporary.SharedPreference;
import com.goldingmedia.temporary.SystemInfo;
import com.goldingmedia.temporary.Variables;
import com.goldingmedia.utils.HandlerUtils;

import java.io.File;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class MediaCenterService extends Service implements HandlerUtils.OnReceiveMessageListener{
    public static final ArrayList<String> m_DevList = new ArrayList<>();
	private Context mContext;
	private static FBlock mFBlock;
	private Thread mMultiComThread;

	private MulticastSocket mMultiSocketReceive = null;
	private String result = "";

	private static final  String MulticastReceive="239.245.245.245";

	private boolean mEnableADPlay = false;
	private boolean mDownLoading = false;
	private int countMarquee = 300;

	private static final int AD_COUNTER_START = 15;
	private static final int BASE_COUNTER = 415;
	private static final int OUT_COUNTER = 450;
	private static final int TAKE_PHOTO = 600;
	private boolean mSendRunning = false;
	private boolean bEnableTakePhoto = true;
	
	private long mTakePhotoCounter = 0;
	private int adsPlaying = 5;
	private HandlerUtils.HandlerHolder handlerHolder;
	
	private void register(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Contant.Actions.PLAY);
		filter.addAction("com.golding.media.playend");
		filter.addAction(Contant.Actions.CONVERSATION_SEND);
		filter.addAction("com.golding.start.playads");
		filter.addAction("com.golding.screen.setting");
		filter.addAction(Contant.Actions.APK_EXCHANGE);
		filter.addAction("com.golding.sendfile");
		filter.addAction("com.mydemo.Test");
		filter.addAction("com.golding.isTsStop");
		filter.addAction("com.golding.nullTsExit");
		filter.addAction("com.golding.exitadplay");
		filter.addAction("com.goldingmedia.initdata.finish");
		filter.addAction("android.intent.action.BOOT_COMPLETED");
		registerReceiver(receiver, filter);
	}

	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if("com.golding.media.playend".equals(action)){
				Command.sendCommandString(Contant.Actions.ADS_SWITCH);
			} else if(Contant.Actions.CONVERSATION_SEND.equals(action)){
				String mSend = intent.getStringExtra("conversation");
				Command.sendCommandString(mSend);
			} else if("com.golding.start.playads".equals(action.trim())){
				Log.e("","=====> DownLoad Status = " + mDownLoading);
				// 如果正在播放已付费(Variables.isPaid)的视频 就不启动广告
				if(!mDownLoading && (!Variables.isPaid) && Variables.isTsStop){
					Variables.isPaid = false;
					Intent mIntent = new Intent(mContext, AdsPlayActivity.class);
					mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mIntent.putExtra("onclick", false);
					mContext.startActivity(mIntent);
				}
			} else if("com.golding.screen.setting".equals(action)){
				boolean mValue = intent.getBooleanExtra("value",false);
				SharedPreference.setParamentString(mContext, "fullScreen", String.valueOf(mValue));
			}  else if(Contant.Actions.ENG_MODE.equals(action)){
				boolean mValue = intent.getBooleanExtra("value",false);
				Command.sendCommandString(Contant.Actions.ENG_MODE + "%#%"+String.valueOf(mValue));
			} else if("com.golding.sendfile".equals(action)){
				mSendRunning = true;
			} else if("com.mydemo.Test".equals(action)){
			} else if("com.golding.nullTsExit".equals(action) || "com.golding.exitadplay".equals(action)){
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(120000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Variables.isTsStop = true;
					}
				}).start();
			} else if("android.intent.action.BOOT_COMPLETED".equals(action)) {
			}else  if("com.goldingmedia.initdata.finish".equals(action)) {
				boolean isFinish = intent.getBooleanExtra("isFinish",false);
				Log.e("MediaCenterService", "Upzip finish:"+isFinish);
				DataSharePreference.saveFirstStart(mContext,isFinish);
				EventBus.getDefault().post(new EventBusCMD(Contant.MsgID.REFLESH_DATA));
			}
		}
	};    

	private int mAdsCounter = 0;
	private int mEvenCounter = 5;
	private int mEvenOutCounterAdd = 0;
	private int mEvenOutCounter = 900;
	private int mAdStartCounter = 65;
	private boolean mRegularAdStart = false;
	private boolean mEvenAdStart = false;
	private boolean mThreadStarted = false;

	@Override
	public void handlerMessage(Message msg) {
		switch (msg.what) {
			case 1:
				handlerHolder.sendEmptyMessageDelayed(1, 2000);

				adsPlaying++;
				if (!mThreadStarted) {// 广告播放处理
					if (mAdsCounter == AD_COUNTER_START) {
						mThreadStarted = true;
						try {
							mMultiSocketReceive = new MulticastSocket(IP.MULTIPORT);
							InetAddress receiveAddress = InetAddress.getByName(MulticastReceive);
							mMultiSocketReceive.joinGroup(receiveAddress);
							mMultiComThread.start();
							mEnableADPlay = IP.isServerTerminal();
						} catch (Exception e) {
							e.printStackTrace();
						}
						mAdsCounter = BASE_COUNTER;
					} else {
						mAdsCounter++;
					}
				} else {
					//Log.i("", "-----mEnableADPlay|mAdsCounter = "+mEnableADPlay+"|"+mAdsCounter);
					if (mEnableADPlay && !mDownLoading) {
						if (mAdsCounter >= OUT_COUNTER) {
							if (mAdsCounter % OUT_COUNTER == 0 && GDApplication.getmInstance().getTruckMedia().getcAds().getExtendTypeTrucksMap(Contant.ADS_EXTEND_TYPE_ADS).size() > 0) { //450// 10 minutes is one cycle
								mRegularAdStart = true;
							}
							//Log.i("", "-----mRegularAdStart = "+mRegularAdStart);

							List<TruckMediaProtos.CTruckMediaNode> mEvenDataList = GDApplication.getmInstance().getTruckMedia().getcAds().getExtendTypeTrucksMap(Contant.ADS_EXTEND_TYPE_EVEN);
							if (mAdsCounter % mEvenOutCounter == mEvenOutCounterAdd && mEvenDataList.size() > 0) {
								mEvenAdStart = true;

								if (mEvenCounter < 5) {
									if (mEvenCounter == 0) {
										mEvenOutCounter = 60;
										if (mEvenDataList.size() == 0) {
											mEvenOutCounter = 450000;
											mEvenAdStart = false;
										} else {
											for (int i = 0; i < mEvenDataList.size(); i++) {
												mEvenOutCounter = mEvenOutCounter + mEvenDataList.get(i).getPlayInfo().getTotalTime();
											}
											mEvenOutCounterAdd = mAdsCounter % mEvenOutCounter - 1;
										}
									} else if (mEvenCounter <= 3) {
										mEvenOutCounter = 450;
										mEvenOutCounterAdd = 0;
									} else {
										mEvenOutCounterAdd = 0;
										mEvenOutCounter = 900;
									}
									mEvenCounter++;
									//write mEvenCounter
									SharedPreference.setParamentInt(mContext, "mEvenCounter", mEvenCounter);
								}
							}

							if ((mRegularAdStart || mEvenAdStart)) {// 定时广告 | 即使广告
								if (mAdStartCounter >= Variables.mLimitOutCounter / 2 + 10) {
									mAdStartCounter = 0;
									Command.sendCommandString(Contant.Actions.START_ADS_ACTIVITY + "%#%" + true + "%#%" + mRegularAdStart + "%#%" + mEvenAdStart + "%#%" + Variables.mSVStart);
									mRegularAdStart = false;
									mEvenAdStart = false;
								} else if (mAdStartCounter < (Variables.mLimitOutCounter / 2 - 5) && mAdStartCounter > 2) {
									Command.sendCommandString(Contant.Actions.START_ADS_ACTIVITY + "%#%" + false + "%#%" + mRegularAdStart + "%#%" + mEvenAdStart + "%#%" + Variables.mSVStart);
									mRegularAdStart = false;
									mEvenAdStart = false;
								}
							}

						} else if (mAdsCounter == OUT_COUNTER - 1) {
							//if (Variables.mGpsPlace != null && !Variables.mGpsPlace.equals("香港")) {// 不在香港范围就播放安全指南
                                Variables.mSVStart = true;
							//}
							try {
								mEvenCounter = SharedPreference.getParamentInt(mContext, "mEvenCounter");
								mEvenAdStart = true;
								List<TruckMediaProtos.CTruckMediaNode> mEvenDataList = GDApplication.getmInstance().getTruckMedia().getcAds().getExtendTypeTrucksMap(Contant.ADS_EXTEND_TYPE_EVEN);
								if (mEvenDataList.size() == 0) {
									mEvenOutCounter = 450000;
									mEvenAdStart = false;
								}
								if (mEvenCounter < 5) {
									if (mEvenCounter == 0) {
										mEvenOutCounter = 450 + 60;
										for (int i = 0; i < mEvenDataList.size(); i++) {
											mEvenOutCounter = mEvenOutCounter + mEvenDataList.get(i).getPlayInfo().getTotalTime();
										}
									} else if (mEvenCounter <= 3) {
										mEvenOutCounter = 450;
									} else {
										mEvenOutCounter = 900;
									}
									mEvenCounter++;
									//write mEvenCounter
									SharedPreference.setParamentInt(mContext, "mEvenCounter", mEvenCounter);
								}
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
						}
						mAdStartCounter++;
						mAdsCounter++;
					}
				}

				if (bEnableTakePhoto) {// 后台拍照处理
					mTakePhotoCounter++;
					if (mTakePhotoCounter == TAKE_PHOTO) {
						bEnableTakePhoto = false;
						takePhoto();
					}
				}

				countMarquee++;
				if (countMarquee >= 450) {// 走马灯播放处理
					countMarquee = 0;
					ArrayList<String> emgMsg = new ArrayList<String>();
					List<TruckMediaProtos.CTruckMediaNode> list;
					try {
						list = GDApplication.getmInstance().getTruckMedia().getcAds().getExtendTypeTrucksMap(Contant.ADS_EXTEND_TYPE_MGR);
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
					for (int i = 0; i < list.size(); i++) {
						TruckMediaProtos.CTruckMediaNode truck = list.get(i);
						emgMsg.add(truck.getMediaInfo().getTruckMeta().getTruckDesc());
					}
					if (emgMsg != null && emgMsg.size() > 0) {
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

				break;
			case 2:
				break;

			case 3:
				break;
		}
	}
	
	private void takePhoto(){
		if(!TextUtils.isEmpty(SystemInfo.getSeatString())) {
			Intent mIntent = new Intent("com.golding.take.photo");
			mContext.sendBroadcast(mIntent);
		}
	}

	@Subscribe(threadMode = ThreadMode.MainThread)
	public void OnEventCmd(final  EventBusCMD cmd) {

		GDApplication.post2WorkRunnable(new Runnable() {
			@Override
			public void run() {
				switch (cmd.getCmdId()){
					case Contant.MsgID.REFLESH_ADS:
						switch (cmd.getValues()){
							case Contant.ADS_EXTEND_TYPE_MGR+"":// 走马灯广告推送，则更新完毕马上播放
								countMarquee = 450;
								break;
							case Contant.ADS_EXTEND_TYPE_EVEN+"":// 即时广告推送，则更新完毕马上播放
								mEvenCounter = 0;
								if (mAdsCounter > OUT_COUNTER) {
									mEvenOutCounter = 10;
									mEvenOutCounterAdd = mAdsCounter%mEvenOutCounter-1;
								}
								//write mEvenCounter
								SharedPreference.setParamentInt(mContext, "mEvenCounter", mEvenCounter);
								break;
						}
						break;
				}
			}
		});
	}
	
	private long systemLastTime = 0;
	private Boolean orderThreadStart = true;
    @Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mContext = getApplicationContext();
		handlerHolder = new HandlerUtils.HandlerHolder(this);
		SharedPreference.setParamentString(mContext, "adPos", String.valueOf(0));
		Variables.isTsStop = true;
		Variables.tsStopAllowPlay = true;
		String version = getVersion();
		Log.e("","<===========   MediaCenterService start----");
		mFBlock = FBlock.GetInstance();
		mFBlock.SetContext(this);
		for (int i = 0; i < 10; i++)
			m_DevList.add("/dev/mdev" + i + "-ca12");
		register();
		mSendRunning = false;
		systemLastTime = System.currentTimeMillis();
		Log.i("", "-----systemLastTime：" + systemLastTime);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					
					if(mSendRunning && mFBlock != null && mFBlock.serverAddr != null ){
						mSendRunning = false;
					}
					
					long systemTime = System.currentTimeMillis();
					long stepTime = systemTime - systemLastTime;
					if (stepTime < 0 || stepTime > 600000) {
						stepTime = 5000;
					}

					ArrayList<Modes.Order> orderList = DataHelper.getMediaOrderList(mContext);
					for (int i = 0; i < orderList.size(); i++) {// 遍历订单
						Modes.Order order = orderList.get(i);
						if ("1".equals(order.status)) {// 已付款的订单
							Log.i("", "-----order.time：" + order.time);
							if (order.time > 14400000) {// 订单已用4小时，订单过期
								DataHelper.deleteMediaOrder(mContext, order.ordersn);
								mContext.sendBroadcast(new Intent("com.golding.getIsPayment"));
							} else {
								// 更新订单付款后商品的可用有效时间
								order.time = order.time + stepTime;
								DataHelper.updateMediaOrderTime(mContext, order);
							}
						} else {// 未付款的订单
							if (order.count > 28800000) {// 订单生成已超8小时，删除订单(该情况只有在没有找到订单信息出现)
								DataHelper.deleteMediaOrder(mContext, order.ordersn);
							} else {// 更新订单付有效时间
								order.count = order.count + stepTime;
								DataHelper.updateMediaOrderCount(mContext, order);
							}

							// 从后台接口获取付款状态
							HashMap<String, String> map2 = HtmlRequest.getHtmlResult(Contant.PAY_HOST + "/orderBusiness/queryOrder?ordersn="+order.ordersn);
							Boolean mStatus = false;
							if (map2 != null) {// 得到查询结果
								try {
									String netStatus = map2.get("netStatus");
									if ("s1".equals(netStatus)) {// 得到订单信息
										if (map2.get("status") == null) {
											Log.i("", "map.get(status) != null");
										} else if ("1".equals(map2.get("status"))) {// 查询到订单被付款
											Log.i("", "已付款");
											mStatus = true;
											// 写入数据库，订单 已付款
											order.status = "1";
											DataHelper.updateMediaOrderStatus(mContext, order);
											//订单付款Broadcast
											mContext.sendBroadcast(new Intent("com.golding.getIsPayment"));
										} else {
											Log.i("", "-----order.count：" + order.count);
											if (orderThreadStart) {
												mStatus = false;
											} else {
												mStatus = true;
												if (order.count > 600000) {// 订单生成已超10分钟还没付款，则删除订单
													DataHelper.deleteMediaOrder(mContext, order.ordersn);
												}
											}
										}
									} else if ("s0".equals(netStatus)) {
										Log.i("", "没有找到该订单信息！");
									} else {
										Log.i("", "查询订单信息时，" + mContext.getResources().getString(R.string.disconnect_to_server)+" "+netStatus);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								Log.i("", mContext.getResources().getString(R.string.network_anomaly));
							}
							if (!mStatus) {
								if ("0".equals(order.status)) {
									order.status = "11";
									DataHelper.updateMediaOrderStatus(mContext, order);
								}
							}
						}
					}
					systemLastTime = systemTime;
					orderThreadStart = false;
					
					try {	
						Thread.sleep(5*1000);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();

		handlerHolder.sendEmptyMessageDelayed(1, 100);
		mMultiComThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				byte data[] = new byte[1024];
				Arrays.fill(data, (byte)0);
				DatagramPacket dp = new DatagramPacket(data, 1024);
				while (true) {
					try {
						mMultiSocketReceive.receive(dp);
						result = new String(data, 0, dp.getLength());
						if(!TextUtils.isEmpty(result)){
							Arrays.fill(data, (byte)0);
							String [] mResult = result.split("%#%");
							if(mResult.length > 0)
								result = mResult[0];
							Log.e("","<==== data from socket " + result);
							if(Contant.Actions.START_ADS_ACTIVITY.equals(result.trim())){
								if (mResult.length > 4) {
									Variables.mRegularAdStart = "true".equals(mResult[2]);
									Variables.mEvenAdStart = "true".equals(mResult[3]);
									Variables.mSVStart = "true".equals(mResult[4]);
									if ("true".equals(mResult[1]) && adsPlaying > 4) {
										Intent mIntent = new Intent("com.golding.start.playads");
										mContext.sendBroadcast(mIntent);
									}
								} else if (adsPlaying > 4) {
									Variables.mRegularAdStart = true;
									Intent mIntent = new Intent("com.golding.start.playads");
									mContext.sendBroadcast(mIntent);
								}
								adsPlaying = 0;
							} else if(Contant.Actions.ADS_SWITCH.equals(result.trim())){
								Intent mIntent = new Intent(Contant.Actions.ADS_SWITCH);
								if(mResult.length > 1) {
									Log.e("","ADS_SWITCH--mResult[1] = " + mResult[1]);
						            mIntent.putExtra("status", mResult[1]);
								}
								mContext.sendBroadcast(mIntent);
							} else if("com.golding.exitadplay".equals(result.trim())){
								Intent mIntent = new Intent("com.golding.exitadplay");
								mContext.sendBroadcast(mIntent);
							} else if(Contant.Actions.ENG_MODE.equals(result.trim())){
								SharedPreference.setParamentString(mContext, "eng", mResult[1]);
							} else if("com.golding.message.txt.ad".equals(result.trim())){
								if (mResult.length > 1) {
									countMarquee = 0;
									Intent mIntentEmg1 = new Intent("com.marqueetextView.start");
									mIntentEmg1.putExtra("type", 2);
									mIntentEmg1.putExtra("emgMsg", mResult[1].split("##"));
									mIntentEmg1.putExtra("level", 1);
									mIntentEmg1.putExtra("backdrop", 1);
									mIntentEmg1.putExtra("onOff", 1);// 0:Off,1:On
									mIntentEmg1.putExtra("textX", 800);
									mIntentEmg1.putExtra("move", true);
									mContext.sendBroadcast(mIntentEmg1);
								}
							} else {
								Intent mConversationIntent = new Intent(Contant.Actions.CONVERSATION_MSG);
								mConversationIntent.putExtra("conversation", result);
								mContext.sendBroadcast(mConversationIntent);
								String type = result.split("@")[0];

								if(type.equals("EngineeringAndTesting")){
									String[] msgA = result.split("@");
									String onOrOff =msgA[1];
									if(onOrOff.equals("on")) {
										String otherMsg =msgA.length>=3?msgA[2]:"0";
										Intent request = new Intent(mContext ,SettingActivity.class);
										request.putExtra("position", -1);
										request.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(request);

									} else if(onOrOff.equals("off")) {
										Intent finishActivityBroadcast = new Intent("com.golding.seat.finishactivity");
										sendBroadcast(finishActivityBroadcast);
									} else if(onOrOff.equals("openApp")) {
										String pkgName = msgA.length>=3?msgA[2]:"";
										if (pkgName != null && !"".equals(pkgName)) {
											try {
												Intent openAppIntent = getPackageManager().getLaunchIntentForPackage(pkgName);
												openAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
												startActivity(openAppIntent);
											} catch (Exception e) {
												//  NToast(SettingActivity.this,getResources().getString(R.string.cannotOpenApp));
											}
										}
									} else if(onOrOff.equals("uninstallApp")) {
										String pkgName = msgA.length>=3?msgA[2]:"";
										if (pkgName != null && !"".equals(pkgName)) {
											Uri packageURI = Uri.parse("package:"+pkgName);
											Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
											uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
											uninstallIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
											startActivity(uninstallIntent);
										}
									}
								} else if(type.equals("seat")) {//系统启动时候  每个座位全车同步下
									/**
									 * 同步全车座位号【ip+seat】
									 */
									String msgArr[] = result.split("@");
									if (msgArr.length < 2) {
										return;
									}
									String elementValue =  msgArr[1];//IP+座位号
									String elementIpValue =  msgArr[1].split("#")[0];//设置座位号的终端的IP地址
									String elementSeatValue =  msgArr[1].split("#")[1];//设置座位号的终端的座位号
									boolean ISLocal = elementIpValue.equals(IP.getLocalHostIp());
									boolean exit = elementSeatValue.equals("exit");//清空全车座位号码
									File chatfile = new File(Contant.CHATSEAT);
									if (ISLocal) {//收到本机设置座位号消息处理
										if (!exit) {
											//验证网络
											Intent seatSettingbroadcast = new Intent("com.golding.seat.seatsetting");
											SystemInfo.SynchronizedSeat(elementValue);
											seatSettingbroadcast.putExtra("issucceed", true);
											seatSettingbroadcast.putExtra("elementValue", elementValue);
											sendBroadcast(seatSettingbroadcast);
										}else{
											if (chatfile.exists()) {
												chatfile.delete();
											}else{
											}
										}
									}else{//收到其他终端设置座位号消息处理
										if (!exit) {
											SystemInfo.SynchronizedSeat(elementValue);
											if (SystemInfo.getLocalSeat().equals(elementValue.split("#")[1])) {
												SystemInfo.SetLocalSeat("");
											}
										} else {

											if (chatfile.exists()) {
												chatfile.delete();
											}else{
											}
											File seattxtfile = new File(Contant.SETTINGDATETXT);
											if (seattxtfile.exists()) {
												Intent i = new Intent("com.golding.seat.updatenumberseat");
												sendBroadcast(i);
												seattxtfile.delete();
											}else{
											}
										}
									}
								} else if (type.equals("arrangement")) {
									String msgArr[] = result.split("@");
									if (msgArr.length < 2) {
										return;
									}
									String mtr = msgArr[1];
									if (!TextUtils.isEmpty(mtr)){
										SystemInfo.SaveSeatArrangement(mtr);
										Intent i = new Intent("com.golding.seat.updatesetting");
										sendBroadcast(i);
									}

								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
     
	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	@Override
	public void onTrimMemory(int level) {
		// TODO Auto-generated method stub
		super.onTrimMemory(level);
		Log.e("","<*********  Trim Memory **********>"+level);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("","<*********onDestroy**********>");
		mMultiComThread.interrupt();
		
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getVersion() {
		 try {
			 PackageManager manager = this.getPackageManager();
			 PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			 return info.versionName;
		 } catch (Exception e) {
			 e.printStackTrace();
			 return "";
		 }
	}
}
