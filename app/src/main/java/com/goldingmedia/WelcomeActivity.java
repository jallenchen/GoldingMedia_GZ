package com.goldingmedia;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.goldingmedia.contant.Contant;
import com.goldingmedia.jni.LcdPowerSwitch;
import com.goldingmedia.mvp.view.fragment.INICBurningFragment;
import com.goldingmedia.sqlite.DataSharePreference;
import com.goldingmedia.utils.HandlerUtils;
import com.goldingmedia.utils.NLog;
import com.goldingmedia.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class WelcomeActivity extends BaseActivity implements HandlerUtils.OnReceiveMessageListener{
    private Context mContext;
    private ImageView imageView;
    private HandlerUtils.HandlerHolder handlerHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_welcome);
        imageView = (ImageView) findViewById(R.id.image) ;
        LcdPowerSwitch.lcdOn();
        NLog.e("WelcomeActivity","onCreate");
        Intent intent = new Intent("com.goldingsysservice.restart");
        sendBroadcast(intent);
        handlerHolder = new HandlerUtils.HandlerHolder(this);

        if(initINIC()) {
            File txtSeatPath = new File("sdcard/.txt/");
            if(!txtSeatPath.exists()) {
                txtSeatPath.mkdir();
            }

            String saveFilePath = "data/data/com.goldingmedia/databases/"+Contant.DATABASE_NAME;
            File dec = new File(saveFilePath);

            try {
                InputStream is = context.getAssets().open("updateDBCode.txt");
                String dbCode = Utils.inputStream2String(is);
                if(!dbCode.equals( DataSharePreference.getDBCode(this)) || !dec.exists() || dec.length() < 100*1024){
                    boolean isCopyDB = Utils.copyFilesFromAssets(getApplicationContext(),Contant.DATABASE_NAME, saveFilePath);
                    DataSharePreference.saveDBCode(this,dbCode);
                    NLog.d("WelcomeActivity","copyFilesFromAssets:"+isCopyDB);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            startAnimi();
            getDBData();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        NLog.e("WelcomeActivity","onStart");
    }

    /**
     * 加载动画
     */
    private void startAnimi(){
       Animation rotate = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.animi_rotate);
        rotate.setRepeatMode(Animation.RESTART);
        imageView.startAnimation(rotate);
    }

    @Override
    public void handlerMessage(Message msg) {
        Intent intent = new Intent(WelcomeActivity.this,LauncherActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean initINIC() {
        return true;
//        if (new File("sdcard/inicFlag").exists()) {
//            return true;
//        } else {
//            INICBurningFragment mINICBurningFragment = new INICBurningFragment(mContext);
//            FragmentTransaction ft = this.getFragmentManager().beginTransaction();
//            ft.replace(R.id.welcome, mINICBurningFragment, "mINICBurningFragment");
//            ft.commit();
//            mINICBurningFragment.start();
//            return false;
//        }
    }

    public void getDBData(){
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

        handlerHolder.sendEmptyMessageDelayed(0,1000);
    }

}
