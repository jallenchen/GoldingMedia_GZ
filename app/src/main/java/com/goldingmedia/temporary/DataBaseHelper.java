package com.goldingmedia.temporary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.goldingmedia.contant.Contant.CATEGORY_ID;
import static com.goldingmedia.contant.Contant.CATEGORY_SUB_ID;
import static com.goldingmedia.contant.Contant.STATISTICS_CARRIER_NUMBER;
import static com.goldingmedia.contant.Contant.STATISTICS_CLICK_COUNT;
import static com.goldingmedia.contant.Contant.STATISTICS_DEAT_NUMBER;
import static com.goldingmedia.contant.Contant.STATISTICS_DEVUCE_ID;
import static com.goldingmedia.contant.Contant.STATISTICS_DEV_TYPE;
import static com.goldingmedia.contant.Contant.STATISTICS_END_TIME;
import static com.goldingmedia.contant.Contant.STATISTICS_ID;
import static com.goldingmedia.contant.Contant.STATISTICS_MASTER_ID;
import static com.goldingmedia.contant.Contant.STATISTICS_MEDIA_NAME;
import static com.goldingmedia.contant.Contant.STATISTICS_MEDIA_TYPE;
import static com.goldingmedia.contant.Contant.STATISTICS_MEDIA_UUID;
import static com.goldingmedia.contant.Contant.STATISTICS_PAY_RESULT;
import static com.goldingmedia.contant.Contant.STATISTICS_PAY_STATE;
import static com.goldingmedia.contant.Contant.STATISTICS_PLAY_AREA;
import static com.goldingmedia.contant.Contant.STATISTICS_PLAY_DURATION;
import static com.goldingmedia.contant.Contant.STATISTICS_START_TIME;
import static com.goldingmedia.contant.Contant.STATISTICS_TIMESTAMP;
import static com.goldingmedia.contant.Contant.TABLE_NAME_STATISTICS;

public class DataBaseHelper extends SQLiteOpenHelper {

	private static DataBaseHelper dbHelper = null;

	public static DataBaseHelper getSingleDbHelper(Context context,
			String DataBaseName, CursorFactory factory, int version) {
		if (dbHelper == null) {
			synchronized (DataBaseHelper.class) {
				if (dbHelper == null) {
					dbHelper = new DataBaseHelper(context, DataBaseName, factory, version);
				}
			}
		}
		return dbHelper;
	}

	private DataBaseHelper(Context context, String DataBaseName,
			CursorFactory factory, int version) {
		super(context, DataBaseName, factory, version);
	}

	private void bootStrpDb(SQLiteDatabase db){
		
		db.execSQL("create table " + RowParams.TABLE_NAME_MEDIA_ORDER + " ("
				+ RowParams.MEDIA_ORDER_ID + " INTEGER PRIMARY KEY autoincrement,"
				+ RowParams.MEDIA_ORDER + " varchar(20)," 
				+ RowParams.MEDIA_ORDER_CLASS_ID + " varchar(10)," 
				+ RowParams.MEDIA_ORDER_CLASS_SUB_ID + " varchar(10),"
				+ RowParams.MEDIA_ORDER_CARD_ID + " varchar(10),"
				+ RowParams.MEDIA_ORDER_COUNT + " varchar(10),"
				+ RowParams.MEDIA_ORDER_TIME + " varchar(10),"
				+ RowParams.MEDIA_ORDER_STATUS + " varchar(10),"
				+ RowParams.MEDIA_ORDER_QRCODEURI + " varchar(50))");

		db.execSQL("create table " + TABLE_NAME_STATISTICS + " ("
				+ STATISTICS_ID + " INTEGER PRIMARY KEY autoincrement,"
				+ CATEGORY_ID + " varchar(20),"
				+ CATEGORY_SUB_ID + " varchar(20),"
				+ STATISTICS_DEV_TYPE + " varchar(20),"
				+ STATISTICS_DEAT_NUMBER + " varchar(20),"
				+ STATISTICS_DEVUCE_ID + " varchar(20),"
				+ STATISTICS_MASTER_ID + " varchar(20),"
				+ STATISTICS_TIMESTAMP + " varchar(20),"
				+ STATISTICS_CARRIER_NUMBER + " varchar(20),"
				+ STATISTICS_MEDIA_TYPE + " varchar(20),"
				+ STATISTICS_MEDIA_UUID + " varchar(20),"
				+ STATISTICS_MEDIA_NAME + " varchar(20),"
				+ STATISTICS_START_TIME + " varchar(20),"
				+ STATISTICS_END_TIME + " varchar(20),"
				+ STATISTICS_PLAY_AREA + " varchar(20),"
				+ STATISTICS_PLAY_DURATION + " varchar(20),"
				+ STATISTICS_CLICK_COUNT + " varchar(20),"
				+ STATISTICS_PAY_STATE + " varchar(10),"
				+ STATISTICS_PAY_RESULT + " varchar(10))");
		
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.e("","-------onCreate()-------");
		bootStrpDb(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.e("","-------onUpgrade()-------"+oldVersion+"/"+newVersion);
		bootStrpDb(db);
	}

	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
		}
	}
}
