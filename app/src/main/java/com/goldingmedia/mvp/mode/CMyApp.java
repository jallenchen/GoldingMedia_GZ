package com.goldingmedia.mvp.mode;

import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jallen on 2017/8/24 0024 15:45.
 */

public class CMyApp {
    private int categoryId;
    private int categorySubId;
    private List<TruckMediaProtos.CTruckMediaNode> eBooktruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> apptruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> settingtruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
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

    public List<TruckMediaProtos.CTruckMediaNode> geteBooktruckMediaNodes() {
        return eBooktruckMediaNodes;
    }

    public void seteBooktruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> eBooktruckMediaNodes) {
        this.eBooktruckMediaNodes = eBooktruckMediaNodes;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getApptruckMediaNodes() {
        return apptruckMediaNodes;
    }

    public void setApptruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> apptruckMediaNodes) {
        this.apptruckMediaNodes = apptruckMediaNodes;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getSettingtruckMediaNodes() {
        return settingtruckMediaNodes;
    }

    public void setSettingtruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> settingtruckMediaNodes) {
        this.settingtruckMediaNodes = settingtruckMediaNodes;
    }

    public List<Category> getmCategorys() {
        return mCategorys;
    }

    public void setmCategorys(List<Category> mCategorys) {
        this.mCategorys = mCategorys;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getSubList(int subId){
        switch (subId) {
            case Contant.PROPERTY_MYAPP_EBOOK_ID:
                return eBooktruckMediaNodes;
            case Contant.PROPERTY_MYAPP_APP_ID:
                return apptruckMediaNodes;
            case Contant.PROPERTY_MYAPP_SETTING_ID:
                return settingtruckMediaNodes;
            default:
                return new ArrayList<TruckMediaProtos.CTruckMediaNode>();
        }
    }
}
