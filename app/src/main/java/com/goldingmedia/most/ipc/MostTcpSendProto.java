package com.goldingmedia.most.ipc;

import com.goldingmedia.utils.NLog;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;


public class MostTcpSendProto extends Thread {
    private Socket m_SocketSend;
    private boolean mRead = true;
    private String mFileName;
    private String mFilePath;
    
    public MostTcpSendProto(String ip, int nPort) throws SocketException {
    	ip = ip.replace("/", "");
    	try {
    		m_SocketSend = new Socket(ip, nPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	if(null != m_SocketSend){
    		m_SocketSend.setReuseAddress(true);
    		if (!m_SocketSend.getReuseAddress()){
    			NLog.d(null, "This platform does not support reusing Tcp sockets.");
    		}
    	}
    }
    
    public void SetFileName(String filePath, String fileName){
    	mFileName = fileName;
    	mFilePath = filePath;
    }
    
    public void SetTerminalFilePathAndName(String path, String name){
    }
    
    
    public void Stop() {
        interrupt();
        try {
            join();
        } catch (Exception ignored) { }
    }
    
    public boolean getStatus(){
    	return mRead;
    }
    
    @Override
    public void run() {
		DataInputStream din = null;
		DataOutputStream dout = null;
		mRead = true;
        while (!isInterrupted() && mRead) {
        	try {
        		File file = new File(mFilePath+mFileName); // �����ļ�
    			FileInputStream fis = new FileInputStream(file); // �����ļ�������
    			din = new DataInputStream(new BufferedInputStream(fis)); // �û�������װ�ļ�����������߶�ȡ�ٶȣ���Ȼ���ٰ�װ������������
    			dout = new DataOutputStream(m_SocketSend.getOutputStream());// �������������
    			//dout.writeUTF(String.valueOf(file.length())); // �����ļ�����
    			byte[] buffer = new byte[1024]; // ���建��
    			int len = 0;
    			while ((len = din.read(buffer)) != -1) {
    				dout.write(buffer, 0, len); // ���������������
    			}
    			dout.flush();
    			dout.close();
				mRead = false;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
        }
    }
}
