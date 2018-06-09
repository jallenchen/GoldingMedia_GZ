package com.goldingmedia.utils;

import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.text.TextUtils;

import com.goldingmedia.contant.Contant;
import com.goldingmedia.most.ipc.MostTcp;
import com.goldingmedia.most.ipc.MsgAddr;

public class FileSync{
	
	private static MostTcp mTcp;
	private static MostTcp mTcpSend;
	
	public static void getFileFromServer(MsgAddr serverIp, String getpath,
										 String getname, String savepath, String savename, boolean direct){

		if( null == serverIp ){
			return;
		}
		
		if(mTcp == null){
			try {
				mTcp = new MostTcp(serverIp.GetInetAddress().toString() , Contant.TCP_PORT_NUMBER);
			} catch (SocketException e) {
				e.printStackTrace();
			}
    	}
		mTcp.SetTerminalFilePathAndName(savepath, savename);
		mTcp.SetServerFilePathAndName(getpath, getname);
		mTcp.SetDirection(direct);
    	if(!mTcp.isInterrupted())
    		mTcp.run();
    	else
    		mTcp.start();
    	while(mTcp.getStatus());
    	if(mTcp != null)
    		mTcp.Stop();
    	mTcp = null;
	}
	
	public static void sendFileToServer(MsgAddr serverIp){
		if( null == serverIp ){
			return;
		}
		File mFile = new File("/mnt/sdcard/"+ Contant.SeatNum+".jpg");
		if(!mFile.exists()){
			return;
		}
		if(mTcpSend == null){
			try {
				mTcpSend = new MostTcp(serverIp.GetInetAddress().toString() ,Contant.TCP_PORT_NUMBER);
			} catch (SocketException e) {
				e.printStackTrace();
			}
    	}
		mTcpSend.SetTerminalFilePathAndName("/mnt/sdcard/", Contant.SeatNum+".jpg");
		mTcpSend.SetServerFilePathAndName("sdcard/photo/", Contant.SeatNum+".jpg");
		mTcpSend.SetDirection(true);
    	if(!mTcpSend.isInterrupted())
    		mTcpSend.run();
    	else
    		mTcpSend.start();
    	while(mTcpSend.getStatus());
    	if(mTcpSend != null)
    		mTcpSend.Stop();
    	mTcpSend = null;
	}

	public static String[] getLocalMacAddress(){ 
        String macSerial = "";
        String str = "";
        try {
        	Process pp = Runtime.getRuntime().exec("cat /sys/class/net/meth0/address ");
        	InputStreamReader ir = new InputStreamReader(pp.getInputStream());
        	LineNumberReader input = new LineNumberReader(ir);
        	for (; null != str;) {
        		str = input.readLine();
        		if (str != null) {
        			macSerial = str.trim();
        			break;
        		}
        	}
        } catch (Exception ex) {
                ex.printStackTrace();
        }
        if(!TextUtils.isEmpty(macSerial)){
        	return macSerial.split(":");
        } else {
        	return null;
        }
	}
	
    public static String[] getLocalIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getAddress().toString().trim().split(":");
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}