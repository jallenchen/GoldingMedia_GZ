package com.goldingmedia.most.ipc;

import android.os.AsyncTask;
import android.util.Log;

import com.goldingmedia.GDApplication;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.ethernet.IP;
import com.goldingmedia.most.RecvDataMsg;
import com.goldingmedia.mvp.mode.EventBusCMD;
import com.goldingmedia.utils.NLog;
import com.goldingmedia.utils.Utils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import de.greenrobot.event.EventBus;

import static java.lang.Thread.sleep;

/**
 * Created by Jallen on 2017/9/15 0015 13:55.
 */

public class MostTcpProtoBuf extends AsyncTask<Void,Void,String> {
    private static final String TAG ="MostTcpProtoBuf";
    private String mIp = " ";
    private int mPort = 9000;
    private byte[] proto_msg = new byte[8192];
    RecvDataMsg DataMsg = new RecvDataMsg();
    public static Socket m_Socket;
    public  boolean isClosed = false;
    public static boolean isSendMasterId = true;

    public void init() throws SocketException{
        String ip = IP.getLocalHostIp();
        if(ip.equals("") || ip.length() < 7){
            return;
        }
        ip = ip.substring(0,7);

        EventBus.getDefault().post(new EventBusCMD(Contant.MsgID.NET_MOST_OK));

        mIp = ip+"1";

        Contant.UpgradeServer = "http://"+mIp+":81/Upgrade/";
        Contant.MediaServer = "http://"+mIp+":81/Media/";
        Contant.ConfigServer = "http://"+mIp+":81/Config/";

        mIp = mIp.replace("/", "");
        try {
            m_Socket = new Socket(mIp, mPort);
           // m_Socket.setSoTimeout(sotimeout);//设置超时时间
            m_Socket.setKeepAlive(true);//开启保持活动状态的套接字
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(null != m_Socket){
            m_Socket.setReuseAddress(true);
            if (!m_Socket.getReuseAddress()){
                NLog.e(TAG, "This platform does not support reusing Tcp sockets.");
            }
            isClosed = isServerClose();
            if(!isClosed){
                NLog.d(TAG,"Socket Connected");
                EventBus.getDefault().post(new EventBusCMD(Contant.MsgID.NET_4G_OK));
                makeTCPMsgAndSend(Contant.CATEGORY_LOGIN_ID,Contant.PROPERTY_TERMINAL_LOGIN_ID, GDApplication.getmInstance().getDataUtils().sendLogOnMsg());
                isSendMasterId = true;
            }
        }
    }

    public boolean makeTCPMsgAndSend(int categoryid,int categorySubId,byte[] data){
        if(data.length > 65535){
            NLog.e(TAG,"protobuffer is over 64K");
            return false;
        }
        int len =  data.length;

        NLog.d(TAG,"makeTCPMsgAndSend:"+len);
        byte[] msg = new byte[len+12];
        byte[] lenbyte = Utils.intToBytes(len);
        msg[0] = (byte) 0xEB;
        msg[1] = (byte) 0xEB;
        msg[2] = (byte) 0x90;
        msg[3] = (byte) categoryid;
        msg[4] = (byte) categorySubId;
        msg[5] = (byte) 0x03;// 1.Android 2.Master 3.Server
        msg[6] = lenbyte[3];
        msg[7] = lenbyte[2];
        System.arraycopy(data,0, msg, 8, data.length);
        //adler 32 校验
        byte[] adler = Utils.calcAdler32CheckSum(data);
        msg[len+8] = adler[3] ;
        msg[len+9] = adler[2] ;
        msg[len+10] = adler[1];
        msg[len+11] = adler[0] ;

        return sendTCPMsg(msg);
    }

    /**
     * TCP 发送数据
     * @param msg
     * @return
     */
    private boolean sendTCPMsg(byte[] msg){
        try {
            if(m_Socket == null ||m_Socket.isConnected() == false  || isServerClose() ){
                NLog.e("sendTcpmsg","m_Socket:is Disconnected");
                return true;
            }
            OutputStream out = m_Socket.getOutputStream();
            out.write(msg);
            out.flush();
            // out.close();
            NLog.e("sendTcpmsg","ok");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * TCP 接收数据
     */
    private void readMsg(){
        try {
            InputStream inputStream = m_Socket.getInputStream();
            DataInputStream input = new DataInputStream(inputStream);
            int length = 0;
            while ((length = input.read(proto_msg))!=-1){
                Log.d(TAG,"readMsg length:"+length);
                parseBytes(length,proto_msg);
            }
        }catch(Exception ex)     {
            ex.printStackTrace();
        }
    }


    /**
     * 判断是否断开连接，断开返回true,没有返回false
     * @return
     */
    public Boolean isServerClose(){
        try{
            m_Socket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        }catch(Exception se){
            return true;
        }
    }

    @Override  protected String doInBackground(Void...param){
        while (true){
            isClosed = isServerClose();
            if(!isClosed){
                readMsg();
            }
            while (isClosed){
                try {
                    NLog.e(TAG,"socket reConnecting");
                    EventBus.getDefault().post(new EventBusCMD(Contant.MsgID.NET_4G_NG));
                    init();
                    try {
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        continue;
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    /**
     * 接收数据解析
     * @param len
     * @param buffer
     * @throws Exception
     */
    private void parseBytes(int len,byte[] buffer) throws Exception{
        DataMsg.onRecvDataMsg(buffer,len);
    }
}
