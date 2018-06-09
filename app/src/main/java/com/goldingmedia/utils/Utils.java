package com.goldingmedia.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextPaint;
import android.text.format.Formatter;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.goldingmedia.GDApplication;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.MediaStatisticsProtos;
import com.goldingmedia.goldingcloud.PushServiceProtos;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.mvp.mode.ReDownLoadParam;
import com.goldingmedia.temporary.DataHelper;
import com.goldingmedia.temporary.Modes;
import com.goldingmedia.temporary.SystemInfo;
import com.goldingmedia.temporary.Variables;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import static com.goldingmedia.contant.Contant.MasterId;
import static com.goldingmedia.temporary.DataHelper.insertStatistics;
import static com.goldingmedia.temporary.ImageProcessing.ConvertBitMap;

/**
 * Created by Jallen on 2017/7/26 0026 19:06.
 */

public class Utils {
    private final String TAG ="Utils";
    private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f' };
    /**
     * 获取当前时间
     *
     * @return 时间字符串 24小时制
     * @author drowtram
     */
    public static String getStringTime(String type) {
       // Time t = new Time();
       // t.setToNow(); // 取得系统时间。
        Date mDate = new Date();
        String hour = mDate.getHours() < 10 ? "0" + (mDate.getHours()) : mDate.getHours() + ""; // 默认24小时制
        String minute = mDate.getMinutes() < 10 ? "0" + (mDate.getMinutes()) : mDate.getMinutes() + "";
        return hour + type + minute;
    }
    /**
     * 获取当前日期，包含星期几
     * @return 日期字符串 xx月xx号 星期x
     * @author drowtram
     */
    public static String getStringData(){
        final Calendar c = Calendar.getInstance();
        Date mDate = new Date();
       c.setTime(mDate);
       // c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if("1".equals(mWay)){
            mWay ="日";
        }else if("2".equals(mWay)){
            mWay ="一";
        }else if("3".equals(mWay)){
            mWay ="二";
        }else if("4".equals(mWay)){
            mWay ="三";
        }else if("5".equals(mWay)){
            mWay ="四";
        }else if("6".equals(mWay)){
            mWay ="五";
        }else if("7".equals(mWay)){
            mWay ="六";
        }
        return mMonth + "月" + mDay + "日\n"+"星期"+mWay;
    }

