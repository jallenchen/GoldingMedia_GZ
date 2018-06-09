package com.goldingmedia.utils;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.goldingmedia.GDApplication;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.LogonProtos;
import com.goldingmedia.goldingcloud.MediaMetaProtos;
import com.goldingmedia.goldingcloud.PayMetaProtos;
import com.goldingmedia.goldingcloud.PlayerMetaProtos;
import com.goldingmedia.goldingcloud.RequestSessionProtos;
import com.goldingmedia.goldingcloud.SystemInfoProtos;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.mvp.mode.CAds;
import com.goldingmedia.temporary.SystemInfo;


/**
 * Created by Jallen on 2017/8/21 0021 19:36.
 */

public class DataUtils {
    private final String TAG ="DataUtils";
    private Context mContext;

    public DataUtils(Context ct){
        mContext = ct;
    }
    /**
     * 登录信息
     * @return
     */
    public  byte[] sendLogOnMsg(){
        LogonProtos.CLogon.Builder logon = LogonProtos.CLogon.newBuilder();
        logon.setSysObjValue(2);
        logon.setDevId(Utils.getSerialID());
        logon.setUserName("admin");
        logon.setUserPwd("admin");
        Log.e(TAG,Utils.getSerialID());

        return logon.build().toByteArray();
    }

    /**
     * 系统信息
     * @return
     */
    public  byte[] sendSystemInfoMsg(){
        SystemInfoProtos.CSystemInfo.Builder sysInfo = SystemInfoProtos.CSystemInfo.newBuilder();
        sysInfo.setMasterId(Contant.MasterId);
        sysInfo.setDeviceType(2);
        sysInfo.setDeviceId(Utils.getSerialID());
        sysInfo.setOsName("M828");
        sysInfo.setOsBoard("1");//1.Android
        sysInfo.setOsVersion(Utils.getSysOsVersion());
        sysInfo.setOsMemTotal(Utils.getRomTotalSize(mContext));
        sysInfo.setOsMemFree(Utils.getRomAvailableSize(mContext));
        sysInfo.setOsLanuage(Utils.getSysLanguage());
        sysInfo.setDeviceSeatId(SystemInfo.getLocalSeat());
        sysInfo.setAppVersion(Utils.getAppVersion(mContext));
        return sysInfo.build().toByteArray();
    }

    /**
     * 请求会话
     */

    public byte[] sendRequestSession(int rqCode,String rqParam){
        RequestSessionProtos.CRequestSession.Builder builder = RequestSessionProtos.CRequestSession.newBuilder();
        builder.setDevId(Utils.getSerialID());
        builder.setDevType(2);
        builder.setRqCode(rqCode);
        builder.setRqParam(rqParam);
        return builder.build().toByteArray();
    }

    /**
     * 保存ProtoBuffer 到本地
     * @param data
     * @param path
     */
    public  void saveProto2Local(byte[] data,String path){
            Utils.writeFile(data,path);
    }

    /**
     * 读取本地保存的ProtoBuffer 的bin文件
     * @param data
     * @throws Exception
     */
    public  void readProtoFromLocal(byte[] data) throws Exception{
    }

