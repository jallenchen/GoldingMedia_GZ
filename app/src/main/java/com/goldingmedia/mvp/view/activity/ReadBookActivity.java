package com.goldingmedia.mvp.view.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.goldingmedia.BaseActivity;
import com.goldingmedia.GDApplication;
import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.mvp.view.adapter.BookAdapter;
import com.goldingmedia.mvp.view.ui.BookLayout;
import com.goldingmedia.mvp.view.ui.ImageViewHolder;
import com.goldingmedia.utils.HandlerUtils;
import com.goldingmedia.utils.NLog;
import com.goldingmedia.utils.NToast;
import com.goldingmedia.utils.StreamTool;


public class ReadBookActivity extends BaseActivity{
    /** Called when the activity is first created. */
    private static final String TAG = "ReadBookActivity";
    private BookLayout bk;
    private TextView textView;
    private String ebookName;
    private int mTopCount;
    private int mTimerCounter = 0;
    private static final int LOOP_TIME = 15000;
    private boolean[] isBannerLoopEnd = {false,false,false};
    private boolean isBannerAll = false;
    private HandlerUtils.HandlerHolder handlerHolder;
    private ConvenientBanner[] mBanners = new ConvenientBanner[3];
    private ConvenientBanner mBannerAll ;
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
        mBannerAll = (ConvenientBanner)findViewById(R.id.img_cb_all);
        mBanners[0] = (ConvenientBanner)findViewById(R.id.img_cb1);
        mBanners[1] = (ConvenientBanner)findViewById(R.id.img_cb2);
        mBanners[2] = (ConvenientBanner)findViewById(R.id.img_cb3);

