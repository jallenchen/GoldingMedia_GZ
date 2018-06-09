package com.goldingmedia.temporary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.goldingmedia.GDApplication;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.MediaStatisticsProtos;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.temporary.Modes.Order;

import java.util.ArrayList;
import java.util.List;

import static com.goldingmedia.contant.Contant.CATEGORY_ID;
import static com.goldingmedia.contant.Contant.CATEGORY_SUB_ID;
import static com.goldingmedia.contant.Contant.MasterId;
import static com.goldingmedia.contant.Contant.STATISTICS_CARRIER_NUMBER;
import static com.goldingmedia.contant.Contant.STATISTICS_CLICK_COUNT;
import static com.goldingmedia.contant.Contant.STATISTICS_DEAT_NUMBER;
import static com.goldingmedia.contant.Contant.STATISTICS_DEVUCE_ID;
import static com.goldingmedia.contant.Contant.STATISTICS_DEV_TYPE;
import static com.goldingmedia.contant.Contant.STATISTICS_END_TIME;
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
import static com.goldingmedia.temporary.RowParams.DATABASE_NAME;
import static com.goldingmedia.temporary.RowParams.MEDIA_ORDER;
import static com.goldingmedia.temporary.RowParams.MEDIA_ORDER_CARD_ID;
import static com.goldingmedia.temporary.RowParams.MEDIA_ORDER_CLASS_ID;
import static com.goldingmedia.temporary.RowParams.MEDIA_ORDER_CLASS_SUB_ID;
import static com.goldingmedia.temporary.RowParams.MEDIA_ORDER_COUNT;
import static com.goldingmedia.temporary.RowParams.MEDIA_ORDER_QRCODEURI;
import static com.goldingmedia.temporary.RowParams.MEDIA_ORDER_STATUS;
import static com.goldingmedia.temporary.RowParams.MEDIA_ORDER_TIME;
import static com.goldingmedia.temporary.RowParams.TABLE_NAME_MEDIA_ORDER;
import static com.goldingmedia.temporary.RowParams.VERSION;
import static com.goldingmedia.utils.Utils.getSerialID;
import static java.lang.System.currentTimeMillis;


public class DataHelper {

	private static DataBaseHelper getDataHelper(Context context) {
		return DataBaseHelper.getSingleDbHelper(context,
				DATABASE_NAME, null, VERSION);
	}

	/*-------------------------------订单处理Start-------------------------------*/

	// 添加订单
	public static void insertMediaOrder(Context context, Order data) {
		ContentValues values = new ContentValues();
		values.put(MEDIA_ORDER, data.ordersn);
		values.put(MEDIA_ORDER_CLASS_ID, data.classId);
		values.put(MEDIA_ORDER_CLASS_SUB_ID, data.classSubId);
		values.put(MEDIA_ORDER_CARD_ID, data.cardUuid);
		values.put(MEDIA_ORDER_COUNT, data.count);
		values.put(MEDIA_ORDER_TIME, data.time);
		values.put(MEDIA_ORDER_STATUS, data.status);
		values.put(MEDIA_ORDER_QRCODEURI, data.qrcodeUrl);
		long mRet = getDataHelper(context).getReadableDatabase().
				insert(TABLE_NAME_MEDIA_ORDER, null, values);
		Log.i("","<== insertMediaOrder result ==> "+mRet);
	}

	// 更新订单付款状态
	public static void updateMediaOrderStatus(Context context, Order data) {
		ContentValues values = new ContentValues();
		values.put(MEDIA_ORDER_STATUS, data.status);
		int rows = getDataHelper(context).getReadableDatabase().update(TABLE_NAME_MEDIA_ORDER,
				values, MEDIA_ORDER + "=?", new String[] {data.ordersn});
		Log.i("","<== updateMediaOrderStatus result ==> "+rows);
	}

	// 更新订单付款后商品的可用有效时间
	public static void updateMediaOrderTime(Context context, Order data) {
		ContentValues values = new ContentValues();
		values.put(MEDIA_ORDER_TIME, data.time);
		int rows = getDataHelper(context).getReadableDatabase().update(TABLE_NAME_MEDIA_ORDER,
				values, MEDIA_ORDER + "=?", new String[] {data.ordersn});
		Log.i("","<== updateMediaOrderTime result ==> "+rows);
	}

	// 更新订单付有效时间
	public static void updateMediaOrderCount(Context context, Order data) {
		ContentValues values = new ContentValues();
		values.put(MEDIA_ORDER_COUNT, data.count);
		int rows = getDataHelper(context).getReadableDatabase().update(TABLE_NAME_MEDIA_ORDER,
				values, MEDIA_ORDER + "=?", new String[] {data.ordersn});
		Log.i("","<== updateMediaOrderCount result ==> "+rows);
	}

