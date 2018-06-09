package com.goldingmedia.most.ts_renderer;

import android.view.Surface;

import com.goldingmedia.utils.NLog;

import java.net.DatagramPacket;


/*----------------------------------------------------------*/
/*!
 * \brief  receives a transport stream from a character
 *         device, finds sub streams for audio and video and
 *         plays the sub streams
 */
/*----------------------------------------------------------*/
public class TsReceiver extends TsPlayer implements Runnable {
    private static final boolean SHOW_DEBUG_OUTPUT = false;
    private int m_nTsPackets = 0;
    private Thread m_Thread = null;
    private boolean mPause = false;
    private final TsSource m_TsSource;
    private long m_tLastTimer = 0;
    private long m_tLastCycle = 0;
    private long m_nMaxThreadCycleTime = 0;




    /*----------------------------------------------------------*/
	/*! \brief construct a TS receiver
	 */
	/*----------------------------------------------------------*/
    public TsReceiver(TsAudioSink AudioSink, TsSource Source) {
        super(AudioSink);
        m_TsSource = Source;
    }

    public void SetPause(boolean pause){
    	mPause = pause;
    	super.setPause(pause);
    }



    /*----------------------------------------------------------*/
	/*! \brief packets received since last time calling this
	 *         function
	 */
	/*----------------------------------------------------------*/
    public int GetNumPackets() {
        int nRet = m_nTsPackets;
        m_nTsPackets = 0;
        return nRet;
    }




    /*----------------------------------------------------------*/
	/*! \brief packets received since last time calling this
	 *         function
	 */
	/*----------------------------------------------------------*/
    public boolean GetIsRunning() {return (null != m_Thread);}




    /*----------------------------------------------------------*/
	/*! \brief prints out statistics
	 */
	/*----------------------------------------------------------*/
    void TraceInfos() {
        if ( false == SHOW_DEBUG_OUTPUT )
            return;

        final long tNow = System.nanoTime();
        final long tElapsedTimer = tNow-m_tLastTimer;
        final long tElapsedCycle = tNow-m_tLastCycle;

        // catch longest thread cycle time
        if ( 0 != m_tLastCycle && tElapsedCycle > m_nMaxThreadCycleTime )
            m_nMaxThreadCycleTime = tElapsedCycle;

        m_tLastCycle = tNow;

        // only trace every 30 seconds
        if (tElapsedTimer < 30000000000L)
            return;

        long nRead = GetNumPackets()*188;
        long nBw = (nRead*8) / (tElapsedTimer/1000/1000);

        String debug =
                " APid: 0x" + Integer.toHexString(GetAudioPid()) +
                        " VPid: 0x" + Integer.toHexString(GetVideoPid()) +
                        " ErrSync: " + GetErrSync() +
                        " ErrDisc: " + GetErrDiscontinuity() +
                        " ErrPtsVideo: " + GetErrPtsVideo() +
                        " ErrRenderedLate: " + GetErrRenderedLate() +
                        " Max Cycle Time [ms]: " + (m_nMaxThreadCycleTime / 1000000) +
                        " Bw [kbps]: " + nBw;

        NLog.d("TsReceiver","TS: " + debug);

        m_tLastTimer = tNow;
    }




    /*----------------------------------------------------------*/
	/*! \brief receiving thread
	 */
	/*----------------------------------------------------------*/
    @Override
    public void run() {
        byte[] pTs = new byte[TsPacket.LENGTH];
        DatagramPacket dp;
        int nPackets;
        int offset = 0;

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        while (!m_Thread.isInterrupted()) {
        	if(!mPause){
        		TraceInfos();
	            try {
	                dp = m_TsSource.TsSource_Read();
	                if ( null == dp )
	                    continue;
	                byte[] data = dp.getData();
	                int len = dp.getLength();
	                if ( !TsPacket.GetHasSyncByte(data,offset) ) {
	                    offset = TsPacket.GetSyncByteOffset(data,len);
	                    if (offset >= 0) {
	                        NLog.e("", "Stream alignment changed to: " + offset);
	                    } else {
	                        continue;
	                    }
	                }

	                if (offset == 0) {
	                    nPackets = len / 188;
	                    m_nTsPackets += nPackets;
	                    for (int i = 0; i < nPackets; i++) {
	                        System.arraycopy(data, i * 188, pTs, 0, 188);
	                        PushTs(pTs);
	                    }
	                } else {
	                    System.arraycopy(data, 0, pTs, (188 - offset), offset);
	                    PushTs(pTs);
	                    nPackets = len / 188;
	                    m_nTsPackets += nPackets;
	                    for (int i = 0; i < (nPackets - 1); i++) {
	                        System.arraycopy(data, offset + (i * 188), pTs, 0, 188);
	                        PushTs(pTs);
	                    }
	                    System.arraycopy(data, offset + ((nPackets - 1) * 188), pTs, 0, (188 - offset));
	                }
	            }catch (Exception ex) {
	                ex.printStackTrace();
	            }
          }
        }
    }
    
    /*----------------------------------------------------------*/
	/*! \brief starts receiver
	 *
	 * \param VideoSurface - Surface, video has to be rendered to
	 * \param UdpSock - Socket to receive stream from
	 */
	/*----------------------------------------------------------*/
    public boolean Start(Surface VideoSurface) {
        return Start(VideoSurface,TsPacket.PID_INVALID,TsPacket.PID_INVALID);
    }




    /*----------------------------------------------------------*/
	/*! \brief starts receiver
	 *
	 * \param VideoSurface - Surface, video has to be rendered to
	 * \param UdpSock - Socket to receive stream from
	 */
	/*----------------------------------------------------------*/
    public boolean Start(Surface VideoSurface, int nAPid, int nVPid) {
        NLog.d(null, "TS: start receiver");
        Stop();                                             // stop if already running
        Start(nVPid, nAPid, VideoSurface);                  // start player
        m_Thread = new Thread(this);                        // start reading from source
        m_Thread.start();
        return true;
    }

    public void ChangePid(int aPid, int vPid)
    {
    	PlayerChangePid(aPid,vPid);
    }

    /*----------------------------------------------------------*/
	/*! \brief starts receiver
	 *
	 * \param see above
	 */
	/*----------------------------------------------------------*/

    public synchronized void stopThread(){
    	if(m_Thread != null){
    		Thread moribund = m_Thread;
    		m_Thread.interrupt();
    		m_Thread = null;
    		moribund.interrupt();

    	}
    }


    /*----------------------------------------------------------*/
	/*! \brief stop received
	 */
	/*----------------------------------------------------------*/
    public void Stop() {
        if ( null == m_Thread )
            return;

        NLog.d(null, "TS: stop receiver");
        m_Thread.interrupt();                               // inform thread to stop execution

        NLog.d(null, "TS: stop receiver1");
        try {                                               // wait until thread is terminated
            m_Thread.join();
        } catch (Exception ignored) { }

        NLog.d(null, "TS: stop receiver2");
        super.Stop();                                       // stop player
        m_Thread = null;
        NLog.d(null, "TS: stopped receiver");
    }

    public void Close() {
		NLog.e("","====exitActivity  Close()1");
    	m_TsSource.Close_Read();
		NLog.e("","====exitActivity  Close()2");
    }
}