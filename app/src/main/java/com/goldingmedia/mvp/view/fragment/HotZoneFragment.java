package com.goldingmedia.mvp.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.goldingmedia.GDApplication;
import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.mvp.mode.EventBusCMD;
import com.goldingmedia.mvp.view.ui.ImageViewHolder;
import com.goldingmedia.temporary.CardManager;
import com.goldingmedia.utils.NLog;
import com.goldingmedia.utils.NToast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HotZoneFragment extends BaseFragment{
    private static String TAG = "HotZoneFragment";
    private  SparseArray<List<TruckMediaProtos.CTruckMediaNode>> mTruckMapNodes = new SparseArray<>();
    private ConvenientBanner[] mBanners = new ConvenientBanner[5];

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
        mBanners[0] = (ConvenientBanner) view.findViewById(R.id.img_cb1);
        mBanners[1] = (ConvenientBanner) view.findViewById(R.id.img_cb2);
        mBanners[2] = (ConvenientBanner) view.findViewById(R.id.img_cb3);
        mBanners[3] = (ConvenientBanner) view.findViewById(R.id.img_cb4);
        mBanners[4] = (ConvenientBanner) view.findViewById(R.id.img_cb5);
    }

    /**
     * 设置监听器和Banner的配置
     */
    private void setBannerConfig(){
        for(int i = 0 ;i < mBanners.length;i++){
            mBanners[i].setOnItemClickListener(new MyListener(i));
            setBannerDatas(mBanners[i],i);
        }

    }

    private class MyListener implements com.bigkoo.convenientbanner.listener.OnItemClickListener{
        int nBannerNum;

        public MyListener(int bannerNum){
            nBannerNum= bannerNum;
            NLog.e(TAG,"MyListener banner num :"+nBannerNum);
        }

        @Override
        public void onItemClick(int position) {
            TruckMediaProtos.CTruckMediaNode truckMediaNode =  mTruckMapNodes.get(nBannerNum).get(position);

            if (truckMediaNode != null) {
                CardManager.getInstance().action(position, truckMediaNode, getActivity());
            }
        }
    }

    /**
     * 设置开始轮播以及轮播时间
     * @param banners
     */
    private void startBannersTurning(ConvenientBanner[] banners){
        for(ConvenientBanner bn : banners){
            bn.startTurning(2500);
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

//        for (int i = 0; i < mTruckMapNodes.get(dataType).size(); i++) {
//        String fileName =  mTruckMapNodes.get(dataType).get(i).getMediaInfo().getTruckMeta().getTruckFilename();
//        String   imgPath =  Contant.HOTZONE_PATH + fileName+"/"+fileName+".jpg";
//        list.add(imgPath);
//        }
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

    private void setBannerDatas(ConvenientBanner mBanner,int dataType) {
        List<String> imgPaths =  getImgPath(dataType);
        if(imgPaths.size() < 2){
            mBanner.setCanLoop(false);//当只有一个时，不自动轮播
        }else {
            mBanner.setCanLoop(true);//自动轮播
        }

        mBanner.setManualPageable(true);//设置不能手动影响  默认是手指触摸 轮播图不能翻页
        mBanner.setPages(new CBViewHolderCreator<ImageViewHolder>() {
            @Override
            public ImageViewHolder createHolder() {
                return new ImageViewHolder();
            }
        },imgPaths)
                .setPageIndicator(new int[]{R.mipmap.ponit_normal,R.mipmap.point_select}) //设置两个点作为指示器
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL); //设置指示器的方向水平居中
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
