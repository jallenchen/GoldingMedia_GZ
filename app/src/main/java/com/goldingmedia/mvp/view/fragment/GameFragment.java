package com.goldingmedia.mvp.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.goldingmedia.GDApplication;
import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.mvp.mode.EventBusCMD;
import com.goldingmedia.mvp.view.adapter.GameGirdAdapter;
import com.goldingmedia.mvp.view.ui.GlideImageLoader;
import com.goldingmedia.temporary.CardManager;
import com.goldingmedia.utils.NLog;
import com.goldingmedia.utils.Utils;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends BaseFragment implements View.OnClickListener,AdapterView.OnItemClickListener{
    private static String TAG ="GameFragment";
    public ImageView[] game_typeLogs;
    public static  final int GAME_LOOP_TIME = 5000;
    private List<TruckMediaProtos.CTruckMediaNode> truckMMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> truckSMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> truckLMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private SparseArray<List<TruckMediaProtos.CTruckMediaNode>> mTruckGameMapAds = new SparseArray<>();
    private GridView mGridView;
    private GameGirdAdapter mAdapter;
    private Banner mBannerS;
    private ImageView[] mGameIms = new ImageView[4];
    private FrameLayout[] mFraIms = new FrameLayout[4];


    public GameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        mGridView =(GridView) view.findViewById(R.id.gv_game);
        mGameIms[0] =(ImageView) view.findViewById(R.id.game_iv_0);
        mGameIms[1] =(ImageView) view.findViewById(R.id.game_iv_1);
        mGameIms[2] =(ImageView) view.findViewById(R.id.game_iv_2);
        mGameIms[3] =(ImageView) view.findViewById(R.id.game_iv_3);
        mBannerS =(Banner) view.findViewById(R.id.img_cb_small);
        mFraIms[0] =(FrameLayout) view.findViewById(R.id.game_fl0);
        mFraIms[1] =(FrameLayout) view.findViewById(R.id.game_fl1);
        mFraIms[2] =(FrameLayout) view.findViewById(R.id.game_fl2);
        mFraIms[3] =(FrameLayout) view.findViewById(R.id.game_fl3);
       // mFraIms[4] =(FrameLayout) view.findViewById(R.id.game_fl4);

        mAdapter = new GameGirdAdapter(getActivity());
        mGridView.setAdapter(mAdapter);

        initWindowAds();
        setLisenter();
        initData();
        setBannerConfig();

        return view;
    }

    private void setLisenter(){
        mGridView.setOnItemClickListener(this);
        for (FrameLayout frameLayout : mFraIms){
            frameLayout.setOnClickListener(this);
        }
    }

    private void initData(){
        truckLMediaNodes = GDApplication.getmInstance().getTruckMedia().getcGameCenter().getLMediaNodes();
        truckMMediaNodes = GDApplication.getmInstance().getTruckMedia().getcGameCenter().getMMediaNodes();
        truckSMediaNodes =GDApplication.getmInstance().getTruckMedia().getcGameCenter().getSMediaNodes();
        getPadIndexView();
    }

    private void getPadIndexView() {
        String imgPath;
        try {
            if(truckLMediaNodes.size() != 0){
                imgPath = Contant.getTruckMetaNodePath(Contant.CATEGORY_GAME_ID,1,truckLMediaNodes.get(0).getMediaInfo().getTruckMeta().getTruckFilename(),true);
                String imgLName = truckLMediaNodes.get(0).getMediaInfo().getTruckMeta().getTruckImage();

              //  mGameIms[0].setImageBitmap( BitmapFactory.decodeFile(imgPath+"/"+imgLName));
                Glide.with(getActivity()).load(imgPath+"/"+imgLName)
                        .placeholder(R.color.transparent)
                        .into(mGameIms[0]);
            }

            for(int i = 0;i < 3;i++){
                imgPath = Contant.getTruckMetaNodePath(Contant.CATEGORY_GAME_ID,3,truckSMediaNodes.get(i).getMediaInfo().getTruckMeta().getTruckFilename(),true);
                String imgSName = truckSMediaNodes.get(i).getMediaInfo().getTruckMeta().getTruckImage();
                //mGameIms[i+1].setImageBitmap( BitmapFactory.decodeFile(imgPath+"/"+imgSName));
                Glide.with(getActivity()).load(imgPath+"/"+imgSName)
                        .placeholder(R.color.transparent)
                        .into(mGameIms[i+1]);
            }

            List<String> mGameMidImgPath = new ArrayList<>();
            for(int i = 0;i < 5;i++){
                imgPath = Contant.getTruckMetaNodePath(Contant.CATEGORY_GAME_ID,2,truckMMediaNodes.get(i).getMediaInfo().getTruckMeta().getTruckFilename(),true);
                String imgSName = truckMMediaNodes.get(i).getMediaInfo().getTruckMeta().getTruckImage();
                mGameMidImgPath.add(imgPath+"/"+imgSName);
            }

            //TODO
            mAdapter.refresh(mGameMidImgPath,getImgPath(Contant.ADS_GAME_MIDDLE));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
            int smallGameIndex = -1;
        try {
            switch (v.getId()){
                case R.id.game_fl0:
                    break;
                case R.id.game_fl1:
                    smallGameIndex = 0;
                    break;
                case R.id.game_fl2:
                    smallGameIndex = 1;
                    break;
                case R.id.game_fl3:
                    smallGameIndex = 2;
                    break;
            }
            if(smallGameIndex == -1){
                Utils.openApp(getActivity(),truckLMediaNodes.get(0).getMediaInfo().getTruckMeta().getTruckDesc());
            }else{
                Utils.openApp(getActivity(),truckSMediaNodes.get(smallGameIndex).getMediaInfo().getTruckMeta().getTruckDesc());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mBannerS.setDelayTime(GAME_LOOP_TIME);
        mBannerS.startAutoPlay();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBannerS.stopAutoPlay();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NLog.e(TAG,"-----MyOnItemListener:"+position);
        Utils.openApp(getActivity(),truckMMediaNodes.get(position).getMediaInfo().getTruckMeta().getTruckDesc());
    }




    private void setBannerConfig(){
            mBannerS.setOnBannerListener(new MyOnItemListener(1));
            mAdapter.setBannerListener(new MyOnItemListener(2));
            setBannerDatas();
    }



    private void setBannerDatas() {
        List<String> imgPaths =  getImgPath(Contant.ADS_GAME_SMALL);
//        if(imgPaths.size() < 2){
//            mBanner.setCanLoop(false);//当只有一个时，不自动轮播
//        }else {
//            mBanner.setCanLoop(true);//自动轮播
//        }
        mBannerS.isAutoPlay(true);//自动轮播
        mBannerS.setViewPagerIsScroll(true);//设置不能手动影响  默认是手指触摸 轮播图不能翻页
        mBannerS.setImages(imgPaths);
        mBannerS.setImageLoader(new GlideImageLoader());
        if(imgPaths.size() != 0){
            mBannerS.start();
        }else {
            mBannerS.setOnClickListener(null);
        }
    }

    private List<String> getImgPath(int type){
        List<String> list = new ArrayList<>();
        TruckMediaProtos.CTruckMediaNode truck = null ;
        String fileName;
        String imgPath;
        if(type == Contant.ADS_GAME_SMALL){
            for (int i = 0; i < mTruckGameMapAds.get(Contant.ADS_GAME_SMALL).size(); i++) {
                truck = mTruckGameMapAds.get(Contant.ADS_GAME_SMALL).get(i);
                fileName =  truck.getMediaInfo().getTruckMeta().getTruckFilename();
                imgPath =  Contant.getTruckMetaNodePath(truck.getMediaInfo().getCategoryId(),truck.getMediaInfo().getCategorySubId(),
                        fileName+"/"+fileName+".jpg",true);

                list.add(imgPath);
            }
        }else  if(type == Contant.ADS_GAME_MIDDLE){
            for (int i = 0; i < mTruckGameMapAds.get(Contant.ADS_GAME_MIDDLE).size(); i++) {
                truck = mTruckGameMapAds.get(Contant.ADS_GAME_MIDDLE).get(i);
                fileName =  truck.getMediaInfo().getTruckMeta().getTruckFilename();
                imgPath =  Contant.getTruckMetaNodePath(truck.getMediaInfo().getCategoryId(),truck.getMediaInfo().getCategorySubId(),
                        fileName+"/"+fileName+".jpg",true);

                list.add(imgPath);
            }
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
            NLog.e(TAG,nBannerNum+"-----onItemClick:"+position);
            TruckMediaProtos.CTruckMediaNode truckMediaNode = null;
            switch (nBannerNum){
                case Contant.ADS_GAME_SMALL:
                    truckMediaNode =  mTruckGameMapAds.get(Contant.ADS_GAME_SMALL).get(position);
                    break;
                case Contant.ADS_GAME_MIDDLE:
                    truckMediaNode =  mTruckGameMapAds.get(Contant.ADS_GAME_MIDDLE).get(position);
                    break;
            }

            if (truckMediaNode != null) {
                CardManager.getInstance().action(position, truckMediaNode,getActivity(),null);
            }
        }

    }

    private void  initWindowAds() {
        List<TruckMediaProtos.CTruckMediaNode> list;
        list = getBannerSGameList(Contant.ADS_GAME_SMALL);
        mTruckGameMapAds.put(Contant.ADS_GAME_SMALL,list);
        list = getBannerSGameList(Contant.ADS_GAME_MIDDLE);
        mTruckGameMapAds.put(Contant.ADS_GAME_MIDDLE,list);

    }

    private List<TruckMediaProtos.CTruckMediaNode> getBannerSGameList(int type) {
        return GDApplication.getmInstance().getTruckMedia().getcAds().getGameOrientTrucksMap(type);
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
