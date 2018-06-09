package com.goldingmedia.mvp.mode;

import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jallen on 2017/8/24 0024 15:44.
 */

public class CELive {
    private int categoryId;
    private int categorySubId;
    private List<TruckMediaProtos.CTruckMediaNode> mMalltruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> mHoteltruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> mFoodtruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> mTraveltruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
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

    public List<TruckMediaProtos.CTruckMediaNode> getmMalltruckMediaNodes() {
        return mMalltruckMediaNodes;
    }

    public void setmMalltruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> mMalltruckMediaNodes) {
        this.mMalltruckMediaNodes = mMalltruckMediaNodes;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getmHoteltruckMediaNodes() {
        return mHoteltruckMediaNodes;
    }

    public void setmHoteltruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> mHoteltruckMediaNodes) {
        this.mHoteltruckMediaNodes = mHoteltruckMediaNodes;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getmFoodtruckMediaNodes() {
        return mFoodtruckMediaNodes;
    }

    public void setmFoodtruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> mFoodtruckMediaNodes) {
        this.mFoodtruckMediaNodes = mFoodtruckMediaNodes;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getmTraveltruckMediaNodes() {
        return mTraveltruckMediaNodes;
    }

    public void setmTraveltruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> mTraveltruckMediaNodes) {
        this.mTraveltruckMediaNodes = mTraveltruckMediaNodes;
    }

    public List<Category> getmCategorys() {
        return mCategorys;
    }

    public void setmCategorys(List<Category> mCategorys) {
        this.mCategorys = mCategorys;
    }


    public List<TruckMediaProtos.CTruckMediaNode> getSubList(int subId) {
        switch (subId) {
            case Contant.PROPERTY_ELIVE_MALL_ID:
                return mMalltruckMediaNodes;
            case Contant.PROPERTY_ELIVE_HOTEL_ID:
                return mHoteltruckMediaNodes;
            case Contant.PROPERTY_ELIVE_FOOD_ID:
                return mFoodtruckMediaNodes;
            case Contant.PROPERTY_ELIVE_TRAVEL_ID:
                return mTraveltruckMediaNodes;
            default:
                return null;
        }
    }
}
