package com.goldingmedia.most;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.goldingmedia.most.ts_renderer.TsSource;
import com.goldingmedia.utils.NLog;


import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Iterator;


/*----------------------------------------------------------*/
/*!
 * \brief  reads Transport Stream from CDEV or a UDP socket
 */
/*----------------------------------------------------------*/
public class SourceMostUdp implements TsSource {
    ArrayList<String> DevList = new ArrayList<>();
    private Context context;
    public static final int CHARDEV_READBUF_SIZE = 188 * 40;
    private static final int UDP_PORT_TS = 1234;
    private static SourceMostUdp staticInstance = null;
    static final DatagramPacket m_UdpPacket = new DatagramPacket(new byte[CHARDEV_READBUF_SIZE], CHARDEV_READBUF_SIZE);
    private DatagramSocket m_UdpUniTsIn = null;
    private MulticastSocket m_UdpMultiTsIn = null;
    private FileInputStream m_CharDevTsIn = null;




    /*----------------------------------------------------------*/
	/*! \brief get the single instance
	 */
	/*----------------------------------------------------------*/
    public static SourceMostUdp GetInstance(Context ct)
    {
        if (null == staticInstance)
            staticInstance = new SourceMostUdp(ct);
        return staticInstance;
    }


    public void SetContext(Context c) {
        context = c.getApplicationContext();
    }


    /*----------------------------------------------------------*/
	/*! \brief constructor
	 */
	/*----------------------------------------------------------*/
    private SourceMostUdp(Context ct) {
        context = ct.getApplicationContext();
        //USB attached devices
        for (int i = 0; i < 5; i++)
            DevList.add("/dev/mdev" + i + "-ep83");

        //MLB attached devices
        for (int i = 0; i < 5; i++)
            DevList.add("/dev/mdev" + i + "-ca12");
    }

    public void Close_Read() {
        if ( null != m_CharDevTsIn) {
            try {
				m_CharDevTsIn.close();
	            m_CharDevTsIn = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }


    /*----------------------------------------------------------*/
	/*! \brief TS renderer triggers to read next chunk of stream
	 *         data
	 */
	/*----------------------------------------------------------*/
    @Override
    public DatagramPacket TsSource_Read() {

        if ( null != m_CharDevTsIn) {
            //
            // read from file
            //
            try {
                int nRead = m_CharDevTsIn.read(m_UdpPacket.getData(), 0, CHARDEV_READBUF_SIZE);

                if ( 0 == nRead )
                    return null;
                else if (nRead < 0)
                {
                    m_CharDevTsIn.close();
                    m_CharDevTsIn = null;
                    return null;
                }

                if ( nRead != CHARDEV_READBUF_SIZE)
                    m_UdpPacket.setLength(nRead);

                return m_UdpPacket;
            } catch (IOException e) {
                try {
                    m_CharDevTsIn.close();
                } catch (IOException ignored) {}

                m_CharDevTsIn = null;
                e.printStackTrace();
                return null;
            }
        }
        else if ( null != m_UdpUniTsIn || null != m_UdpMultiTsIn ) {
            //
            // read from socket
            //
            try {
				if (GlobalSettings.UdpUsesMulticast)
					m_UdpMultiTsIn.receive(m_UdpPacket);
				else
					m_UdpUniTsIn.receive(m_UdpPacket);
                return m_UdpPacket;
            } catch (IOException e) {
                NLog.e("", "SP: unable to receive TS via UDP");
                e.printStackTrace();
                try {
                    Thread.sleep(1, 0);
                } catch (Exception ignored) {
                }
                return null;
            }
        }
        else
        {
            //
            // try to open one of the listed input character devices
            //
            Iterator<String> it = DevList.iterator();

            for (; GlobalSettings.CdevEnabled && (null == m_CharDevTsIn) && (it.hasNext()); ) {
                String szCharDev = it.next();
                if (new java.io.File(szCharDev).exists()) {
                    try {
                        NLog.d(null, "SP: Opening TS in char dev: '" + szCharDev + "'");
                        m_CharDevTsIn = new FileInputStream(szCharDev);
                        break;
                    } catch (Exception e) {
                        NLog.e("", "SP: unable to open file: " + szCharDev + ", message:" + e.getMessage());
                        m_CharDevTsIn = null;
                    }
                }
            }

            //
            // try to open UDP socket to receive stream
            //
            if (GlobalSettings.UdpEnabled && null == m_CharDevTsIn) {
                NLog.d(null, "SP: open UDP socket to receive TS");
                if (GlobalSettings.UdpUsesMulticast) {
                    NLog.d(null, "SP: open UDP mutlicast socket to receive TS");
                    if (context == null)
                    {
                        NLog.d(null, "SP: Can not open UDP mutlicast socket without context, see code!");
                        return null;
                    }
                    try {
                        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                        if (wifi != null){
                            WifiManager.MulticastLock lock = wifi.createMulticastLock("mylock");
                            lock.acquire();
                        }
                        m_UdpMultiTsIn = new MulticastSocket(UDP_PORT_TS);
                        m_UdpMultiTsIn.joinGroup(InetAddress.getByName("239.255.42.99"));
                        m_UdpMultiTsIn.setReceiveBufferSize(5 * 1024 * 1024 / 8);
                    } catch (IOException e) {
                        NLog.e("", "SP: unable to open UDP unicast socket " + UDP_PORT_TS);
                    }
                } else {
                try {
                        m_UdpUniTsIn = new DatagramSocket(UDP_PORT_TS);
                        m_UdpUniTsIn.setReceiveBufferSize(5 * 1024 * 1024 / 8);
                } catch (IOException e) {
                    NLog.e("", "SP: unable to open unicast UDP socket " + UDP_PORT_TS);
                    }
                }
            }
        }
        return null;
    }
}
