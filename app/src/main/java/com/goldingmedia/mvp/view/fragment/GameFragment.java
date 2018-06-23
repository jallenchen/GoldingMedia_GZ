package com.goldingmedia.mvp.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.goldingmedia.activity.WebActivity;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.mvp.mode.EventBusCMD;
import com.goldingmedia.mvp.view.adapter.GameGirdAdapter;
import com.goldingmedia.utils.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends BaseFragment implements View.OnClickListener,AdapterView.OnItemClickListener{
    private static String TAG ="GameFragment";
    public ImageView[] game_typeLogs;
    private List<TruckMediaProtos.CTruckMediaNode> truckMMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> truckSMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> truckLMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private GridView mGridView;
    private GameGirdAdapter mAdapter;
    private ImageView[] mGameIms = new ImageView[5];
    private FrameLayout[] mFraIms = new FrameLayout[5];


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
        mGameIms[4] =(ImageView) view.findViewById(R.id.game_iv_4);
        mFraIms[0] =(FrameLayout) view.findViewById(R.id.game_fl0);
        mFraIms[1] =(FrameLayout) view.findViewById(R.id.game_fl1);
        mFraIms[2] =(FrameLayout) view.findViewById(R.id.game_fl2);
        mFraIms[3] =(FrameLayout) view.findViewById(R.id.game_fl3);
        mFraIms[4] =(FrameLayout) view.findViewById(R.id.game_fl4);

        mAdapter = new GameGirdAdapter(getActivity());
        mGridView.setAdapter(mAdapter);

        setLisenter();
        initData();
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

            for(int i = 0;i<truckSMediaNodes.size();i++){
                imgPath = Contant.getTruckMetaNodePath(Contant.CATEGORY_GAME_ID,3,truckSMediaNodes.get(i).getMediaInfo().getTruckMeta().getTruckFilename(),true);
                String imgSName = truckSMediaNodes.get(i).getMediaInfo().getTruckMeta().getTruckImage();
                //mGameIms[i+1].setImageBitmap( BitmapFactory.decodeFile(imgPath+"/"+imgSName));
                Glide.with(getActivity()).load(imgPath+"/"+imgSName)
                        .placeholder(R.color.transparent)
                        .into(mGameIms[i+1]);
            }
            //TODO
            mAdapter.refresh(truckMMediaNodes);
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
                case R.id.game_fl4:
                    smallGameIndex = 3;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position == truckMMediaNodes.size()){
            String url="";
            List<TruckMediaProtos.CTruckMediaNode> truckMediaNodes = GDApplication.getmInstance().getTruckMedia().getcAds().getExtendTypeTrucksMap(Contant.ADS_EXTEND_TYPE_GAMEAREA);
            if(truckMediaNodes.size() != 0){
                url = truckMediaNodes.get(0).getMediaInfo().getAdsMeta().getTruckAdsUrl();
            }
            Intent intent = new Intent(getActivity(), WebActivity.class);
            intent.putExtra("online",true);
            intent.putExtra("url","http://www.cnblogs.com");
           // intent.putExtra("url",url);
            startActivity(intent);

        }else{
            Utils.openApp(getActivity(),truckMMediaNodes.get(position).getMediaInfo().getTruckMeta().getTruckDesc());
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
