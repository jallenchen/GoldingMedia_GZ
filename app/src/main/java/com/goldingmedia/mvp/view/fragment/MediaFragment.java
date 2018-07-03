package com.goldingmedia.mvp.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.goldingmedia.GDApplication;
import com.goldingmedia.R;
import com.goldingmedia.activity.WebActivity;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.mvp.mode.Category;
import com.goldingmedia.mvp.mode.EventBusCMD;
import com.goldingmedia.mvp.view.adapter.MyBaseGirdViewAdapter;
import com.goldingmedia.mvp.view.ui.MyRadioGroupView;
import com.goldingmedia.temporary.CardManager;
import com.goldingmedia.utils.NLog;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import static com.goldingmedia.temporary.Variables.mTruckMediaMovieNodes;
import static com.goldingmedia.temporary.Variables.mTruckMediaTypeNodes;


/**
 * A simple {@link Fragment} subclass.
 */
public class MediaFragment extends BaseFragment implements AdapterView.OnItemClickListener,RadioGroup.OnCheckedChangeListener{
    private static String TAG ="MediaFragment";
    private List<TruckMediaProtos.CTruckMediaNode> mNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> mAdsNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private MyBaseGirdViewAdapter mGirdAdapter;
    private RadioGroup mRadioGroup;
    private ScrollView mRadioGroupLayout;
    private GridView mGirdView;
    private  MyRadioGroupView myRadioGroupView;
    private List<Category> mCategorys = new ArrayList<Category>();
    private int mCheckId = 1;
    private int mTruckCount = 0;
    private int mType = Contant.MEDIA_TYPE_MOVIE;

    public MediaFragment() {
        // Required empty public constructor
    }

    public boolean refreshData(Bundle bundle) throws Exception{
        List<TruckMediaProtos.CTruckMediaNode> truckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
        int type = bundle.getInt("TYPE");
        mCheckId = bundle.getInt("CHECKID",mCheckId);
        NLog.e(TAG,"mCheckId:"+mCheckId+"-type:"+type);
        switch (type){
            case Contant.MEDIA_TYPE_MOVIE:  // 取大类 Media 的第一个子类
                truckMediaNodes = mTruckMediaMovieNodes.get(mCheckId);
                NLog.e(TAG,"mCheckId:"+mCheckId+"-size:"+truckMediaNodes.size());
                if(truckMediaNodes.size() == 0){
                    return false;
                }
                if(mCheckId >= mCategorys.size()){
                    ((RadioButton) mRadioGroup.getChildAt(mCategorys.size() -1)).setChecked(true);
                }else{
                    ((RadioButton) mRadioGroup.getChildAt(mCheckId -1)).setChecked(true);
                }

                mRadioGroupLayout.setVisibility(View.VISIBLE);
                break;
            case Contant.MEDIA_TYPE_CARTOON:  // 大类 Media 各子类中遍历所有 CARTOON 类
            case Contant.MEDIA_TYPE_SPORT:   // 大类 Media 各子类中遍历所有 SPORT 类
            case Contant.MEDIA_TYPE_TVSHOW:  // 大类 Media 各子类中遍历所有 TVSHOW 类
            case Contant.MEDIA_TYPE_DRAMA:   // 大类 Media 各子类中遍历所有 DRAMA 类
            case Contant.MEDIA_TYPE_MTV:     // 大类 Media 各子类中遍历所有 MTV 类
            case Contant.MEDIA_TYPE_MUSIC:   // 大类 Media 各子类中遍历所有 MUSIC 类
                truckMediaNodes = mTruckMediaTypeNodes.get(type-1);
                if(truckMediaNodes.size() == 0){
                    return false;
                }
//                NToast.shortToast(getActivity(), "-----getTruckTitle = "+truckMediaNodes.get(0).getMediaInfo().getTruckMeta().getTruckTitle());
        		mRadioGroupLayout.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        mType = type;
        if(mType == Contant.MEDIA_TYPE_MOVIE){
            mGirdAdapter.refresh(truckMediaNodes,getAdsImgPath());
        }else{
            mGirdAdapter.refresh(truckMediaNodes,null);
        }

        return true;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_base_grid, container, false);
        mGirdView =(GridView) view.findViewById(R.id.gv_base);
        mRadioGroup = (RadioGroup) view.findViewById(R.id.rg_ly);
        mRadioGroupLayout = (ScrollView)  view.findViewById(R.id.sv_ly);
        mGirdAdapter = new MyBaseGirdViewAdapter(getActivity());
        mGirdView.setAdapter(mGirdAdapter);
        mGirdView.setOnItemClickListener(this);
        mRadioGroup.setOnCheckedChangeListener(this);
        mGirdAdapter.setBannerListener(new myBannerLisenter());
        initData();
        return view;
    }

