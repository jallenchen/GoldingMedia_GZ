package com.goldingmedia.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

/**
 * Created by Jallen on 2017/9/12 0012 17:36.
 */

public class FileUtil {
    private String SDPATH="";

    public FileUtil(String path){
        //得到外部存储设备的目录（/SDCARD）
        File dir = new File(Environment.getExternalStorageDirectory() + "/"+"goldingmedia/");
        if(!dir.exists()){
            dir.mkdir();
        }

        SDPATH = Environment.getExternalStorageDirectory() + "/"+"goldingmedia/"+path + "/";
        createDir();
    }

    /**
     * 在SD卡上创建文件
     * @param fileName
     * @return
     * @throws java.io.IOException
     */
    private File createSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建目录
     * @return 文件目录
     */
    private File createDir(){
        File dir = new File(SDPATH);
        if(!dir.exists()){
            dir.mkdir();
        }
        return dir;
    }

    /**
     * 判断文件是否存在
     * @param fileName
     * @return
     */
    public void isFileExistDel(String fileName){
        File file = new File(SDPATH + fileName);
        if(file.exists()){
            file.delete();
        }
    }

    public String write2SDFromInput(String fileName,InputStream input){
        File file ;
        OutputStream output = null;
        MessageDigest md5 = null;
        try {
            createDir();
            file =createSDFile(fileName.trim());
            output = new FileOutputStream(file);
            //老三样 写文件
            byte[] buffer=new byte[1024*10];

            md5 = MessageDigest.getInstance("MD5");
            int len;
            while((len=input.read(buffer))!=-1){  //先读到内存
                md5.update(buffer,0,len);
                output.write(buffer, 0, len);
            }
            output.flush();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }catch (Exception e) {
            System.out.println("error");
            return "";
        }
        finally {
            try {
                if(output != null){
                    output.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
        return  Utils.toHexString(md5.digest());
    }
}
