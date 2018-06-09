package com.goldingmedia.mvp.view.fragment;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldingmedia.GDApplication;
import com.goldingmedia.LauncherActivity;
import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.mvp.mode.Category;
import com.goldingmedia.mvp.mode.EventBusCMD;
import com.goldingmedia.mvp.view.adapter.MoviesShowAdapter;
import com.goldingmedia.utils.NLog;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesShowFragment extends BaseFragment implements View.OnClickListener,AdapterView.OnItemClickListener{
    private static String TAG ="MoviesShowFragment";
    private MoviesShowAdapter mAdapter;
    private GridView mGirdView;
    private ImageView mImg;
    private TextView mTxt;
    private FrameLayout frameLayout;
    private MediaFragment mediaf;
    private static final  int ALL_TYPE = 0;
    private List<Category> categories = new ArrayList<Category>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_moviesshow, container, false);
        mGirdView =(GridView) view.findViewById(R.id.gv_to);
        frameLayout = (FrameLayout) view.findViewById(R.id.fl_to_1);
        mImg = (ImageView) view.findViewById(R.id.iv_to_1);
        mTxt = (TextView) view.findViewById(R.id.textView);
        mAdapter = new MoviesShowAdapter(getActivity());
        mGirdView.setAdapter(mAdapter);
        mGirdView.setOnItemClickListener(this);
        frameLayout.setOnClickListener(this);

        initData();
        return view;
    }

    private void initData(){
        categories =  GDApplication.getmInstance().getTruckMedia().getcMoviesShow().getmCategorys();
        if(categories.size() == 0){
            return;
        }
        initView();
        mAdapter.refresh(categories);
    }

    private void initView(){
        String imgPath = Contant.MOVIESSHOW_PATH+Contant.MEDIA_GOLDING+"/1.jpg";
        mTxt.setText(categories.get(0).getCategorySubDesc());
        mImg.setImageBitmap( BitmapFactory.decodeFile(imgPath));
    }

    @Override
    public void onClick(View v) {
        if(categories.size() == 0){
            return;
        }
      LauncherActivity.getmInstance().mediaTypeLisenter(ALL_TYPE,categories.get(0).getCategorySubId());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(categories.size() < 1){
            return;
        }
        LauncherActivity.getmInstance().mediaTypeLisenter(ALL_TYPE,categories.get(position+1).getCategorySubId());
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