         ArrayList<String> str = StreamTool.getEbookData();
         BookAdapter ba = new BookAdapter(this);
         ba.addItem(str);
         bk.setPageAdapter(ba);
        textView.setText(ebookName);
        initWindowAds();
        setBannerConfig();
    }

    /**
     * 设置开始轮播以及轮播时间
     * @param banners
     */
    private void startBannersTurning(ConvenientBanner[] banners){
        for(ConvenientBanner bn : banners){
            bn.startTurning(LOOP_TIME);
        }
        if(mBannerAll!=null && isBannerAll){
            mBannerAll.startTurning(LOOP_TIME);
        }
    }

    /**
     * 停止轮播
     * @param banners
     */
    private void stopBannersTurning(ConvenientBanner[] banners){
        for(ConvenientBanner bn : banners){
            if(bn!=null){
                bn.stopTurning();   //停止轮播
            }
        }
        if(mBannerAll!=null && isBannerAll){
            mBannerAll.stopTurning();   //停止轮播
        }
    }

    private void setBannerVisibility(boolean isCbAllDis,boolean isBannerAll){
        NLog.d(TAG,"AllBanner display:"+isCbAllDis);
        if(!isBannerAll) return;
        if(isCbAllDis){
            mBannerAll.startTurning(LOOP_TIME);
            mBannerAll.setVisibility(View.VISIBLE);
            for(ConvenientBanner cb : mBanners){
                cb.setVisibility(View.GONE);
                cb.stopTurning();
            }
        }else{
            mBannerAll.stopTurning();
            mBannerAll.setVisibility(View.GONE);
            for(ConvenientBanner cb : mBanners){
                cb.setVisibility(View.VISIBLE);
                cb.startTurning(LOOP_TIME);
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
    }

    private void setBannerConfig(){
        if(isBannerAll){
            mBannerAll.setOnItemClickListener(new MyOnItemListener( Contant.ADS_WINDOW_ORIENT_All));
            setBannerDatas(mBannerAll, Contant.ADS_WINDOW_ORIENT_All);
        }

        for(int i = 0 ;i < mBanners.length;i++){
            mBanners[i].setOnItemClickListener(new MyOnItemListener(i+1));
            setBannerDatas(mBanners[i],i+1);
        }

    }

    private void setBannerDatas(ConvenientBanner mBanner,int dataType) {
        List<String> imgPaths =  new ArrayList<>();
        imgPaths = getImgPath(dataType);
//        if(imgPaths.size() < 2){
//            mBanner.setCanLoop(false);//当只有一个时，不自动轮播
//        }else {
//            mBanner.setCanLoop(true);//自动轮播
//        }
        mBanner.setCanLoop(true);//自动轮播
        mBanner.setManualPageable(true);//设置不能手动影响  默认是手指触摸 轮播图不能翻页
        mBanner.setPages(new CBViewHolderCreator<ImageViewHolder>() {
            @Override
            public ImageViewHolder createHolder() {
                return new ImageViewHolder();
            }
        },imgPaths)
                .setPageIndicator(new int[]{R.mipmap.ponit_normal,R.mipmap.point_select}) //设置两个点作为指示器
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL) //设置指示器的方向水平居中
                .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        if(state == 0 && mBanner.getCurrentItem() == 0){
                               if(dataType == Contant.ADS_WINDOW_ORIENT_All){
                                    setBannerVisibility(false,isBannerAll);
                               }else if(dataType == Contant.ADS_WINDOW_ORIENT_TOP){
                                   isBannerLoopEnd[0] = true;
                               }else if(dataType == Contant.ADS_WINDOW_ORIENT_MIDDLE){
                                   isBannerLoopEnd[1] = true;
                               }else if(dataType == Contant.ADS_WINDOW_ORIENT_BOTTOM){
                                   isBannerLoopEnd[2] = true;
                               }

                               if(isBannerLoopEnd[0] && isBannerLoopEnd[1] && isBannerLoopEnd[2]){
                                   setBannerVisibility(true,isBannerAll);
                                   isBannerLoopEnd[0] = false;
                                   isBannerLoopEnd[1] = false;
                                   isBannerLoopEnd[2] = false;
                               }
                        }
                    }
                });
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
                 list.add(imgPath);
                 list.add(imgPath);
            }

        return list;
    }

    private class MyOnItemListener implements com.bigkoo.convenientbanner.listener.OnItemClickListener{
        int nBannerNum;

        public MyOnItemListener(int bannerNum){
            nBannerNum= bannerNum;
        }

        @Override
        public void onItemClick(int position) {
            NToast.longToast(ReadBookActivity.this,nBannerNum+":mBanners点击了条目"+position);
        }
    }


    private void  initWindowAds(){
        List<TruckMediaProtos.CTruckMediaNode> list;

        list = getWindowList(Contant.ADS_WINDOW_ORIENT_All);
      //  list = getWindowList(Contant.ADS_WINDOW_ORIENT_MIDDLE);
        mTruckMapNodes.put(Contant.ADS_WINDOW_ORIENT_All,list);
        if(list.size() != 0){
            isBannerAll = true;
            setBannerVisibility(true,isBannerAll);
        }else{
            isBannerAll = false;
            setBannerVisibility(false,isBannerAll);
        }

        list = getWindowList(Contant.ADS_WINDOW_ORIENT_TOP);
        mTruckMapNodes.put(Contant.ADS_WINDOW_ORIENT_TOP,list);
        list = getWindowList(Contant.ADS_WINDOW_ORIENT_MIDDLE);
        mTruckMapNodes.put(Contant.ADS_WINDOW_ORIENT_MIDDLE,list);
        list = getWindowList(Contant.ADS_WINDOW_ORIENT_BOTTOM);
        mTruckMapNodes.put(Contant.ADS_WINDOW_ORIENT_BOTTOM,list);
    }

    private List<TruckMediaProtos.CTruckMediaNode> getWindowList(int orient) {
        return GDApplication.getmInstance().getTruckMedia().getcAds().getWindowOrientTrucksMap(orient);
    }

    public void onBack(View v){
        finish();
    }
}