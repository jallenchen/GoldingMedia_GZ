package com.goldingmedia;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.jni.LcdPowerSwitch;
import com.goldingmedia.mvp.view.activity.ReadBookActivity;
import com.goldingmedia.mvp.view.ui.Anticlockwise;
import com.goldingmedia.mvp.view.ui.ImageViewHolder;
import com.goldingmedia.utils.NToast;

import java.util.ArrayList;
import java.util.List;

public class BlackActivity extends BaseActivity {
    private Anticlockwise mTimerV;
    private ConvenientBanner mBanner;
    private static final int LOOP_TIME = 5000;
    List<TruckMediaProtos.CTruckMediaNode> nodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black);
        mTimerV = (Anticlockwise) findViewById(R.id.timer);
        mBanner = (ConvenientBanner) findViewById(R.id.img_cb_full);


        mTimerV.setOnTimeCompleteListener(new Anticlockwise.OnTimeCompleteListener() {
            @Override
            public void onTimeComplete() {
                LcdPowerSwitch.lcdOff();
                mBanner.setVisibility(View.GONE);
            }
        });
        mBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                NToast.longToast(BlackActivity.this,"mBanners点击了条目"+position);
            }
        });
        mTimerV.initTime(10);
        mTimerV.reStart();
        mBanner.startTurning(LOOP_TIME);


        initScreenOffAds();
        setBannerConfig();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mBanner.getVisibility() == View.GONE){
            finish();
        }else{
            mTimerV.reStart(10);
            mBanner.startTurning(LOOP_TIME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBanner.stopTurning();
        mTimerV.onPause();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }

    private void setBannerConfig(){
        List<String> imgPaths =  new ArrayList<>();
        imgPaths = getImgPath();

        mBanner.setCanLoop(true);//自动轮播
        mBanner.setManualPageable(true);//设置不能手动影响  默认是手指触摸 轮播图不能翻页
        mBanner.setPages(new CBViewHolderCreator<ImageViewHolder>() {
            @Override
            public ImageViewHolder createHolder() {
                return new ImageViewHolder();
            }
        },imgPaths)
                .setPageIndicator(new int[]{R.mipmap.ponit_normal,R.mipmap.point_select}) //设置两个点作为指示器
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL) ;//设置指示器的方向水平居中

    }

    private void initScreenOffAds(){
        nodes = getScreenOffList();
    }

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
            imgPath = "/mnt/sdcard/goldingmedia/ads/av/winBottom_201710_05/winBottom_201710_05.jpg";
            list.add(imgPath);
            list.add(imgPath);
            list.add(imgPath);
        }
        imgPath = "/mnt/sdcard/goldingmedia/ads/av/winBottom_201710_05/winBottom_201710_05.jpg";
        list.add(imgPath);
        list.add(imgPath);

        return list;
    }

    /*
    *获取关闭屏幕的广告
     */
    private List<TruckMediaProtos.CTruckMediaNode> getScreenOffList() {
        return GDApplication.getmInstance().getTruckMedia().getcAds().getExtendTypeTrucksMap(Contant.ADS_EXTEND_TYPE_SCREENOFF);
    }

}
