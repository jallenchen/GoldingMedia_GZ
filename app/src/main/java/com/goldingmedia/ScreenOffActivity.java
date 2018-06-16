package com.goldingmedia;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.jni.LcdPowerSwitch;
import com.goldingmedia.mvp.view.ui.Anticlockwise;
import com.goldingmedia.mvp.view.ui.ImageViewHolder;
import com.goldingmedia.temporary.CardManager;
import com.goldingmedia.utils.NToast;

import java.util.ArrayList;
import java.util.List;

public class ScreenOffActivity extends BaseActivity {
    private Anticlockwise mTimerV;
    private ConvenientBanner mBanner;
    private static final int LOOP_TIME = 5000;
    private static final int TIMER = 10;
    List<TruckMediaProtos.CTruckMediaNode> nodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenoff);
        mTimerV = (Anticlockwise) findViewById(R.id.timer);
        mBanner = (ConvenientBanner) findViewById(R.id.img_cb_full);


        mTimerV.setOnTimeCompleteListener(new Anticlockwise.OnTimeCompleteListener() {
            @Override
            public void onTimeComplete() {
                //倒计时结束后关闭背光
                LcdPowerSwitch.lcdOff();
                mBanner.setVisibility(View.GONE);
            }
        });
        mBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                TruckMediaProtos.CTruckMediaNode truckMediaNode =  nodes.get(position);

                if (truckMediaNode != null) {
                    CardManager.getInstance().action(position, truckMediaNode,ScreenOffActivity.this);
                }
            }
        });
        mTimerV.initTime(TIMER);
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
        }else if(nodes.size() != 0){
            mTimerV.reStart(TIMER);
            mBanner.startTurning(LOOP_TIME);
        }else{
            LcdPowerSwitch.lcdOff();
            mBanner.setVisibility(View.GONE);
            mTimerV.setVisibility(View.GONE);
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
        finish();//屏保模式下，亮屏时，退出 Activity
        return super.onTouchEvent(event);
    }

    /**
     * 配置Banner参数
     */
    private void setBannerConfig(){
        List<String> imgPaths = getImgPath();;

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
