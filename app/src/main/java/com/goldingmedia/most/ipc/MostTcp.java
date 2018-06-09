package com.goldingmedia.most.ipc;

import com.goldingmedia.utils.NLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;


/*----------------------------------------------------------*/
/*! writer: xxgong
 * \brief  Inter File transmit
 *         1. sends messages using MOST protocol over TCP
 *         2. receives messages and write as files
 */
/*----------------------------------------------------------*/
public class MostTcp extends Thread {
    private Socket m_Socket;
    private boolean mRead = true;
    private final int TCP_RECEIVE_BUFFER = 1024 * 1024;
    private String mFileName = "";
    private String mFilePath = "";
    private String mServerFilePath;
    private String mServerFileName;
    private boolean bSendDirection = false;
    
    public MostTcp(String ip, int nPort) throws SocketException {
    	ip = ip.replace("/", "");
    	try {
			m_Socket = new Socket(ip, nPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	if(null != m_Socket){
    		m_Socket.setReuseAddress(true);
    		if (!m_Socket.getReuseAddress()){
    			NLog.d(null, "This platform does not support reusing Tcp sockets.");
    		}
    	}
    }
    
    public void SetServerFilePathAndName(String path, String name){
    	mServerFilePath = path;
    	mServerFileName = name;
    }
    
    public void SetTerminalFilePathAndName(String path, String name){
    	mFilePath = path;
    	mFileName = name;
    }
    
    public void SetDirection(boolean send){
    	bSendDirection = send;
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
		DataOutputStream dout = null;
		DataInputStream din = null;
		if(m_Socket == null){
			return;
		}
		
		mRead = true;
        while (!isInterrupted() && mRead) {
        	int bytesRead;
        	byte[] buffs = new byte[200];
        	buffs = (mServerFilePath + mServerFileName.trim()).getBytes();
        	try {

            	OutputStream os = m_Socket.getOutputStream();
    			os.write(buffs);
    			os.flush();
    			Thread.sleep(500);
    			
    			if(bSendDirection){
	        		
    				File file = new File(mFilePath + mFileName.trim());
	    			FileInputStream fis = new FileInputStream(file);
	    			din = new DataInputStream(new BufferedInputStream(fis));
	    			dout = new DataOutputStream(m_Socket.getOutputStream());
	    			//dout.writeUTF("");
	    			byte[] buffer = new byte[TCP_RECEIVE_BUFFER];
	    			int len = 0;
	    			while ((len = din.read(buffer,0,buffer.length)) != -1) {
	    				dout.write(buffer, 0, len);
	    				NLog.e("","====send length = "+len);
	    			}

	    			din.close();
	    			dout.flush();
	    			dout.close();
	    			
	    			
    			}else {
	            	byte[] buffer = new byte[TCP_RECEIVE_BUFFER];
	            	
	            	File destDir = new File(mFilePath);
	            	if (!destDir.exists()) {
	            		destDir.mkdirs();
	            	}
	            	
	            	NLog.e("","====write file "+mFilePath + mFileName.trim());
	        		
	            	File file = new File( mFilePath + mFileName.trim() );
					FileOutputStream fos = new FileOutputStream(file);
					dout = new DataOutputStream(new BufferedOutputStream(fos));
					
					long startTime = System.currentTimeMillis();					
					InputStream inputStream = m_Socket.getInputStream();

					while ((bytesRead = inputStream.read(buffer)) != -1){
						dout.write(buffer, 0, bytesRead);
					}
					long length = System.currentTimeMillis() - startTime;
					NLog.e("","<== Consumption time ==> " + length);
					dout.flush();
					dout.close();
    			}
				mRead = false;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
        }
    }

}
