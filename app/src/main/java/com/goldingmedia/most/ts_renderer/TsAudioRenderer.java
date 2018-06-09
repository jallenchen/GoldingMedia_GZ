package com.goldingmedia.most.ts_renderer;

import android.media.MediaCodec;
import android.media.MediaFormat;

import com.goldingmedia.utils.NLog;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;


/*----------------------------------------------------------*/
/*!
 * \brief  renders a compressed audio stream
 */
/*----------------------------------------------------------*/
class TsAudioRenderer extends Thread {
    private int m_nSampleRateOut = 0;
    private int m_nChannelsOut = 0;
    private final ConcurrentLinkedQueue<TsSample> m_SampleQueue = new ConcurrentLinkedQueue<>();
   
    private TsSample m_CurTsSample = null;
    private MediaCodec m_DecAudio = null;
    private final MediaCodec.BufferInfo m_PendingOutInfoAudio = new MediaCodec.BufferInfo();
    private byte[] m_BufPcm = null;
    private int m_BufPcmLen = 0;
    private TsAudioSink m_Sink = null;
    private boolean mPause = false;
    private long mCount = 0;
    private int mCount2 = 0;
    /*----------------------------------------------------------*/
	/*! \brief construct a audio renderer
	 */
	/*----------------------------------------------------------*/
    public TsAudioRenderer(TsAudioSink Sink) {
        m_Sink = Sink;
    }


    public void setPause(boolean pause){
    	mPause = pause;
    }
    /*----------------------------------------------------------*/
	/*! \brief add a sample to the queue
	 *
	 *  \param s - complete sample
	 */
	/*----------------------------------------------------------*/
    void AddSample(TsSample Sample) { if ( null != Sample ) m_SampleQueue.add(Sample); }




    /*----------------------------------------------------------*/
	/*! \brief sets playback rate (1.0 normal, 1.05 means 5%
	 *         faster, ...)
	 */
	/*----------------------------------------------------------*/
    void setPlaybackRate(float fPlaybackRate) { m_Sink.AudioSink_SetPlaybackRate(fPlaybackRate); }




    /*----------------------------------------------------------*/
	/*! \brief returns current playback rate (1.0 normal,
	 *         1.05 means 5% faster, ...)
	 */
	/*----------------------------------------------------------*/
    float getPlaybackRate() { return m_Sink.AudioSink_GetPlaybackRate(); }




    /*----------------------------------------------------------*/
	/*! \brief closes audio output and releases resources
	 */
	/*----------------------------------------------------------*/
    private void Close() {
        if ( null != m_DecAudio ) {
            m_DecAudio.release();
            m_DecAudio = null;
        }
    }




    /*----------------------------------------------------------*/
	/*! \brief creates a audio codec fitting to a given elementary
	 *         stream packet
	 *
	 *  \param s - complete elementary stream packet
	 */
	/*----------------------------------------------------------*/
    private boolean CreateAudioCodec(TsSample s) {
        if ( !s.GetHasAudioFormat() ) {
            NLog.d(null, "TS: waiting for sample containing audio format");
            return false;
        }

        if ( (1 != s.GetAudioChannelCount()) && (2 != s.GetAudioChannelCount()) ) {
            NLog.e("TsAudioRenderer", "TS: unsupported number of audio channels");
            return false;
        }

        m_nChannelsOut = s.GetAudioChannelCount();
        m_nSampleRateOut = s.GetAudioSampleRate();

        MediaFormat Format = MediaFormat.createAudioFormat(s.GetMimeType(), s.GetAudioSampleRate(), s.GetAudioChannelCount());
        if (s.GetMimeType() == "audio/mp4a-latm") {
            Format.setInteger(MediaFormat.KEY_IS_ADTS, 1);
            Format.setByteBuffer("csd-0", s.GetEsds());
        }
        NLog.d("TsAudioRenderer", "Format: " + Format);

        try {
            m_DecAudio = MediaCodec.createDecoderByType(s.GetMimeType());
            m_DecAudio.configure(Format, null, null, 0);
            m_DecAudio.start();
            m_Sink.AudioSink_Start(s.GetAudioChannelCount(),s.GetAudioSampleRate(),16,(s.GetPts()/45)*1000);
        } catch (Exception e) {
            NLog.e("TsAudioRenderer", "TS: failed to create audio codec. " + Format);
            Close();
            return false;
        }

        return true;
    }




    /*----------------------------------------------------------*/
	/*! \brief get number of pending TS samples in buffer
	 */
	/*----------------------------------------------------------*/
    public int GetNumPendingSamples() { return m_SampleQueue.size(); }




