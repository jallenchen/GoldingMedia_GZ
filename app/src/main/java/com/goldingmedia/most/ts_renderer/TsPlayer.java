package com.goldingmedia.most.ts_renderer;

import android.view.Surface;

import com.goldingmedia.utils.NLog;


/*----------------------------------------------------------*/
/*!
 * \brief  Play a transport stream containing audio and video
 */
/*----------------------------------------------------------*/
public class TsPlayer {
    private static final boolean AUTO_PID_SEARCH = false;
    private static final int SAMPLES_BUFFERED_MAX = 200;
    private static final int SAMPLES_BUFFERED_HIGH = (SAMPLES_BUFFERED_MAX/100)*80;
    private static final int SAMPLES_BUFFERED_MID  = (SAMPLES_BUFFERED_MAX/100)*50;
    private static final int SAMPLES_BUFFERED_LOW  = (SAMPLES_BUFFERED_MAX/100)*35;
    private static final float PLAYBACK_RATE_HIGH = 1.01f;
    private static final float PLAYBACK_RATE_MID  = 1.00f;
    private static final float PLAYBACK_RATE_LOW  = 0.99f;
    private TsElementaryStream m_EsAudio = new TsElementaryStream();
    private TsElementaryStream m_EsVideo = new TsElementaryStream();
    private TsVideoRenderer m_VideoRenderer;
    private TsAudioRenderer m_AudioRenderer;
    private Surface surface;
    private int m_nPmtPid;
    private boolean m_threadStarted = false;
    private TsAudioSink mAudioSink;



    /*----------------------------------------------------------*/
	/*! \brief constructs a player instance
	 *
	 *  \param nVPid - PID of video
	 *  \param nAPid - PID of audio
	 *  \param Surface - surface, the video is rendered to
	 */
	/*----------------------------------------------------------*/
    public TsPlayer(TsAudioSink AudioSink) {
    	mAudioSink = AudioSink;
        m_AudioRenderer = new TsAudioRenderer(AudioSink);
        m_VideoRenderer = new TsVideoRenderer(AudioSink);
    }




    /*----------------------------------------------------------*/
	/*! \brief gets number of stream discontinuities
	 */
	/*----------------------------------------------------------*/
    public int GetErrDiscontinuity() {
        return m_EsAudio.GetErrDiscontinuity() + m_EsVideo.GetErrDiscontinuity();
    }

    /*----------------------------------------------------------*/
	/*! \brief gets number of PTS discontinuities in video
	 */
	/*----------------------------------------------------------*/
    public int GetErrPtsVideo() {
        return m_VideoRenderer.GetErrPts();
    }

    /*----------------------------------------------------------*/
	/*! \brief gets number of video frames rendered too late
	 */
	/*----------------------------------------------------------*/
    public int GetErrRenderedLate() {
        return m_VideoRenderer.GetErrRenderedLate();
    }

    /*----------------------------------------------------------*/
	/*! \brief gets number of errors due to missing sync byte
	 */
	/*----------------------------------------------------------*/
    public int GetErrSync() {
        return m_EsAudio.GetErrSync() + m_EsVideo.GetErrSync();
    }

    /*----------------------------------------------------------*/
	/*! \brief gets number of TS samples pending for rendering
	 */
	/*----------------------------------------------------------*/
    public int GetNumPendingSamples() { return m_AudioRenderer.GetNumPendingSamples() + m_VideoRenderer.GetNumPendingSamples(); }

    /*----------------------------------------------------------*/
	/*! \brief gets number of audio TS samples pending for rendering
	 */
	/*----------------------------------------------------------*/
    public int GetNumPendingSamplesAudio() { return m_AudioRenderer.GetNumPendingSamples();}

    /*----------------------------------------------------------*/
	/*! \brief gets number of video TS samples pending for rendering
	 */
	/*----------------------------------------------------------*/
    public int GetNumPendingSamplesVideo() { return m_VideoRenderer.GetNumPendingSamples();}




    /*----------------------------------------------------------*/
	/*! \brief gets currently buffered decoded audio ready for
	 *         rendering in ms
	 */
	/*----------------------------------------------------------*/
    public int GetFrameWaitMs() { return m_VideoRenderer.getFrameWaitMs(); }

    /*----------------------------------------------------------*/
	/*! \brief get audio PID
	 */
	/*----------------------------------------------------------*/
    public int GetAudioPid() {return m_EsAudio.GetPid();}

