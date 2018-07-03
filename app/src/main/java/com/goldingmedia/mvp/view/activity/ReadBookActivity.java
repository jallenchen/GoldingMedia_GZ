package com.goldingmedia.mvp.view.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.goldingmedia.BaseActivity;
import com.goldingmedia.GDApplication;
import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.mvp.view.adapter.BookAdapter;
import com.goldingmedia.mvp.view.ui.BookLayout;
import com.goldingmedia.mvp.view.ui.GlideImageLoader;
import com.goldingmedia.temporary.CardManager;
import com.goldingmedia.utils.HandlerUtils;
import com.goldingmedia.utils.NLog;
import com.goldingmedia.utils.NToast;
import com.goldingmedia.utils.StreamTool;
import com.goldingmedia.utils.Utils;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;


public class ReadBookActivity extends BaseActivity implements HandlerUtils.OnReceiveMessageListener{
    /** Called when the activity is first created. */
    private static final String TAG = "ReadBookActivity";
    private BookLayout bk;
    private TextView textView;
    private String ebookName;
    private int mTopCount;
    private int mTimerCounter = 0;
    private static final int LOOP_TIME = 30000;
    private boolean[] isBannerLoopEnd = {false,false,false};
    private boolean isBannerAll = false;
    private HandlerUtils.HandlerHolder handlerHolder;
    private Banner[] mBanners = new Banner[3];
    private int[] winSize = new int[3];
    private Banner mBannerAll ;
    private SparseArray<List<TruckMediaProtos.CTruckMediaNode>> mTruckMapNodes = new SparseArray<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_read_ebook);

        Intent mIntentMsg = new Intent("com.goldingmedia.system.load.script");
        mIntentMsg.putExtra("scriptpath",Contant.SWITCH_HEADPHONE);
        sendBroadcast(mIntentMsg);

        ebookName = getIntent().getStringExtra("ebookName");
    	 bk = (BookLayout) findViewById(R.id.booklayout);
        textView = (TextView) findViewById(R.id.ebook_name);
        mBannerAll = (Banner)findViewById(R.id.img_cb_all);
        mBanners[0] = (Banner)findViewById(R.id.img_cb1);
        mBanners[1] = (Banner)findViewById(R.id.img_cb2);
        mBanners[2] = (Banner)findViewById(R.id.img_cb3);

         ArrayList<String> str = StreamTool.getEbookData();
         BookAdapter ba = new BookAdapter(this);
         ba.addItem(str);
         bk.setPageAdapter(ba);
        textView.setText(ebookName);
        handlerHolder = new HandlerUtils.HandlerHolder(this);
        initWindowAds();
        setBannerConfig();
    }

    @Override
    public void handlerMessage(Message msg) {
        switch (msg.what){
            case Contant.BANNER_WINALL:
                setBannerVisibilityType(Contant.BANNER_WINALL);
                break;
            case Contant.BANNER_WINSUB:
                setBannerVisibilityType(Contant.BANNER_WINSUB);
                break;
        }
    }

    /**
     * 设置开始轮播以及轮播时间
     * @param banners
     */
    private void startBannersTurning(Banner[] banners){
        for(Banner bn : banners){
            if(bn.getVisibility() == View.VISIBLE){
                bn.startAutoPlay();
            }
        }
        if(mBannerAll.getVisibility() == View.VISIBLE){
            mBannerAll.startAutoPlay();
            if(mTruckMapNodes.get(Contant.ADS_WINDOW_ORIENT_All).size() == 1){
                handlerHolder.sendEmptyMessageDelayed(Contant.BANNER_WINSUB,LOOP_TIME);
            }
        }

    }

    /**
     * 停止轮播
     * @param banners
     */
    private void stopBannersTurning(Banner[] banners){
        for(Banner bn : banners){
            if(bn!=null){
                bn.stopAutoPlay();   //停止轮播
            }
        }
        if(isBannerAll){
            mBannerAll.stopAutoPlay();   //停止轮播
        }
    }

    private void setBannerVisibilityType(int bannerType){
          if(bannerType == Contant.BANNER_WINALL){
              mBannerAll.setVisibility(View.VISIBLE);
              mBannerAll.start();
              if(mTruckMapNodes.get(Contant.ADS_WINDOW_ORIENT_All).size() == 1){
                  handlerHolder.sendEmptyMessageDelayed(Contant.BANNER_WINSUB,LOOP_TIME);
              }

            for(Banner cb : mBanners){
                cb.setVisibility(View.GONE);
                cb.stopAutoPlay();
            }
        }else  if(bannerType == Contant.BANNER_WINSUB){

            if(winSize[0] == 0 &&winSize[1] == 0 && winSize[2] == 0){
                return;
            }
            mBannerAll.stopAutoPlay();
            mBannerAll.setVisibility(View.GONE);
            for(Banner cb : mBanners){
                cb.setVisibility(View.VISIBLE);
                cb.start();
            }

            if(Utils.getMax(winSize) == 1){
                handlerHolder.sendEmptyMessageDelayed(Contant.BANNER_WINALL,LOOP_TIME);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        startBannersTurning(mBanners);
    }

    @Override
    protected void onPause() {
        stopBannersTurning(mBanners);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerHolder.removeMessages(Contant.BANNER_WINALL);
        handlerHolder.removeMessages(Contant.BANNER_WINSUB);
        handlerHolder = null;
    }

    private void setBannerConfig(){
        mBannerAll.setOnBannerListener(new MyOnItemListener( Contant.ADS_WINDOW_ORIENT_All));
        setBannerDatas(mBannerAll, Contant.ADS_WINDOW_ORIENT_All);

        for(int i = 0 ;i < mBanners.length;i++){
            mBanners[i].setOnBannerListener(new MyOnItemListener(i+1));
            setBannerDatas(mBanners[i],i+1);
        }

    }

    private void setBannerDatas(Banner mBanner,int dataType) {
        List<String> imgPaths =  imgPaths = getImgPath(dataType);

//        if(imgPaths.size() < 2){
//            mBanner.setCanLoop(false);//当只有一个时，不自动轮播
//        }else {
//            mBanner.setCanLoop(true);//自动轮播
//        }
        mBanner.setDelayTime(LOOP_TIME);
        mBanner.isAutoPlay(true);//自动轮播
        mBanner.setViewPagerIsScroll(false);//设置不能手动影响  默认是手指触摸 轮播图不能翻页
        mBanner.setImages(imgPaths);
        mBanner.setImageLoader(new GlideImageLoader());
        mBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int pos = -1;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pos = position+1;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                int size = mTruckMapNodes.get(dataType).size();
                if(state == 2 && size == pos){
                    if(dataType == Contant.ADS_WINDOW_ORIENT_All){
                        setBannerVisibilityType(Contant.BANNER_WINSUB);
                    }else if(dataType == Contant.ADS_WINDOW_ORIENT_TOP){
                        isBannerLoopEnd[0] = true;
                    }else if(dataType == Contant.ADS_WINDOW_ORIENT_MIDDLE){
                        isBannerLoopEnd[1] = true;
                    }else if(dataType == Contant.ADS_WINDOW_ORIENT_BOTTOM){
                        isBannerLoopEnd[2] = true;
                    }

                    if(isBannerLoopEnd[0] && isBannerLoopEnd[1] && isBannerLoopEnd[2]){
                        setBannerVisibilityType(Contant.BANNER_WINALL);
                        for(int i=0;i<3;i++){
                            if(mTruckMapNodes.get(i+1).size() <= 1){
                                isBannerLoopEnd[i] = true;
                            }else{
                                isBannerLoopEnd[i] = false;
                            }
                        }
                    }
                }
            }
        });
        if(imgPaths.size() != 0){
            mBanner.start();
        }else {
            mBanner.setOnClickListener(null);
        }

    }

    private List<String> getImgPath(int dataType){
        List<String> list = new ArrayList<>();
        TruckMediaProtos.CTruckMediaNode truck = null ;
        String fileName;
        String imgPath;

         for (int i = 0; i < mTruckMapNodes.get(dataType).size(); i++) {
                truck = mTruckMapNodes.get(dataType).get(i);
                fileName =  truck.getMediaInfo().getTruckMeta().getTruckFilename();
                imgPath =  Contant.getTruckMetaNodePath(truck.getMediaInfo().getCategoryId(),truck.getMediaInfo().getCategorySubId(),
                        fileName+"/"+fileName+".jpg",true);

                 list.add(imgPath);
            }

        return list;
    }

    private class MyOnItemListener implements OnBannerListener{
        int nBannerNum;

        public MyOnItemListener(int bannerNum){
            nBannerNum= bannerNum;
        }

        @Override
        public void OnBannerClick(int position) {
            NLog.d(TAG,nBannerNum+":onItemClick:"+position);
            TruckMediaProtos.CTruckMediaNode truckMediaNode =  mTruckMapNodes.get(nBannerNum).get(position);

            if (truckMediaNode != null) {
                CardManager.getInstance().action(position, truckMediaNode,ReadBookActivity.this,null);
            }
        }
    }


    private void  initWindowAds(){
        List<TruckMediaProtos.CTruckMediaNode> list;

        list = getWindowList(Contant.ADS_WINDOW_ORIENT_All);
      //  list = getWindowList(Contant.ADS_WINDOW_ORIENT_MIDDLE);
        mTruckMapNodes.put(Contant.ADS_WINDOW_ORIENT_All,list);
        if(list.size() != 0){
            isBannerAll = true;
        }else{
            isBannerAll = false;
            //setBannerVisibility(false,isBannerAll);
        }

        list = getWindowList(Contant.ADS_WINDOW_ORIENT_TOP);
        mTruckMapNodes.put(Contant.ADS_WINDOW_ORIENT_TOP,list);
        if(list.size() > 1){
            isBannerLoopEnd[0] = false;
        }else{
            isBannerLoopEnd[0] = true;
        }
        winSize[0] = list.size();

        list = getWindowList(Contant.ADS_WINDOW_ORIENT_MIDDLE);
        mTruckMapNodes.put(Contant.ADS_WINDOW_ORIENT_MIDDLE,list);
        if(list.size() > 1){
            isBannerLoopEnd[1] = false;
        }else{
            isBannerLoopEnd[1] = true;
        }
        winSize[1] = list.size();

        list = getWindowList(Contant.ADS_WINDOW_ORIENT_BOTTOM);
        mTruckMapNodes.put(Contant.ADS_WINDOW_ORIENT_BOTTOM,list);
        if(list.size() > 1){
            isBannerLoopEnd[2] = false;
        }else{
            isBannerLoopEnd[2] = true;
        }
        winSize[2] = list.size();

        if(isBannerAll){
            mBannerAll.setVisibility(View.VISIBLE);
            for(Banner cb : mBanners){
                cb.setVisibility(View.GONE);
            }
        }else{
            mBannerAll.setVisibility(View.GONE);
            for(Banner cb : mBanners){
                cb.setVisibility(View.VISIBLE);
            }
        }
    }

    private List<TruckMediaProtos.CTruckMediaNode> getWindowList(int orient) {
        return GDApplication.getmInstance().getTruckMedia().getcAds().getWindowOrientTrucksMap(orient);
    }

    public void onBack(View v){
        finish();
    }
}