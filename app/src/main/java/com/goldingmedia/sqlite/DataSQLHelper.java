package com.goldingmedia.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.MediaMetaProtos;
import com.goldingmedia.goldingcloud.MediaResourceInfoProtos;
import com.goldingmedia.goldingcloud.PayMetaProtos;
import com.goldingmedia.goldingcloud.PlayerMetaProtos;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.goldingcloud.UpgradeServiceProtos;
import com.goldingmedia.mvp.mode.Category;
import com.goldingmedia.mvp.mode.ReDownLoadParam;
import com.goldingmedia.mvp.mode.ReUpgradeParam;
import com.goldingmedia.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jallen on 2017/8/21 0021 08:57.
 */

public class DataSQLHelper {
    private BaseDataSQLHelper dbHelper;
    public static HashMap<String,ReDownLoadParam> downLoadParamMap = new HashMap<> ();

    public DataSQLHelper(Context context) {
        dbHelper = BaseDataSQLHelper.getSingleDbHelper(context,
                Contant.DATABASE_NAME, null, Contant.VERSION);
    }


    public void insertOrReplaceCategory(MediaMetaProtos.CMediaMeta mediaMeta) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
            ContentValues cv = new ContentValues();
            cv.put(Contant.CATEGORY_ID, mediaMeta.getCategoryId());
            cv.put(Contant.CATEGORY_SUB_ID, mediaMeta.getCategorySubId());
            cv.put(Contant.TRUCK_SUB_INDEX, mediaMeta.getTruckSubIndex());
            cv.put(Contant.TRUCK_SUB_DESC, mediaMeta.getTruckSubDesc());

        int result = db.updateWithOnConflict(Contant.TABLE_NAME_CATEGORY,cv,"category_id=? and category_sub_id=?", new String[]{mediaMeta.getCategoryId()+"",mediaMeta.getCategorySubId()+""},SQLiteDatabase.CONFLICT_REPLACE); //如果有记录，则更新，并返回1，否则返回0