    /*----------------------------------------------------------*/
	/*! \brief pushes a complete elementary stream packet into
	 *         audio decoder
	 *
	 *  \param s - complete elementary stream packet
	 *
	 *  \return true if sample was consumed
	 */
	/*----------------------------------------------------------*/
    private boolean PushAudioSampleIntoCodec(TsSample s) {
        //
        // create a codec if necessary
        //
        if ( null == m_DecAudio ) {
            CreateAudioCodec(s);

            if ( null == m_DecAudio )
                return true;
        }

        //
        // write sample into decoder memory
        //
        int i;
        try {
            i = m_DecAudio.dequeueInputBuffer(0);
        } catch (Exception e){
            e.printStackTrace();
            m_DecAudio.release();
            m_DecAudio = null;
            return true;
        }

        if (i >= 0)
        {
            ByteBuffer buffer = m_DecAudio.getInputBuffers()[i];

            buffer.clear();
            buffer.put(s.GetArray(), 0, s.GetSize());

            try {
                m_DecAudio.queueInputBuffer(i, 0, s.GetSize(), (s.GetPts() / 45) * 1000, 0);
            } catch (Exception e){
                e.printStackTrace();
                m_DecAudio.release();
                m_DecAudio = null;
                return true;
            }

            return true;
        }

        return false;
    }



    /*----------------------------------------------------------*/
	/*! \brief make a short pause
	 */
	/*----------------------------------------------------------*/
    private void Sleep() {
        try {
            sleep(1);
        } catch (InterruptedException e) {
            interrupt();
        }
    }




    /*----------------------------------------------------------*/
	/*! \brief decoder & renderer thread
	 */
	/*----------------------------------------------------------*/
    @Override
    public void run() {
        ByteBuffer OutputBuffer;
        while ( !isInterrupted() ) {
        	if( !mPause ){
            //
            // service audio sink
            //
            if (m_Sink.AudioSink_Service()) {
	            mCount++;
			} else {
				if (mCount < 300) {
		            if (mCount2 == 30) {
						NLog.e("","-----mCount2-30");
		            //	Utils.mContext.sendBroadcast(new Intent("com.golding.nullTsExit"));
					}
					NLog.e("","-----mCount = "+mCount2);
					mCount2++;
				} else {
					mCount2 = 0;
				}
	            mCount = 0;
			}

            //
            // get next sample to be decoded
            //
            if ( null == m_CurTsSample )
                m_CurTsSample = m_SampleQueue.poll();

            if ( null != m_CurTsSample ) {
                //
                // in case of a discontinuity we throw away all samples
                //
                if ( m_CurTsSample.GetDiscontinuity()  ) {
                    NLog.d(null, "TS: dump buffered audio sample after discontinuity");
                    while ( null != m_CurTsSample ) {
                        TsSample.Recycle(m_CurTsSample);
                        m_CurTsSample = m_SampleQueue.poll();
                    }
                }
                else if ( PushAudioSampleIntoCodec(m_CurTsSample) ) {
                    TsSample.Recycle(m_CurTsSample);
                    m_CurTsSample = null;
                }
                else
                    Sleep();
            }
            else
                Sleep();

            //
            // decode compressed audio
            //
            if ( null != m_DecAudio ) {
                try {
                    int outIndex = m_DecAudio.dequeueOutputBuffer(m_PendingOutInfoAudio, 0);
                    if (0 <= outIndex) {
                        if (0 < m_PendingOutInfoAudio.size) {
                            OutputBuffer = m_DecAudio.getOutputBuffers()[outIndex];
                            if (m_BufPcmLen < m_PendingOutInfoAudio.size) {
                                m_BufPcmLen = m_PendingOutInfoAudio.size;
                                m_BufPcm = new byte[m_PendingOutInfoAudio.size];
                            }
                            OutputBuffer.get(m_BufPcm, 0, m_PendingOutInfoAudio.size);
                            m_Sink.AudioSink_PcmOut(m_BufPcm, m_PendingOutInfoAudio.size,m_PendingOutInfoAudio.presentationTimeUs);
                        }
                        m_DecAudio.releaseOutputBuffer(outIndex, false);
                    } else if (MediaCodec.INFO_OUTPUT_FORMAT_CHANGED == outIndex) {
                        try {
                            MediaFormat format = m_DecAudio.getOutputFormat();
                            if ( m_nChannelsOut   != format.getInteger(MediaFormat.KEY_CHANNEL_COUNT) ||
                                 m_nSampleRateOut != format.getInteger(MediaFormat.KEY_SAMPLE_RATE) ) {
                                NLog.d(null, "TS: audio output format changed");
                                Close();
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                            Close();
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    Close();
                }
            }
}
        }

        //
        // release used resources
        //
        Close();
    }




    /*----------------------------------------------------------*/
	/*! \brief stop audio rendering
	 */
	/*----------------------------------------------------------*/
    public synchronized void Stop() {
        NLog.d(null,"TS: stop AudioRenderer");
        interrupt();
        try {
            join();
        } catch (Exception ignored) { }
        if (null != m_Sink)
            m_Sink.AudioSink_Stop();
        NLog.d(null,"TS: stopped AudioRenderer");
    }
}
