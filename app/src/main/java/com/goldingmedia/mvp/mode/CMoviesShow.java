package com.goldingmedia.mvp.mode;

import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jallen on 2017/8/24 0024 15:44.
 */

public class CMoviesShow {
    private int categoryId;
    private int categorySubId;
    private List<TruckMediaProtos.CTruckMediaNode> mDGTVtruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> mGOLDINGtruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> mWhalestruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> mSDLMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<HashMap<Integer,List<TruckMediaProtos.CTruckMediaNode>> > mTruckMediaNodes = new ArrayList<HashMap<Integer,List<TruckMediaProtos.CTruckMediaNode>> >();
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

    public List<TruckMediaProtos.CTruckMediaNode> getmDGTVtruckMediaNodes() {
        return mDGTVtruckMediaNodes;
    }

    public void setmDGTVtruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> mDGTVtruckMediaNodes) {
        this.mDGTVtruckMediaNodes = mDGTVtruckMediaNodes;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getmGOLDINGtruckMediaNodes() {
        return mGOLDINGtruckMediaNodes;
    }

    public void setmGOLDINGtruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> mGOLDINGtruckMediaNodes) {
        this.mGOLDINGtruckMediaNodes = mGOLDINGtruckMediaNodes;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getmWhalestruckMediaNodes() {
        return mWhalestruckMediaNodes;
    }

    public void setmWhalestruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> mWhalestruckMediaNodes) {
        this.mWhalestruckMediaNodes = mWhalestruckMediaNodes;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getmSDLMediaNodes() {
        return mSDLMediaNodes;
    }

    public void setmSDLMediaNodes(List<TruckMediaProtos.CTruckMediaNode> mSDLMediaNodes) {
        this.mSDLMediaNodes = mSDLMediaNodes;
    }

    public List<Category> getmCategorys() {
        return mCategorys;
    }

    public void setmCategorys(List<Category> mCategorys) {
        this.mCategorys = mCategorys;
    }

    public List<HashMap<Integer, List<TruckMediaProtos.CTruckMediaNode>>> getmTruckMediaNodes() {
        return mTruckMediaNodes;
    }

    public void setmTruckMediaNodes(List<HashMap<Integer, List<TruckMediaProtos.CTruckMediaNode>>> mTruckMediaNodes) {
        this.mTruckMediaNodes = mTruckMediaNodes;
    }


    public List<TruckMediaProtos.CTruckMediaNode> getSubList(int subId){
        switch (subId) {
            case Contant.PROPERTY_MEDIA_GOLDING_ID:
                return mGOLDINGtruckMediaNodes;
            case Contant.PROPERTY_MEDIA_GDTV_ID:
                return mDGTVtruckMediaNodes;
            case Contant.PROPERTY_MEDIA_WHALES_ID:
                return mWhalestruckMediaNodes;
            case Contant.PROPERTY_MEDIA_SHENGDELIN_ID:
                return mSDLMediaNodes;
            default:
                return null;
        }
    }

}
