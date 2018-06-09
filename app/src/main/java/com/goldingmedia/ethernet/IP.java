package com.goldingmedia.ethernet;

import android.text.TextUtils;
import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IP {

    public static int MULTIPORT = 8899;
    
    public static String getLocalHostIp() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ip.getHostAddress())) {
                        return ip.getHostAddress();
                    }
                }
            }
        } catch(SocketException e) {
        	e.printStackTrace();
        }
        return ipaddress;
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
            Log.e("", ex.toString());
        }
        return null;
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
    public static String getLocalMacAddresss(){
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
            return macSerial;
        } else {
            return null;
        }
    }
	 
	public static boolean GetLocalIpData(byte[] buffer) {
	 	String ipAddr = getLocalHostIp();
        if(!TextUtils.isEmpty(ipAddr)){
            String[] mStr = ipAddr.split("\\.");
            try {
                for(int i = 0; i< mStr.length; i++){
                    buffer[i] = Byte.valueOf(mStr[i]);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
	}
    
    public static boolean isServerTerminal(){
    	String mStr = getLocalHostIp();
    	Log.e("","<==== Local Ip " + mStr);
        if( "10.0.3.2".equals(mStr.trim())  || "10.0.0.2".equals(mStr.trim())){
    		return true;
    	}
    	return false;
    }
    
    public static boolean isUSBFirstTerminal(){
    	String mStr = getLocalHostIp();
    	if("10.0.0.2".equals(mStr.trim())){
    		return true;
    	}
    	return false;
    }
    
    public static boolean isFirstTerminal(){
    	String mStr = getLocalHostIp();
    	if( "10.0.3.2".equals(mStr.trim())  || "10.0.0.2".equals(mStr.trim())){
    		return true;
    	}
    	return false;
    }
}