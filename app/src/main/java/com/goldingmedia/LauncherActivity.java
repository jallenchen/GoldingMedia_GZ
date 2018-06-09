package com.goldingmedia;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.golding.goldinglauncher.GoldingLauncherActivity;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.GpsNavigatorProtos;
import com.goldingmedia.jni.LcdPowerSwitch;
import com.goldingmedia.most.ipc.MostTcpProtoBuf;
import com.goldingmedia.mvp.mode.EventBusCMD;
import com.goldingmedia.mvp.mode.ReDownLoadParam;
import com.goldingmedia.mvp.mode.ReUpgradeParam;
import com.goldingmedia.mvp.view.adapter.FragAdapter;
import com.goldingmedia.mvp.view.fragment.ELineFragment;
import com.goldingmedia.mvp.view.fragment.GameFragment;
import com.goldingmedia.mvp.view.fragment.GoldingFragment;
import com.goldingmedia.mvp.view.fragment.HotZoneFragment;
import com.goldingmedia.mvp.view.fragment.MediaFragment;
import com.goldingmedia.mvp.view.fragment.MoviesShowFragment;
import com.goldingmedia.mvp.view.fragment.MyAppFragment;
import com.goldingmedia.mvp.view.ui.DepthPageTransformer;
import com.goldingmedia.service.MediaCenterService;
import com.goldingmedia.service.MsgDisplayService;
import com.goldingmedia.service.PictureGetService;
import com.goldingmedia.temporary.Variables;
import com.goldingmedia.utils.HandlerUtils;
import com.goldingmedia.utils.HttpDownloader;
import com.goldingmedia.utils.LruCacheUtils;
import com.goldingmedia.utils.NLog;
import com.goldingmedia.utils.NToast;
import com.goldingmedia.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class LauncherActivity extends BaseActivity implements HandlerUtils.OnReceiveMessageListener{

    private final String TAG = "LauncherActivity";
    public static LauncherActivity mInstance;
    public FrameLayout fl_bottom_container;
    private TextView tv_time, time_colon;
    private TextView gps_place;
    private TextView tv_weather;
    private ImageView iv_net_state;
    private ImageView iv_weather;
    private ImageView iv_4G;
    public  ViewPager vpager;
    private RadioGroup title_group, rg_video_type_bottom;
    private FragAdapter adapter;
    private List<Fragment> fragments;
    private int mCurrentStatus = -1;
    private TextView tv_main_date,tv_update_msg;

    private MediaFragment mediaf;
    private static final int type_recommend = 0;
    private static final int type_video = 1;
    private static final int type_golding_media = 2;
    private static final int type_game_center = 3;
    private static final int type_e_line = 4;
    private static final int type_my_app = 5;
    boolean isPushDownload = false;
    boolean isUpgradeDownload = false;
    private HandlerUtils.HandlerHolder handlerHolder;
    public static LauncherActivity getmInstance(){
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        NLog.e(TAG,"onCreate");
        //open most gpio
        GoldingLauncherActivity.openDevice();
        GoldingLauncherActivity.setInput();

        mInstance = this;
        handlerHolder = new HandlerUtils.HandlerHolder(this);
        initView();//初始化
        Intent  intent =  new Intent(this, MediaCenterService.class);
        startService(intent);
        intent =  new Intent(this, MsgDisplayService.class);
        startService(intent);
        intent =  new Intent(this, PictureGetService.class);
        startService(intent);

    }

    private void initView(){
        findViewById();
        setListener();
        ((RadioButton)title_group.getChildAt(0)).setChecked(true);
        handlerHolder.sendEmptyMessageDelayed(Contant.MsgID.REFLESH_TIME, 1000);// 刷新时间

        if(GDApplication.getmInstance().getTruckMedia().getcHotZone().getTruckMediaNodes().size() == 0){
            getDBData();
        }
    }

    public void onPower(View v){
        NLog.e(TAG,"onPower");
        new AlertDialog.Builder(this)
                .setTitle(R.string.screen_off)
                .setMessage(R.string.screen_off_msg)
                .setPositiveButton(R.string.enter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LcdPowerSwitch.lcdOff();
                        Intent intent = new Intent(LauncherActivity.this,BlackActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }


    private void findViewById(){
        fl_bottom_container = (FrameLayout) findViewById(R.id.fl_container);
        tv_time = (TextView) findViewById(R.id.tv_main_time);
        time_colon = (TextView) findViewById(R.id.time_colon);
        vpager = (ViewPager) findViewById(R.id.pager);
        title_group = (RadioGroup) findViewById(R.id.title_group);
        rg_video_type_bottom = (RadioGroup) findViewById(R.id.rg_video_type_bottom);
        tv_main_date = (TextView) findViewById(R.id.tv_main_date);
        tv_update_msg = (TextView)findViewById(R.id.tv_update_msg);
        iv_net_state = (ImageView) findViewById(R.id.most_net) ;
        gps_place = (TextView) findViewById(R.id.GPS_place);
        tv_weather  = (TextView) findViewById(R.id.weather);
        iv_weather = (ImageView) findViewById(R.id.weather_iv);
        iv_4G = (ImageView) findViewById(R.id.net_4g);
        fragments = new ArrayList<>();
        initFragment();
        adapter  = new FragAdapter(getSupportFragmentManager(),fragments);
        vpager.setAdapter(adapter);
        vpager.setCurrentItem(0);
        vpager.setPageTransformer(true, new DepthPageTransformer());
    }

    private void initFragment(){
        Bundle args = new Bundle();

        HotZoneFragment recomf = new HotZoneFragment();
        args.putInt(Contant.KEY_TOPNUM, 0);
        recomf.setArguments(args);
        fragments.add(recomf);

        MoviesShowFragment topicf = new MoviesShowFragment();
        args.putInt(Contant.KEY_TOPNUM, 1);
        topicf.setArguments(args);
        fragments.add(topicf);

        GoldingFragment  goldingf = new GoldingFragment();
        args.putInt(Contant.KEY_TOPNUM, 2);
        goldingf.setArguments(args);
        fragments.add(goldingf);

        GameFragment  gamef = new GameFragment();
        args.putInt(Contant.KEY_TOPNUM, 3);
        gamef.setArguments(args);
        fragments.add(gamef);

        ELineFragment elinef = new ELineFragment();
        args.putInt(Contant.KEY_TOPNUM,4);
        elinef.setArguments(args);
        fragments.add(elinef);

        MyAppFragment appf = new MyAppFragment();
        args.putInt(Contant.KEY_TOPNUM,5);
        appf.setArguments(args);
        fragments.add(appf);

        newInstanceAddFragment();
    }

    private void newInstanceAddFragment(){
        Bundle pBundle = new Bundle();
        pBundle.putString("TYPE", "MOVIE");
        mediaf =  MediaFragment.newInstance(pBundle);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_container, mediaf);
        ft.commit();
        mediaf.setUserVisibleHint(false);
        fl_bottom_container.setVisibility(View.INVISIBLE);
    }


    private void setListener(){
        int j = title_group.getChildCount();
        for (int i = 0; i < j; i++) {
            final int index = i;
            View v = title_group.getChildAt(i);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if(vpager.getVisibility() == View.INVISIBLE){
                        vpager.setVisibility(View.VISIBLE);
                        mediaf.setUserVisibleHint(false);
                        fl_bottom_container.setVisibility(View.INVISIBLE);
                        rg_video_type_bottom.clearCheck();
                    }
                    vpager.setCurrentItem(index, true);
                }
            });
        }

        int k = rg_video_type_bottom.getChildCount();
        for(int i=0;i<k;i++){
            rg_video_type_bottom.getChildAt(i).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    NLog.e(TAG, "------------onClick");
                    mediaTypeLisenter(v.getId(),1);
                }
            });
        }

        /**
         * ViewPager的PageChangeListener(页面改变的监听器)
         */
        vpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            /**
             * 滑动viewPage页面获取焦点时更新导航标记
             */
            @Override
            public void onPageSelected(int position) {
                int i = title_group.getChildCount();
                NLog.d(TAG, "position="+position+"..i="+i);
                if(position<i){
                    ((RadioButton)title_group.getChildAt(position)).setChecked(true);
                }
                switch (position) {
                    case type_recommend:

                        break;
                    case type_video:

                        break;
                    case type_golding_media:

                        break;
                    case type_game_center:

                        break;
                    case type_e_line:

                        break;
                    case type_my_app:

                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int position) {

            }
        });

    }

    public void mediaTypeLisenter(int vid,int checkid){
        Bundle pBundle = new Bundle();
        int mType = Contant.MEDIA_TYPE_MOVIE;
        switch (vid) {
            case R.id.rb_bm_movie:
                mType = Contant.MEDIA_TYPE_MOVIE;
                break;
            case R.id.rb_bm_comic:
                mType = Contant.MEDIA_TYPE_CARTOON;
                break;
            case R.id.rb_bm_sports:
                mType = Contant.MEDIA_TYPE_SPORT;
                break;
            case R.id.rb_bm_drama:
                mType = Contant.MEDIA_TYPE_DRAMA;
                break;
            case R.id.rb_bm_music:
                mType = Contant.MEDIA_TYPE_MUSIC;
                break;
            case R.id.rb_bm_mtv:
                mType = Contant.MEDIA_TYPE_MTV;
               break;
            case R.id.rb_bm_tv_show:
                mType = Contant.MEDIA_TYPE_TVSHOW;
               // Utils.testCodeProto2DB();
                break;
            default:
              //  pBundle.putInt("CHECKID",checkid);
                ((RadioButton)rg_video_type_bottom.getChildAt(0)).setChecked(true);
                break;
        }
        pBundle.putInt("TYPE", mType);
        pBundle.putInt("CHECKID",checkid);
        boolean isRefresh ;
        try {
            isRefresh = mediaf.refreshData(pBundle);
            if(!isRefresh){
                NToast.shortToast(LauncherActivity.this, "暂未开放,敬请期待！");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            NToast.longToast(LauncherActivity.this, e.getMessage());
            return;
        }
        title_group.clearCheck();
        if(fl_bottom_container.getVisibility() == View.INVISIBLE){
            vpager.setVisibility(View.INVISIBLE);
            mediaf.setUserVisibleHint(true);
            fl_bottom_container.setVisibility(View.VISIBLE);
        }
    }

//    private final  Handler homeHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            // 调用窗口消息处理函数
//            onMessage(msg);
//
//        }
//    };

    private int mSystemInfoCnt = 0;
    @Override
    public void handlerMessage(Message msg) {
        onMessage(msg);

        mSystemInfoCnt ++;
        if(mSystemInfoCnt > 120 && mCurrentStatus == 0){
            mSystemInfoCnt = 0;
           boolean isTcpClosed =  GDApplication.getmInstance().getMostTcp().makeTCPMsgAndSend(Contant.CATEGORY_SYSTEM_ID,Contant.PROPERTY_SYSTEM_INFO_ID, GDApplication.getmInstance().getDataUtils().sendSystemInfoMsg());
            if(isTcpClosed){
                initMostTcp(0);
            }
        }
    }

    private void getMostState(){
        int mStatus = GoldingLauncherActivity.getValue();
        if (mStatus != mCurrentStatus) {
            mCurrentStatus = mStatus;
            //1.most ng  0.most ok
            if (1 == mStatus) {
                //fix most network display disconnect prob,20170620 by jallen
                iv_net_state.setSelected(false);
                iv_4G.setSelected(false);
            } else if (0 == mStatus) {
              //  iv_net_state.setSelected(false);

            }
            initMostTcp(mStatus);
        }
    }

    private void initMostTcp(final int isRunning){
        GDApplication.post2WorkRunnable(new Runnable() {
            @Override
            public void run() {

                try {
                    if(isRunning == 0){
                        MostTcpProtoBuf task = new MostTcpProtoBuf();
                        task.init();
                        GDApplication.getmInstance().setMostTcp(task);
                        task.execute();
                    }else{
                        GDApplication.getmInstance().getMostTcp().cancel(true);
                    }

                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onMessage(final Message msg) {
        if (msg != null) {
            switch (msg.what) {
                case Contant.MsgID.REFLESH_TIME:
                    tv_time.setText(Utils.getStringTime(" "));
                    tv_main_date.setText(Utils.getStringData());
                    if (time_colon.getVisibility() == View.VISIBLE) {
                        time_colon.setVisibility(View.GONE);
                    } else {
                        time_colon.setVisibility(View.VISIBLE);
                    }
                    if(!GoldingLauncherActivity.isError){
                        getMostState();
                    }


                    handlerHolder.sendEmptyMessageDelayed(
                            Contant.MsgID.REFLESH_TIME, 1000);
                    break;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        GoldingLauncherActivity.closeDevice();
        try {
            if(MostTcpProtoBuf.m_Socket != null){
                MostTcpProtoBuf.m_Socket.close();
            }


            GDApplication.getmInstance().setMostTcp(null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e2){
                e2.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void OnEventCmd(EventBusCMD cmd) {
        switch (cmd.getCmdId()){
            case Contant.MsgID.DOWNLOAD_MSG_V:
                tv_update_msg.setVisibility(View.VISIBLE);
                break;
            case Contant.MsgID.DOWNLOAD_MSG_G:
                tv_update_msg.setVisibility(View.GONE);
                break;
            case Contant.MsgID.NET_4G_NG:
                iv_4G.setSelected(false);
                break;
            case Contant.MsgID.NET_4G_OK:
                iv_4G.setSelected(true);
                break;
            case Contant.MsgID.NET_MOST_OK:
                iv_net_state.setSelected(true);
                willDownloadUpgrade();
                willDownloadPush();
                break;
        }
        if(cmd.getCmdId() == 0x20){
            setLanguage();
            recreate();//刷新界面
        }
    }

    private void willDownloadPush(){
        if(isPushDownload){
            return;
        }

        //重新下载没有的资源
        GDApplication.post2WorkRunnable(new Runnable() {
            @Override
            public void run() {
                List<ReDownLoadParam> downLoadParams = new ArrayList<>();
                downLoadParams =  Utils.getHashMapValues(GDApplication.getmInstance().getDataInsert().downLoadParamMap,downLoadParams);
                NLog.d(TAG,"willDownloadPush size:"+downLoadParams.size());
                if(downLoadParams.size() != 0){
                    isPushDownload = true;
                }else{
                    return;
                }
                int code = -1;
                String fileName="";
                HttpDownloader downloader = new HttpDownloader();
                for(int i = 0;i<downLoadParams.size();i++){
                    fileName =  downLoadParams.get(i).getFileName()+".tar.gz";
                    EventBus.getDefault().post(new EventBusCMD(Contant.MsgID.DOWNLOAD_MSG_V));
                    File file = new File(Contant.PushPath+fileName);
                    if (file.exists() && Utils.md5sum(Contant.PushPath+fileName).equals(downLoadParams.get(i).getMd5()) ) {
                        code = 0;
                        NLog.d(TAG,"fileName:"+fileName+"=exists");
                    }else{
                         code = downloader.downloadFileByCategory(downLoadParams.get(i).getCategoryId(),downLoadParams.get(i).getCategorySubId(),fileName,downLoadParams.get(i).getMd5());
                    }

                    NLog.d(TAG,downLoadParams.get(i).getFileName());
                    if(code == 0){
                        String imgPath = Contant.getTruckMetaNodePath(downLoadParams.get(i).getCategoryId(),downLoadParams.get(i).getCategorySubId(),"",true);
                        //UpGzip
                        File srcfile = new File(Contant.PushPath+fileName);
                        try {
                            Utils.doUnTarGz(srcfile,imgPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }


                        ContentValues cv = new ContentValues();
                        cv.put(Contant.TRUCK_VALID,1);
                        GDApplication.getmInstance().getDataInsert().updateTruckMetaByFileName(downLoadParams.get(i).getTabName(),downLoadParams.get(i).getFileName(),cv);
                        downLoadParams.get(i).setVail(1);
                        GDApplication.getmInstance().getDataInsert().downLoadParamMap.get(downLoadParams.get(i).getFileName()).setVail(1);
                    }
                }
                if (isPushDownload){
                    GDApplication.getmInstance().getDataInsert().downLoadParamMap.clear();
                    downLoadParams.clear();
                    EventBus.getDefault().post(new EventBusCMD(Contant.MsgID.REFLESH_DATA));
                    EventBus.getDefault().post(new EventBusCMD(Contant.MsgID.DOWNLOAD_MSG_G));
                    isPushDownload = false;
                }
            }
        });
    }

    private void willDownloadUpgrade(){
        if(isUpgradeDownload){
            return;
        }
        GDApplication.post2WorkRunnable(new Runnable() {
            @Override
            public void run() {
                List<ReUpgradeParam> filenamesAndmd5 = GDApplication.getmInstance().getDataInsert().getWillUpgradeFileNameAndMD5();
                NLog.d(TAG,"willDownloadUpgrade size:"+filenamesAndmd5.size());
                if(filenamesAndmd5.size() != 0){
                    isUpgradeDownload = true;
                }else{
                    return;
                }

                HttpDownloader downloader = new HttpDownloader();
                int code = -1;
                for(int i = 0;i<filenamesAndmd5.size();i++){
                    EventBus.getDefault().post(new EventBusCMD(Contant.MsgID.DOWNLOAD_MSG_V));
                    File file = new File(Contant.PushPath+filenamesAndmd5.get(i).getFilename());
                    if (file.exists() && Utils.md5sum(Contant.PushPath+filenamesAndmd5.get(i).getFilename()).equals(filenamesAndmd5.get(i).getMd5()) ) {
                        code = 0;
                    }else{
                         code = downloader.downloadUpgradeByFileName(filenamesAndmd5.get(i).getFilename(),filenamesAndmd5.get(i).getMd5());
                    }

                }
                if (isUpgradeDownload){
                    filenamesAndmd5.clear();
                    EventBus.getDefault().post(new EventBusCMD(Contant.MsgID.REFLESH_DATA));
                    EventBus.getDefault().post(new EventBusCMD(Contant.MsgID.DOWNLOAD_MSG_G));
                    isUpgradeDownload = false;
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void OnEventGPS(GpsNavigatorProtos.CGpsNavigator gpsNavigator) {
        String place = TextUtils.isEmpty(gpsNavigator.getCity()) ? gpsNavigator.getProvince() : gpsNavigator.getCity();
        Variables.mGpsPlace = place==null?"":place;
        gps_place.setText(place);
        tv_weather.setText(gpsNavigator.getWeather());
        // load weather image
        try {
            InputStream ims = getAssets().open("weather/"+gpsNavigator.getWeather()+".png");
            Drawable d = Drawable.createFromStream(ims, null);
            iv_weather.setImageDrawable(d);
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
