package com.goldingmedia.temporary;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import com.goldingmedia.contant.Contant;
import com.goldingmedia.ethernet.IP;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.goldingmedia.temporary.Txt.getStrings;

public class SystemInfo {

	public static List<AppInfoMode> getAppInfoList(Context context) {
		List<AppInfoMode> list = new ArrayList<AppInfoMode>();
		PackageManager pm = context.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
		Collections.sort(resolveInfos,
				new ResolveInfo.DisplayNameComparator(pm));
		if (list != null) {
			list.clear();
			for (ResolveInfo reInfo : resolveInfos) {
				String pkgName = reInfo.activityInfo.packageName;
				if (pkgName != null) {
					Intent launchIntent = new Intent();
					launchIntent.setComponent(new ComponentName(pkgName,
							reInfo.activityInfo.name));
					AppInfoMode appInfo = new AppInfoMode();
					appInfo.setPkgName(pkgName);
					appInfo.setAppIcon(reInfo.loadIcon(pm));
					appInfo.setAppLabel((String) (reInfo.loadLabel(pm)));
					appInfo.setIntent(launchIntent);
					list.add(appInfo);
				}

			}
		}
		resolveInfos.clear();
		return list;
	}

	public static String getVersionName(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return  info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static int getVersionCode(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 获得SD卡总大小
	 *
	 * @return
	 */
	public static String getSDTotalSize(Context context) {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return Formatter.formatFileSize(context, blockSize * totalBlocks);
	}

	/**
	 * 获得sd卡剩余容量，即可用大小
	 *
	 * @return
	 */
	public static String getSDAvailableSize(Context context) {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(context, blockSize * availableBlocks);
	}

	/**
	 * 获得机身内存总大小
	 *
	 * @return
	 */
	public static String getRomTotalSize(Context context) {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return Formatter.formatFileSize(context, blockSize * totalBlocks);
	}

	/**
	 * 获得机身可用内存
	 *
	 * @return
	 */
	public static String getRomAvailableSize(Context context) {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(context, blockSize * availableBlocks);
	}

	public static String getSeatString(){
		String mtr ;
		mtr = getLocalSeat();
		if ("".equals(mtr)) {
			mtr = IP.getLocalHostIp();
		}
		return mtr;
	}

	public static String getLocalSeat(){
		String mtr;
		mtr = getStrings(Contant.SETTINGDATETXT);
		if (!TextUtils.isEmpty(mtr) && mtr.split("#").length == 2) {
			mtr=mtr.split("#")[1];
			if(mtr.equals("exit")){
				mtr = "";
			}
		}
		return mtr;
	}

	public static void SetLocalSeat(String parameter){
		File file = new File(Contant.SETTINGDATETXT);
		if (!TextUtils.isEmpty(parameter)) {
			try {
				if (file.exists()) {
					file.createNewFile();
				}
				FileWriter command = new FileWriter(file);
				command.write(parameter.toString());
				command.write("\n");
				command.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (file.exists()) {
				file.delete();
				Log.i("", "-----SetLocalSeat: send network seat is empty,so delete seattxtfile!");
			}
		}
	}

	/**
	 * 座位设置排列数据操作
	 */
	public static void SaveSeatArrangement(String seatsarrangement){
		File file = new File(Contant.SEATSARRANGEMENT);
		if (!TextUtils.isEmpty(seatsarrangement)) {
			try {
				if (file.exists()) {
					file.createNewFile();
				}
				FileWriter command = new FileWriter(file);
				command.write(seatsarrangement.toString());
				command.write("\n");
				command.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getSeatArrangement(){
		try {
			String seatAarrengement = getStrings(Contant.SEATSARRANGEMENT);
			String seat[] = seatAarrengement.split("&");
			if(seat.length == 3 &&
					Integer.parseInt(seat[0])*Integer.parseInt(seat[1]) == seat[2].split("#", -1).length) {
				return seatAarrengement;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static ArrayList<String> getAeeangementSeatList(){
		ArrayList<String> chatList = new ArrayList<String>();
		String  mtr = getSeatArrangement();
		if (!TextUtils.isEmpty(mtr)) {
			String[] temp =mtr.split("&")[2].split("#");
			if (temp.length>0) {
				for (int i = 0; i < temp.length; i++) {
					if (!TextUtils.isEmpty(temp[i])) {
						chatList.add(temp[i]);
					}
				}
			}
		}
		return chatList;
	}

	/**
	 * 验证网络里 每个座位是不同号的
	 * @param
	 * @return false：没有相同的座位号   true：有相同的座位号
	 */
	public static boolean validataSeat(String validata){
		boolean validataSeat = false;
		String mtr = getStrings(Contant.CHATSEAT);
		if (!TextUtils.isEmpty(mtr)) {
			String[] ElementSeat =mtr.split("@");
			String validataseat  = validata.split("#")[1];
			String validataip = validata.split("#")[0];
			for (int i = 0; i < ElementSeat.length; i++) {
                String[] ElementSeatInfo = ElementSeat[i].split("#");
				if (ElementSeatInfo.length == 2 && validataseat.equals(ElementSeatInfo[1])) {
					if (validataip.equals(ElementSeatInfo[0])) {
						validataSeat = false;
						break;
					} else {
						validataSeat = true;
						break;
					}
				}
			}
		} else {
			validataSeat = false;
		}

		return validataSeat;
	}

	public static void SynchronizedSeat(String str)
	{
		boolean isSameLocalSeat = false;
		StringBuffer allseat =new StringBuffer();
		StringBuffer tempBuffer =new StringBuffer();
		allseat.append(getStrings(Contant.CHATSEAT));
		if (TextUtils.isEmpty(allseat)) {
			tempBuffer.append(str);
		}else{
			//遍历是否有相同的IP   修改座位号？
			String[] temp =allseat.toString().split("@");
			for (int i = 0; i < temp.length; i++) {
				String[] checkSeatInfo = temp[i].split("#");
				if (checkSeatInfo.length == 2) {
					if (checkSeatInfo[0].equals(str.split("#")[0])) {//同一个终端
						temp[i] = str;
						isSameLocalSeat= true;
					} else if (checkSeatInfo[1].equals(str.split("#")[1])) {//存在相同座位号
						continue;
					}
					if (0==i) {
						tempBuffer.append(temp[i]);
					}else{
						tempBuffer.append("@");
						tempBuffer.append(temp[i]);
					}
				}
			}
			if (!isSameLocalSeat) {
				tempBuffer.append("@");
				tempBuffer.append(str);
			}
		}
		File chatfile = new File(Contant.CHATSEAT);
		if (!TextUtils.isEmpty(tempBuffer.toString())){
			try {
				if (chatfile.exists()) {
					chatfile.createNewFile();
				}
				FileWriter command = new FileWriter(chatfile);
				command.write(tempBuffer.toString());
				command.write("\n");
				command.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			if (chatfile.exists()) {
				chatfile.delete();
			}
		}
	}
}
