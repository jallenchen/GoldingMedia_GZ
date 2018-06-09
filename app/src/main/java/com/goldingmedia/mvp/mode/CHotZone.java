package com.goldingmedia.mvp.mode;

import android.util.SparseArray;

import com.goldingmedia.goldingcloud.TruckMediaProtos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jallen on 2017/8/24 0024 15:43.
 */

public class CHotZone {
    private int categoryId;
    private int categorySubId;
    private List<TruckMediaProtos.CTruckMediaNode> truckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private SparseArray<List<TruckMediaProtos.CTruckMediaNode>> subTrucksMap = new SparseArray<>();
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

    public List<TruckMediaProtos.CTruckMediaNode> getTruckMediaNodes() {
        return truckMediaNodes;
    }

    public void setTruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> truckMediaNodes) {
        this.truckMediaNodes = truckMediaNodes;
    }


    public void  delTrucks(int categorySubId,String uuid){
        subTrucksMap.get(categorySubId).remove(uuid);
    }


    public List<TruckMediaProtos.CTruckMediaNode> getIndexNodes(int index){
        List<TruckMediaProtos.CTruckMediaNode> nodes = new ArrayList<>();
        for(TruckMediaProtos.CTruckMediaNode truckMediaNode : truckMediaNodes){
            if(truckMediaNode.getMediaInfo().getTruckMeta().getTruckIndex() == index+1){
                nodes.add(truckMediaNode);
            }
        }
        return nodes;
    }

}