    /**
     * 卡片Media按大类子类和操作分类
     * @param mediaMeta
     * @param payMeta
     * @param playerMeta
     * @param sop
     */
    public  void parseTruckMedia(int sop,MediaMetaProtos.CMediaMeta  mediaMeta,PayMetaProtos.CPayMeta payMeta,PlayerMetaProtos.CPlayerMeta playerMeta,boolean vail,String md5){
        int categoryId = mediaMeta.getCategoryId();

        GDApplication.getmInstance().getDataInsert().insertOrReplaceCategory(mediaMeta);
        switch (categoryId){
            case Contant.CATEGORY_HOTZONE_ID:
                if(vail){
                    ContentValues cv = new ContentValues();
                    cv.put(Contant.TRUCK_VALID,1);
                    GDApplication.getmInstance().getDataInsert().updateTruckMetaByFileName(Contant.TABLE_NAME_HOTZONE,mediaMeta.getTruckMeta().getTruckFilename(),cv);
                    return;
                }

                if(sop == Contant.SOP_ADD || sop == Contant.SOP_UPDATE){
                    GDApplication.getmInstance().getDataInsert().insertOrReplaceTruckMeta(Contant.TABLE_NAME_HOTZONE,mediaMeta,playerMeta,payMeta,md5);
                }else if(sop == Contant.SOP_DEL){
                    GDApplication.getmInstance().getDataInsert().deleteTruckMetaByFileName(Contant.TABLE_NAME_HOTZONE,mediaMeta.getTruckMeta().getTruckFilename());
                }

                break;
            case Contant.CATEGORY_MEDIA_ID:
                if(vail){
                    ContentValues cv = new ContentValues();
                    cv.put(Contant.TRUCK_VALID,1);
                    GDApplication.getmInstance().getDataInsert().updateTruckMetaByFileName(Contant.TABLE_NAME_MOVIESSHOW,mediaMeta.getTruckMeta().getTruckFilename(),cv);
                    return;
                }

                if(sop == Contant.SOP_ADD || sop == Contant.SOP_UPDATE){
                    GDApplication.getmInstance().getDataInsert().insertOrReplaceTruckMeta(Contant.TABLE_NAME_MOVIESSHOW,mediaMeta,playerMeta,payMeta,md5);
                 }else if(sop == Contant.SOP_DEL){
                    GDApplication.getmInstance().getDataInsert().deleteTruckMetaByFileName(Contant.TABLE_NAME_MOVIESSHOW,mediaMeta.getTruckMeta().getTruckFilename());
                 }

                break;
            case Contant.CATEGORY_GAME_ID:
                if(vail){
                    ContentValues cv = new ContentValues();
                    cv.put(Contant.TRUCK_VALID,1);
                    GDApplication.getmInstance().getDataInsert().updateTruckMetaByFileName(Contant.TABLE_NAME_GAME,mediaMeta.getTruckMeta().getTruckFilename(),cv);
                    return;
                }

                if(sop == Contant.SOP_ADD || sop == Contant.SOP_UPDATE){
                    GDApplication.getmInstance().getDataInsert().insertOrReplaceTruckMeta(Contant.TABLE_NAME_GAME,mediaMeta,playerMeta,payMeta,md5);
                }else if(sop == Contant.SOP_DEL){
                    GDApplication.getmInstance().getDataInsert().deleteTruckMetaByFileName(Contant.TABLE_NAME_GAME,mediaMeta.getTruckMeta().getTruckFilename());
                }

                break;
            case Contant.CATEGORY_ELIVE_ID:
                if(vail){
                    ContentValues cv = new ContentValues();
                    cv.put(Contant.TRUCK_VALID,1);
                    GDApplication.getmInstance().getDataInsert().updateTruckMetaByFileName(Contant.TABLE_NAME_ELIVE,mediaMeta.getTruckMeta().getTruckFilename(),cv);
                    return;
                }

                if(sop == Contant.SOP_ADD || sop == Contant.SOP_UPDATE){
                    GDApplication.getmInstance().getDataInsert().insertOrReplaceTruckMeta(Contant.TABLE_NAME_ELIVE,mediaMeta,playerMeta,payMeta,md5);
                }else if(sop == Contant.SOP_DEL){
                    GDApplication.getmInstance().getDataInsert().deleteTruckMetaByFileName(Contant.TABLE_NAME_ELIVE,mediaMeta.getTruckMeta().getTruckFilename());
                }

                break;
            case Contant.CATEGORY_GOLDING_ID:

                if(vail){
                    ContentValues cv = new ContentValues();
                    cv.put(Contant.TRUCK_VALID,1);
                    GDApplication.getmInstance().getDataInsert().updateTruckMetaByFileName(Contant.TABLE_NAME_GOLDING,mediaMeta.getTruckMeta().getTruckFilename(),cv);
                    return;
                }

                if(sop == Contant.SOP_ADD || sop == Contant.SOP_UPDATE){
                    GDApplication.getmInstance().getDataInsert().insertOrReplaceTruckMeta(Contant.TABLE_NAME_GOLDING,mediaMeta,playerMeta,payMeta,md5);
                }else if(sop == Contant.SOP_DEL){
                    GDApplication.getmInstance().getDataInsert().deleteTruckMetaByFileName(Contant.TABLE_NAME_GOLDING,mediaMeta.getTruckMeta().getTruckFilename());
                }

                break;
            case Contant.CATEGORY_MYAPP_ID:
                if(vail){
                    ContentValues cv = new ContentValues();
                    cv.put(Contant.TRUCK_VALID,1);
                    GDApplication.getmInstance().getDataInsert().updateTruckMetaByFileName(Contant.TABLE_NAME_MYAPP,mediaMeta.getTruckMeta().getTruckFilename(),cv);
                    return;
                }

                if(sop == Contant.SOP_ADD || sop == Contant.SOP_UPDATE){
                    GDApplication.getmInstance().getDataInsert().insertOrReplaceTruckMeta(Contant.TABLE_NAME_MYAPP,mediaMeta,playerMeta,payMeta,md5);
                }else if(sop == Contant.SOP_DEL){
                    GDApplication.getmInstance().getDataInsert().deleteTruckMetaByFileName(Contant.TABLE_NAME_MYAPP,mediaMeta.getTruckMeta().getTruckFilename());
                }

                break;
            case Contant.CATEGORY_ADS_ID:
                if(vail){
                    try {
                        CAds.XFTP_ADS[Integer.parseInt(mediaMeta.getAdsMeta().getTruckExtendType())] = true;// 标识哪类广告推送更新，有的更新完毕需要马上播放
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ContentValues cv = new ContentValues();
                    cv.put(Contant.TRUCK_VALID,1);
                    GDApplication.getmInstance().getDataInsert().updateTruckMetaByFileName(Contant.TABLE_NAME_ADS,mediaMeta.getTruckMeta().getTruckFilename(),cv);
                    return;
                }

                if(sop == Contant.SOP_ADD || sop == Contant.SOP_UPDATE){
                    GDApplication.getmInstance().getDataInsert().insertOrReplaceTruckMeta(Contant.TABLE_NAME_ADS,mediaMeta,playerMeta,payMeta,md5);
                }else if(sop == Contant.SOP_DEL){
                    GDApplication.getmInstance().getDataInsert().deleteTruckMetaByFileName(Contant.TABLE_NAME_ADS,mediaMeta.getTruckMeta().getTruckFilename());
                }
                break;

        }
    }

}
