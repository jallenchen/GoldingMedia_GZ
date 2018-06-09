package com.goldingmedia.most.fblock;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.goldingmedia.contant.Contant;
import com.goldingmedia.most.ipc.MostIpc;
import com.goldingmedia.most.ipc.MostMsg;
import com.goldingmedia.most.ipc.MostMsgTx;
import com.goldingmedia.most.ipc.MsgAddr;
import com.goldingmedia.most.ipc.MsgFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by M21059 on 23.09.2014.
 */

public class FBlock implements MsgFilter.IMsgHandler, Runnable{

/*    public enum PlayModes {
        Pause, Play}*/


    private final int MAC_ADDR_LEN = 6;
    public static MsgAddr serverAddr = null;
    private boolean pidsReceived;
	private boolean statusReceived;
    private int pidVideo;
    private int pidAudio;
    private int pidPmt;
	private static int playStatus = -2;
    private static MostIpc m_Ipc;
    private static FBlock staticInstance = null;
    private static final int MOST_UDP_PORT_TX = 5544;
    private static final int MOST_UDP_PORT_RX = 5545;
    private static final int FBLOCK = 17;

    private static final int INST_VOD = 0;

    private static final int TIMEOUT_MS = 100;
    private final SendHandler SendHandler = new SendHandler();
    public final PlayModeImpl PlayMode = new PlayModeImpl();
    public final SelectFileImpl SelectFile = new SelectFileImpl();
    public final TimePosImpl TimePosition = new TimePosImpl();
    public final RepetitionImpl Repetition = new RepetitionImpl();
    public final ServerVersionImpl ServerVersion = new ServerVersionImpl();
    public final MediaPidImpl MediaPid = new MediaPidImpl();
    public final AdsFileSetImpl AdsFileSet = new AdsFileSetImpl();
	public final PlayStatusImpl PlayStatus = new PlayStatusImpl();
	public final AdsStatusImpl AdsStatus = new AdsStatusImpl();

	public final FolderFileGetImpl FolderFileGet = new FolderFileGetImpl();
	public final SelectAdsFileImpl SelectAdsFile = new SelectAdsFileImpl();
	public final BoardcastSetImpl BoardcastSet = new BoardcastSetImpl();
    public final SelectConfigImpl SelectConfigFile = new SelectConfigImpl();

    public final StopVodStreamImpl StopStram = new StopVodStreamImpl();
    public final StopAdsStreamImpl StopAdsStram = new StopAdsStreamImpl();
    public final GetSystemTimeImpl GetSystemTime = new GetSystemTimeImpl();
    public final GetGpsPositionImpl GetGpsPosition = new GetGpsPositionImpl();

	private Context mContext;
    private boolean mReadEnd = false;
    private String mFileListStr = "";
    private byte [] payloadMac = new byte[MAC_ADDR_LEN];

    private void preloadMacData(byte[] buffer){
    	for(int i = 0; i < MAC_ADDR_LEN; i++){
    		buffer[i] = payloadMac[i];
    	}

    }
    public boolean getReadEndStatus(){
    	return mReadEnd;
    }

    public static MsgAddr getServerAddress(){
    	return serverAddr;
    }

    public void setReadEndStatus(boolean status){
    	mReadEnd = status;
    	mFileListStr = "";
    }

