package com.goldingmedia.sqlite;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.goldingmedia.contant.Contant;


/**
 * Created by Jallen on 2017/8/16 0016 19:47.
 */

public class BaseDataSQLHelper extends SQLiteOpenHelper {
    private static BaseDataSQLHelper dbHelper = null;

    private BaseDataSQLHelper(Context context, String DataBaseName,
                              SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DataBaseName, factory, version);
    }

    public static BaseDataSQLHelper getSingleDbHelper(Context context,
                                                      String DataBaseName, SQLiteDatabase.CursorFactory factory, int version) { if (dbHelper == null) {
            synchronized (BaseDataSQLHelper.class) {
                if (dbHelper == null) {
                    dbHelper = new BaseDataSQLHelper(context, DataBaseName,
                            factory, version);
                }
            }
        }
        return dbHelper;
    }

    private void bootStrpDb(SQLiteDatabase db){
        createCategoryDB(db);
        createHotzoneTruckDB(db);
        createMoviesShowTruckDB(db);
        createGoldingTruckDB(db);
        createGameTruckDB(db);
        createEliveTruckDB(db);
        createMyappTruckDB(db);
        createAdsMetaDB(db);
        createMediaStatisticsDB(db);
        createUpgradeServiceDB(db);
        createPushServiceDB(db);
    }

    public String getMetaTruckKeys(){
        String keys = " ("
                + Contant._ID + " INTEGER PRIMARY KEY autoincrement,"
                + Contant.CATEGORY_ID + " varchar(20),"
                + Contant.CATEGORY_SUB_ID + " varchar(20),"
                + Contant.TRUCK_SUB_DESC + " varchar(20),"
                + Contant.TRUCK_SUB_INDEX + " varchar(10),"
                + Contant.TRUCK_FORMAT + " varchar(10),"
                + Contant.UUID + " varchar(20),"
                + Contant.TRUCK_TITLE + " varchar(50),"
                + Contant.TRUCK_IMAGE + " varchar(20),"
                + Contant.TRUCK_FILENAME + " varchar(40),"
                + Contant.TRUCK_DESC + " varchar(1000),"
                + Contant.TRUCK_EXTRA + " varchar(10),"
                + Contant.TRUCK_PROVIDER + " varchar(20),"
                + Contant.TRUCK_PERIOD + " varchar(20),"
                + Contant.TRUCK_SHOW + " varchar(10),"
                + Contant.TRUCK_VALID + " varchar(10),"
                + Contant.TRUCK_FAVOR + " varchar(10),"
                + Contant.TRUCK_INDEX + " varchar(10),"
                + Contant.TRUCK_MEDIA_TYPE_ID+ " varchar(10),"
                + Contant.TRUCK_MEDIA_TYPE_THEME + " varchar(20),"
                + Contant.TRUCK_PUSH_NUM+ " varchar(10),"
                + Contant.TRUCK_EXTEND_TYPE + " varchar(10),"
                + Contant.TRUCK_WND_STYLE + " varchar(10),"
                + Contant.TRUCK_WND_ORIENT + " varchar(20),"
                + Contant.TRUCK_PLAY_AREA + " varchar(10),"
                + Contant.TRUCK_PLAY_PLAN + " varchar(10),"
                + Contant.TOTAL_TIME + " varchar(20),"
                + Contant.CURRENT_TIME + " varchar(20),"
                + Contant.PLAY_COUNT + " varchar(20),"
                + Contant.PLAY_DELAY + " varchar(20),"
                + Contant.PLAY_INTERVAL + " varchar(20),"
                + Contant.PLAY_PRIOTITY + " varchar(20),"
                + Contant.PLAY_STATE + " varchar(20),"
                + Contant.PAY_TYPE + " varchar(10),"
                + Contant.PAY_RESULT + " varchar(10),"
                + Contant.PAY_STATE + " varchar(10),"
                + Contant.PAY_FREE_TIME + " varchar(20),"
                + Contant.PAY_VALUE + " varchar(20),"
                + Contant.PAY_UNIT + " varchar(10),"
                + Contant.PAY_INTERFACE + " varchar(10),"
                + Contant.PAY_MODE + " varchar(10),"
                + Contant.PAY_TIME + " varchar(20),"
                + Contant.PUSH_MD5 + " varchar(35),"
                + Contant.TRUCK_TIMESTAMP + " varchar(20))";

        return keys;
    }

    private void createCategoryDB(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS table_category;");
        db.execSQL("create table " + Contant.TABLE_NAME_CATEGORY + " ("
                + Contant._ID + " INTEGER PRIMARY KEY autoincrement,"
                + Contant.CATEGORY_ID + " varchar(10),"
                + Contant.CATEGORY_SUB_ID + " varchar(10),"
                + Contant.TRUCK_SUB_DESC + " varchar(20),"
                + Contant.TRUCK_SUB_INDEX + " varchar(10))");
    }

    private void createHotzoneTruckDB(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS table_hotzone;");
        db.execSQL("create table " + Contant.TABLE_NAME_HOTZONE + getMetaTruckKeys());
    }
    private void createMoviesShowTruckDB(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS table_moviesshow;");
        db.execSQL("create table " + Contant.TABLE_NAME_MOVIESSHOW + getMetaTruckKeys());
    }
    private void createGoldingTruckDB(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS table_golding;");
        db.execSQL("create table " + Contant.TABLE_NAME_GOLDING + getMetaTruckKeys());
    }
    private void createGameTruckDB(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS table_game;");
        db.execSQL("create table " + Contant.TABLE_NAME_GAME + getMetaTruckKeys());
    }
    private void createEliveTruckDB(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS table_elive;");
        db.execSQL("create table " + Contant.TABLE_NAME_ELIVE + getMetaTruckKeys());
    }
    private void createMyappTruckDB(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS table_myapp;");
        db.execSQL("create table " + Contant.TABLE_NAME_MYAPP + getMetaTruckKeys());
    }
    private void createAdsMetaDB(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS table_ads;");
        db.execSQL("create table " + Contant.TABLE_NAME_ADS + getMetaTruckKeys());
    }
    private void createMediaStatisticsDB(SQLiteDatabase db){

    }
    /**
     * 推送数据库表
     * @param db
     */
    private void createPushServiceDB(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS table_pushservice;");
        db.execSQL("create table " + Contant.TABLE_NAME_PUSH + " ("
                + Contant._ID + " INTEGER PRIMARY KEY autoincrement,"
                + Contant.UUID + " varchar(20),"
                + Contant.PUSH_VERSION + " varchar(20),"
                + Contant.PUSH_SOP + " varchar(20),"
                + Contant.PUSH_GZURI + " varchar(20),"
                + Contant.PUSH_TSURI + " varchar(20),"
                + Contant.PUSH_MD5 + " varchar(35),"
                + Contant.PUSH_UPMODE + " varchar(20),"
                + Contant.PUSH_TIMESTAMP + " varchar(20),"
                + Contant.TRUCK_FILENAME + " varchar(40),"
                + Contant.PUSH_DONE + " varchar(20))");
    }

    /**
     * 升级数据库表
     * @param db
     */
    private void createUpgradeServiceDB(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS table_upgradeservice;");
        db.execSQL("create table " + Contant.TABLE_NAME_UPGRADE + " ("
                + Contant._ID + " INTEGER PRIMARY KEY autoincrement,"
                + Contant.UPGRADE_FILENAME + " varchar(40),"
                + Contant.UPGRADE_VERSION + " varchar(20),"
                + Contant.UPGRADE_URI + " varchar(20),"
                + Contant.UPGRADE_MD5 + " varchar(35),"
                + Contant.UPGRADE_TYPE + " varchar(20),"
                + Contant.UPGRADE_TIMESTAMP + " varchar(20),"
                + Contant.UPGRADE_UPMODE + " varchar(20),"
                + Contant.UPGRADE_DONE + " varchar(20))");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        bootStrpDb(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion > newVersion){
            return;
        }
        try {
            db.execSQL("alter table table_pushservice add filename varchar(50)");
            db.execSQL("alter table table_hotzone add md5 varchar(35)");
            db.execSQL("alter table table_moviesshow add md5 varchar(35)");
            db.execSQL("alter table table_golding add md5 varchar(35)");
            db.execSQL("alter table table_game add md5 varchar(35)");
            db.execSQL("alter table table_elive add md5 varchar(35)");
            db.execSQL("alter table table_myapp add md5 varchar(35)");
            db.execSQL("alter table table_ads add md5 varchar(35)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
