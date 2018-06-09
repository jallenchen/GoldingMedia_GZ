package com.goldingmedia.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.goldingmedia.GDApplication;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.*;
import com.goldingmedia.most.ipc.MostTcpProtoBuf;
import com.goldingmedia.mvp.mode.EventBusCMD;
import com.goldingmedia.sqlite.DataSharePreference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Jallen on 2017/8/18 0018 14:08.
 */




public class ProtoDataParse {
    private static final String TAG = "ProtoDataParse";
    private Context mContext;


    public ProtoDataParse(Context ct){
        mContext = ct;
    }

    public  void ParseProto(int categoryId,int categorySubId,byte[] protoData) throws Exception{
        switch (categoryId){
            case Contant.CATEGORY_HOTZONE_ID:
                parserHotZoneSub(categorySubId,protoData);
                break;
            case Contant.CATEGORY_MEDIA_ID:
                parserMediaSub(categorySubId,protoData);
                break;
            case Contant.CATEGORY_GOLDING_ID:
                parserGoldingSub(categorySubId,protoData);
                break;
            case Contant.CATEGORY_GAME_ID:
                parserGameSub(categorySubId,protoData);
                break;
            case Contant.CATEGORY_ELIVE_ID:
                parserEliveSub(categorySubId,protoData);
                break;
            case Contant.CATEGORY_MYAPP_ID:
                parserMyAppSub(categorySubId,protoData);
                break;
            case Contant.CATEGORY_STATISTICS_ID:
                parserStatistcsSub(categorySubId,protoData);
                break;
            case Contant.CATEGORY_DIAGNOSE_ID:
                parserDiagnoseSub(categorySubId,protoData);
                break;
            case Contant.CATEGORY_CONFIG_ID:
                parserConfigSub(categorySubId,protoData);
                break;
            case Contant.CATEGORY_SYSTEM_ID:
                parserSystemSub(categorySubId,protoData);
                break;
            case Contant.CATEGORY_LOGIN_ID:
                parserLoginSub(categorySubId,protoData);
                break;
            case Contant.CATEGORY_XFTP_ID:
                parserXftpSub(categorySubId,protoData);
                break;
            case Contant.CATEGORY_COMMAND_ID:
                parserCommandSub(categorySubId,protoData);
                break;
        }
    }