    /*----------------------------------------------------------*/
	/*! \brief get video PID
	 */
	/*----------------------------------------------------------*/
    public int GetVideoPid() {return m_EsVideo.GetPid();}



    /*----------------------------------------------------------*/
	/*! \brief adds received data to the elementary stream
	 *
	 *  \param pTs - 188 byte transport stream packet
	 */
	/*----------------------------------------------------------*/
    public void PushTs(byte[] pTs) {

        //Avoid parsing of stuffing packets
        if (TsPacket.GetIsStuffing(pTs)) {
            return;
        }
        //
        // Control play rate. The strategy is:
        // - is the buffer getting full, play faster
        // - if mid of buffer is reached, play normal
        // - is buffer running dry, play slower
        //
        int pendingSamples = GetNumPendingSamples();
        if ( SAMPLES_BUFFERED_HIGH < pendingSamples )
            m_AudioRenderer.setPlaybackRate(PLAYBACK_RATE_HIGH);

        if ( SAMPLES_BUFFERED_MID > pendingSamples &&
             SAMPLES_BUFFERED_LOW < pendingSamples &&
             PLAYBACK_RATE_MID <  m_AudioRenderer.getPlaybackRate() )
            m_AudioRenderer.setPlaybackRate(PLAYBACK_RATE_MID);

        if ( SAMPLES_BUFFERED_LOW > pendingSamples )
            m_AudioRenderer.setPlaybackRate(PLAYBACK_RATE_LOW);

        //
        // if the player is flooded, wait before reading again from character device
        //
        if (SAMPLES_BUFFERED_MAX < pendingSamples ) {
            NLog.e("TsPlayer", "TS: receive buffer overflow " + pendingSamples);
            m_AudioRenderer.setPlaybackRate(PLAYBACK_RATE_HIGH);
           // return;
        }

        //
        // if PIDs for audio and video are not set, get them
        // from stream's PAT and PMT
        //
        if (AUTO_PID_SEARCH) {
            if (TsPacket.PID_INVALID == m_EsAudio.GetPid() && TsPacket.PID_INVALID == m_EsVideo.GetPid()) {
                if (TsPacket.GetIsPat(pTs)) {
                    m_nPmtPid = TsPacket.GetPmtPidFromPat(pTs, 0);
                } else if (TsPacket.GetHasPid(pTs, m_nPmtPid)) {
                    m_EsAudio.SetPid(TsPacket.GetAudioPidFromPmt(pTs));
                    m_EsVideo.SetPid(TsPacket.GetVideoPidFromPmt(pTs));
                }
                return;
            }
        }

        //
        // push data into elementary streams, to get back samples (=NAL units)
        // which are pushed into the decoders
        //
        m_VideoRenderer.AddSample(m_EsVideo.PushTs(pTs));
        m_AudioRenderer.AddSample(m_EsAudio.PushTs(pTs));
    }




    /*----------------------------------------------------------*/
	/*! \brief starts renderer
	 */
	/*----------------------------------------------------------*/
    public void Start(int nVPid, int nAPid, Surface surf) {
    	surface = surf;
        m_EsVideo.SetPid(nVPid);

        m_EsAudio.SetPid(nAPid);
        if (!m_threadStarted)
        {
            m_threadStarted = true;
            m_VideoRenderer.Start(surf);
            m_AudioRenderer.start();
        }
    }

    public void PlayerChangePid(int aPid, int vPid)
    {
    	m_VideoRenderer.Stop();
    	m_AudioRenderer.Stop();
    	m_VideoRenderer = new TsVideoRenderer(mAudioSink);
    	m_AudioRenderer = new TsAudioRenderer(mAudioSink);
    	m_EsAudio = new TsElementaryStream();
    	m_EsVideo = new TsElementaryStream();
    	m_EsVideo.SetPid(vPid);
        m_VideoRenderer.Start(surface);
        m_EsAudio.SetPid(aPid);
        m_AudioRenderer.start();
    }


    /*----------------------------------------------------------*/
	/*! \brief stops renderer
	 */
	/*----------------------------------------------------------*/
    public synchronized void Stop() {
        m_VideoRenderer.Stop();
        m_AudioRenderer.Stop();
    }
    
    public void setPause(boolean pause){
    	
    	m_VideoRenderer.setPause(pause);
        m_AudioRenderer.setPause(pause);
    	
    }
    
    
}
