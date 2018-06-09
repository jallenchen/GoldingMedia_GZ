package com.goldingmedia.mvp.mode;

import com.goldingmedia.goldingcloud.TruckMediaProtos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jallen on 2017/8/24 0024 15:44.
 */

public class CGameCenter {
    private int mCategoryId;
    private int mCategorySubId;
    private List<TruckMediaProtos.CTruckMediaNode> mMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> sMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> ltruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();


    public int getmCategoryId() {
        return mCategoryId;
    }

    public void setmCategoryId(int mCategoryId) {
        this.mCategoryId = mCategoryId;
    }

    public int getmCategorySubId() {
        return mCategorySubId;
    }

    public void setmCategorySubId(int mCategorySubId) {
        this.mCategorySubId = mCategorySubId;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getMMediaNodes() {
        return mMediaNodes;
    }

    public void setMMediaNodes(List<TruckMediaProtos.CTruckMediaNode> mMediaNodes) {
        this.mMediaNodes = mMediaNodes;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getSMediaNodes() {
        return sMediaNodes;
    }

    public void setSMediaNodes(List<TruckMediaProtos.CTruckMediaNode> sMediaNodes) {
        this.sMediaNodes = sMediaNodes;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getLMediaNodes() {
        return ltruckMediaNodes;
    }

    public void setLMediaNodes(List<TruckMediaProtos.CTruckMediaNode> ltruckMediaNodes) {
        this.ltruckMediaNodes = ltruckMediaNodes;
    }

}
