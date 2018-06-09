package com.goldingmedia.mvp.mode;

import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jallen on 2017/8/24 0024 15:44.
 */

public class CGoldingMedia {
    private int categoryId;
    private int categorySubId;
    private List<TruckMediaProtos.CTruckMediaNode> mJTVtruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> mMagazinetruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> mJMalltruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<Category> mCategorys = new ArrayList<Category>();
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getCategorySubId() {
        return categorySubId;
    }

    public void setCategorySubId(int categorySubId) {
        this.categorySubId = categorySubId;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getmJTVtruckMediaNodes() {
        return mJTVtruckMediaNodes;
    }

    public void setmJTVtruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> mJTVtruckMediaNodes) {
        this.mJTVtruckMediaNodes = mJTVtruckMediaNodes;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getmMagazinetruckMediaNodes() {
        return mMagazinetruckMediaNodes;
    }

    public void setmMagazinetruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> mMagazinetruckMediaNodes) {
        this.mMagazinetruckMediaNodes = mMagazinetruckMediaNodes;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getmJMalltruckMediaNodes() {
        return mJMalltruckMediaNodes;
    }

    public void setmJMalltruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> mJMalltruckMediaNodes) {
        this.mJMalltruckMediaNodes = mJMalltruckMediaNodes;
    }

    public List<Category> getmCategorys() {
        return mCategorys;
    }

    public void setmCategorys(List<Category> mCategorys) {
        this.mCategorys = mCategorys;
    }



    public List<TruckMediaProtos.CTruckMediaNode> getSubList(int subId){
        switch (subId) {
            case Contant.PROPERTY_GOLDING_JTV_ID:
                return mJTVtruckMediaNodes;
            case Contant.PROPERTY_GOLDING_MAGAZINE_ID:
                return mMagazinetruckMediaNodes;
            case Contant.PROPERTY_GOLDING_MALL_ID:
                return mJMalltruckMediaNodes;
            default:
                return null;
        }
    }
}