    private void initData(){
        mCategorys = GDApplication.getmInstance().getTruckMedia().getcMoviesShow().getmCategorys();
        if(mCategorys.size() == 0){
            return;
        }
        mNodes = GDApplication.getmInstance().getTruckMedia().getcMoviesShow().getSubList(mCategorys.get(0).getCategorySubId());

        try {
            new MyRadioGroupView(getActivity(),mRadioGroup,mCategorys,0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAdsNodes = getBannerMovicesList();
        mGirdAdapter.refresh(getMediaType(mNodes,mType),getAdsImgPath());

        mTruckMediaMovieNodes = new SparseArray<List<TruckMediaProtos.CTruckMediaNode>>();
        mTruckMediaTypeNodes = new ArrayList<List<TruckMediaProtos.CTruckMediaNode>>();
        for (int i = 0; i<mCategorys.size(); i++) {
            mTruckMediaMovieNodes.put(mCategorys.get(i).getCategorySubId(),getMediaType(mCategorys.get(i).getCategorySubId(), Contant.MEDIA_TYPE_MOVIE));
            for( int j=0; j<7; j++) {
                if (mTruckMediaTypeNodes.size() == j) mTruckMediaTypeNodes.add(new ArrayList<TruckMediaProtos.CTruckMediaNode>());
                mTruckMediaTypeNodes.get(j).addAll(getMediaType(i+1,j+1));
            }
        }
    }


    public static MediaFragment newInstance(Bundle bundle) {
        MediaFragment fragment = new MediaFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      //  NToast.shortToast(getActivity(),TAG+"-"+position);
        NLog.e(TAG,"count:"+ mTruckCount+"position:"+position);

        TruckMediaProtos.CTruckMediaNode truck = (TruckMediaProtos.CTruckMediaNode) mGirdAdapter.getItem(position);
        CardManager.getInstance().action(position, truck, getActivity(),null);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if(group.getChildAt(0).getId() == checkedId){
            mCheckId = 1;
            mNodes = GDApplication.getmInstance().getTruckMedia().getcMoviesShow().getSubList(mCategorys.get(0).getCategorySubId());
        }else  if(group.getChildAt(1).getId() == checkedId){
            mCheckId = 2;
            mNodes = GDApplication.getmInstance().getTruckMedia().getcMoviesShow().getSubList(mCategorys.get(1).getCategorySubId());
        }else  if(group.getChildAt(2).getId() == checkedId){
            mCheckId = 3;
            mNodes = GDApplication.getmInstance().getTruckMedia().getcMoviesShow().getSubList(mCategorys.get(2).getCategorySubId());
        }else  if(group.getChildAt(3).getId() == checkedId){
            mCheckId = 4;
            mNodes = GDApplication.getmInstance().getTruckMedia().getcMoviesShow().getSubList(mCategorys.get(3).getCategorySubId());
        }

        mGirdAdapter.refresh(getMediaType(mNodes,mType),getAdsImgPath());
    }

    private  List<TruckMediaProtos.CTruckMediaNode> getMediaType(List<TruckMediaProtos.CTruckMediaNode> list,int typeid){
        List<TruckMediaProtos.CTruckMediaNode> mediatypeList = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
        if( list ==null ||  list.size() == 0){
            return mediatypeList;
        }
        for(TruckMediaProtos.CTruckMediaNode truckMediaNode:list ){
            if(truckMediaNode.getMediaInfo().getTruckMeta().getTruckMediaType() == typeid){
                mediatypeList.add(truckMediaNode);
            }
        }


        return mediatypeList;
    }

    private  List<TruckMediaProtos.CTruckMediaNode> getMediaType(int mediaSubId,int typeid){
        List<TruckMediaProtos.CTruckMediaNode> mediatypeList = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
        mNodes = GDApplication.getmInstance().getTruckMedia().getcMoviesShow().getSubList(mediaSubId);
        mediatypeList = getMediaType(mNodes,typeid);
        if(mediaSubId == 1 && typeid == 1 ){
            mTruckCount = mediatypeList.size();
        }
        return mediatypeList;
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

    private List<TruckMediaProtos.CTruckMediaNode> getBannerMovicesList() {
        return GDApplication.getmInstance().getTruckMedia().getcAds().getExtendTypeTrucksMap(Contant.ADS_EXTEND_TYPE_MOVICEAREA);
    }
    private List<String> getAdsImgPath(){
        List<String> list = new ArrayList<>();
        TruckMediaProtos.CTruckMediaNode truck = null ;
        String fileName;
        String imgPath;
            for (int i = 0; i < mAdsNodes.size(); i++) {
                truck = mAdsNodes.get(i);
                fileName =  truck.getMediaInfo().getTruckMeta().getTruckFilename();
                imgPath =  Contant.getTruckMetaNodePath(truck.getMediaInfo().getCategoryId(),truck.getMediaInfo().getCategorySubId(),
                        fileName+"/"+fileName+".jpg",true);

                list.add(imgPath);
            }
        return list;
    }
    private class myBannerLisenter implements OnBannerListener{
        @Override
        public void OnBannerClick(int position) {
            TruckMediaProtos.CTruckMediaNode truckMediaNode = mAdsNodes.get(position);
            NLog.d(TAG,"myBannerLisenter:"+position);
            if (truckMediaNode != null) {
                CardManager.getInstance().action(position, truckMediaNode,getActivity(),null);
            }
        }
    }
}
