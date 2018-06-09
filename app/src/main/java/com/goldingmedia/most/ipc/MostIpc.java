package com.goldingmedia.most.ipc;


import com.goldingmedia.utils.NLog;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

/*----------------------------------------------------------*/
/*!
 * \brief  Inter Process Communication
 *         1. sends messages using MOST protocol over UDP
 *         2. receives messages and forwards them to their
 *            handlers
 */
/*----------------------------------------------------------*/
public class MostIpc extends Thread {
    private final ConcurrentLinkedQueue<MostMsgTx> m_MsgTxQueue;
    private final ConcurrentLinkedQueue<MsgFilter> m_MsgFilterQueue;
    private DatagramSocket m_Socket;




    /*----------------------------------------------------------*/
	/*! \brief constructs Inter Process Communication
	 */
	/*----------------------------------------------------------*/
    public MostIpc(int nPort) throws SocketException {
        m_MsgTxQueue = new ConcurrentLinkedQueue<>();
        m_MsgFilterQueue = new ConcurrentLinkedQueue<>();
        m_Socket = new DatagramSocket();
        m_Socket.setBroadcast(true);
        m_Socket.setReuseAddress(true);
        if (!m_Socket.getReuseAddress()){
            NLog.d(null, "This platform does not support reusing UDP sockets.");
        }
        this.start();
    }




    /*----------------------------------------------------------*/
	/*! \brief send a MOST message
	 */
	/*----------------------------------------------------------*/
    public void SendMsg(MostMsgTx Msg){
        m_MsgTxQueue.offer(Msg);
    }




    /*----------------------------------------------------------*/
	/*! \brief adds a message handler
	 */
	/*----------------------------------------------------------*/
    public void RegisterMessageHandler(MsgFilter Filter) {
        m_MsgFilterQueue.offer(Filter);
    }




    /*----------------------------------------------------------*/
	/*! \brief removes a message handler
	 */
	/*----------------------------------------------------------*/
    public void UnregisterMessageHandler(MsgFilter Filter) {
        m_MsgFilterQueue.remove(Filter);
    }




    /*----------------------------------------------------------*/
	/*! \brief stop received
	 */
	/*----------------------------------------------------------*/
    public void Stop() {
        interrupt();
        try {
            join();
        } catch (Exception ignored) { }
    }




    /*----------------------------------------------------------*/
	/*! \brief handle a received message
	 */
	/*----------------------------------------------------------*/
    private void OnMsgReceived(DatagramPacket Packet) {
        MsgAddr Addr = new MsgAddr(Packet.getAddress(),Packet.getPort());
        byte[] data = Packet.getData();
        MostMsg msg = new MostMsg();
        for (int i = 0; data != null && i < Packet.getLength(); i++) {
            if (!msg.Parse(data[i]))
            {
                NLog.d(null, "OnMsgReceived parsing UDP datagram failed");
                break;
            }
            if (msg.IsValid()) {
                for (MsgFilter aM_MsgFilterQueue : m_MsgFilterQueue) {
                    aM_MsgFilterQueue.Filter(Addr, msg);
                }
                msg = new MostMsg();
            }
        }
    }




    /*----------------------------------------------------------*/
	/*! \brief sending/receiving thread
	 */
	/*----------------------------------------------------------*/
    @Override
    public void run() {
        MostMsgTx MsgTx;
        DatagramPacket PacketRx = new DatagramPacket(new byte[MostMsg.MAX_PAYLOAD_LENGTH],MostMsg.MAX_PAYLOAD_LENGTH);

        try {
            m_Socket.setSoTimeout(10);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

        while (!isInterrupted()) {
            //
            // try to send
            //
            MsgTx = m_MsgTxQueue.poll();
            if ( null != MsgTx ) {
                DatagramPacket PacketTx = new DatagramPacket(MsgTx.ToByteArray(),MsgTx.ToByteArrayLen(),MsgTx.GetAddr().GetInetAddress(),MsgTx.GetAddr().GetPort());
                try {
                    m_Socket.send(PacketTx);
                    MsgTx.MsgSent(true);
                    
                    if(MsgTx.GetFunc() == 13)
                    	NLog.e("MostIpc", "===>Msg sent: " + MsgTx.GetFunc());
                } catch (IOException e) {
                    e.printStackTrace();
                    MsgTx.MsgSent(false);
                }
            }
            //
            // try to receive
            //
            try {
                m_Socket.receive(PacketRx);
                OnMsgReceived(PacketRx);
            } catch (IOException ignored) {
            }
            try {
                sleep(10);
            } catch (InterruptedException ignored) {
                return;
            }
        }
    }
}
