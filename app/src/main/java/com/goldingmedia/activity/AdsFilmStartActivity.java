package com.goldingmedia.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
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
import com.goldingmedia.temporary.Variables;
import com.goldingmedia.utils.HandlerUtils;
import com.goldingmedia.utils.NLog;
import com.goldingmedia.utils.Utils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jallen on 2018/6/19 0019 09:21.
 */
public class AdsFilmStartActivity extends BaseActivity implements HandlerUtils.OnReceiveMessageListener{
    private static final String TAG = "AdsFilmStartActivity";
    private static final int ADS_TIME_MAX = 17;
    private List<TruckMediaProtos.CTruckMediaNode> mFileStartList;
    private SurfaceView mSurfaceView;
    private TsReceiver m_TsReceiver = new TsReceiver(GlobalSettings.GetAudioSink(), GlobalSettings.GetSource());
    private final byte[] payloadMac = new byte[4];
    private FBlock mFBlock;
    private int mTimeCounter = 0;
    private Context mContext;
    private int mPos = 0;
    private Boolean isExitActivity = false;
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
        NLog.d(TAG,"---onCreate");
        mFBlock = FBlock.GetInstance();
        IP.GetLocalIpData(payloadMac);
        setContentView(R.layout.activity_ads_play);
        register();
        initAdsdata();
        mContext = this;
        handlerHolder = new HandlerUtils.HandlerHolder(this);
        mSurfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        mSurfaceView.getHolder().addCallback( new SurfaceHolder.Callback() {
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

//        Intent mIntent = getIntent();
//         position = mIntent.getIntExtra("position", 0);
//         classId = mIntent.getIntExtra("classId", 0);
//         classSubId = mIntent.getIntExtra("classSubId", 0);
//         classMainId = mIntent.getIntExtra("classMainId", 0);


        timer.schedule(task, 100,1000);
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
            Log.e(TAG,"-----WindowAdsPlayActivity action = "+action);
            if("com.golding.nullTsExit".equals(action)){
                nullTsExitActivity();
            } else if("com.golding.start.playads".equals(action) || Contant.Actions.CLOSEACTIVITY.equals(action)){
                ExitActivity();
            } else if(Contant.Actions.PLAY_STATUS_REPORT.equals(action)){
                int status = intent.getIntExtra("status", -2);
                Log.e(TAG,"=====return status "+status);
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

                        break;
                    case Contant.StatusCode.PLAY_STATUS_SUCCESS:
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

    @Override
    public void handlerMessage(Message msg) {
        switch (msg.what){
            case 1:
                if(mTimeCounter < ADS_TIME_MAX){
                    mTimeCounter ++;
                }else{
                    ExitActivity();
                }

                if(mTimeCounter % 15 == 0){
                    switchAds();
                }


                break;
        }

    }

    private void initAdsdata(){
        mFileStartList = getAdsFilmStartList();
        NLog.d(TAG,"---mFileStartList size:"+mFileStartList.size());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        NLog.d(TAG,"---onDestroy");
        unregisterReceiver(receiver);
        timer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        switchAds();

    }



    private List<TruckMediaProtos.CTruckMediaNode> getAdsFilmStartList() {
        return GDApplication.getmInstance().getTruckMedia().getcAds().getExtendTypeTrucksMap(Contant.ADS_EXTEND_TYPE_ADS);
      //  return GDApplication.getmInstance().getTruckMedia().getcAds().getWindowOrientTrucksMap(Contant.ADS_EXTEND_TYPE_FIRST);
    }

    private void switchAds(){
        if(mFileStartList.size() == 0){
            ExitActivity();
            return;
        }
        if(mPos >= mFileStartList.size()){
            ExitActivity();
            return;
        }
        playAds(mFileStartList.get(mPos));
        mPos++;
    }

    private void playAds(TruckMediaProtos.CTruckMediaNode ad){
         if(ad.getCategorySubId() == Contant.PROPERTY_ADS_MEDIA_ID ){ // 视频
            if ( null != m_TsReceiver ) {
                m_TsReceiver.SetPause(false);
            }

            FBlock.ResetStatus();
            if (null != mFBlock) {
                mFBlock.AdsFileSet.Set((byte)4, ad.getMediaInfo().getTruckMeta().getTruckFilename()+".ts");
                mFBlock.Repetition.Set(false);
            }
        }
    }

    private synchronized void ExitActivity() {
        if (isExitActivity) return;
        NLog.d(TAG,"---ExitActivity");
        isExitActivity = true;
       // Utils.onDemandRecording(false, mOrient==0 ? Contant.PROPERTY_STATISTICS_MEDIA_ID:Contant.PROPERTY_STATISTICS_ADS_ID, null, mContext);
        mFBlock.StopStram.Set();
        if ( null != m_TsReceiver ) {
            m_TsReceiver.SetPause(true);
            m_TsReceiver.Stop();
            m_TsReceiver  = null;
        }
        finish();
    }
    private synchronized void nullTsExitActivity(){
        NLog.d(TAG,"---nullTsExitActivity");
        mFBlock.StopStram.Set();
        if ( null != m_TsReceiver ) {
            m_TsReceiver.SetPause(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
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
                                        mSurfaceView.getHolder().getSurface(),
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

}
