package com.goldingmedia.most.ts_renderer;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import com.goldingmedia.utils.NLog;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;


/*----------------------------------------------------------*/
/*!
 * \brief  Decode and render Elementary Stream Packets
 *         (=Samples) to a surface
 */
/*----------------------------------------------------------*/
class TsVideoRenderer extends Thread {
    private static final boolean RESTART_CODEC_AFTER_DISCONT = true;
    private static final long MAX_PTS_JITTER = 500 * 1000;
    private static final long MAX_PT_DIFF = 6000;
    private final ConcurrentLinkedQueue<TsSample> m_SampleQueue = new ConcurrentLinkedQueue<>();
    private TsSample m_CurTsSample = null;
    private MediaCodec m_DecVideo = null;
    private Surface m_Surface;
    private final MediaCodec.BufferInfo m_PendingOutInfoVideo = new MediaCodec.BufferInfo();
    private int m_nErrPts = 0;
    private int m_nErrLate = 0;
    private int m_nWaitMs = 0;
    private int m_nNumFramesRendered = 0;
    private long m_nPtsOffs = 0;
    private final TsAudioSink m_AudioSink;
    private boolean m_threadStarted = false;
    private long m_nLastPts;
    private boolean mPause = false;


    /*----------------------------------------------------------*/
	/*! \brief construct a video renderer
	 */
	/*----------------------------------------------------------*/
    public TsVideoRenderer(TsAudioSink AudioSink) {
        m_AudioSink = AudioSink;
    }
    public void setPause(boolean pause){
    	mPause = pause;
    }
    public int getFrameWaitMs() { return m_nWaitMs; }

    /*----------------------------------------------------------*/
	/*! \brief add a sample to the queue
	 *
	 *  \param s - complete sample
	 */
	/*----------------------------------------------------------*/
    void AddSample(TsSample Sample) { if ( null != Sample ) m_SampleQueue.offer(Sample); }




    /*----------------------------------------------------------*/
	/*! \brief creates a video codec fitting to a given elementary
	 *         stream packet
	 *
	 *  \param s - complete elementary stream packet
	 */
	/*----------------------------------------------------------*/
    private boolean CreateVideoCodec(TsSample s) throws IOException {
        if ( !s.GetHasVideoFormat() ) {
            NLog.d(null, "TS: waiting for sample containing video format");
            return false;
        }
        final MediaFormat Format = MediaFormat.createVideoFormat(s.GetMimeType(), s.GetVideoWidth(), s.GetVideoHeigth());
        Format.setByteBuffer("csd-0", s.GetSps());
        m_DecVideo = MediaCodec.createDecoderByType(s.GetMimeType());
        m_nLastPts = 0;
        try {
            m_DecVideo.configure(Format, m_Surface, null, 0);
            m_DecVideo.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
            NLog.d(null, "TS: m_DecVideo.configure() IllegalArgumentException");
		}

        return true;
    }




    /*----------------------------------------------------------*/
	/*! \brief get number of pending samples in buffer
	 */
	/*----------------------------------------------------------*/
    public int GetNumPendingSamples() { return m_SampleQueue.size(); }




    /*----------------------------------------------------------*/
	/*! \brief get number of rendered frames
	 */
	/*----------------------------------------------------------*/
    public int GetNumFramesRendered() { return m_nNumFramesRendered; }




    /*----------------------------------------------------------*/
	/*! \brief get number of frames rendered too late
	 */
	/*----------------------------------------------------------*/
    public int GetErrRenderedLate() { return m_nErrLate; }




    /*----------------------------------------------------------*/
	/*! \brief get number of frames with PTS difference to last
	 *         frame bigger than MAX_PT_DIFF
	 */
	/*----------------------------------------------------------*/
    public int GetErrPts() { return m_nErrPts; }




