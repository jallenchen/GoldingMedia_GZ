package com.goldingmedia.mvp.mode;

import android.util.SparseArray;

import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jallen on 2017/8/25 0025 11:04.
 */

public class CMediaTypeMeta {
    private List<TruckMediaProtos.CTruckMediaNode> mTVShowtruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> mCartoontruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> mSporttruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> mDramatruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> mMTVtruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> mMusictruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();
    private List<TruckMediaProtos.CTruckMediaNode> mMovicetruckMediaNodes = new ArrayList<TruckMediaProtos.CTruckMediaNode>();

    private SparseArray<List<TruckMediaProtos.CTruckMediaNode>> subTrucksMap = new SparseArray<>();

    public List<TruckMediaProtos.CTruckMediaNode> getmTVShowtruckMediaNodes() {
        return mTVShowtruckMediaNodes;
    }

    public void setmTVShowtruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> mTVShowtruckMediaNodes) {
        this.mTVShowtruckMediaNodes = mTVShowtruckMediaNodes;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getmCartoontruckMediaNodes() {
        return mCartoontruckMediaNodes;
    }

    public void setmCartoontruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> mCartoontruckMediaNodes) {
        this.mCartoontruckMediaNodes = mCartoontruckMediaNodes;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getmSporttruckMediaNodes() {
        return mSporttruckMediaNodes;
    }

    public void setmSporttruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> mSporttruckMediaNodes) {
        this.mSporttruckMediaNodes = mSporttruckMediaNodes;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getmDramatruckMediaNodes() {
        return mDramatruckMediaNodes;
    }

    public void setmDramatruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> mDramatruckMediaNodes) {
        this.mDramatruckMediaNodes = mDramatruckMediaNodes;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getmMTVtruckMediaNodes() {
        return mMTVtruckMediaNodes;
    }

    public void setmMTVtruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> mMTVtruckMediaNodes) {
        this.mMTVtruckMediaNodes = mMTVtruckMediaNodes;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getmMusictruckMediaNodes() {
        return mMusictruckMediaNodes;
    }

    public void setmMusictruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> mMusictruckMediaNodes) {
        this.mMusictruckMediaNodes = mMusictruckMediaNodes;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getmMovicetruckMediaNodes() {
        return mMovicetruckMediaNodes;
    }

    public void setmMovicetruckMediaNodes(List<TruckMediaProtos.CTruckMediaNode> mMovicetruckMediaNodes) {
        this.mMovicetruckMediaNodes = mMovicetruckMediaNodes;
    }

    public SparseArray<List<TruckMediaProtos.CTruckMediaNode>> getSubTrucksMap() {
        return subTrucksMap;
    }

    public void setSubTrucksMap(SparseArray<List<TruckMediaProtos.CTruckMediaNode>>  subTrucksMap) {
        this.subTrucksMap = subTrucksMap;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getCategorySubTrucks(int mediaTypeId){
        return subTrucksMap.get(mediaTypeId);
    }

    public void setCategorySubTruck(int mediaTypeId,TruckMediaProtos.CTruckMediaNode truck){
        switch (mediaTypeId) {
            case Contant.MEDIA_TYPE_MOVIE:
                mMovicetruckMediaNodes.add(truck);
                subTrucksMap.put(mediaTypeId,mMovicetruckMediaNodes);
                break;
            case Contant.MEDIA_TYPE_TVSHOW:
                mTVShowtruckMediaNodes.add(truck);
                subTrucksMap.put(mediaTypeId,mTVShowtruckMediaNodes);
                break;
            case Contant.MEDIA_TYPE_CARTOON:
                mCartoontruckMediaNodes.add(truck);
                subTrucksMap.put(mediaTypeId,mCartoontruckMediaNodes);
                break;
            case Contant.MEDIA_TYPE_SPORT:
                mSporttruckMediaNodes.add(truck);
                subTrucksMap.put(mediaTypeId,mSporttruckMediaNodes);
                break;
            case Contant.MEDIA_TYPE_DRAMA:
                mDramatruckMediaNodes.add(truck);
                subTrucksMap.put(mediaTypeId,mDramatruckMediaNodes);
                break;
            case Contant.MEDIA_TYPE_MTV:
                mMTVtruckMediaNodes.add(truck);
                subTrucksMap.put(mediaTypeId,mMTVtruckMediaNodes);
                break;
            case Contant.MEDIA_TYPE_MUSIC:
                mMusictruckMediaNodes.add(truck);
                subTrucksMap.put(mediaTypeId,mMusictruckMediaNodes);
                break;
            default:
                break;
        }


    }
}