	// 删除订单
	public static void deleteMediaOrder(Context context, String ordersn) {
		int rows = getDataHelper(context).getReadableDatabase().delete(TABLE_NAME_MEDIA_ORDER,
				MEDIA_ORDER +"=?", new String[]{ordersn});
		Log.i("","<== deleteMediaOrder result ==> "+rows);
	}

	// 查询该商品是否已付款
	public static Boolean getIsPayment(Context context, int classId, int classSubId, String cardUuid) {
		Cursor cursor = getDataHelper(context).getReadableDatabase().
				query(TABLE_NAME_MEDIA_ORDER, new String[]{MEDIA_ORDER},
						MEDIA_ORDER_STATUS + " GLOB '" + "1" + "' and " +
								MEDIA_ORDER_CLASS_ID + " GLOB '" + classId + "' and " +
								MEDIA_ORDER_CLASS_SUB_ID + " GLOB '" + classSubId + "' and " +
								MEDIA_ORDER_CARD_ID + " GLOB '" + cardUuid + "'", null, null, null, null);

		Boolean isPayment = false;
		if(cursor != null && cursor.getCount() > 0){
			isPayment = true;
		}
		if (cursor!=null) {
			cursor.close();
		}
		return isPayment;
	}

	// 查询该商品是否存在可用订单
	public static Order getQRCodeUrl(Context context, int classId, int classSubId, String cardUuid) {
		Cursor cursor = getDataHelper(context).getReadableDatabase().
				query(TABLE_NAME_MEDIA_ORDER, new String[] { MEDIA_ORDER_QRCODEURI, MEDIA_ORDER_COUNT },
						MEDIA_ORDER_STATUS + " GLOB '" + "0" + "' and " +
								MEDIA_ORDER_CLASS_ID + " GLOB '" + classId + "' and " +
								MEDIA_ORDER_CLASS_SUB_ID + " GLOB '" + classSubId + "' and " +
								MEDIA_ORDER_CARD_ID + " GLOB '" + cardUuid + "'", null, null, null, null);

		Order data = null;
		if(cursor != null && cursor.getCount() > 0){
			while (cursor.moveToNext()) {
				String timeStr = cursor.getString(cursor.getColumnIndex(MEDIA_ORDER_COUNT));
				long count = Long.parseLong(timeStr);
				if (count < 300000) {
					data = new Order();
					data.qrcodeUrl = cursor.getString(cursor.getColumnIndex(MEDIA_ORDER_QRCODEURI));
					data.count = count;
				}
			}
		}

		if (cursor!=null) {
			cursor.close();
		}
		return data;
	}


