package com.goldingmedia;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.goldingmedia.contant.Contant;
import com.goldingmedia.jni.LcdPowerSwitch;
import com.goldingmedia.mvp.mode.EventBusCMD;
import com.goldingmedia.sqlite.DataSharePreference;
import com.goldingmedia.utils.NLog;
import com.goldingmedia.utils.Utils;

import java.util.Locale;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class BaseActivity extends FragmentActivity{
    private final String TAG = "BaseActivity";
    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = BaseActivity.this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 设置横屏
       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        EventBus.getDefault().register(this);
        setLanguage();

        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        NLog.d("BaseActivity", "mWidth="+dm.widthPixels+"..mHeight="+dm.heightPixels+".mDensity."+dm.density);
    }

    public void setLanguage(){
        String local = DataSharePreference.getLanguage(this,"local");
        if(local.equals("EN") ){
            Configuration config = getResources().getConfiguration();
            DisplayMetrics dm = getResources() .getDisplayMetrics();
            config.locale = Locale.US;
            getResources().updateConfiguration(config, dm);
        }else if(local.equals("TW") ){
            Configuration config = getResources().getConfiguration();
            DisplayMetrics dm = getResources() .getDisplayMetrics();
            config.locale = Locale.TAIWAN;
            getResources().updateConfiguration(config, dm);
        }else{
            Configuration config = getResources().getConfiguration();
            DisplayMetrics dm = getResources() .getDisplayMetrics();
            config.locale = Locale.SIMPLIFIED_CHINESE;
            getResources().updateConfiguration(config, dm);
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void OnEventCmd(final  EventBusCMD cmd) {
        NLog.e(TAG,"cmd:"+cmd.getCmdId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        NLog.e(TAG,"onDestroy:");
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
    }

    public void getDBData(){
        GDApplication.post2WorkRunnable(new Runnable() {
            @Override
            public void run() {
                GDApplication.getmInstance().getTruckMedia().getcHotZone().setTruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_HOTZONE,1));
                GDApplication.getmInstance().getTruckMedia().getcMoviesShow().setmCategorys(GDApplication.getmInstance().getDataInsert().getCategoryData(Contant.CATEGORY_MEDIA_ID));
                GDApplication.getmInstance().getTruckMedia().getcMoviesShow().setmGOLDINGtruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_MOVIESSHOW,1));
                GDApplication.getmInstance().getTruckMedia().getcMoviesShow().setmDGTVtruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_MOVIESSHOW,2));
                GDApplication.getmInstance().getTruckMedia().getcMoviesShow().setmWhalestruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_MOVIESSHOW,3));
                GDApplication.getmInstance().getTruckMedia().getcMoviesShow().setmSDLMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_MOVIESSHOW,4));
                GDApplication.getmInstance().getTruckMedia().getcGoldingMedia().setmCategorys(GDApplication.getmInstance().getDataInsert().getCategoryData(Contant.CATEGORY_GOLDING_ID));
                GDApplication.getmInstance().getTruckMedia().getcGoldingMedia().setmJTVtruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_GOLDING,1));
                GDApplication.getmInstance().getTruckMedia().getcGoldingMedia().setmMagazinetruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_GOLDING,2));
                GDApplication.getmInstance().getTruckMedia().getcGoldingMedia().setmJMalltruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_GOLDING,3));
                GDApplication.getmInstance().getTruckMedia().getcGameCenter().setLMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_GAME,1));
                GDApplication.getmInstance().getTruckMedia().getcGameCenter().setMMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_GAME,2));
                GDApplication.getmInstance().getTruckMedia().getcGameCenter().setSMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_GAME,3));
                GDApplication.getmInstance().getTruckMedia().getCeLive().setmCategorys(GDApplication.getmInstance().getDataInsert().getCategoryData(Contant.CATEGORY_ELIVE_ID));
                GDApplication.getmInstance().getTruckMedia().getCeLive().setmMalltruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_ELIVE,1));
                GDApplication.getmInstance().getTruckMedia().getCeLive().setmHoteltruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_ELIVE,2));
                GDApplication.getmInstance().getTruckMedia().getCeLive().setmFoodtruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_ELIVE,3));
                GDApplication.getmInstance().getTruckMedia().getCeLive().setmTraveltruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_ELIVE,4));
                GDApplication.getmInstance().getTruckMedia().getcMyApp().setmCategorys(GDApplication.getmInstance().getDataInsert().getCategoryData(Contant.CATEGORY_MYAPP_ID));
                GDApplication.getmInstance().getTruckMedia().getcMyApp().seteBooktruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_MYAPP,1));
                GDApplication.getmInstance().getTruckMedia().getcMyApp().setApptruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_MYAPP,2));
                GDApplication.getmInstance().getTruckMedia().getcMyApp().setSettingtruckMediaNodes(GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_MYAPP,3));
                GDApplication.getmInstance().getTruckMedia().getcAds().setmCategorys(GDApplication.getmInstance().getDataInsert().getCategoryData(Contant.CATEGORY_ADS_ID));

                EventBus.getDefault().post(new EventBusCMD(Contant.MsgID.REFLESH_DATA));
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            if(LcdPowerSwitch.lcdGet() == 0){
                LcdPowerSwitch.lcdOn();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