    public void parserHotZoneSub(int subId,byte[] protoData){
        switch (subId) {
            default:
                break;
        }
    }
    public void parserMediaSub(int subId,byte[] protoData){
        switch (subId) {
            default:
                break;
        }
    }
    public void parserGameSub(int subId,byte[] protoData){
        switch (subId) {
            default:
                break;
        }
    }
    public void parserStatistcsSub(int subId,byte[] protoData){
        switch (subId) {
            case Contant.PROPERTY_STATISTICS_MEDIA_ID:
                break;
            default:
                break;
        }
    }
    public void parserDiagnoseSub(int subId,byte[] protoData){
        try {
            switch (subId) {
                case Contant.PROPERTY_DIAGNOSE_DEVICE_ID:
                    ParseDeviceDiagnose(protoData);
                    break;
                case Contant.PROPERTY_DIAGNOSE_SYSTEM_ID:
                    ParseSystemDiagnose(protoData);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void parserConfigSub(int subId,byte[] protoData){
        switch (subId) {
            case Contant.PROPERTY_CONFIG_SYSTEM_ID:
                try {
                    ParseSystemConfig(protoData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Contant.PROPERTY_LAUNCHER_CONFIG_ID:
                break;
            default:
                break;
        }
    }
    public void parserSystemSub(int subId,byte[] protoData){
        try {
            switch (subId) {
                case Contant.PROPERTY_SYSTEM_INFO_ID:
                    ParseSystemInfo(protoData);
                    break;
                case Contant.PROPERTY_SYSTEM_GPS_ID:
                    ParseGpsNavigator(protoData);
                    break;
                case Contant.PROPERTY_SYSTEM_VERIFACE_ID:
                    ParseVeriFace(protoData);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void parserLoginSub(int subId,byte[] protoData){
        switch (subId) {
            case Contant.PROPERTY_TERMINAL_LOGIN_ID:
                break;
            default:
                break;
        }
    }
    public void parserEliveSub(int subId,byte[] protoData){
        switch (subId) {
            default:
                break;
        }
    }
    public void parserGoldingSub(int subId,byte[] protoData){
        switch (subId) {
            default:
                break;
        }
    }
    public void parserMyAppSub(int subId,byte[] protoData){
        switch (subId) {
            default:
                break;
        }
    }
    public void parserXftpSub(int subId,final byte[] protoData) throws Exception{
        switch (subId){
            case Contant.PROPERTY_XFTP_PUSH_ID:
                GDApplication.post2WorkRunnable(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ParsePushService(protoData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                break;
            case Contant.PROPERTY_XFTP_UPGRADE_ID:
                GDApplication.post2WorkRunnable(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ParseUpgrade(protoData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    public void parserCommandSub(int subId,byte[] protoData){
        try {
            switch (subId) {
             case Contant.PROPERTY_CMMD_RESPONSE_ID:
                //TODO
                    ParseTransferResponse(protoData);
                break;
            case Contant.PROPERTY_CMMD_REQUEST_ID:
                    ParseRequestSession(protoData);
                break;
            case Contant.PROPERTY_CMMD_CUSTOM_ID:
                break;
            default:
                break;
             }
            } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public  void ParseLogon(byte[] data) throws Exception{
        LogonProtos.CLogon logon = LogonProtos.CLogon.parseFrom(data);
    }
    public void ParseDeviceDiagnose(byte[] data) throws Exception{
        DeviceDiagnoseProtos.CDeviceDiagnose deviceDiagnose = DeviceDiagnoseProtos.CDeviceDiagnose.parseFrom(data);
    }
    public void ParseGpsNavigator(byte[] data) throws Exception{
        NLog.e(TAG,"ParseGpsNavigator");
        GpsNavigatorProtos.CGpsNavigator gpsNavigator = GpsNavigatorProtos.CGpsNavigator.parseFrom(data);
        EventBus.getDefault().post(gpsNavigator);
    }
    public void ParseLauncherUi(byte[] data) throws Exception{
        LauncherUiProtos.CLauncherUiTemplate launcherUiTem = LauncherUiProtos.CLauncherUiTemplate.parseFrom(data);
    }
    public void ParseMediaMeta(byte[] data) throws Exception{
        MediaMetaProtos.CMediaMeta mediaMeta = MediaMetaProtos.CMediaMeta.parseFrom(data);
    }
    public void ParseMediaResourceInfo(byte[] data) throws Exception{
        MediaResourceInfoProtos.CMediaResourceInfo mediaResourceInfo = MediaResourceInfoProtos.CMediaResourceInfo.parseFrom(data);
        List<MediaResourceInfoProtos.CMediaSourceMeta>  mediaSourceMetas = mediaResourceInfo.getSourceListList();
    }
    public  void ParseMediaStatistics(byte[] data) throws Exception{
        MediaStatisticsProtos.CMediaStatistics mediaStatistics = MediaStatisticsProtos.CMediaStatistics.parseFrom(data);
    }
    public void ParsePayMeta(byte[] data) throws Exception{
        PayMetaProtos.CPayMeta payMeta = PayMetaProtos.CPayMeta.parseFrom(data);
    }
    public void ParsePlayerMeta(byte[] data) throws Exception{
        PlayerMetaProtos.CPlayerMeta playerMeta = PlayerMetaProtos.CPlayerMeta.parseFrom(data);
    }

    PushServiceProtos.CFtpMeta ftpMeta ;
    MediaMetaProtos.CMediaMeta  mediaMeta;
    PayMetaProtos.CPayMeta payMeta;
    PlayerMetaProtos.CPlayerMeta playerMeta;
    int mSop = 0;
    public void ParsePushService(byte[] data) throws Exception{
        PushServiceProtos.CPushService pushService = PushServiceProtos.CPushService.parseFrom(data);
        List<PushServiceProtos.CPushMediaMeta> pushMediaMetas = pushService.getPushListList();


        DataSharePreference.savePushVer(mContext,pushService.getVersion());
                long startTime = System.nanoTime();
        // 显示资源更新提示
                EventBus.getDefault().post(new EventBusCMD(Contant.MsgID.DOWNLOAD_MSG_V));
                for(PushServiceProtos.CPushMediaMeta pushMediaMeta : pushMediaMetas){
                    mSop = pushMediaMeta.getSop();
                    ftpMeta =  pushMediaMeta.getFtpInfo();
                    mediaMeta = pushMediaMeta.getMediaInfo();
                    payMeta = pushMediaMeta.getPayInfo();
                    playerMeta = pushMediaMeta.getPlayInfo();
                    NLog.d(TAG,"Gmd5:"+ftpMeta.getGzMd5()+"  Tmd5:"+ftpMeta.getTsMd5());
                    classifyPushTruckMeta(mSop,mediaMeta,payMeta,playerMeta,false,ftpMeta.getGzMd5());
                    int code = pushDownload(ftpMeta,mediaMeta);
                     if(code == 0){
                         classifyPushTruckMeta(mSop,mediaMeta,payMeta,playerMeta,true,ftpMeta.getGzMd5());
                     }
                }
                // 关闭资源更新提示
                EventBus.getDefault().post(new EventBusCMD(Contant.MsgID.DOWNLOAD_MSG_G));
                EventBus.getDefault().post(new EventBusCMD(Contant.MsgID.REFLESH_DATA));

                long consumingTime = System.nanoTime() - startTime; //消耗時間
                NLog.d(TAG,"downLoad and Upzip finish time:"+consumingTime/1000+"微秒");
    }

    public void ParseUpgrade(byte[] data) throws Exception{
        UpgradeServiceProtos.CUpgradeService upgradeService = UpgradeServiceProtos.CUpgradeService.parseFrom(data);
        String remoteFilename = upgradeService.getUpgradeMeta().getFileName();
        String remoteVersion = upgradeService.getUpgradeMeta().getVersion();
        Contant.UpgradeVersion = "";
        Contant.UpgradeDone = 0;
        GDApplication.getmInstance().getDataInsert().getUpgradeVerByFileName(remoteFilename);

        if(remoteVersion.equals(Contant.UpgradeVersion) && Contant.UpgradeDone == 1){
            NLog.d(TAG,"update version is the same");
            return ;
        }

        // 显示资源更新提示
        EventBus.getDefault().post(new EventBusCMD(Contant.MsgID.DOWNLOAD_MSG_V));
        GDApplication.getmInstance().getDataInsert().insertUpgradeServiceData(upgradeService);
         int code = upgradeDownload(upgradeService);
                // 关闭资源更新提示
         EventBus.getDefault().post(new EventBusCMD(Contant.MsgID.DOWNLOAD_MSG_G));
    }
    public RequestSessionProtos.CRequestSession ParseRequestSession(byte[] data) throws Exception{
        NLog.e(TAG,"ParseRequestSession");
        RequestSessionProtos.CRequestSession requestSession = RequestSessionProtos.CRequestSession.parseFrom(data);
        NLog.e(TAG,"RqParam:"+requestSession.getRqParam());
        parseRequestCMD(requestSession.getRqCode(),requestSession.getRqParam());
        return requestSession;
    }
    public void ParseSystemConfig(byte[] data) throws Exception{
        SystemConfigProtos.CSystemConfig systemConfig = SystemConfigProtos.CSystemConfig.parseFrom(data);
        NLog.e(TAG,systemConfig.getConfig().getSystemDate());
      //  Utils.longToDate(Long.valueOf(systemConfig.getConfig().getSystemDate()));
        long time = Long.valueOf(systemConfig.getConfig().getSystemDate());
        long nowTime = System.currentTimeMillis()/1000;
        if(time - nowTime > 1800){
              Intent intent = new Intent("com.goldingmedia.system.updatesystemtime");
              intent.putExtra("longtime",time*1000);
            mContext.sendBroadcast(intent);
        }
    }
    public  void ParseSystemDiagnose(byte[] data) throws Exception{
        SystemDiagnoseProtos.CSystemDiagnose systemDiagnose = SystemDiagnoseProtos.CSystemDiagnose.parseFrom(data);
    }
    public  void ParseSystemInfo(byte[] data) throws Exception{
        SystemInfoProtos.CSystemInfo systemInfo = SystemInfoProtos.CSystemInfo.parseFrom(data);
    }
    public void ParseTransferResponse(byte[] data) throws Exception{
        TransferResponseProtos.CTransferResponse transferResponse = TransferResponseProtos.CTransferResponse.parseFrom(data);
        NLog.e(TAG,"ParseTransferResponse:"+"masterId:"+transferResponse.getMasterId()+"   terminalId:"+transferResponse.getDevId());
        Contant.MasterId = Utils.replaceBlank(transferResponse.getMasterId());
        if(MostTcpProtoBuf.isSendMasterId){
            GDApplication.getmInstance().getMostTcp().makeTCPMsgAndSend(Contant.CATEGORY_SYSTEM_ID,Contant.PROPERTY_SYSTEM_INFO_ID, GDApplication.getmInstance().getDataUtils().sendSystemInfoMsg());
            MostTcpProtoBuf.isSendMasterId = false;
        }
    }
    public void ParseTruckMedia(byte[] data) throws Exception{
        TruckMediaProtos.CTruckMediaNode truckMediaNode = TruckMediaProtos.CTruckMediaNode.parseFrom(data);
    }
    public void ParseVeriFace(byte[] data) throws Exception{
        VeriFaceProtos.CVeriFace veriFace = VeriFaceProtos.CVeriFace.parseFrom(data);
    }

    public void classifyPushTruckMeta(int sop,MediaMetaProtos.CMediaMeta  mediaMeta,PayMetaProtos.CPayMeta payMeta,PlayerMetaProtos.CPlayerMeta playerMeta,boolean vail,String md5){
              GDApplication.getmInstance().getDataUtils().parseTruckMedia(sop,mediaMeta,payMeta,playerMeta,vail,md5);
    }

    public int pushDownload(PushServiceProtos.CFtpMeta ftpMeta,MediaMetaProtos.CMediaMeta mediaMeta){
        String MD5 = ftpMeta.getGzMd5();
        //TODO dowmload done
        String fileName = mediaMeta.getTruckMeta().getTruckFilename()+".tar.gz";
       String urlStr= Contant.MediaServer + fileName;
        String path=Contant.Push ;
        int code = -1;

        NLog.d(TAG,"categoryId:"+mediaMeta.getCategoryId()+" categorySubId:"+mediaMeta.getCategorySubId());
        NLog.d(TAG,"fileName:"+fileName+"="+MD5);
        File file = new File(Contant.PushPath+fileName);
        if (file.exists() && Utils.md5sum(Contant.PushPath+fileName).equals(MD5) ) {
            code = 0;
            NLog.d(TAG,"fileName:"+fileName+"=exists");
        }else{
            HttpDownloader downloader = new HttpDownloader();
            code = downloader.downloadFile(urlStr,path,fileName,MD5);

        }

        if(code == 0){
            NLog.d(TAG,mediaMeta.getTruckMeta().getTruckFilename()+"--pushDownload:Sucss");
            String imgPath = Contant.getTruckMetaNodePath(mediaMeta.getCategoryId(),mediaMeta.getCategorySubId(),"",true);
            //UpGzip
            File srcfile = new File(Contant.PushPath+fileName);
            try {
                Utils.doUnTarGz(srcfile,imgPath);
            } catch (IOException e) {
                e.printStackTrace();
                return  1;
            }

        }else{
            NLog.e(TAG,mediaMeta.getTruckMeta().getTruckFilename()+"--PushDownload:fail");
        }
        FinshResp(Contant.CATEGORY_XFTP_ID,Contant.PROPERTY_XFTP_PUSH_ID,code);
    return code;
    }

    public int upgradeDownload(UpgradeServiceProtos.CUpgradeService upgradeService){
            //TODO dowmload done
        int code = -1;
        String MD5 = upgradeService.getUpgradeMeta().getMd5();
        String urlStr= Contant.UpgradeServer + upgradeService.getUpgradeMeta().getFileName();
        String path=Contant.Upgrade;
        String fileName = upgradeService.getUpgradeMeta().getFileName();
        File file = new File(Contant.Upgrade+fileName);
        if(file.exists() && Utils.md5sum(Contant.Upgrade+fileName).equals(MD5)){
             code = 0;//if file exists and md5 is the same ,do not download
             NLog.d(TAG,"fileName:"+fileName+"=exists");
        }else{
            HttpDownloader downloader = new HttpDownloader();
            code = downloader.downloadFile(urlStr,path,fileName,MD5);
        }
        if(code == 0){
            ContentValues cv = new ContentValues();
            cv.put(Contant.UPGRADE_DONE, 1);
            GDApplication.getmInstance().getDataInsert().updateUpgradeService(Contant.TABLE_NAME_UPGRADE,upgradeService.getUpgradeMeta().getFileName(),cv);
            FinshResp(Contant.CATEGORY_XFTP_ID,Contant.PROPERTY_XFTP_UPGRADE_ID,code);
            NLog.d(TAG,upgradeService.getUpgradeMeta().getFileName()+"--UpdateDownload:Sucss");
            if(upgradeService.getUpgradeMeta().getUpobjectValue() == 6){
                //update app
                if(fileName.contains("GoldingSysService")){
                    Intent intent = new Intent("com.goldingmedia.basesystemservice.install.apk");
                    intent.putExtra("apkpath",Contant.UpgradePath+fileName);
                    mContext.sendBroadcast(intent);
                }else{
                    Intent intent = new Intent("com.goldingmedia.system.install.apk");
                    intent.putExtra("apkpath",Contant.UpgradePath+fileName);
                    mContext.sendBroadcast(intent);
                }

            }else if(upgradeService.getUpgradeMeta().getUpobjectValue() == 7){
                //update system
                Intent intent = new Intent("com.goldingmedia.system.update");
                intent.putExtra("updatepath",Contant.UpgradePath+fileName);
                intent.putExtra("MD5",MD5);
                mContext.sendBroadcast(intent);
            }else if(upgradeService.getUpgradeMeta().getUpobjectValue() == 5){
                if(fileName.endsWith(".db")){
                    NLog.d(TAG,fileName);
                    File srcFile = new File(Contant.UpgradePath+fileName);
                    File destFile = new File("data/data/com.goldingmedia/databases/"+Contant.DATABASE_NAME);
                    Utils.copyfile(srcFile, destFile, true);

                    Intent intent = new Intent("com.goldingmedia.system.load.script");
                    intent.putExtra("scriptpath",Contant.REBOOT);
                    mContext.sendBroadcast(intent);
                }else   if(fileName.endsWith(".zip")){
                   boolean isOk = Utils.unZipFiles(Contant.UpgradePath+fileName,Contant.UpgradePath);

                    Intent inte =  new Intent("com.goldingmedia.cmd");
                    ArrayList<String> cms = new ArrayList<>();
                    cms.add("cp /mnt/sdcard/goldingmedia/upgrade/gdsystem.sh  /data/gdsystem.sh");
                    cms.add("rm /mnt/sdcard/goldingmedia/upgrade/gdsystem.sh");
                    inte.putStringArrayListExtra("CMD",cms);
                    mContext.sendBroadcast(inte);
                }else if(fileName.endsWith(".sh")){
                    Intent intent = new Intent("com.goldingmedia.system.load.script");
                    intent.putExtra("scriptpath","/system/bin/sh  "+Contant.UpgradePath+fileName);
                    mContext.sendBroadcast(intent);
                }
            }
        }else{
            NLog.e(TAG,upgradeService.getUpgradeMeta().getFileName()+"--UpdateDownload:fail");
        }
        FinshResp(Contant.CATEGORY_XFTP_ID,Contant.PROPERTY_XFTP_UPGRADE_ID,code);
        return code;
    }

    public void FinshResp(int categoryid, int subCategoryId, int stateCode){
        TransferResponseProtos.CTransferResponse.Builder response = TransferResponseProtos.CTransferResponse.newBuilder();
        response.setDevId(Utils.getSerialID());
        response.setDevType(2);
        response.setMasterId(Contant.MasterId);
        response.setCategoryId(categoryid);
        response.setPropertyId(subCategoryId);
        TransferResponseProtos.CResponseStateMeta.Builder state = TransferResponseProtos.CResponseStateMeta.newBuilder();
        state.setState(stateCode);
        response.setStateData(state);

        GDApplication.getmInstance().getMostTcp().makeTCPMsgAndSend(Contant.CATEGORY_COMMAND_ID,Contant.PROPERTY_CMMD_RESPONSE_ID,response.build().toByteArray());
    }


    public void parseRequestCMD(int reCode,String reParam){
        HashMap<String,String>   mMap = new HashMap<>();
        ContentValues cv = new ContentValues();
        Utils.parseCommandLine(reParam,mMap);
        if(mMap.get("CATEGORY").equals("null")){
            return;
        }
        if(mMap.get("PROPERTY").equals("null")){
            return;
        }
        int categoryId = Integer.valueOf(mMap.get("CATEGORY"));
        int categorySubId = Integer.valueOf(mMap.get("PROPERTY"));

        NLog.e("parseRequestCMD","reCode:"+reCode+" categoryId:"+categoryId+" categorySubId:"+categorySubId);
        switch (reCode){
            case Contant.CMD_REQUEST_MEDIA_PAY:
                cv.put(Contant.PAY_TYPE,(mMap.get("PayType").equals("true")) ? 1:0);
                cv.put(Contant.PAY_VALUE,mMap.get("PayValue"));
                cv.put(Contant.PAY_FREE_TIME,mMap.get("Duration"));
                if(categoryId == Contant.CATEGORY_MEDIA_ID){
                    GDApplication.getmInstance().getDataInsert().updateTruckMetaByFileName(Contant.TABLE_NAME_MOVIESSHOW,mMap.get("FileName"),cv);
                }else if(categoryId == Contant.CATEGORY_HOTZONE_ID){
                    GDApplication.getmInstance().getDataInsert().updateTruckMetaByFileName(Contant.TABLE_NAME_HOTZONE,mMap.get("FileName"),cv);
                }
                EventBus.getDefault().post(new EventBusCMD(Contant.MsgID.REFLESH_DATA));
                break;
            case Contant.REQUEST_MEDIA_DELETE:
                if(categoryId == Contant.CATEGORY_GAME_ID){
                    String pkgname  =  GDApplication.getmInstance().getDataInsert().getAppPkgName(Contant.TABLE_NAME_GAME,mMap.get("FileName"));
                    Intent intent = new Intent("com.goldingmedia.system.uninstall.apk");
                    intent.putExtra("pkgname",pkgname);
                    mContext.sendBroadcast(intent);
                    GDApplication.getmInstance().getDataInsert().deleteTruckMetaByFileName(Contant.TABLE_NAME_GAME,mMap.get("FileName"));
                    String path = Contant.getTruckMetaNodePath(categoryId,categorySubId,mMap.get("FileName"),false);
                    Utils.delFile(new File(path));
                }else if(categoryId ==Contant.CATEGORY_MYAPP_ID && categorySubId == Contant.PROPERTY_MYAPP_APP_ID ){
                    String pkgname  =  GDApplication.getmInstance().getDataInsert().getAppPkgName(Contant.TABLE_NAME_MYAPP,mMap.get("FileName"));
                    Intent intent = new Intent("com.goldingmedia.system.uninstall.apk");
                    intent.putExtra("pkgname",pkgname);
                    mContext.sendBroadcast(intent);
                    GDApplication.getmInstance().getDataInsert().deleteTruckMetaByFileName(Contant.TABLE_NAME_MYAPP,mMap.get("FileName"));
                    String path = Contant.getTruckMetaNodePath(categoryId,categorySubId,mMap.get("FileName"),false);
                    Utils.delFile(new File(path));
                }else{
                    GDApplication.getmInstance().getDataInsert().deleteTruckMetaByFileName(Contant.getTabNameByCategoryId(categoryId),mMap.get("FileName"));
                    String path = Contant.getTruckMetaNodePath(categoryId,categorySubId,mMap.get("FileName"),false);
                    Utils.delFile(new File(path));
                }
                EventBus.getDefault().post(new EventBusCMD(Contant.MsgID.REFLESH_DATA));
                break;
            case Contant.CMD_REQUEST_MEDIA_GET:
                break;
            case Contant.CMD_REQUEST_MEDIA_STOP:

                break;
            case Contant.CMD_REQUEST_MEDIA_PLAY:

                break;
            case Contant.CMD_REQUEST_MEDIA_SHOW:
                cv.put(Contant.TRUCK_SHOW,mMap.get("ShowStyle"));
                if(categoryId == Contant.CATEGORY_MEDIA_ID){
                    GDApplication.getmInstance().getDataInsert().updateTruckMetaByFileName(Contant.TABLE_NAME_MOVIESSHOW,mMap.get("FileName"),cv);
                }else if(categoryId == Contant.CATEGORY_HOTZONE_ID){
                    GDApplication.getmInstance().getDataInsert().updateTruckMetaByFileName(Contant.TABLE_NAME_HOTZONE,mMap.get("FileName"),cv);
                }
                EventBus.getDefault().post(new EventBusCMD(Contant.MsgID.REFLESH_DATA));
                break;
        }
        FinshResp(Contant.CATEGORY_COMMAND_ID,Contant.PROPERTY_CMMD_REQUEST_ID,0);
    }
}