	// 获取有效订单
	public static ArrayList<Order> getMediaOrderList(Context context) {
		Cursor cursor = getDataHelper(context).getReadableDatabase().
				query(TABLE_NAME_MEDIA_ORDER,
						new String[] {
								MEDIA_ORDER,
								MEDIA_ORDER_COUNT,
								MEDIA_ORDER_TIME,
								MEDIA_ORDER_STATUS},
						null, null, null, null, null);

		ArrayList<Order> list = new ArrayList<Order>();
		if(cursor != null){
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Order date = new Order();
					date.ordersn = cursor.getString(cursor.getColumnIndex(MEDIA_ORDER));
					date.count = cursor.getLong(cursor.getColumnIndex(MEDIA_ORDER_COUNT));
					date.time = cursor.getLong(cursor.getColumnIndex(MEDIA_ORDER_TIME));
					date.status = cursor.getString(cursor.getColumnIndex(MEDIA_ORDER_STATUS));
					list.add(date);
				}
			}
			cursor.close();
		}
		return list;
	}
	/*-------------------------------订单处理End-------------------------------*/


	/*-------------------------------统计处理Start-------------------------------*/
	// 添加某卡片统计信息
	public static long insertStatistics(Context context, TruckMediaProtos.CTruckMediaNode mTruck, int categoryId, int categorySubId) {
		return insertStatistics(context,  mTruck, categoryId, categorySubId, 0);
	}
	public static long insertStatistics(Context context, TruckMediaProtos.CTruckMediaNode mTruck, int categoryId, int categorySubId, int playDuration) {
		ContentValues values = new ContentValues();
		values.put(CATEGORY_ID, categoryId);
		values.put(CATEGORY_SUB_ID, categorySubId);
		values.put(STATISTICS_DEV_TYPE, Contant.DEV_TYPE_TERMINAL);
		values.put(STATISTICS_DEAT_NUMBER, SystemInfo.getSeatString()/*modify*/);
		values.put(STATISTICS_DEVUCE_ID, getSerialID());
		values.put(STATISTICS_MASTER_ID, MasterId/*modify*/);
		values.put(STATISTICS_TIMESTAMP, "timestamp"/*modify*/);
//		values.put(STATISTICS_CARRIER_NUMBER, "arrierNumber"/*modify*/);
		values.put(STATISTICS_MEDIA_TYPE, mTruck.getMediaInfo().getTruckMeta().getTruckMediaType());
		values.put(STATISTICS_MEDIA_UUID, mTruck.getMediaInfo().getTruckMeta().getTruckUuid());
		values.put(STATISTICS_MEDIA_NAME, mTruck.getMediaInfo().getTruckMeta().getTruckFilename());
		values.put(STATISTICS_START_TIME, currentTimeMillis()+"");
		values.put(STATISTICS_END_TIME, currentTimeMillis()+"");
		values.put(STATISTICS_PLAY_AREA, Variables.mGpsPlace);
		values.put(STATISTICS_PLAY_DURATION, playDuration/*modify*/);
//		values.put(STATISTICS_CLICK_COUNT, "clickCount"/*modify*/);
		boolean isPaid = DataHelper.getIsPayment(GDApplication.getmInstance(), mTruck.getCategoryId(), mTruck.getCategorySubId(), mTruck.getMediaInfo().getTruckMeta().getTruckUuid());
		values.put(STATISTICS_PAY_STATE, isPaid?1:0);
		values.put(STATISTICS_PAY_RESULT, isPaid?1:0/*modify*/);
		long ret = getDataHelper(context).getReadableDatabase().
				insert(TABLE_NAME_STATISTICS, null, values);
		Log.i("","<== insertStatistics result ==> "+ret);
		return ret;
	}

	// 更新某卡片统计信息(点播时间)
	public static long updateStatistics(Context context, long add_time, TruckMediaProtos.CTruckMediaNode mTruck, int categorySubId) {
		Cursor cursor = getDataHelper(context).getReadableDatabase().
				query(TABLE_NAME_STATISTICS,
						new String[] {
								STATISTICS_START_TIME,
								STATISTICS_END_TIME},
						STATISTICS_MEDIA_UUID +"=?", new String[]{mTruck.getMediaInfo().getTruckMeta().getTruckUuid()}, null, null, null);

		long startTime;
		long endTime;
		if(cursor != null && cursor.getCount() > 0 && cursor.moveToNext()) {
			startTime = Long.parseLong(cursor.getString(cursor.getColumnIndex(STATISTICS_START_TIME)));
			endTime = Long.parseLong(cursor.getString(cursor.getColumnIndex(STATISTICS_END_TIME)));

            ContentValues values = new ContentValues();
			long end_time = System.currentTimeMillis();
			if(end_time - endTime > 60000 || end_time - endTime < 0) { //假如5秒更新播放时间，如果end_time - endTime > 5秒 或<0 ，说明可能系统系统时间更改了
				values.put(STATISTICS_START_TIME, startTime+(end_time - endTime) + add_time);
			}
			values.put(STATISTICS_END_TIME, end_time);
			int rows = getDataHelper(context).getReadableDatabase().update(TABLE_NAME_STATISTICS,
					values, STATISTICS_MEDIA_UUID + "=?", new String[] {mTruck.getMediaInfo().getTruckMeta().getTruckUuid()});
//			Log.i("","<== updateStatistics end_time = "+end_time+", endTime = "+endTime);
//			Log.i("","<== updateStatistics result ==> "+rows);
			if(cursor != null) cursor.close();
			return rows;
		} else {
			if(cursor != null) cursor.close();
			return insertStatistics(context, mTruck, Contant.CATEGORY_STATISTICS_ID, categorySubId);
		}
	}

	// 删除某卡片统计信息
	public static int deleteStatistics(Context context, String uuid) {
		int rows = getDataHelper(context).getReadableDatabase().delete(TABLE_NAME_STATISTICS,
				STATISTICS_MEDIA_UUID +"=?", new String[] {uuid});
		Log.i("","<== deleteStatistics result ==> "+rows);
		return rows;
	}

	// 获取某卡片统计信息
	public static MediaStatisticsProtos.CMediaStatistics getStatistics(Context context, TruckMediaProtos.CTruckMediaNode mTruck) {
		Cursor cursor = getDataHelper(context).getReadableDatabase().
				query(TABLE_NAME_STATISTICS,
						new String[] {
								CATEGORY_ID,
								CATEGORY_SUB_ID,
								STATISTICS_DEV_TYPE,
								STATISTICS_DEAT_NUMBER,
								STATISTICS_DEVUCE_ID,
								STATISTICS_MASTER_ID,
								STATISTICS_TIMESTAMP,
								STATISTICS_CARRIER_NUMBER,
								STATISTICS_MEDIA_TYPE,
								STATISTICS_MEDIA_UUID,
								STATISTICS_MEDIA_NAME,
								STATISTICS_START_TIME,
								STATISTICS_END_TIME,
								STATISTICS_PLAY_AREA,
								STATISTICS_PLAY_DURATION,
								STATISTICS_CLICK_COUNT,
								STATISTICS_PAY_STATE,
								STATISTICS_PAY_RESULT},
						STATISTICS_MEDIA_UUID +"=?", new String[]{mTruck.getMediaInfo().getTruckMeta().getTruckUuid()}, null, null, null);

		//List<MediaStatisticsProtos.CMediaStatistics> statisticsNodes = new ArrayList<MediaStatisticsProtos.CMediaStatistics>();
		if(cursor != null && cursor.getCount() > 0 && cursor.moveToNext()){
			MediaStatisticsProtos.CMediaStatistics.Builder statisticsNode = MediaStatisticsProtos.CMediaStatistics.newBuilder();

			statisticsNode.setCategoryId(cursor.getInt(cursor.getColumnIndex(CATEGORY_ID)));
			statisticsNode.setCategorySubId(cursor.getInt(cursor.getColumnIndex(CATEGORY_SUB_ID)));
			statisticsNode.setDevType(cursor.getInt(cursor.getColumnIndex(STATISTICS_DEV_TYPE)));
			try {
				statisticsNode.setSeatNumber(cursor.getInt(cursor.getColumnIndex(STATISTICS_DEAT_NUMBER)));
			} catch (Exception e) {
				e.printStackTrace();
			}
			statisticsNode.setDeviceId(cursor.getString(cursor.getColumnIndex(STATISTICS_DEVUCE_ID)));
			statisticsNode.setMasterId(cursor.getString(cursor.getColumnIndex(STATISTICS_MASTER_ID)));
			statisticsNode.setTimestamp(cursor.getString(cursor.getColumnIndex(STATISTICS_TIMESTAMP)));
//			statisticsNode.setCarrierNumber(cursor.getString(cursor.getColumnIndex(STATISTICS_CARRIER_NUMBER)));

			MediaStatisticsProtos.CPassengerMeta.Builder passengerMeta = MediaStatisticsProtos.CPassengerMeta.newBuilder();
			passengerMeta.setPassengerAge(255);
			statisticsNode.setPassengerData(passengerMeta);

			MediaStatisticsProtos.CMediaDataMeta.Builder dataMeta = MediaStatisticsProtos.CMediaDataMeta.newBuilder();
			dataMeta.setMediaType(cursor.getInt(cursor.getColumnIndex(STATISTICS_MEDIA_TYPE)));
			dataMeta.setMediaUuid(cursor.getString(cursor.getColumnIndex(STATISTICS_MEDIA_UUID)));
			dataMeta.setMediaName(cursor.getString(cursor.getColumnIndex(STATISTICS_MEDIA_NAME)));
			dataMeta.setStartTime(cursor.getString(cursor.getColumnIndex(STATISTICS_START_TIME)));
			dataMeta.setEndTime(cursor.getString(cursor.getColumnIndex(STATISTICS_END_TIME)));
			dataMeta.setPlayArea(cursor.getString(cursor.getColumnIndex(STATISTICS_PLAY_AREA)));
			dataMeta.setPlayDuration(cursor.getInt(cursor.getColumnIndex(STATISTICS_PLAY_DURATION)));
//			dataMeta.setClickCount(cursor.getInt(cursor.getColumnIndex(STATISTICS_CLICK_COUNT)));
			dataMeta.setPayState(cursor.getInt(cursor.getColumnIndex(STATISTICS_PAY_STATE)));
			dataMeta.setPayResult(cursor.getInt(cursor.getColumnIndex(STATISTICS_PAY_RESULT)));
			statisticsNode.setMediaData(dataMeta);

			if(cursor != null) cursor.close();
			return statisticsNode.build();
		}
		if(cursor != null) cursor.close();
		return null;
	}

	// 获取所有统计信息，返回list
	public static List<MediaStatisticsProtos.CMediaStatistics> getStatisticsList(Context context, int categorySubId) {
		return getStatisticsList(context, CATEGORY_SUB_ID + " GLOB '" + categorySubId + "'");
	}

	// 获取统计信息，返回list
	private static List<MediaStatisticsProtos.CMediaStatistics> getStatisticsList(Context context, String selection) {
		Cursor cursor = getDataHelper(context).getReadableDatabase().
				query(TABLE_NAME_STATISTICS,
						new String[] {
								CATEGORY_ID,
								CATEGORY_SUB_ID,
								STATISTICS_DEV_TYPE,
								STATISTICS_DEAT_NUMBER,
								STATISTICS_DEVUCE_ID,
								STATISTICS_MASTER_ID,
								STATISTICS_TIMESTAMP,
								STATISTICS_CARRIER_NUMBER,
								STATISTICS_MEDIA_TYPE,
								STATISTICS_MEDIA_UUID,
								STATISTICS_MEDIA_NAME,
								STATISTICS_START_TIME,
								STATISTICS_END_TIME,
								STATISTICS_PLAY_AREA,
								STATISTICS_PLAY_DURATION,
								STATISTICS_CLICK_COUNT,
								STATISTICS_PAY_STATE,
								STATISTICS_PAY_RESULT},
						selection, null, null, null, null);

		List<MediaStatisticsProtos.CMediaStatistics> statisticsNodes = new ArrayList<MediaStatisticsProtos.CMediaStatistics>();
		if(cursor != null){
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					MediaStatisticsProtos.CMediaStatistics.Builder statisticsNode = MediaStatisticsProtos.CMediaStatistics.newBuilder();

					statisticsNode.setCategoryId(cursor.getInt(cursor.getColumnIndex(CATEGORY_ID)));
					statisticsNode.setCategorySubId(cursor.getInt(cursor.getColumnIndex(CATEGORY_SUB_ID)));
					statisticsNode.setDevType(cursor.getInt(cursor.getColumnIndex(STATISTICS_DEV_TYPE)));
					try {
						statisticsNode.setSeatNumber(cursor.getInt(cursor.getColumnIndex(STATISTICS_DEAT_NUMBER)));
						statisticsNode.setMasterId(cursor.getString(cursor.getColumnIndex(STATISTICS_MASTER_ID)));
					} catch (Exception e) {
						e.printStackTrace();
					}
					statisticsNode.setDeviceId(cursor.getString(cursor.getColumnIndex(STATISTICS_DEVUCE_ID)));
					statisticsNode.setTimestamp(cursor.getString(cursor.getColumnIndex(STATISTICS_TIMESTAMP)));
//					statisticsNode.setCarrierNumber(cursor.getString(cursor.getColumnIndex(STATISTICS_CARRIER_NUMBER)));

					MediaStatisticsProtos.CPassengerMeta.Builder passengerMeta = MediaStatisticsProtos.CPassengerMeta.newBuilder();
					passengerMeta.setPassengerAge(255);
					statisticsNode.setPassengerData(passengerMeta);

					MediaStatisticsProtos.CMediaDataMeta.Builder dataMeta = MediaStatisticsProtos.CMediaDataMeta.newBuilder();
					dataMeta.setMediaType(cursor.getInt(cursor.getColumnIndex(STATISTICS_MEDIA_TYPE)));
					dataMeta.setMediaUuid(cursor.getString(cursor.getColumnIndex(STATISTICS_MEDIA_UUID)));
					dataMeta.setMediaName(cursor.getString(cursor.getColumnIndex(STATISTICS_MEDIA_NAME)));
					dataMeta.setStartTime(cursor.getString(cursor.getColumnIndex(STATISTICS_START_TIME)));
					dataMeta.setEndTime(cursor.getString(cursor.getColumnIndex(STATISTICS_END_TIME)));
					dataMeta.setPlayArea(cursor.getString(cursor.getColumnIndex(STATISTICS_PLAY_AREA)));
					dataMeta.setPlayDuration(cursor.getInt(cursor.getColumnIndex(STATISTICS_PLAY_DURATION)));
//					dataMeta.setClickCount(cursor.getInt(cursor.getColumnIndex(STATISTICS_CLICK_COUNT)));
					dataMeta.setPayState(cursor.getInt(cursor.getColumnIndex(STATISTICS_PAY_STATE)));
					dataMeta.setPayResult(cursor.getInt(cursor.getColumnIndex(STATISTICS_PAY_RESULT)));
					statisticsNode.setMediaData(dataMeta);

					statisticsNodes.add(statisticsNode.build());
				}
			}
		}
		if(cursor != null) cursor.close();
		return statisticsNodes;
	}

	/*-------------------------------统计处理End-------------------------------*/
}
