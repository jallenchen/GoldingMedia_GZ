package com.goldingmedia.temporary;

import android.util.SparseArray;

import com.goldingmedia.goldingcloud.TruckMediaProtos;

import java.util.ArrayList;
import java.util.List;

public class Variables {

    public static class StatusItem {
        public static final String position = "position";
        public static final String classId = "classId";
        public static final String classSubId = "classSubId";
        public static final String classMainId = "classMainId";
        public static final String percent = "percent";
    }

    public static final int TRUCK_SHOW_NORMAL = 1;

    public static Boolean isMarqueeShow = false;// 判断走马灯是否在播放，true播放
    public static Boolean mRegularAdStart = false;// 定时广告来时设定true
    public static Boolean mEvenAdStart = false;// 即时广告来时设定true
    public static int mLimitOutCounter = 0;// 本次广告播放总时长
    public static Boolean isPaid = false;// 正在播放的视频是否付款，true已付款
    public static Boolean mSVStart = false;// 安全指南是否播放，true播放
    public static Boolean isTsStop = true;// 判断是否ts接口被占用，true未占用
    public static Boolean tsStopAllowPlay = true;// 判断是否正在退出播放，true正在退出
    public static Boolean serviceStartFrist = false;// 判断安全指南是否已经播放过
    public static String mGpsPlace = "";// 安全指南是否播放，true播放

    public static SparseArray<List<TruckMediaProtos.CTruckMediaNode>> mTruckMediaMovieNodes;
    public static ArrayList<List<TruckMediaProtos.CTruckMediaNode>> mTruckMediaTypeNodes;

}