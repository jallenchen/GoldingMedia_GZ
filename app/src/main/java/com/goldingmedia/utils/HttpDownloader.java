package com.goldingmedia.utils;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Intent;

import com.goldingmedia.GDApplication;
import com.goldingmedia.contant.Contant;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**下载工具类
 * Created by Jallen on 2017/9/12 0012 17:35.
 */

public class HttpDownloader {
    private URL url = null;
    private final String TAG = "TAG";

    /**
     * 读取任何文件
     * 返回1 ，代表下载失败。返回0，代表成功。返回1代表文件已经存在
     *
     * @param urlStr
     * @param path
     * @param fileName
     * @return
     */
    public int downloadFile(String urlStr, String path, String fileName, String md5) {
        InputStream input = null;
        NLog.d(TAG,"urlStr:"+urlStr);
        try {
            FileUtil fileUtil = new FileUtil(path);
            fileUtil.isFileExistDel(fileName);

            input = getInputStearmFormUrl(urlStr);
            String MyMd5 = fileUtil.write2SDFromInput(fileName,input);

            NLog.e(TAG,"MD5:"+MyMd5+"=="+md5);
           if(!MyMd5.equals(md5)){
               fileUtil.isFileExistDel(fileName);
               return 1;
            };
        } catch (IOException e) {
            e.printStackTrace();
          return 1;
        }
       finally {
            try {
                if(input != null){
                    input.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
       }
        return  0;
    }

    public int startDownloadFileByCategory(int categoryId,int categorySubId,String fileName,String md5) throws IOException,InterruptedException {
        InputStream input = null;
        FileUtil fileUtil = new FileUtil(Contant.Push);
        fileUtil.isFileExistDel(fileName);
        input = getInputStearmFormUrl(Contant.MediaServer + fileName);
        if(input == null) {
            NLog.e("Http download failed:"+fileName);
            return 1;
        }

        String MyMd5 = fileUtil.write2SDFromInput(fileName,input);
        if(!MyMd5.equals(md5)){
            fileUtil.isFileExistDel(fileName); // delete gz file
            input.close();
            NLog.e("Http download failed for md5 check:"+fileName+",md5:"+md5 + "!="+MyMd5);
            return 1;
        }


        input.close();

        return 0;
    }


    public int downloadFileByCategory(int categoryId,int categorySubId,String fileName,String md5){
        int ret = 1;
        try {
            ret = startDownloadFileByCategory(categoryId,categorySubId,fileName,md5);
        }catch (IOException e) {
            e.printStackTrace();
        }catch (InterruptedException intErr) {
            intErr.printStackTrace();
            return ret;
        }
        if(ret == 1) {
            return ret;
        }
        return  0;
    }

    public int downloadUpgradeByFileName(String fileName,String MD5){
        InputStream input = null;
        try {
            FileUtil fileUtil = new FileUtil(Contant.Push);
            fileUtil.isFileExistDel(fileName);

            input = getInputStearmFormUrl(Contant.UpgradeServer+fileName);
            fileUtil.write2SDFromInput(fileName,input);

        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
        finally {
            try {
                if(input != null){
                    input.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
                return 1;
            }
        }

        ContentValues cv = new ContentValues();
        cv.put(Contant.UPGRADE_DONE, 1);
        GDApplication.getmInstance().getDataInsert().updateUpgradeService(Contant.TABLE_NAME_UPGRADE,fileName,cv);

        if(fileName.endsWith(".apk")){
            //update app
            Intent intent = new Intent("com.goldingmedia.system.install.apk");
            intent.putExtra("apkpath",Contant.UpgradePath+fileName);
            GDApplication.getmInstance().sendBroadcast(intent);
        }else  if(fileName.endsWith(".zip")){
            Intent intent = new Intent("com.goldingmedia.system.update");
            intent.putExtra("updatepath",Contant.UpgradePath+fileName);
            intent.putExtra("MD5",MD5);
            GDApplication.getmInstance().sendBroadcast(intent);
        }else  if(fileName.endsWith(".db")){
            File srcFile = new File(Contant.UpgradePath+fileName);
            File destFile = new File("data/data/com.goldingmedia/databases/"+fileName);
            Utils.copyfile(srcFile, destFile, true);

            Intent intent = new Intent("com.goldingmedia.system.load.script");
            intent.putExtra("scriptpath",Contant.REBOOT);
            GDApplication.getmInstance().sendBroadcast(intent);
        }

        return  0;
    }

    public InputStream getInputStearmFormUrl(String urlStr) throws IOException {
        InputStream input = null;
        url = new URL(urlStr);
        NLog.e(TAG,"请求url download:"+urlStr);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setRequestMethod("GET");      //请求get方法
        urlConn.setReadTimeout(6* 1000);
        urlConn.setConnectTimeout(6* 1000);

        if (urlConn.getResponseCode() != 200){
            NLog.e(TAG,"请求url失败:"+urlConn.getResponseCode());
        } else{
            input = urlConn.getInputStream();
        }
        return input;
    }

    /**
     * 多文件上传的方法
     *
     * @param actionUrl：上传的路径 http://120.76.47.24:7046/golding/passengerImg/masterDevId_clientDevId_20170920123204.jpg
     * @param uploadFilePaths：需要上传的文件路径，数组
     * @return
     */
    @SuppressWarnings("finally")
    public  String uploadFile(String actionUrl, String[] uploadFilePaths) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        DataOutputStream ds = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;

        try {
            // 统一资源
            URL url = new URL(actionUrl);
            // 连接类的父类，抽象类
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;

            // 设置是否从httpUrlConnection读入，默认情况下是true;
            httpURLConnection.setDoInput(true);
            // 设置是否向httpUrlConnection输出
            httpURLConnection.setDoOutput(true);
            // Post 请求不能使用缓存
            httpURLConnection.setUseCaches(false);
            // 设定请求的方法，默认是GET
            httpURLConnection.setRequestMethod("POST");
            // 设置字符编码连接参数
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            // 设置字符编码
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            // 设置请求内容类型
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            // 设置DataOutputStream
            ds = new DataOutputStream(httpURLConnection.getOutputStream());
            for (int i = 0; i < uploadFilePaths.length; i++) {
                String uploadFile = uploadFilePaths[i];
                String filename = uploadFile.substring(uploadFile.lastIndexOf("//") + 1);
                ds.writeBytes(twoHyphens + boundary + end);
                ds.writeBytes("Content-Disposition: form-data; " + "name=\"file" + i + "\";filename=\"" + filename
                        + "\"" + end);
                ds.writeBytes(end);
////////////////////////////////////////////
                byte[] buffer = Utils.getBitmapByte(uploadFile);
                ds.write(buffer, 0, buffer.length);
                ///////////////////////////////////
                //////////////////////////////////////
                ds.writeBytes(end);
            }
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            /* close streams */
            ds.flush();
            if (httpURLConnection.getResponseCode() >= 300) {
                throw new Exception(
                        "HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(inputStreamReader);
                tempLine = null;
                resultBuffer = new StringBuffer();
                while ((tempLine = reader.readLine()) != null) {
                    resultBuffer.append(tempLine);
                    resultBuffer.append("\n");
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (ds != null) {
                try {
                    ds.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            return resultBuffer.toString();
        }
    }

}