    public String getFileListString(){
    	return mFileListStr;
    }
    public void run() {
        while(serverAddr == null) {
            try {
                Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
                for (NetworkInterface netint : Collections.list(nets)) {
                    for (InterfaceAddress address : netint.getInterfaceAddresses()) {
                        if (address != null && address.getBroadcast() != null) {
                            ServerVersion.Get(address.getBroadcast());
                            Log.d(null, "Device Discovery request sent to: " + address.getBroadcast());
                            GetLocalMacAddr(payloadMac);
                        }
                    }
                }
                if (serverAddr == null)
                    Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private FBlock() throws Exception {
        // start IPC
        MsgFilter FilterServerVersionGet = new MsgFilter(FBLOCK, Contant.FUNC_SERVERVERSION,  MostMsg.OP_STATUS, this);
        MsgFilter FilterServerMediaPidGet = new MsgFilter(FBLOCK, Contant.FUNC_MEDIA_PID, MostMsg.OP_STATUS, this);
        MsgFilter FilterServerPlayStatusGet = new MsgFilter(FBLOCK, Contant.FUNC_PLAY_STATUS, MostMsg.OP_STATUS, this);
        MsgFilter FilterFolderFileListGet = new MsgFilter(FBLOCK, Contant.FUNC_FOLDER_FILE_LIST_GET, MostMsg.OP_STATUS, this);
        MsgFilter FilterBoardcastGet = new MsgFilter(FBLOCK, Contant.FUNC_VOD_BOARDCAST, MostMsg.OP_STATUS, this);
        MsgFilter FilterAdsStatusGet = new MsgFilter(FBLOCK, Contant.FUNC_ADS_FILE_PLAYSTATUS, MostMsg.OP_STATUS, this);
        MsgFilter FilterSystemTimeGet = new MsgFilter(FBLOCK, Contant.FUNC_SYSTEM_TIME_GET, MostMsg.OP_STATUS, this);
        MsgFilter FilterGpsPostionGet = new MsgFilter(FBLOCK, Contant.FUNC_GPS_POSITION_GET, MostMsg.OP_STATUS, this);

        m_Ipc = new MostIpc(MOST_UDP_PORT_RX);
        m_Ipc.RegisterMessageHandler(FilterServerVersionGet);
        m_Ipc.RegisterMessageHandler(FilterServerMediaPidGet);
		m_Ipc.RegisterMessageHandler(FilterServerPlayStatusGet);
		m_Ipc.RegisterMessageHandler(FilterFolderFileListGet);
		m_Ipc.RegisterMessageHandler(FilterBoardcastGet);
		m_Ipc.RegisterMessageHandler(FilterAdsStatusGet);
		m_Ipc.RegisterMessageHandler(FilterSystemTimeGet);
		m_Ipc.RegisterMessageHandler(FilterGpsPostionGet);

        Thread m_Thread = new Thread(this);
        m_Thread.start();
        Log.d(null, "IPC started");
    }

    public void SetContext(Context context){
    	mContext = context;
    }
    private boolean GetLocalMacAddr(byte[] buffer) {
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
        } catch (IOException ex) {
                ex.printStackTrace();
        }
        if(!TextUtils.isEmpty(macSerial)){
            String[] mStr = macSerial.split(":");
            for(int i = 0; i<mStr.length; i++){
                buffer[i] = Byte.valueOf(mStr[i], 16);
            }
            return true;
        }
        return false;
    }


    public void OnMsg(MsgAddr Addr, MostMsg Msg) {
        if (Addr == null || Msg == null) {
            Log.e(null,"FBlock.OnMsg was called with invalid parameters");
            return;
        }
        serverAddr = Addr;
        switch (Msg.GetFunc()) {
            case Contant.FUNC_SERVERVERSION:
                if (Msg.GetOpType() == MostMsg.OP_STATUS && Msg.GetPayloadLen() >= 4) {
                    Log.e(null,"Got server version "
                            + Msg.GetPayload()[0] + "."
                            + Msg.GetPayload()[1] + "."
                            + Msg.GetPayload()[2] + "."
                            + Msg.GetPayload()[3]);
                }
                break;
            case Contant.FUNC_MEDIA_PID:
                if (Msg.GetOpType() == MostMsg.OP_STATUS && Msg.GetPayloadLen() >= 6) {
                    pidsReceived = true;
                    pidVideo = Msg.GetPayload()[0] << 8 | Msg.GetPayload()[1];
                    pidAudio = Msg.GetPayload()[2] << 8 | Msg.GetPayload()[3];
                    pidPmt = Msg.GetPayload()[4] << 8 | Msg.GetPayload()[5];
                    Log.e(null,"Got Video PID: " +pidVideo + ", Audio PID:" + pidAudio
                        + ", PMT PID:" + pidPmt);
                }
                break;

            case Contant.FUNC_VOD_FILE_LIST:
            case Contant.FUNC_FOLDER_FILE_LIST_GET:
            	char[] mChar = new char[Msg.GetPayloadLen()];
            	for(int i=0 ; i<Msg.GetPayloadLen(); i++){
            		mChar[i] = (char)Msg.GetPayload()[i];
            	}
            	mFileListStr = String.copyValueOf(mChar);
            	mReadEnd = true;
            	break;

			case Contant.FUNC_PLAY_STATUS:
			case Contant.FUNC_ADS_FILE_PLAYSTATUS:
				if (Msg.GetOpType() == MostMsg.OP_STATUS && Msg.GetPayloadLen() >= 1) {
					int mOldStatus = playStatus;
					statusReceived = true;
					playStatus = Msg.GetPayload()[0];
					//Log.e("","=====return status 0 "+playStatus);
					if(mOldStatus != playStatus){
						Intent mIntent = new Intent(Contant.Actions.PLAY_STATUS_REPORT);
						mIntent.putExtra("status", playStatus);
						mContext.sendBroadcast(mIntent);
					}
				}
				break;
			case Contant.FUNC_SYSTEM_TIME_GET:
				char[] mMsg = new char[Msg.GetPayloadLen()];
            	for(int i=0 ; i<Msg.GetPayloadLen(); i++){
            		mMsg[i] = (char)Msg.GetPayload()[i];
            	}
            	String mTimeMsg = String.copyValueOf(mMsg);
            	FBlock.GetInstance().BoardcastSet.Set("com.golding.updatesystemtime"+"%#%"+mTimeMsg + "%#%");
				break;
        }
    }

    public static void ResetStatus(){
    	playStatus = -2;
    }

    public static FBlock GetInstance() {
        if (staticInstance == null)
            try {
                staticInstance = new FBlock();
            } catch (Exception e ){
                Log.d(null, "Can't get instance FBlockDemo: " + e.getMessage());
            }
        return staticInstance;
    }


    public class PlayModeImpl{

        public void Set(boolean playEnabled) {
            if (null == serverAddr) {
                Log.d(null, "Can not send, because InetAddress object is null!");
                return;
            }
            byte [] payload = new byte[MAC_ADDR_LEN + 1];

            preloadMacData(payload);
            payload[MAC_ADDR_LEN] = playEnabled ? (byte)1 : (byte)0;
            m_Ipc.SendMsg(new MostMsgTx(
                            new MsgAddr(serverAddr.GetInetAddress(), MOST_UDP_PORT_TX),
                            FBLOCK,
                            INST_VOD,
                            Contant.FUNC_PLAY_MODE,
                            MostMsg.OP_SET,
                            payload.length,
                            payload,
                            TIMEOUT_MS,
                            SendHandler,
                            null)
            );
        }
    }

    public class BoardcastSetImpl {

	    public void Set(String mStrSend) {
        if (null == serverAddr) {
        	Log.d(null, "Can not send, because InetAddress object is null!");
			return;
        }
        byte[] mStr = mStrSend.trim().getBytes();
        m_Ipc.SendMsg(new MostMsgTx(
        		new MsgAddr(serverAddr.GetInetAddress(), MOST_UDP_PORT_TX),
        		FBLOCK,
        		INST_VOD,
        		Contant.FUNC_VOD_BOARDCAST,
        		MostMsg.OP_SET,
        		mStr.length,
        		mStr,
        		TIMEOUT_MS,
        		SendHandler,
        		null)
			  );
		  }
	  }

    public class TimePosImpl{
        public boolean Set(Integer position){
            if (null == serverAddr) {
                Log.d(null, "Can not send, because InetAddress object is null!");
                return false;
            }
            boolean result = false;
            byte [] payload = new byte[MAC_ADDR_LEN + 2];

            preloadMacData(payload);
            if ( (position >= 0) && (position <= 1000) ) {
                payload[MAC_ADDR_LEN + 1] = (byte) (position / 256);
                payload[MAC_ADDR_LEN] = (byte) (position % 256);
                m_Ipc.SendMsg(new MostMsgTx(
                                new MsgAddr(serverAddr.GetInetAddress(), MOST_UDP_PORT_TX),
                                FBLOCK,
                                INST_VOD,
                                Contant.FUNC_TIME_POSITION,
                                MostMsg.OP_SET,
                                payload.length,
                                payload,
                                TIMEOUT_MS,
                                SendHandler,
                                null)
                );
                Log.d("Position", position.toString());
                result = true;
            } else {
                Log.d(null, "Invalid value for TimePos.Set:" + position);
            }
            return result;
        }
    }

    public class SelectFileImpl {

        public void Set(Contant.ContentType contentType, String FileName, String fileDetails) {
            if (null == serverAddr) {
                Log.d(null, "Can not send, because InetAddress object is null!");
                return;
            }

            byte[] fileDetailsbyte = null;
            try {
            	fileDetailsbyte = fileDetails.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
                return;
			}

            String mSendStr = FileName;

            int length = mSendStr.length() + fileDetailsbyte.length;
            Log.e("","====Send String ==="+ mSendStr + "****" + length);

            byte [] payload = new byte[MAC_ADDR_LEN + 2 + length];

            preloadMacData(payload);
            switch (contentType) {
                case audio:
                	payload[MAC_ADDR_LEN ] = 2;
                    break;
                case video:
                	payload[MAC_ADDR_LEN ] = 1;
                	break;
                default:
                	payload[MAC_ADDR_LEN ] = 0;
                    break;
            }


            for (int i = 0; i < fileDetailsbyte.length; i++)
            	payload[MAC_ADDR_LEN + i + 1] = fileDetailsbyte[i];

            for (int i = 0; i < mSendStr.length(); i++)
            	payload[MAC_ADDR_LEN + i + 1+fileDetailsbyte.length] = (byte) mSendStr.charAt(i);

            payload[MAC_ADDR_LEN+ length + 1] = 0; // Termination of String

            m_Ipc.SendMsg(new MostMsgTx(
                            new MsgAddr(serverAddr.GetInetAddress(), MOST_UDP_PORT_TX),
                            FBLOCK,
                            INST_VOD,
                            Contant.FUNC_SELECT_FILE,
                            MostMsg.OP_SET,
                            payload.length,
                            payload,
                            TIMEOUT_MS,
                            SendHandler,
                            null)
            );
        }

        public void Set2(Contant.ContentType contentType, String FileName, String fileDetails) {
            if (null == serverAddr) {
                Log.d(null, "Can not send, because InetAddress object is null!");
                return;
            }

            byte[] fileDetailsbyte = null;
            try {
            	fileDetailsbyte = fileDetails.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
                return;
			}

            String mSendStr = FileName;

            int length = mSendStr.length() + fileDetailsbyte.length;
            Log.e("","====Send String ==="+ mSendStr + "****" + length);

            byte [] payload = new byte[MAC_ADDR_LEN + 2 + length];

            preloadMacData(payload);
            switch (contentType) {
                case audio:
                	payload[MAC_ADDR_LEN ] = 2;
                    break;
                case video:
                	payload[MAC_ADDR_LEN ] = 1;
                	break;
                default:
                	payload[MAC_ADDR_LEN ] = 0;
                    break;
            }


            for (int i = 0; i < fileDetailsbyte.length; i++)
            	payload[MAC_ADDR_LEN + i + 1] = fileDetailsbyte[i];

            for (int i = 0; i < mSendStr.length(); i++)
            	payload[MAC_ADDR_LEN + i + 1+fileDetailsbyte.length] = (byte) mSendStr.charAt(i);

            payload[MAC_ADDR_LEN+ length + 1] = 0; // Termination of String

            m_Ipc.SendMsg(new MostMsgTx(
                            new MsgAddr(serverAddr.GetInetAddress(), MOST_UDP_PORT_TX),
                            FBLOCK,
                            INST_VOD,
                            Contant.FUNC_SELECT_FILE,
                            MostMsg.OP_SET,
                            payload.length,
                            payload,
                            TIMEOUT_MS,
                            SendHandler,
                            null)
            );
        }
    }

    public class SelectAdsFileImpl {

        public void Set(Contant.ContentType contentType, String FileName) {
            if (null == serverAddr) {
                Log.d(null, "Can not send, because InetAddress object is null!");
                return;
            }
            int length = FileName.length();

            byte [] payload = new byte[MAC_ADDR_LEN + 2 +length];

            preloadMacData(payload);
            switch (contentType) {
                case audio:
                	payload[MAC_ADDR_LEN ] = 2;
                    break;
                case video:
                	payload[MAC_ADDR_LEN ] = 1;
                	break;
                default:
                	payload[MAC_ADDR_LEN ] = 0;
                    break;
            }
            for (int i = 0; i < length; i++)
            	payload[MAC_ADDR_LEN + i + 1] = (byte) FileName.charAt(i);
            payload[MAC_ADDR_LEN + length + 1] = 0; // Termination of String

            m_Ipc.SendMsg(new MostMsgTx(
                            new MsgAddr(serverAddr.GetInetAddress(), MOST_UDP_PORT_TX),
                            FBLOCK,
                            INST_VOD,
                            Contant.FUNC_VOD_SELECT_ADS_FILE,
                            MostMsg.OP_SET,
                            payload.length,
                            payload,
                            TIMEOUT_MS,
                            SendHandler,
                            null)
            );
        }
    }

    public class RepetitionImpl {

        public void Set(boolean isRepeated) {
            if (null == serverAddr) {
                Log.d(null, "Can not send, because InetAddress object is null!");
                return;
            }
            byte [] payload = new byte[MAC_ADDR_LEN + 1];

            preloadMacData(payload);
            payload[MAC_ADDR_LEN] = isRepeated ? (byte)1 : (byte)0;
            m_Ipc.SendMsg(new MostMsgTx(
                            new MsgAddr(serverAddr.GetInetAddress(), MOST_UDP_PORT_TX),
                            FBLOCK,
                            INST_VOD,
                            Contant.FUNC_REPETITION,
                            MostMsg.OP_SET,
                            payload.length,
                            payload,
                            TIMEOUT_MS,
                            SendHandler,
                            null)
            );

        }
    }

	public class PlayStatusImpl {

		  public void Get() {
            if (null == serverAddr) {
				  Log.d(null, "Can not send, because InetAddress object is null!");
				  return;
            }

            m_Ipc.SendMsg(new MostMsgTx(
            		new MsgAddr(serverAddr.GetInetAddress(), MOST_UDP_PORT_TX),
            		FBLOCK,
            		INST_VOD,
            		Contant.FUNC_PLAY_STATUS,
            		MostMsg.OP_GET,
                    payloadMac.length,//0,
            		payloadMac,//null,
            		TIMEOUT_MS,
            		SendHandler,
            		null)
			  );
		  }
	  }

	public class AdsStatusImpl {

		  public void Get() {
          if (null == serverAddr) {
				  Log.d(null, "Can not send, because InetAddress object is null!");
				  return;
		  }

          m_Ipc.SendMsg(new MostMsgTx(
          		new MsgAddr(serverAddr.GetInetAddress(), MOST_UDP_PORT_TX),
          		FBLOCK,
          		INST_VOD,
          		Contant.FUNC_ADS_FILE_PLAYSTATUS,
          		MostMsg.OP_GET,
          		payloadMac.length,//0,
          		payloadMac,//null,
          		TIMEOUT_MS,
          		SendHandler,
          		null)
			  );
		  }
	  }



    public class ServerVersionImpl {

        public void Get() {
            if (null == serverAddr) {
                Log.d(null, "Can not send, because InetAddress object is null!");
                return;
            }

            m_Ipc.SendMsg(new MostMsgTx(
                            new MsgAddr(serverAddr.GetInetAddress(), MOST_UDP_PORT_TX),
                            FBLOCK,
                            INST_VOD,
                            Contant.FUNC_SERVERVERSION,
                            MostMsg.OP_GET,
                            payloadMac.length,//0,
                            payloadMac,//null,
                            TIMEOUT_MS,
                            SendHandler,
                            null)
            );
        }

        public void Get(InetAddress address) {
            if (null == address) {
                Log.d(null, "Can not send, because given InetAddress object is null!");
                return;
            }

            m_Ipc.SendMsg(new MostMsgTx(
                            new MsgAddr(address, MOST_UDP_PORT_TX),
                            FBLOCK,
                            INST_VOD,
                            Contant.FUNC_SERVERVERSION,
                            MostMsg.OP_GET,
                            payloadMac.length,//0,
                            payloadMac,//null,
                            TIMEOUT_MS,
                            SendHandler,
                            null)
            );
        }
    }

    public class MediaPidImpl {

        public boolean Get() {
            if (!pidsReceived) {
                if (null == serverAddr) {
                    Log.d(null, "Can not send, because InetAddress object is null!");
                    return false;
                }

                m_Ipc.SendMsg(new MostMsgTx(
                                new MsgAddr(serverAddr.GetInetAddress(), MOST_UDP_PORT_TX),
                                FBLOCK,
                                INST_VOD,
                                Contant.FUNC_MEDIA_PID,
                                MostMsg.OP_GET,
                                payloadMac.length,//0,
                                payloadMac,//null,
                                TIMEOUT_MS,
                                SendHandler,
                                null)
                );
                return false;
            }
            return true;
        }

		public int GetPlayStatus(){
			return statusReceived?playStatus : -1;
		}
        public int GetVideoPid()
        {
            return pidsReceived ? pidVideo : -1;
        }

        public int GetAudioPid()
        {
            return pidsReceived ? pidAudio : -1;
        }

        public int GetPmtPid()
        {
            return pidsReceived ? pidPmt : -1;
        }
    }

    public class AdsFileSetImpl {

        public boolean Set(byte channelData, String fileName) {

            if (null == serverAddr) {
                Log.d(null, "Can not send, because InetAddress object is null!");
                return false;
            }
            byte [] payload = new byte[MAC_ADDR_LEN + 2 + fileName.length()];

            preloadMacData(payload);
            payload[MAC_ADDR_LEN] = channelData;
            byte [] bFileName = fileName.trim().getBytes();
            for(int i=0 ; i<bFileName.length ; i++){
            	payload[ MAC_ADDR_LEN +1 + i ] = bFileName[i];
            }

            payload[ MAC_ADDR_LEN +1 + bFileName.length ] = 0;
            m_Ipc.SendMsg(new MostMsgTx(
            		new MsgAddr(serverAddr.GetInetAddress(), MOST_UDP_PORT_TX),
            			FBLOCK,
            			INST_VOD,
            			Contant.FUNC_ADS_FILE_SET,
            			MostMsg.OP_SET,
            			payload.length,//0,
            			payload,
            			TIMEOUT_MS,
            			SendHandler,
            			null)
            );
            return true;
        }
    }

    public class FolderFileGetImpl {

		public void Get(Contant.ContentType contentType) {
        if (null == serverAddr) {
				  Log.d(null, "Can not send, because InetAddress object is null!");
				  return;
			  }

        byte [] payload = new byte[MAC_ADDR_LEN + 1];

        preloadMacData(payload);
        switch (contentType) {
        	case video:
        		payload[MAC_ADDR_LEN] = 1;
        		break;
        	case audio:
        		payload[MAC_ADDR_LEN] = 2;
        		break;
        	case img:
        		payload[MAC_ADDR_LEN] = 3;
        		break;
        	case map:
        		payload[MAC_ADDR_LEN] = 4;
        		break;
        	case apk:
        		payload[MAC_ADDR_LEN] = 5;
        		break;
        	case xml:
        		payload[MAC_ADDR_LEN] = 6;
        		break;
        	case lrc:
        		payload[MAC_ADDR_LEN] = 7;
        		break;
        	case custom:
        		payload[MAC_ADDR_LEN] = 8;
        		break;
        	case system:
        		payload[MAC_ADDR_LEN] = 9;
        		break;
        	case music:
        		payload[MAC_ADDR_LEN] = 10;
        		break;
        	case sysdata:
        		payload[MAC_ADDR_LEN] = 11;
        		break;
        	case ebooks:
        		payload[MAC_ADDR_LEN] = 12;
        		break;
        	case windowAd:
        		payload[MAC_ADDR_LEN] = 13;
        		break;
        	case jtv:
        		payload[MAC_ADDR_LEN] = 14;
        		break;
        	case jmagazine:
        		payload[MAC_ADDR_LEN] = 15;
        		break;
        	case jmall:
        		payload[MAC_ADDR_LEN] = 16;
        		break;
        	case windowTop:
        		payload[MAC_ADDR_LEN] = 17;
        		break;
        	case windowMiddle:
        		payload[MAC_ADDR_LEN] = 18;
        		break;
        	case windowBottom:
        		payload[MAC_ADDR_LEN] = 19;
        		break;
        	case windowCrossbar:
        		payload[MAC_ADDR_LEN] = 20;
        		break;
        	case windowText:
        		payload[MAC_ADDR_LEN] = 21;
        		break;
        	case guangdongTv:
        		payload[MAC_ADDR_LEN] = 22;
        		break;
        	case evenAd:
        		payload[MAC_ADDR_LEN] = 23;
        		break;
        	case famGodGrass:
        		payload[MAC_ADDR_LEN] = 24;
        		break;
        	case financialEye:
        		payload[MAC_ADDR_LEN] = 25;
        		break;
        	case orangeFlavor:
        		payload[MAC_ADDR_LEN] = 26;
        		break;
        	case securityVideo:
        		payload[MAC_ADDR_LEN] = 27;
        		break;
        	case seaWhale:
        		payload[MAC_ADDR_LEN] = 28;
        		break;
        	case bingYan:
        		payload[MAC_ADDR_LEN] = 29;
        		break;
        	default:
        		payload[MAC_ADDR_LEN] = 0;
        		break;
        }

        m_Ipc.SendMsg(new MostMsgTx(
        		new MsgAddr(serverAddr.GetInetAddress(), MOST_UDP_PORT_TX),
        		FBLOCK,
        		INST_VOD,
        		Contant.FUNC_FOLDER_FILE_LIST_GET,
        		MostMsg.OP_GET,
        		payload.length,
        		payload,
        		TIMEOUT_MS,
        		SendHandler,
        		null)
			  );
		  }
	  }

    public class SelectConfigImpl {

        public void Set(String FileName) {
            if (null == serverAddr) {
                Log.d(null, "Can not send, because InetAddress object is null!");
                return;
            }

            int length = FileName.length();
            byte[] payload = new byte[length + 1];
            for (int i = 0; i < length; i++)
                payload[i] = (byte) FileName.charAt(i);
            payload[length] = 0; // Termination of String

            m_Ipc.SendMsg(new MostMsgTx(
                            new MsgAddr(serverAddr.GetInetAddress(), MOST_UDP_PORT_TX),
                            FBLOCK,
                            INST_VOD,
                            Contant.FUNC_NM_EXECUTE_CONFIG,
                            MostMsg.OP_SET,
                            payload.length,
                            payload,
                            TIMEOUT_MS,
                            SendHandler,
                            null)
            );
        }
    }


    public class StopVodStreamImpl {

        public void Set() {
            if (null == serverAddr) {
                Log.d(null, "Can not send, because InetAddress object is null!");
                return;
            }

            m_Ipc.SendMsg(new MostMsgTx(
                            new MsgAddr(serverAddr.GetInetAddress(), MOST_UDP_PORT_TX),
                            FBLOCK,
                            INST_VOD,
                            Contant.FUNC_STOP_STREAM,
                            MostMsg.OP_SET,
                            payloadMac.length,
                            payloadMac,
                            TIMEOUT_MS,
                            SendHandler,
                            null)
            );
        }
    }

    public class StopAdsStreamImpl {

        public void Set() {
            if (null == serverAddr) {
                Log.d(null, "Can not send, because InetAddress object is null!");
                return;
            }
            Log.e("","====> Send Stop Ads Stream commmand");
            m_Ipc.SendMsg(new MostMsgTx(
            		new MsgAddr(serverAddr.GetInetAddress(), MOST_UDP_PORT_TX),
            		FBLOCK,
            		INST_VOD,
            		Contant.FUNC_STOP_ADS_STREAM,
            		MostMsg.OP_SET,
            		payloadMac.length,
            		payloadMac,
            		TIMEOUT_MS,
            		SendHandler,
            		null)
            );
        }
    }

    public class GetSystemTimeImpl {

        public void Get() {
            if (null == serverAddr) {
                Log.d(null, "Can not send, because InetAddress object is null!");
                return;
            }

            m_Ipc.SendMsg(new MostMsgTx(
            		new MsgAddr(serverAddr.GetInetAddress(), MOST_UDP_PORT_TX),
            		FBLOCK,
            		INST_VOD,
            		Contant.FUNC_SYSTEM_TIME_GET,
            		MostMsg.OP_GET,
            		payloadMac.length,//0,
            		payloadMac,//null,
            		TIMEOUT_MS,
            		SendHandler,
            		null)
            );
        }
    }

    public class GetGpsPositionImpl {
        public void Get() {
            if (null == serverAddr) {
                Log.d(null, "Can not send, because InetAddress object is null!");
                return;
            }

            m_Ipc.SendMsg(new MostMsgTx(
            		new MsgAddr(serverAddr.GetInetAddress(), MOST_UDP_PORT_TX),
            		FBLOCK,
            		INST_VOD,
            		Contant.FUNC_GPS_POSITION_GET,
            		MostMsg.OP_GET,
            		payloadMac.length,//0,
            		payloadMac,//null,
            		TIMEOUT_MS,
            		SendHandler,
            		null)
            );
        }
    }

    public class SendHandler implements MostMsgTx.IMessageSent {
        public void OnMessageSent(boolean bSucceeded, MostMsgTx Msg)
        {
            if(null != serverAddr && !bSucceeded)
            {
                try {
                    serverAddr = new MsgAddr(InetAddress.getByName("10.0.0.255"), MOST_UDP_PORT_TX);
                } catch (UnknownHostException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

}