//    // currentTime要转换的long类型的时间
//    // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
//    public static Date longToDate(long currentTime)
//            throws ParseException {
//        String formatType ="yyyy-MM-dd HH:mm:ss";
//        mDate = new Date(currentTime*1000); // 根据long类型的毫秒数生命一个date类型的时间
//        return mDate;
//    }

    /**
     * 读取文件
     * @param path
     * @return
     */
    public static byte[] readFile(String path){
        try
        {
            // 打开文件输入流
            FileInputStream fis = new FileInputStream(path);
            int length = fis.available();
            byte[] buffer = new byte[length];
            fis.read(buffer);
            fis.close();
          //  DataUtils.readProtoFromLocal(buffer);
            return buffer;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 写proto文件到本地
     * @param pathName .bin文件
     * @return
     */
    public static void writeFile(byte[] data,String pathName){
        try{
            FileOutputStream fout = new FileOutputStream(pathName);
            fout.write(data);
            fout.close();
        }

        catch(Exception e){
            e.printStackTrace();

        }
    }

    public static void copyfile(File fromFile, File toFile,Boolean rewrite ){
        if (!fromFile.exists() || !fromFile.isFile() || !fromFile.canRead()) {
            return;
        }

        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }

        if (toFile.exists() && rewrite) {
            toFile.delete();
        }
        FileInputStream fosfrom = null;
        FileOutputStream fosto = null;
        try{
            fosfrom = new FileInputStream(fromFile);
            fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(fosfrom != null){
                    fosfrom.close();
                }
                if(fosto != null){
                    fosto.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取protobuffer的路径
     * @param mediaFormat
     * @param fileName
     * @return
     */
    public static String getSaveProtoPath(String mediaFormat,String fileName){
        String path = Contant.BasePath+mediaFormat+"/"+fileName+"/";
        File file = new File(path);
        try{
        if (!file.exists()) {
            NLog.d("TestFile", "Create the file:" + path);
            file.getParentFile().mkdirs();
            file.createNewFile();
        }else{
            delFile(file);
        }

    } catch (Exception e) {
        NLog.e("TestFile", "Error on write File:" + e);
    }
        return path;
    }

    /**
     * 删除文件夹和文件,add by jallen 20170711
     * @param file
     */
    public static void delFile(File file) {
        if (!file.exists()) {
            return;
        } else {
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    delFile(f);
                }
                file.delete();
            }else{
                file.delete();
            }
        }
    }

    /**
     * 扫描文件
     */
    public static  void searchFile() {
        File[] file = new File(Contant.BasePath).listFiles();//设定扫描路径
        searchProtoFile(file);
    }

    private static void searchProtoFile(final File[] file) {
        for(int i=0 ; file!= null && i<file.length ;i++) {
            //判读是否文件以及文件后缀名
            if(file[i].isFile() && file[i].getName().endsWith(".bin")){
                readFile(file[i].toString());
            }
            //如果是文件夹，递归扫描
            else if(file[i].isDirectory()) {
                final File[] newFileList = new File(file[i].getAbsolutePath()).listFiles();
                searchProtoFile(newFileList);
                //通过多线程来加速
/*				new Thread(new Runnable() {
                    public void run() {
                        readFile(newFileList);
                    }
                }).start();*/
            }
        }
    }

    /**
     * 解压zip文件
     * @param zipPath
     * @param desDir
     * @return
     */
    public static boolean unZipFiles(String zipPath, String desDir){
        boolean bResult = false;
        File zipFile = new File(zipPath);
        File pathFile = new File(desDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }

        try {
            ZipFile zip = new ZipFile(zipFile);
            for (Enumeration<?> entries = zip.entries(); entries.hasMoreElements();) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();
                InputStream is = zip.getInputStream(entry);
                String outPath = (desDir + zipEntryName).replaceAll("\\*", "/");
                File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
                if (!file.exists()) {
                    file.mkdirs();
                }
                if (new File(outPath).isDirectory()) {
                    continue;
                }

                OutputStream os = new FileOutputStream(outPath);
                byte[] buf = new byte[1024];
                int len;
                while ((len = is.read(buf)) > 0) {
                    os.write(buf, 0, len);
                }
                closeQuietly(is, os);
            }
            bResult = true;
            zip.close();
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        zipFile.delete();
        return bResult;
    }

    private static void closeQuietly(Closeable... closeables){
        try {
            close(closeables);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private static void close(Closeable... closeables){
        if(closeables != null){
            for (Closeable closeable : closeables) {
                if(closeable != null){
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    /**
     *tar.gz解压缩
     */
    public static void doUnTarGz(File srcfile, String destpath)
            throws IOException {
        byte[] buf = new byte[1024];
        FileInputStream fis = new FileInputStream(srcfile);
         BufferedInputStream bis = new BufferedInputStream(fis);
         GzipCompressorInputStream cis = new GzipCompressorInputStream(bis);
        TarArchiveInputStream tais = new TarArchiveInputStream(cis);
        TarArchiveEntry tae = null;
        int pro = 0;
        while ((tae = tais.getNextTarEntry()) != null) {
            File f = new File(destpath + "/" + tae.getName());
            if (tae.isDirectory()) {
                f.mkdirs();
            } else {
                /*
                 * 父目录不存在则创建
                 */
                File parent = f.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }

                FileOutputStream fos = new FileOutputStream(f);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                int len;
                while ((len = tais.read(buf)) != -1) {
                    bos.write(buf, 0, len);
                }
                bos.flush();
                bos.close();
            }
        }
        tais.close();
    }

    //merge two JPG to one bitmap to display added by jallen
    public static Bitmap mergeBitmap(Bitmap fBitmap, Bitmap sBitmap){
        Bitmap bitmap = Bitmap.createBitmap(fBitmap.getWidth()*2,fBitmap.getHeight(),fBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);

        canvas.drawBitmap(fBitmap, new Matrix(), null);
        canvas.drawBitmap(sBitmap,fBitmap.getWidth(),0,null);
        return bitmap;
    }
    /**
     * 获取某文件夹中后缀为xx的文件；
     * @param Path
     * @param Extension
     * @param IsIterative
     * @return
     */
    public static List<String> getFolderMsgWithFileExtension(String Path, String Extension,boolean IsIterative){
        List<String> mList = new ArrayList<String>();
        File mFile = new File(Path);
        if(mFile.exists()){
            File[] files = new File(Path).listFiles();
            for (int i =0; i < files.length; i++){
                File f = files[i];
                if (f.isFile()){
                    if (f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension)) //判断扩展名
                        mList.add(f.getPath());
                    if (!IsIterative)
                        break;
                } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1)
                    getFolderMsgWithFileExtension(f.getPath(), Extension, IsIterative);
            }
            Collections.sort(mList, String.CASE_INSENSITIVE_ORDER);//不区分大小写排序
        }
        return mList;
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

    /*显示RAM的可用和总容量，RAM相当于电脑的内存条*/
    public static String getRAMInfo(Context context){
        ActivityManager am=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        String[] available=fileSize(mi.availMem);
        String[] total=fileSize(mi.totalMem);
        return available[0]+available[1]+"/"+total[0]+total[1];
    }

    /*返回为字符串数组[0]为大小[1]为单位KB或者MB*/
    private static String[] fileSize(long size){
        String str="";
        if(size>=1000){
            str="KB";
            size/=1000;
            if(size>=1000){
                str="MB";
                size/=1000;
            }
        }
        /*将每3个数字用,分隔如:1,000*/
        DecimalFormat formatter=new DecimalFormat();
        formatter.setGroupingSize(3);
        String result[]=new String[2];
        result[0]=formatter.format(size);
        result[1]=str;
        return result;
    }

    /**
     * Return getSerial ID
     * @return ID
     */
    public static String getSerialID(){
        return Build.SERIAL;
    }

	
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }


    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public static String getAppVersion(Context ct) {
        try {
            PackageManager manager = ct.getPackageManager();
            PackageInfo info = manager.getPackageInfo(ct.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getSysOsVersion(){
        return  android.os.Build.VERSION.RELEASE;
    }

    public static String getSysLanguage(){
        return Locale.getDefault().toString();
    }

    /**
     * 测试当前摄像头能否被使用
     * @return
     */
    public static boolean isCameraCanUse() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            // TODO camera驱动挂掉,处理
            mCamera = Camera.open();
        } catch (Exception e) {
            canUse = false;
        }
        if (canUse) {
            mCamera.release();
            mCamera = null;
        }
        return canUse;
    }

    public static float getTextViewLength(TextView textView, String text){
 		TextPaint paint = textView.getPaint();
 		float textLength = paint.measureText(text);  
 		return textLength;  
 	}
    
	public static int toMinutes(String mTime) {
		String[] data = mTime.split(":");
		int intData = 0;
		int mul = 1;
		try {
			for (int i = 0; i < data.length - 1; i++) {
				intData += Integer.valueOf(data[data.length - 2 - i].trim()) * mul;
				mul *= 60;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (intData == 0) {
			intData = 1;
		}
		return intData;
	}
    
	public static int getScreenBrightness(Activity activity) {
	    int nowBrightnessValue = 0;  
	    ContentResolver resolver = activity.getContentResolver();
	    try {  
	        nowBrightnessValue = android.provider.Settings.System.getInt(  
	                resolver, Settings.System.SCREEN_BRIGHTNESS);
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }  
	    return nowBrightnessValue;  
	}  
	
	public static void setBrightness(Activity activity, int brightness) {
	    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
	    lp.screenBrightness = brightness * (1f / 255f);
	    activity.getWindow().setAttributes(lp);
	}

    /* short to byte[] */
    public static byte[] shortToBytes(short num) {
        byte[] b = new byte[2];

        for (int i = 0; i < 2; i++) {
            b[i] = (byte) (num >>> (i * 8));
        }

        return b;
    }
    /* byte转short */
    public  static short getShort(byte[] buf) {
        short r = 0;
            for (int i = 1; i >= 0; i--) {
                r <<= 8;
                r |= (buf[i] & 0x00ff);
            }
        return r;
    }

    /* byte to  int */
    public  static int getInt(byte[] buf) {
        if (buf == null) {
            throw new IllegalArgumentException("byte array is null!");
        }
        int r = 0;
            for (int i = 3; i >= 0; i--) {
                r <<= 8;
                r |= (buf[i] & 0x000000ff);
            }
        return r;
    }

    /* int -> byte[] */
    public static byte[] intToBytes(int num) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (num >>> (24 - i * 8));
        }

        return b;
    }
    // 把byte 转化为两位十六进制数
    public static String toHex(byte b) {
        String result = Integer.toHexString(b & 0xFF);
        if (result.length() == 1) {
            result = '0' + result;
        }
        return result;
    }
    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        NLog.d("Utils","MD5:"+sb.toString());
        return sb.toString();
    }


    private static long doChecksum(String fileName) {

        try {

            CheckedInputStream cis = null;
            long fileSize = 0;
            try {
                // Computer Adler-32 checksum
                cis = new CheckedInputStream(
                        new FileInputStream(fileName), new Adler32());

                fileSize = new File(fileName).length();

            } catch (FileNotFoundException e) {
                System.err.println("File not found.");
                System.exit(1);
            }

            byte[] buf = new byte[128];
            while(cis.read(buf) >= 0) {
            }

            long checksum = cis.getChecksum().getValue();
            System.out.println(checksum + " " + fileSize + " " + fileName);
            return checksum;

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return  0;
    }

    public static byte[] calcAdler32CheckSum(byte[] data) {
        Adler32 checksum = new Adler32();
        checksum.update(data);
        int sum = (int) checksum.getValue();
        return intToBytes(sum);
    }
    public static int intAdler32CheckSum(byte[] data) {
        Adler32 checksum = new Adler32();
        checksum.update(data);
        int sum = (int) checksum.getValue();
        return sum;
    }
    public static String md5sum(InputStream fis) {
        byte[] buffer = new byte[1024];
        int numRead = 0;
        MessageDigest md5;
        try{
            md5 = MessageDigest.getInstance("MD5");
            while((numRead=fis.read(buffer)) > 0) {
                md5.update(buffer,0,numRead);
            }
            fis.close();
            return toHexString(md5.digest());
        } catch (Exception e) {
            System.out.println("error");
            return null;
        }
    }

    public static String md5sum(String filename) {
        InputStream fis;
        byte[] buffer = new byte[1024];
        int numRead = 0;
        MessageDigest md5;
        try{
            fis = new FileInputStream(filename);
            md5 = MessageDigest.getInstance("MD5");
            while((numRead=fis.read(buffer)) > 0) {
                md5.update(buffer,0,numRead);
            }
            fis.close();
            return toHexString(md5.digest());
        } catch (Exception e) {
            System.out.println("error");
            return null;
        }
    }

    public static byte[] getBitmapByte(String imgPath){
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    public static void parseCommandLine(String cmdLine,HashMap<String,String>  mMap)
    {
        String[] strCmdArray = cmdLine.split("&");
        for (int i = 0; i < strCmdArray.length; i++) {
            String[] strMap = strCmdArray[i].split("=");
            mMap.put(strMap[0],strMap[1]);
        }
    }

    public static void openApp(Context ct,String packageName) {
        try {
            PackageInfo pi = ct.getPackageManager().getPackageInfo(packageName, 0);
            PackageManager packageManager = ct.getPackageManager();
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(pi.packageName);

            List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0);

            ResolveInfo ri = apps.iterator().next();
            if (ri != null ) {
                //String packageName = ri.activityInfo.packageName;
                String className = ri.activityInfo.name;

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);

                ComponentName cn = new ComponentName(packageName, className);

                intent.setComponent(cn);
                ct.startActivity(intent);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void onDemandRecording(boolean start, int categorySubId, TruckMediaProtos.CTruckMediaNode truck, Context context) {
        // 如果点播结束 或 存在点播统计信息，则上传Web，并删除记录数据
        List<MediaStatisticsProtos.CMediaStatistics> statisticsList = DataHelper.getStatisticsList(context, categorySubId);
        for (int i=0; i<statisticsList.size(); i++) {
            MediaStatisticsProtos.CMediaStatistics statistics = statisticsList.get(i);
            long startTime = Long.parseLong(statistics.getMediaData().getStartTime());
            long endTime = Long.parseLong(statistics.getMediaData().getEndTime());
            if (endTime - startTime > 6000) {// 点播超过6秒才上传数据
                GDApplication.getmInstance().getMostTcp().makeTCPMsgAndSend(statistics.getCategoryId(), statistics.getCategorySubId(), statistics.toByteArray());
            }
            // 确认上传成功 才删除记录数据
            if(true) {
                DataHelper.deleteStatistics(context, statistics.getMediaData().getMediaUuid());
            }
        }

        // 如果点播开始，记录数据，每5秒更新一次播放时间
        if (start) insertStatistics(context, truck, Contant.CATEGORY_STATISTICS_ID, categorySubId);
    }

    // 小窗口广告统计
    public static void onDemandRecording(TruckMediaProtos.CTruckMediaNode truck, long endTime, long duration) {
        MediaStatisticsProtos.CMediaStatistics.Builder statisticsNode = MediaStatisticsProtos.CMediaStatistics.newBuilder();

        statisticsNode.setCategoryId(Contant.CATEGORY_STATISTICS_ID);
        statisticsNode.setCategorySubId(Contant.PROPERTY_STATISTICS_ADS_ID);
        statisticsNode.setDevType(Contant.DEV_TYPE_TERMINAL);
        try {
            statisticsNode.setSeatNumber(Integer.parseInt(SystemInfo.getSeatString()));
            statisticsNode.setMasterId(MasterId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        statisticsNode.setDeviceId(getSerialID());
        statisticsNode.setTimestamp("timestamp");
//					statisticsNode.setCarrierNumber(cursor.getString(cursor.getColumnIndex(STATISTICS_CARRIER_NUMBER)));

        MediaStatisticsProtos.CPassengerMeta.Builder passengerMeta = MediaStatisticsProtos.CPassengerMeta.newBuilder();
        passengerMeta.setPassengerAge(255);
        statisticsNode.setPassengerData(passengerMeta);

        MediaStatisticsProtos.CMediaDataMeta.Builder dataMeta = MediaStatisticsProtos.CMediaDataMeta.newBuilder();
        dataMeta.setMediaType(truck.getMediaInfo().getTruckMeta().getTruckMediaType());
        dataMeta.setMediaUuid(truck.getMediaInfo().getTruckMeta().getTruckUuid());
        dataMeta.setMediaName(truck.getMediaInfo().getTruckMeta().getTruckFilename());
        dataMeta.setStartTime((endTime - duration) + "");
        dataMeta.setEndTime(endTime + "");
        dataMeta.setPlayArea(Variables.mGpsPlace);
        dataMeta.setPlayDuration((int)(duration/1000));
//					dataMeta.setClickCount(cursor.getInt(cursor.getColumnIndex(STATISTICS_CLICK_COUNT)));
        dataMeta.setPayState(0);
        dataMeta.setPayResult(0);
        statisticsNode.setMediaData(dataMeta);

        GDApplication.getmInstance().getMostTcp().makeTCPMsgAndSend(statisticsNode.getCategoryId(), statisticsNode.getCategorySubId(), statisticsNode.build().toByteArray());
    }

    public static List<ReDownLoadParam>  getHashMapValues(HashMap<String,ReDownLoadParam> map,List<ReDownLoadParam> downLoadParams ){
        for(Map.Entry<String,ReDownLoadParam> entry : map.entrySet()){
            downLoadParams.add(entry.getValue());
        }
        return downLoadParams;

    }

    public static void testCodeProto2DB(){
        GDApplication.post2WorkRunnable(new Runnable() {
            @Override
            public void run() {
                byte[] protobyte = readFile(Contant.PushPath+"pushServiceAll.bin");
                try {
                    GDApplication.getmInstance().getProtoDataParse().ParsePushService(protobyte);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static boolean m_MagazineInit = false;
    private static HashMap<String,String> mMagazineMd5Map = new HashMap<String,String>();

    public static void loadMagazineProtoInfo(){
        GDApplication.post2WorkRunnable(new Runnable() {
            @Override
            public void run() {
                byte[] protobyte = readFile("/system/usr/defdata/"+Contant.PROTOBUFFER_NAME);
                try {
                    PushServiceProtos.CPushService pushService = PushServiceProtos.CPushService.parseFrom(protobyte);
                    List<PushServiceProtos.CPushMediaMeta> pListArray = pushService.getPushListList();
                    int count = pListArray.size();
                    for(int i = 0; i < count ; i++) {
                        if(pListArray.get(i).getMediaInfo().getCategoryId() == Contant.CATEGORY_GOLDING_ID &&
                                pListArray.get(i).getMediaInfo().getCategorySubId() == Contant.PROPERTY_GOLDING_MAGAZINE_ID) {
                            /* Map Info: Magazine Truck file name --> gz_MD5, for download gzip file from the Master by HTTP */
                            mMagazineMd5Map.put(pListArray.get(i).getMediaInfo().getTruckMeta().getTruckFilename(),
                                    pListArray.get(i).getFtpInfo().getGzMd5());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                m_MagazineInit = true;
            }
        });

    }

    public  static String getMagazineMd5Code(String szGzFileName) {
        if(szGzFileName != null && szGzFileName.length() > 0) {
            if(mMagazineMd5Map.containsKey(szGzFileName)) {
                return mMagazineMd5Map.get(szGzFileName);
            }
        }
        return null;
    }
    public static  boolean isMagazineInit() { return m_MagazineInit; }

    public static  boolean copyFilesFromAssets(Context context, String assetsPath, String savePath) throws IOException
    {
        InputStream is = context.getAssets().open(assetsPath);
        if(is == null){
            return false;
        }
        File sFile = new File(savePath);

        if (!sFile.getParentFile().exists()) {
            sFile.getParentFile().mkdirs();
        }

        if (sFile.exists()) {
             sFile.delete();
        }

        if(!sFile.exists()){
            sFile.createNewFile();
        }
            FileOutputStream fos = new FileOutputStream(sFile);
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                // buffer字节
                fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
            }
            fos.flush();// 刷新缓冲区
            is.close();
            fos.close();
        return true;
    }

    public static String inputStream2String (InputStream in) throws IOException   {
        StringBuffer out = new StringBuffer();
        if(in == null){
            return "0";
        }

        byte[]  b = new byte[1];
        int n;
        while ((n = in.read(b))!= -1){
            out.append(new String(b,0,n));
        }
        return  out.toString();
    }

    /**
     * 获取内置SD卡路径
     * @return
     */
    public static String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }
}
