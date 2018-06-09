package com.goldingmedia.mvp.view.fragment;


import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RadioGroup;

import com.goldingmedia.GDApplication;
import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.mvp.mode.Category;
import com.goldingmedia.mvp.mode.EventBusCMD;
import com.goldingmedia.mvp.view.adapter.MyBaseGirdViewAdapter;
import com.goldingmedia.mvp.view.ui.MyRadioGroupView;
import com.goldingmedia.temporary.CardManager;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ELineFragment extends BaseFragment implements AdapterView.OnItemClickListener,RadioGroup.OnCheckedChangeListener{
    private static String TAG ="ELineFragment";
    private List<TruckMediaProtos.CTruckMediaNode> mNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private MyBaseGirdViewAdapter mGirdAdapter;
    private GridView mGirdView;
    private RadioGroup mRadioGroup;
    private List<Category> mCategorys = new ArrayList<Category>();

    public ELineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_base_grid, container, false);
        mGirdView =(GridView) view.findViewById(R.id.gv_base);
        mRadioGroup = (RadioGroup) view.findViewById(R.id.rg_ly);
        mGirdAdapter = new MyBaseGirdViewAdapter(getActivity());
        mGirdView.setAdapter(mGirdAdapter);
        mGirdView.setOnItemClickListener(this);

        mRadioGroup.setOnCheckedChangeListener(this);
        initData();
        return view;
    }


    private void initData(){
        mCategorys = GDApplication.getmInstance().getTruckMedia().getCeLive().getmCategorys();
        if(mCategorys.size() == 0){
            return;
        }
        mNodes = GDApplication.getmInstance().getTruckMedia().getCeLive().getSubList(mCategorys.get(0).getCategorySubId());
        try {
            new MyRadioGroupView(getActivity(),mRadioGroup,mCategorys,0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mGirdAdapter.refresh(mNodes);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TruckMediaProtos.CTruckMediaNode truck = (TruckMediaProtos.CTruckMediaNode) mGirdAdapter.getItem(position);
        CardManager.getInstance().action(position, truck, getActivity());
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if(group.getChildAt(0).getId() == checkedId){
            mNodes = GDApplication.getmInstance().getTruckMedia().getCeLive().getSubList(mCategorys.get(0).getCategorySubId());
        }else  if(group.getChildAt(1).getId() == checkedId){
            mNodes = GDApplication.getmInstance().getTruckMedia().getCeLive().getSubList(mCategorys.get(1).getCategorySubId());
        }else  if(group.getChildAt(2).getId() == checkedId){
            mNodes = GDApplication.getmInstance().getTruckMedia().getCeLive().getSubList(mCategorys.get(2).getCategorySubId());
        }else  if(group.getChildAt(3).getId() == checkedId){
            mNodes = GDApplication.getmInstance().getTruckMedia().getCeLive().getSubList(mCategorys.get(3).getCategorySubId());
        }
        mGirdAdapter.refresh(mNodes);
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
