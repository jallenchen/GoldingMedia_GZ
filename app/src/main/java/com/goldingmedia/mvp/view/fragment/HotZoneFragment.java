package com.goldingmedia.mvp.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldingmedia.GDApplication;
import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.mvp.mode.EventBusCMD;
import com.goldingmedia.mvp.view.ui.GlideImageLoader;
import com.goldingmedia.temporary.CardManager;
import com.goldingmedia.utils.NLog;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HotZoneFragment extends BaseFragment{
    private static String TAG = "HotZoneFragment";
    private  SparseArray<List<TruckMediaProtos.CTruckMediaNode>> mTruckMapNodes = new SparseArray<>();
    private Banner[] mBanners = new Banner[5];

    public HotZoneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_hotzone, container, false);
        initView(view);
        initData();
        setBannerConfig();
        return view;
    }

    private void initView(View view ){
        mBanners[0] = (Banner) view.findViewById(R.id.img_cb1);
        mBanners[1] = (Banner) view.findViewById(R.id.img_cb2);
        mBanners[2] = (Banner) view.findViewById(R.id.img_cb3);
        mBanners[3] = (Banner) view.findViewById(R.id.img_cb4);
        mBanners[4] = (Banner) view.findViewById(R.id.img_cb5);
    }

    /**
     * 设置监听器和Banner的配置
     */
    private void setBannerConfig(){
        for(int i = 0 ;i < mBanners.length;i++){
            mBanners[i].setOnBannerListener(new MyListener(i));
            setBannerDatas(mBanners[i],i);
        }
    }



    private class MyListener implements OnBannerListener{
        int nBannerNum;

        public MyListener(int bannerNum){
            nBannerNum= bannerNum;
            NLog.e(TAG,"MyListener banner num :"+nBannerNum);
        }

        @Override
        public void OnBannerClick(int position) {
            NLog.e(TAG,nBannerNum+":MyListener banner num :"+position);
            TruckMediaProtos.CTruckMediaNode truckMediaNode =  mTruckMapNodes.get(nBannerNum).get(position);

            if (truckMediaNode != null) {
                CardManager.getInstance().action(position, truckMediaNode, getActivity(),null);
            }
        }
    }

    /**
     * 设置开始轮播以及轮播时间
     * @param banners
     */
    private void startBannersTurning(Banner[] banners){
        for(Banner bn : banners){
            bn.setDelayTime(2500);
            bn.startAutoPlay();
        }
    }

    /**
     * 停止轮播
     * @param banners
     */
    private void stopBannersTurning(Banner[] banners){
        for(Banner bn : banners){
            if(bn!=null){
                bn.stopAutoPlay();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startBannersTurning(mBanners);
    }

    @Override
    public void onPause() {
        stopBannersTurning(mBanners);
        super.onPause();
    }

    private void initData(){
        mTruckMapNodes.clear();
        mTruckMapNodes.put(0,GDApplication.getmInstance().getTruckMedia().getcHotZone().getTruckMediaNodes());
        for (int i = 1; i < 5; i++) {
            mTruckMapNodes.put(i,GDApplication.getmInstance().getTruckMedia().getcAds().getHomeOrientTrucksMap(i));
        }

    }

    private List<String> getImgPath(int dataType){
        List<String> list = new ArrayList<>();
        TruckMediaProtos.CTruckMediaNode truck;
        String fileName;
        String imgPath;
        String path;

        switch (dataType){
            case 0:
                for (int i = 0; i < mTruckMapNodes.get(0).size(); i++) {
                    fileName =  mTruckMapNodes.get(0).get(i).getMediaInfo().getTruckMeta().getTruckFilename();
                    imgPath =  Contant.HOTZONE_PATH + fileName+"/"+fileName+".jpg";
                    list.add(imgPath);
                }
                break;
            case 1:
            case 2:
            case 3:
            case 4:
                for (int i = 0; i < mTruckMapNodes.get(dataType).size(); i++) {
                    truck = mTruckMapNodes.get(dataType).get(i);
                    fileName =  truck.getMediaInfo().getTruckMeta().getTruckFilename();

                    path  = Contant.getTruckMetaNodePath(Contant.CATEGORY_ADS_ID,truck.getCategorySubId(),fileName,true);
                    imgPath =  path+"/"+fileName+".jpg";
                    list.add(imgPath);
                }

                break;
        }

        return list;
    }

    private void setBannerDatas(Banner mBanner,int dataType) {
        List<String> imgPaths =  getImgPath(dataType);

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


    @Override
    public void OnEventCmd(EventBusCMD cmd) {
        super.OnEventCmd(cmd);
        switch (cmd.getCmdId()){
            case Contant.MsgID.REFLESH_UI:
                initData();
                break;
        }
    }

}