    /*----------------------------------------------------------*/
	/*! \brief pushes a complete elementary stream packet into
	 *         video decoder
	 *
	 *  \param s - complete elementary stream packet
	 *  \return true, if sample was consumed
	 *          false if sample should be pushed in later
	 */
	/*----------------------------------------------------------*/
    private boolean PushVideoSampleIntoCodec(TsSample s) {
        //
        // create a codec if necessary
        //
        if ( null == m_DecVideo ) {
            try {
				CreateVideoCodec(s);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            if ( null == m_DecVideo )
                return true;
        }

        //
        // write sample into decoder memory
        //
        int i = 0;
        try {
            i = m_DecVideo.dequeueInputBuffer(0);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (i >= 0)
        {
            final ByteBuffer InputByteBuffer = m_DecVideo.getInputBuffers()[i];

            InputByteBuffer.clear();
            InputByteBuffer.put(s.GetArray(), 0, s.GetSize());

            m_DecVideo.queueInputBuffer(i, 0, s.GetSize(), (s.GetPts() / 45) * 1000, 0);
            return true;
        }
        else if ( MediaCodec.INFO_TRY_AGAIN_LATER != i)
            NLog.d(null,"TS: video dequeueInputBuffer failed with " + i );

        return false;
    }




    /*----------------------------------------------------------*/
	/*! \brief make a short pause
	 */
	/*----------------------------------------------------------*/
    private void Sleep(int ms) {
        try {
            sleep(ms);
        } catch (InterruptedException ignored) {
            interrupt();
        }
    }




    /*----------------------------------------------------------*/
	/*! \brief decoder & renderer thread
	 */
	/*----------------------------------------------------------*/
    @Override
    public void run() {
        int nIdx = 0;
        long nPtAudio, tDiff, tFrm, tSys;


        setPriority(Thread.MAX_PRIORITY);

        while ( !isInterrupted() ) {
            //
            // decode
            //
        	if(!mPause){
            if ( null == m_CurTsSample ) {
                m_CurTsSample = m_SampleQueue.poll();
            }

            if ( null != m_CurTsSample ) {
                if ( RESTART_CODEC_AFTER_DISCONT && m_CurTsSample.GetDiscontinuity() ) {
                    while (null != m_CurTsSample) {
                        TsSample.Recycle(m_CurTsSample);
                        m_CurTsSample = m_SampleQueue.poll();
                    }
                    if ( null != m_DecVideo ) {
                        m_DecVideo.release();
                        m_DecVideo = null;
                    }
                }
                else if ( PushVideoSampleIntoCodec(m_CurTsSample) ) {
                    TsSample.Recycle(m_CurTsSample);
                    m_CurTsSample = null;
                }
                else
                    Sleep(1);
            }
            else
                Sleep(1);

            //
            // render video
            if ( null != m_DecVideo ){
                try {
                    nIdx = m_DecVideo.dequeueOutputBuffer(m_PendingOutInfoVideo,0);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if ( 0 <= nIdx) {
                    tDiff = m_PendingOutInfoVideo.presentationTimeUs - m_nLastPts;
                    if ( (0 != m_nLastPts) &&
                         ( ((0 <= tDiff) && ( tDiff > MAX_PTS_JITTER)) ||
                           ((0 >  tDiff) && (-tDiff > MAX_PTS_JITTER))) ) {
                        NLog.d(null,"TS: video PTS Jitter: " + tDiff);
                        m_DecVideo.releaseOutputBuffer(nIdx, true);
                        m_nNumFramesRendered++;
                        m_nLastPts = m_PendingOutInfoVideo.presentationTimeUs;
                    } else {
                        m_nLastPts = m_PendingOutInfoVideo.presentationTimeUs;
                        if (null != m_AudioSink) {
                            nPtAudio = m_AudioSink.AudioSink_GetPresentationTime();
                            if (0 != nPtAudio) {
                                m_nWaitMs = (int) ((m_PendingOutInfoVideo.presentationTimeUs - nPtAudio) / 1000);
                                if (0 > m_nWaitMs) {
                                    m_nErrLate++;
                                } else if (m_nWaitMs > MAX_PT_DIFF)
                                    m_nErrPts++;
                                else if (0 < m_nWaitMs)
                                    Sleep(m_nWaitMs - 1);
                            } else {
                                tFrm = m_PendingOutInfoVideo.presentationTimeUs;
                                tSys = (System.nanoTime() / 1000) - m_nPtsOffs;
                                m_nWaitMs = (int) ((tFrm - tSys) / 1000);

                                if ((m_nWaitMs < 0 && m_nWaitMs < -500) || (m_nWaitMs >= 0 && m_nWaitMs > 500)) {
                                    m_nPtsOffs = (System.nanoTime() / 1000) - tFrm;
                                } else if (0 < m_nWaitMs) {
                                    Sleep(m_nWaitMs - 1);
                                }
                            }

                            m_DecVideo.releaseOutputBuffer(nIdx, true);
                            m_nNumFramesRendered++;
                        }
                    }
                }
                else if ( MediaCodec.INFO_TRY_AGAIN_LATER != nIdx)
                    NLog.d(null,"TS: video dequeueOutputBuffer failed with " + nIdx );
            }
        }
        }

        if ( null != m_DecVideo ) {
            m_DecVideo.release();
            m_DecVideo = null;
        }
    }




    /*----------------------------------------------------------*/
	/*! \brief start video rendering
	 */
	/*----------------------------------------------------------*/
    public void Start(Surface surf) {
        m_Surface = surf;
        if (!m_threadStarted) {
            m_threadStarted = true;
            start();
        }
    }



    /*----------------------------------------------------------*/
	/*! \brief stop video rendering
	 */
	/*----------------------------------------------------------*/
    public synchronized void Stop() {
        NLog.d(null, "TS: stop VideoRenderer");
        interrupt();
        try {
            join();
        } catch (Exception ignored) { }
        NLog.d(null, "TS: stopped VideoRenderer");
    }
}