        if(result<=0){
                db.insertWithOnConflict(Contant.TABLE_NAME_CATEGORY, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void insertOrReplaceTruckMeta(String tabName,MediaMetaProtos.CMediaMeta mediaMeta,PlayerMetaProtos.CPlayerMeta playerMeta,PayMetaProtos.CPayMeta payMeta,String md5){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        ContentValues cv = new ContentValues();
        cv.put(Contant.CATEGORY_ID, mediaMeta.getCategoryId());
        cv.put(Contant.CATEGORY_SUB_ID, mediaMeta.getCategorySubId());
        cv.put(Contant.TRUCK_SUB_INDEX, mediaMeta.getTruckSubIndex());
        cv.put(Contant.TRUCK_SUB_DESC, mediaMeta.getTruckSubDesc());
        cv.put(Contant.UUID, mediaMeta.getTruckMeta().getTruckUuid());
        cv.put(Contant.TRUCK_TITLE, mediaMeta.getTruckMeta().getTruckTitle());
        cv.put(Contant.TRUCK_IMAGE, mediaMeta.getTruckMeta().getTruckFilename()+".jpg");
        cv.put(Contant.TRUCK_FILENAME, mediaMeta.getTruckMeta().getTruckFilename());
        cv.put(Contant.TRUCK_DESC, mediaMeta.getTruckMeta().getTruckDesc());
        cv.put(Contant.TRUCK_EXTRA, mediaMeta.getTruckMeta().getTruckExtra());
        cv.put(Contant.TRUCK_PROVIDER, mediaMeta.getTruckMeta().getTruckProvider());
        cv.put(Contant.TRUCK_PERIOD, mediaMeta.getTruckMeta().getTruckPeriod());
        cv.put(Contant.TRUCK_SHOW, mediaMeta.getTruckMeta().getTruckShow());
        cv.put(Contant.TRUCK_VALID, mediaMeta.getTruckMeta().getTruckValid());
        cv.put(Contant.TRUCK_FAVOR, mediaMeta.getTruckMeta().getTruckFavor());
        cv.put(Contant.TRUCK_INDEX, mediaMeta.getTruckMeta().getTruckIndex());
        cv.put(Contant.TRUCK_MEDIA_TYPE_ID, mediaMeta.getTruckMeta().getTruckMediaType());
        cv.put(Contant.TRUCK_MEDIA_TYPE_THEME,mediaMeta.getTruckMeta().getTruckMediaTheme());
        cv.put(Contant.TRUCK_EXTEND_TYPE, mediaMeta.getAdsMeta().getTruckExtendType());
        cv.put(Contant.TRUCK_WND_STYLE,mediaMeta.getAdsMeta().getTruckWndStyle());
        cv.put(Contant.TRUCK_WND_ORIENT, mediaMeta.getAdsMeta().getTruckWndOrient());
        cv.put(Contant.TRUCK_PLAY_AREA, mediaMeta.getAdsMeta().getTruckPlayArea());
        cv.put(Contant.TRUCK_PLAY_PLAN, mediaMeta.getAdsMeta().getTruckPlayPlan());
        cv.put(Contant.TOTAL_TIME, playerMeta.getTotalTime());
        cv.put(Contant.CURRENT_TIME, playerMeta.getTotalTime());
        cv.put(Contant.PLAY_COUNT, playerMeta.getCurrentTime());
        cv.put(Contant.PLAY_DELAY,  playerMeta.getPlayDelay());
        cv.put(Contant.PLAY_INTERVAL, playerMeta.getPlayInterval());
        cv.put(Contant.PLAY_PRIOTITY, playerMeta.getPlayPriority());
        cv.put(Contant.PLAY_STATE, playerMeta.getPlayStateValue());
        cv.put(Contant.PAY_TYPE,payMeta.getPayType());
        cv.put(Contant.PAY_RESULT, payMeta.getPayResult());
        cv.put(Contant.PAY_STATE,payMeta.getPayState());
        cv.put(Contant.PAY_FREE_TIME, payMeta.getPayFreeTime());
        cv.put(Contant.PAY_VALUE,payMeta.getPayValue());
        cv.put(Contant.PAY_UNIT, payMeta.getPayUnit());
        cv.put(Contant.PAY_INTERFACE, payMeta.getPayInterface());
        cv.put(Contant.PAY_MODE, payMeta.getPayMode());
        cv.put(Contant.PAY_TIME, payMeta.getPayTime());
        cv.put(Contant.TRUCK_TIMESTAMP, "");
        cv.put(Contant.PUSH_MD5, md5);
        int result = db.updateWithOnConflict(tabName,cv,"truck_filename=?", new String[]{mediaMeta.getTruckMeta().getTruckFilename()},SQLiteDatabase.CONFLICT_REPLACE); //如果有记录，则更新，并返回1，否则返回0

        if(result<=0){
            db.insertWithOnConflict(tabName, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

//    public void updateTruckMetaByUUID(String tabName,String UUID,ContentValues cv){
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        db.beginTransaction();
//        int result = db.updateWithOnConflict(tabName,cv,"uuid=?", new String[]{UUID},SQLiteDatabase.CONFLICT_REPLACE); //如果有记录，则更新，并返回1，否则返回0
//
//        if(result<=0){
//            db.insertWithOnConflict(tabName, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
//        }
//        db.setTransactionSuccessful();
//        db.endTransaction();
//    }

    public void updateTruckMetaByFileName(String tabName,String filename,ContentValues cv){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        int result = db.updateWithOnConflict(tabName,cv,"truck_filename=?", new String[]{filename},SQLiteDatabase.CONFLICT_REPLACE); //如果有记录，则更新，并返回1，否则返回0

        if(result<=0){
            //db.insertWithOnConflict(tabName, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }


    public void insertPushServiceData(MediaMetaProtos.CMediaMeta mediaMeta) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // deleteAllSyncs(db);
        try {
            String sql = "insert into " + Contant.TABLE_NAME_PUSH + "("
                    + Contant.UUID + ","
                    + Contant.PUSH_VERSION + ","
                    + Contant.PUSH_SOP + ","
                    + Contant.PUSH_GZURI + ","
                    + Contant.PUSH_TSURI + ","
                    + Contant.PUSH_MD5 + ","
                    + Contant.PUSH_UPMODE + " varchar(20),"
                    + Contant.PUSH_TIMESTAMP + " varchar(20),"
                    + Contant.PUSH_DONE
                    + ") " + "values(?,?,?,?,?,?,?,?,?)";


            SQLiteStatement stat = db.compileStatement(sql);
            db.beginTransaction();
            stat.bindString(1, String.valueOf(mediaMeta.getCategoryId()));
            stat.bindString(2, String.valueOf(mediaMeta.getCategorySubId()));
            stat.bindString(3, String.valueOf(mediaMeta.getTruckSubIndex()));
            stat.bindString(4, mediaMeta.getTruckSubDesc());

            long result = stat.executeInsert();
            if (result < 0) {
                // return;
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != db) {
                    db.endTransaction();
                    db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void insertUpgradeServiceData(UpgradeServiceProtos.CUpgradeService upgradeService) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        ContentValues cv = new ContentValues();
        cv.put(Contant.UPGRADE_FILENAME, upgradeService.getUpgradeMeta().getFileName());
        cv.put(Contant.UPGRADE_VERSION, upgradeService.getUpgradeMeta().getVersion());
        cv.put(Contant.UPGRADE_URI, upgradeService.getUpgradeMeta().getUri());
        cv.put(Contant.UPGRADE_MD5, upgradeService.getUpgradeMeta().getMd5());
        cv.put(Contant.UPGRADE_TYPE, upgradeService.getUpgradeMeta().getUpobjectValue());
        cv.put(Contant.UPGRADE_TIMESTAMP, upgradeService.getUpgradeMeta().getTimeStamp());
        cv.put(Contant.UPGRADE_UPMODE, upgradeService.getUpgradeMeta().getUpmode());
        cv.put(Contant.UPGRADE_DONE, 0);

        int result = db.updateWithOnConflict(Contant.TABLE_NAME_UPGRADE,cv,"filename=?", new String[]{upgradeService.getUpgradeMeta().getFileName()},SQLiteDatabase.CONFLICT_REPLACE); //如果有记录，则更新，并返回1，否则返回0

        if(result<=0){
            db.insertWithOnConflict(Contant.TABLE_NAME_UPGRADE, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void getUpgradeVerByFileName(String filename){
        String sql = "select * from " + Contant.TABLE_NAME_UPGRADE + " where " + Contant.UPGRADE_FILENAME + " ='" + filename  + "'";
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);
        try{
            while (cursor!=null &&  cursor.moveToNext()){
                Contant.UpgradeVersion = cursor.getString(cursor.getColumnIndex(Contant.UPGRADE_VERSION ));
                Contant.UpgradeDone = cursor.getInt(cursor.getColumnIndex(Contant.UPGRADE_DONE ));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
            }
        }

    }


    public List<ReUpgradeParam> getWillUpgradeFileNameAndMD5(){
        List<ReUpgradeParam> filenamesAndMd5 = new ArrayList<>();
        String sql = "select * from " + Contant.TABLE_NAME_UPGRADE + " where " + Contant.UPGRADE_DONE + " ='" + 0  + "'";
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);
        try{
            while (cursor!=null &&  cursor.moveToNext()){
                ReUpgradeParam param = new ReUpgradeParam();
                param.setFilename(cursor.getString(cursor.getColumnIndex(Contant.UPGRADE_FILENAME )));
                param.setMd5(cursor.getString(cursor.getColumnIndex(Contant.UPGRADE_MD5 )));
                filenamesAndMd5.add(param);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
            }
        }

        return filenamesAndMd5;
    }

    public void updateUpgradeService(String tabName,String filename,ContentValues cv){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        int result = db.updateWithOnConflict(tabName,cv,"filename=?", new String[]{filename},SQLiteDatabase.CONFLICT_REPLACE); //如果有记录，则更新，并返回1，否则返回0

        if(result<=0){
            db.insertWithOnConflict(tabName, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }



    public List<Category> getCategoryData(int categoryId){
        List<Category> categories = new ArrayList<>();
        String sql = "select * from " + Contant.TABLE_NAME_CATEGORY + " where " + Contant.CATEGORY_ID + " ='" + categoryId  + "'"+ "order by " + Contant.TRUCK_SUB_INDEX + " asc";
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);
        try{
            while (cursor!=null &&  cursor.moveToNext()){
                Category category = new Category();
                category.setCategoryId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.CATEGORY_ID ))));
                category.setCategorySubId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.CATEGORY_SUB_ID ))));
                category.setCategorySubIndex(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_SUB_INDEX ))));
                category.setCategorySubDesc(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_SUB_DESC )));
                categories.add(category);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
            }
        }
        return categories;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getMediaMetaDataList(String tabName,int subId){
        List<TruckMediaProtos.CTruckMediaNode> truckMediaNodes = new ArrayList<>();
        String sql = "select * from " + tabName+ " where " + Contant.CATEGORY_SUB_ID + " ='" + subId  + "'"+ "order by " + Contant.TRUCK_INDEX + " asc";
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);
        String md5 ="";
        try{
            while (cursor!=null &&  cursor.moveToNext()){
                TruckMediaProtos.CTruckMediaNode.Builder truckMediaNode = TruckMediaProtos.CTruckMediaNode.newBuilder();
                MediaMetaProtos.CMediaMeta.Builder mediaMeta = MediaMetaProtos.CMediaMeta.newBuilder();

                 mediaMeta.setCategoryId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.CATEGORY_ID ))))
                .setCategorySubId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.CATEGORY_SUB_ID ))))
                .setTruckSubIndex(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_SUB_INDEX ))))
                .setTruckSubDesc(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_SUB_DESC ))).build();

                MediaMetaProtos.CTruckMeta.Builder truckMeta = MediaMetaProtos.CTruckMeta.newBuilder();
                truckMeta.setTruckUuid(cursor.getString(cursor.getColumnIndex(Contant.UUID )));
                truckMeta.setTruckTitle(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_TITLE )));
                truckMeta.setTruckImage(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_IMAGE )));
                truckMeta.setTruckFilename(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_FILENAME )));
                truckMeta.setTruckDesc(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_DESC )));
                truckMeta.setTruckProvider(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_PROVIDER )));
                truckMeta.setTruckExtra(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_EXTRA )));
                truckMeta.setTruckPeriod(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_PERIOD ))));
                truckMeta.setTruckFavor(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_FAVOR ))));
                truckMeta.setTruckIndex(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_INDEX ))));
                truckMeta.setTruckShow(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_SHOW ))));
                truckMeta.setTruckValid(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_VALID ))));
                truckMeta.setTruckMediaType(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_MEDIA_TYPE_ID))));
                truckMeta.setTruckMediaTheme(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_MEDIA_TYPE_THEME ))));
                mediaMeta.setTruckMeta(truckMeta).build();

                MediaMetaProtos.CTruckMetaExpand.Builder  truckMetaExpand = MediaMetaProtos.CTruckMetaExpand.newBuilder();
                truckMetaExpand.setTruckExtendType(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_EXTEND_TYPE )));
                truckMetaExpand.setTruckWndStyle(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_WND_STYLE )));
                truckMetaExpand.setTruckWndOrient(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_WND_ORIENT )));
                truckMetaExpand.setTruckPlayArea(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_PLAY_AREA )));
                truckMetaExpand.setTruckPlayPlan(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_PLAY_PLAN )));
                mediaMeta.setAdsMeta(truckMetaExpand).build();

                truckMediaNode.setMediaInfo(mediaMeta);

                PlayerMetaProtos.CPlayerMeta.Builder playerMeta = PlayerMetaProtos.CPlayerMeta.newBuilder();
                playerMeta.setTotalTime(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TOTAL_TIME ))))
                        .setCurrentTime(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.CURRENT_TIME ))))
                        .setPlayCount(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PLAY_COUNT ))))
                        .setPlayDelay(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PLAY_DELAY ))))
                        .setPlayInterval(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PLAY_INTERVAL ))))
                        .setPlayPriority(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PLAY_PRIOTITY ))))
                        .setPlayStateValue(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PLAY_STATE ))));
                truckMediaNode.setPlayInfo(playerMeta);

                PayMetaProtos.CPayMeta.Builder payMeta = PayMetaProtos.CPayMeta.newBuilder();
                payMeta.setPayType(cursor.getString(cursor.getColumnIndex(Contant.PAY_TYPE )).equals("1"))
                        .setPayResult(cursor.getString(cursor.getColumnIndex(Contant.PAY_RESULT )).equals("1"))
                        .setPayState(cursor.getString(cursor.getColumnIndex(Contant.PAY_STATE )).equals("1"))
                        .setPayFreeTime(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PAY_FREE_TIME ))))
                        .setPayValue(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PAY_VALUE ))))
                        .setPayUnit(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PAY_UNIT ))))
                        .setPayInterface(cursor.getString(cursor.getColumnIndex(Contant.PAY_INTERFACE )))
                        .setPayMode(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PAY_MODE ))))
                        .setPayTime(cursor.getString(cursor.getColumnIndex(Contant.PAY_TIME )));
                truckMediaNode.setPayInfo(payMeta);

                md5 = cursor.getString(cursor.getColumnIndex(Contant.PUSH_MD5 ));

                truckMediaNode.setCategoryId(mediaMeta.getCategoryId());
                truckMediaNode.setCategorySubId(mediaMeta.getCategorySubId());
                truckMediaNodes.add(truckMediaNode.build());

                if(truckMeta.getTruckValid() == 2){
                    ReDownLoadParam downLoadParam = new ReDownLoadParam();
                    downLoadParam.setCategoryId(mediaMeta.getCategoryId());
                    downLoadParam.setCategorySubId(mediaMeta.getCategorySubId());
                    downLoadParam.setFileName(truckMeta.getTruckFilename());
                    downLoadParam.setTabName(tabName);
                    downLoadParam.setVail(2);
                    downLoadParam.setMd5(md5);
                    downLoadParamMap.put(truckMeta.getTruckFilename(),downLoadParam);
                }else{
                    if(downLoadParamMap.containsKey(truckMeta.getTruckFilename())){
                        downLoadParamMap.remove(truckMeta.getTruckFilename());
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
            }
        }
        return truckMediaNodes;
    }

    public MediaResourceInfoProtos.CMediaResourceInfo getMediaResourceInfos(int categoryId, int subId){
        String tabName = Contant.getTabNameByCategoryId(categoryId);
        String sql = "";
        MediaResourceInfoProtos.CMediaResourceInfo.Builder MediaResourceInfo = MediaResourceInfoProtos.CMediaResourceInfo.newBuilder();

        if(categoryId != Contant.CATEGORY_ALL_ID ){
            if(subId != Contant.CATEGORY_ALL_ID ) {
                sql = "select * from " + tabName + " where " + Contant.CATEGORY_SUB_ID + " ='" + subId + "'" + "order by " + Contant.TRUCK_INDEX + " asc";
            }else{
                sql = "select * from " + tabName;
            }
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);
            try{
                while (cursor!=null &&  cursor.moveToNext()){
                    MediaResourceInfoProtos.CMediaSourceMeta.Builder cMediaSourceMeta = MediaResourceInfoProtos.CMediaSourceMeta.newBuilder();
                    cMediaSourceMeta.setCategoryId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.CATEGORY_ID ))))
                            .setCategorySubId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.CATEGORY_SUB_ID ))))
                            .setDevId(Utils.getSerialID())
                            .setMasterId(Contant.MasterId)
                            .setDevType(2)
                            .setMediaName(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_FILENAME )))
                            .setMediaType(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_MEDIA_TYPE_ID))))
                            .setMediaProvider(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_PROVIDER )));

                    MediaResourceInfo.addSourceList(cMediaSourceMeta);

                }
            }catch (Exception e){
                e.printStackTrace();
            }
            finally {
                if(cursor!=null)
                {
                    cursor.close();
                }
            }
            return MediaResourceInfo.build();

        }else{
            for(int i=1;i<8;i++){
                tabName = Contant.getTabNameByCategoryId(i);
                sql = "select * from " + tabName;
                Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);
                try{
                    while (cursor!=null &&  cursor.moveToNext()){
                        MediaResourceInfoProtos.CMediaSourceMeta.Builder cMediaSourceMeta = MediaResourceInfoProtos.CMediaSourceMeta.newBuilder();
                        cMediaSourceMeta.setCategoryId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.CATEGORY_ID ))))
                                .setCategorySubId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.CATEGORY_SUB_ID ))))
                                .setDevId(Utils.getSerialID())
                                .setMasterId(Contant.MasterId)
                                .setDevType(2)
                                .setMediaName(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_FILENAME )))
                                .setMediaType(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_MEDIA_TYPE_ID))))
                                .setMediaProvider(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_PROVIDER )));

                        MediaResourceInfo.addSourceList(cMediaSourceMeta);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    if(cursor!=null)
                    {
                        cursor.close();
                    }
                }
            }
            return MediaResourceInfo.build();
        }
    }

    public List<String> getMediaMetaDataNames(String tabName,int subId){
        List<String> truckMediaNodeNames = new ArrayList<>();
        String sql = "select * from " + tabName+ " where " + Contant.CATEGORY_SUB_ID + " ='" + subId  + "'"+ "order by " + Contant.TRUCK_INDEX + " asc";
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);
        try{
            while (cursor!=null &&  cursor.moveToNext()){
                truckMediaNodeNames.add(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_FILENAME )));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
            }
        }
        return truckMediaNodeNames;
    }

    public List<TruckMediaProtos.CTruckMediaNode> getMediaMetaDataList(int subId,int mediaTypeId){
        List<TruckMediaProtos.CTruckMediaNode> truckMediaNodes = new ArrayList<>();
        String sql = "select * from " + Contant.TABLE_NAME_MOVIESSHOW+ " where " + Contant.CATEGORY_SUB_ID + " ='" + subId + "'"+" and "+Contant.TRUCK_MEDIA_TYPE_ID + " ='" + mediaTypeId + "'";
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);
        try{
            while (cursor!=null &&  cursor.moveToNext()){
                TruckMediaProtos.CTruckMediaNode.Builder truckMediaNode = TruckMediaProtos.CTruckMediaNode.newBuilder();
                MediaMetaProtos.CMediaMeta.Builder mediaMeta = MediaMetaProtos.CMediaMeta.newBuilder();

                mediaMeta.setCategoryId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.CATEGORY_ID ))))
                        .setCategorySubId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.CATEGORY_SUB_ID ))))
                        .setTruckSubIndex(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_SUB_INDEX ))))
                        .setTruckSubDesc(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_SUB_DESC ))).build();

                MediaMetaProtos.CTruckMeta.Builder truckMeta = MediaMetaProtos.CTruckMeta.newBuilder();
                truckMeta.setTruckUuid(cursor.getString(cursor.getColumnIndex(Contant.UUID )));
                truckMeta.setTruckTitle(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_TITLE )));
                truckMeta.setTruckImage(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_IMAGE )));
                truckMeta.setTruckFilename(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_FILENAME )));
                truckMeta.setTruckDesc(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_DESC )));
                truckMeta.setTruckProvider(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_PROVIDER )));
                truckMeta.setTruckExtra(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_EXTRA )));
                truckMeta.setTruckPeriod(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_PERIOD ))));
                truckMeta.setTruckFavor(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_FAVOR ))));
                truckMeta.setTruckIndex(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_INDEX ))));
                truckMeta.setTruckShow(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_SHOW ))));
                truckMeta.setTruckValid(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_VALID ))));
                truckMeta.setTruckMediaType(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_MEDIA_TYPE_ID))));
                truckMeta.setTruckMediaTheme(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_MEDIA_TYPE_THEME ))));
                mediaMeta.setTruckMeta(truckMeta).build();

                MediaMetaProtos.CTruckMetaExpand.Builder  truckMetaExpand = MediaMetaProtos.CTruckMetaExpand.newBuilder();
                truckMetaExpand.setTruckExtendType(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_EXTEND_TYPE )));
                truckMetaExpand.setTruckWndStyle(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_WND_STYLE )));
                truckMetaExpand.setTruckWndOrient(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_WND_ORIENT )));
                truckMetaExpand.setTruckPlayArea(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_PLAY_AREA )));
                truckMetaExpand.setTruckPlayPlan(cursor.getString(cursor.getColumnIndex(Contant.TRUCK_PLAY_PLAN )));
                mediaMeta.setAdsMeta(truckMetaExpand).build();

                truckMediaNode.setMediaInfo(mediaMeta);

                PlayerMetaProtos.CPlayerMeta.Builder playerMeta = PlayerMetaProtos.CPlayerMeta.newBuilder();
                playerMeta.setTotalTime(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.TOTAL_TIME ))))
                        .setCurrentTime(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.CURRENT_TIME ))))
                        .setPlayCount(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PLAY_COUNT ))))
                        .setPlayDelay(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PLAY_DELAY ))))
                        .setPlayInterval(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PLAY_INTERVAL ))))
                        .setPlayPriority(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PLAY_PRIOTITY ))))
                        .setPlayStateValue(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PLAY_STATE ))));
                truckMediaNode.setPlayInfo(playerMeta);

                PayMetaProtos.CPayMeta.Builder payMeta = PayMetaProtos.CPayMeta.newBuilder();
                payMeta.setPayType(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PAY_TYPE ))))
                        .setPayResult(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PAY_RESULT ))))
                        .setPayState(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PAY_STATE ))))
                        .setPayFreeTime(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PAY_FREE_TIME ))))
                        .setPayValue(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PAY_VALUE ))))
                        .setPayUnit(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PAY_UNIT ))))
                        .setPayInterface(cursor.getString(cursor.getColumnIndex(Contant.PAY_INTERFACE )))
                        .setPayMode(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Contant.PAY_MODE ))))
                        .setPayTime(cursor.getString(cursor.getColumnIndex(Contant.PAY_TIME )));
                truckMediaNode.setPayInfo(payMeta);

                truckMediaNode.setCategoryId(mediaMeta.getCategoryId());
                truckMediaNode.setCategorySubId(mediaMeta.getCategorySubId());


                truckMediaNodes.add(truckMediaNode.build());
            }
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
            }
        }
        return truckMediaNodes;
    }

    public String getAppPkgName(String tabname,String filename){
        String pkgName ="";
        String sql = "select * from " + tabname+ " where " + Contant.TRUCK_FILENAME + " ='" + filename + "'";
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql, null);
        if (cursor!=null &&  cursor.moveToNext()){
            pkgName = cursor.getString(cursor.getColumnIndex(Contant.TRUCK_DESC ));
            cursor.close();
        }
        return  pkgName;
    }

//    public void deleteTruckMeta(String tabName,String uuid){
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        db.delete(tabName, Contant.UUID+ " = ?", new String[]{uuid});
//    }

    public void deleteTruckMetaByFileName(String tabName,String filename){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(tabName, Contant.TRUCK_FILENAME+ " = ?", new String[]{filename});
    }
}
