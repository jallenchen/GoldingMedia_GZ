package com.goldingmedia.mvp.mode;

import android.util.SparseArray;

import com.goldingmedia.GDApplication;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Jallen on 2017/8/24 0024 15:45.
 */

public class CAds {
    public static boolean[] XFTP_ADS = new boolean[]{ false, false, false, false, false, false };
    private int categoryId;
    private int categorySubId;
    private List<Category> mCategorys = new ArrayList<Category>();
    private List<List<TruckMediaProtos.CTruckMediaNode>> subTrucksList = new ArrayList<List<TruckMediaProtos.CTruckMediaNode>>();
    private HashMap<String, List<TruckMediaProtos.CTruckMediaNode>> extendTypeTrucksMap = new HashMap<String, List<TruckMediaProtos.CTruckMediaNode>>();;
    private HashMap<String, List<TruckMediaProtos.CTruckMediaNode>> windowOrientTrucksMap = new HashMap<String, List<TruckMediaProtos.CTruckMediaNode>>();
    private HashMap<String, List<TruckMediaProtos.CTruckMediaNode>> promptOrientTrucksMap = new HashMap<String, List<TruckMediaProtos.CTruckMediaNode>>();
    private HashMap<String, List<TruckMediaProtos.CTruckMediaNode>> homeOrientTrucksMap = new HashMap<String, List<TruckMediaProtos.CTruckMediaNode>>();
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

    public List<Category> getmCategorys() {
        return mCategorys;
    }

    public void setmCategorys(List<Category> mCategorys) {
        this.mCategorys = mCategorys;
        subTrucksList.clear();
        extendTypeTrucksMap.clear();
        windowOrientTrucksMap.clear();
        promptOrientTrucksMap.clear();
        homeOrientTrucksMap.clear();
        for (int i=0; i<mCategorys.size(); i++) {
            List<TruckMediaProtos.CTruckMediaNode> trucks = GDApplication.getmInstance().getDataInsert().getMediaMetaDataList(Contant.TABLE_NAME_ADS, mCategorys.get(i).getCategorySubId());
            subTrucksList.add(trucks);
            for (int j=0; j<trucks.size(); j++) {
                TruckMediaProtos.CTruckMediaNode truck = trucks.get(j);
                String type = truck.getMediaInfo().getAdsMeta().getTruckExtendType();
                if (type.equals(Contant.ADS_EXTEND_TYPE_WINDOW+"")) {
                    String orient = truck.getMediaInfo().getAdsMeta().getTruckWndOrient();
                    if(!windowOrientTrucksMap.containsKey(orient)){
                        windowOrientTrucksMap.put(orient, new ArrayList<TruckMediaProtos.CTruckMediaNode>());
                    }
                    windowOrientTrucksMap.get(orient).add(truck);
                }else  if (type.equals(Contant.ADS_EXTEND_TYPE_PROMPT+"")) {
                    String orient = truck.getMediaInfo().getAdsMeta().getTruckWndOrient();
                    if (!promptOrientTrucksMap.containsKey(orient)) {
                        promptOrientTrucksMap.put(orient, new ArrayList<TruckMediaProtos.CTruckMediaNode>());
                    }
                    promptOrientTrucksMap.get(orient).add(truck);
                }else if (type.equals(Contant.ADS_EXTEND_TYPE_HOMECOLUMN+"")) {
                    String orient = truck.getMediaInfo().getAdsMeta().getTruckWndOrient();
                    if (!homeOrientTrucksMap.containsKey(orient)) {
                        homeOrientTrucksMap.put(orient, new ArrayList<TruckMediaProtos.CTruckMediaNode>());
                    }
                    homeOrientTrucksMap.get(orient).add(truck);
                } else {
                    if(!extendTypeTrucksMap.containsKey(type)){
                        extendTypeTrucksMap.put(type, new ArrayList<TruckMediaProtos.CTruckMediaNode>());
                    }
                    extendTypeTrucksMap.get(type).add(truck);
                }
            }
        }

        for(int i = 0; i < XFTP_ADS.length; i++) {
            if(CAds.XFTP_ADS[i]) {
                EventBusCMD cmd = new EventBusCMD(Contant.MsgID.REFLESH_ADS);
                cmd.setValues(i+"");
                EventBus.getDefault().post(cmd);
                CAds.XFTP_ADS[i] = false;
            }
        }
    }



    public void  delTrucks(int categorySubId, String uuid){
    }

    public void addOrUpdateTruck(int categorySubId,List<TruckMediaProtos.CTruckMediaNode> trucks){
    }

    public List<TruckMediaProtos.CTruckMediaNode> getExtendTypeTrucksMap(int type){
        return extendTypeTrucksMap.containsKey(type+"")?extendTypeTrucksMap.get(type+""):new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    }

    public List<TruckMediaProtos.CTruckMediaNode> getWindowOrientTrucksMap(int type){
        return windowOrientTrucksMap.containsKey(type+"")?windowOrientTrucksMap.get(type+""):new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    }
    public List<TruckMediaProtos.CTruckMediaNode> getPromptOrientTrucksMap(int type){
        return promptOrientTrucksMap.containsKey(type+"")?promptOrientTrucksMap.get(type+""):new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    }

    public List<TruckMediaProtos.CTruckMediaNode> getHomeOrientTrucksMap(int type){
        return homeOrientTrucksMap.containsKey(type+"")?homeOrientTrucksMap.get(type+""):new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    }

    public List<TruckMediaProtos.CTruckMediaNode> getSubList(int subId){
        return subTrucksList.size()>0?subTrucksList.get(subId - 1):new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    }
}
