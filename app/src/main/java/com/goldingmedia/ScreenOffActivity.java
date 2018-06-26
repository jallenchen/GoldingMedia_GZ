package com.goldingmedia;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.jni.LcdPowerSwitch;
import com.goldingmedia.mvp.view.ui.Anticlockwise;
import com.goldingmedia.mvp.view.ui.GlideImageLoader;
import com.goldingmedia.temporary.CardManager;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

public class ScreenOffActivity extends BaseActivity {
    private Anticlockwise mTimerV;
    private Banner mBanner;
    private static final int LOOP_TIME = 5000;
    private static final int TIMER = 10;
    List<TruckMediaProtos.CTruckMediaNode> nodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenoff);
        mTimerV = (Anticlockwise) findViewById(R.id.timer);
        mBanner = (Banner) findViewById(R.id.img_cb_full);


        mTimerV.setOnTimeCompleteListener(new Anticlockwise.OnTimeCompleteListener() {
            @Override
            public void onTimeComplete() {
                //倒计时结束后关闭背光
                LcdPowerSwitch.lcdOff();
                mBanner.setVisibility(View.GONE);
            }
        });
        mBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                TruckMediaProtos.CTruckMediaNode truckMediaNode =  nodes.get(position);

                if (truckMediaNode != null) {
                    CardManager.getInstance().action(position, truckMediaNode,ScreenOffActivity.this);
                }
            }
        });

        mTimerV.initTime(TIMER);
        mTimerV.reStart();
        mBanner.setDelayTime(LOOP_TIME);
        initScreenOffAds();
        setBannerConfig();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mBanner.getVisibility() == View.GONE){
            finish();
        }else if(nodes.size() != 0){
            mTimerV.reStart(TIMER);
            mBanner.setDelayTime(LOOP_TIME);
            mBanner.startAutoPlay();
        }else{
            LcdPowerSwitch.lcdOff();
            mBanner.setVisibility(View.GONE);
            mTimerV.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBanner.stopAutoPlay();
        mTimerV.onPause();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();//屏保模式下，亮屏时，退出 Activity
        return super.onTouchEvent(event);
    }

    /**
     * 配置Banner参数
     */
    private void setBannerConfig(){
        List<String> imgPaths = getImgPath();

        mBanner.isAutoPlay(true);//自动轮播
        mBanner.setViewPagerIsScroll(true);//设置不能手动影响  默认是手指触摸 轮播图不能翻页
        mBanner.setImages(imgPaths);
        mBanner.setImageLoader(new GlideImageLoader());
        if(imgPaths.size() != 0){
            mBanner.start();
        }else {
            mBanner.setOnClickListener(null);
        }
    }

    private void initScreenOffAds(){
        nodes = getScreenOffList();
    }

    /**
     * 获取广告图片路径
     * @return 图片路径
     */
    private List<String> getImgPath(){
        List<String> list = new ArrayList<>();
        TruckMediaProtos.CTruckMediaNode truck = null ;
        String fileName;
        String imgPath;

        for (int i = 0; i < nodes.size(); i++) {
            truck = nodes.get(i);
            fileName =  truck.getMediaInfo().getTruckMeta().getTruckFilename();
            imgPath =  Contant.getTruckMetaNodePath(truck.getMediaInfo().getCategoryId(),truck.getMediaInfo().getCategorySubId(),
                    fileName+"/"+fileName+".jpg",true);
            list.add(imgPath);
        }

        return list;
    }

    /*
    *获取关闭屏幕的广告
     */
    private List<TruckMediaProtos.CTruckMediaNode> getScreenOffList() {
        return GDApplication.getmInstance().getTruckMedia().getcAds().getExtendTypeTrucksMap(Contant.ADS_EXTEND_TYPE_SCREENOFF);
    }

}
