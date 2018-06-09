package com.goldingmedia.most;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import com.goldingmedia.utils.NLog;

/*----------------------------------------------------------*/
/*!
 * \brief  outputs pcm data to local sound card
 */
/*----------------------------------------------------------*/
public class AudioSinkLocal extends AudioSinkSilence {
    private static final int PCM_BUFFER_SIZE_SEC = 1;
    private static final long PREROLL_MS = 300;
    private static AudioSinkLocal staticInstance = null;
    private AudioTrack m_AudioTrack = null;
    private int m_nHeadPosOverflow = 0;
    private long m_nLastHeadPos = 0;
    private float m_fPlaybackRateIs = 1.0f;
    private float m_fPlaybackRateReq = 1.0f;
    private byte[] m_PcmSilence = null;
    private int m_nPcmSilenceLen = 0;
    private long m_nPcmBytesWritten = 0;
    private long m_nPtsOffs = 0;
    private int m_nSamplesPerSec = 0;
    private int m_nBitsPerSample = 0;
    private int m_nChannels = 0;




    /*----------------------------------------------------------*/
	/*! \brief constructor
	 */
	/*----------------------------------------------------------*/
    private AudioSinkLocal() {
        super();
    }

    /*----------------------------------------------------------*/
	/*! \brief get the single instance
	 */
	/*----------------------------------------------------------*/
    public static AudioSinkLocal GetInstance()
    {
        if (null == staticInstance)
            staticInstance = new AudioSinkLocal();
        return staticInstance;
    }

    /*----------------------------------------------------------*/
	/*! \brief start audio output
	 */
	/*----------------------------------------------------------*/
    @Override
    public void AudioSink_Start(int nChannels, int nSamplesPerSec, int nBitsPerSample, long nPts) {
        super.AudioSink_Start(nChannels, nSamplesPerSec, nBitsPerSample, nPts);
        //
        // change audio output format?
        //
        if ( (m_nSamplesPerSec != nSamplesPerSec) || (m_nBitsPerSample != nBitsPerSample) || (m_nChannels != nChannels) )
            AudioSink_Stop();

        //
        // start audio output with given format
        //
        if ( null == m_AudioTrack ) {
            NLog.d(null,"TS: create local audio track");

            if ( 0 == nChannels || 0 == nSamplesPerSec || 0 == nBitsPerSample) {
                NLog.e("","TS: invalid local audio output format");
                return;
            }

            m_nChannels = nChannels;
            m_nSamplesPerSec = nSamplesPerSec;
            m_nBitsPerSample = nBitsPerSample;

            int nChannelConfig = (2 == nChannels) ? AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_OUT_MONO;
            int nBufSize = nSamplesPerSec * nChannels * nBitsPerSample / 8 * PCM_BUFFER_SIZE_SEC;
            m_nPcmSilenceLen = (int)(PREROLL_MS * nSamplesPerSec * nChannels * nBitsPerSample / 8) / 1000;
            m_PcmSilence = new byte[m_nPcmSilenceLen];

            for (int i=0; i<m_nPcmSilenceLen; i++)
                m_PcmSilence[i]=0;

            m_AudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,nSamplesPerSec,nChannelConfig,AudioFormat.ENCODING_PCM_16BIT,nBufSize,AudioTrack.MODE_STREAM);
            m_AudioTrack.write(m_PcmSilence,0,m_nPcmSilenceLen);
            m_nPcmBytesWritten = m_nPcmSilenceLen;
            m_nHeadPosOverflow = 0;
            m_nLastHeadPos = 0;

            try {
                m_AudioTrack.play();
            }
            catch (IllegalStateException e) {
                NLog.e("","TS: failed to open local audio device, message:" + e.getMessage());
                e.printStackTrace();
                AudioSink_Stop();
            }
        }
    }




    /*----------------------------------------------------------*/
	/*! \brief stop audio output and releases resources
	 */
	/*----------------------------------------------------------*/
    @Override
    public void AudioSink_Stop() {
        if ( null != m_AudioTrack ) {
            NLog.d(null, "TS: stopped local audio output");
            m_AudioTrack.release();
            m_AudioTrack = null;
        }
    }




    /*----------------------------------------------------------*/
	/*! \brief get current presentation time in us
	 */
	/*----------------------------------------------------------*/
    @Override
    public long AudioSink_GetPresentationTime() {
        return null == m_AudioTrack ? 0 : m_nPtsOffs + (System.nanoTime() / 1000);
    }




    /*----------------------------------------------------------*/
	/*! \brief sets playback rate (1.0 normal, 1.05 means 5%
	 *         faster, ...)
	 */
	/*----------------------------------------------------------*/
    @Override
    public void AudioSink_SetPlaybackRate(float fPlaybackRate) {
        m_fPlaybackRateReq = fPlaybackRate;
    }




    /*----------------------------------------------------------*/
	/*! \brief returns current playback rate (1.0 normal,
	 *         1.05 means 5% faster, ...)
	 */
	/*----------------------------------------------------------*/
    @Override
    public float AudioSink_GetPlaybackRate() {
        return m_fPlaybackRateIs;
    }




    /*----------------------------------------------------------*/
	/*! \brief add PCM data for rendering
	 */
	/*----------------------------------------------------------*/
    @Override
    public void AudioSink_PcmOut(byte[] Pcm, int nLen, long nPts) {
        super.AudioSink_PcmOut(Pcm, nLen, nPts);
        if ( null == m_AudioTrack || null == Pcm || 0 == nLen )
            return;

        //
        // enqueue PCM data for rendering
        //
        if ( nLen != m_AudioTrack.write(Pcm,0,nLen) ) {
            NLog.d(null,"TS: failed to write complete audio sample PCM");
            AudioSink_Stop();
            return;
        }
        m_nPcmBytesWritten += nLen;

        //
        // re-adjust PTS clock
        //
        long nPtsPlayback = (GetPlaybackHeadPosition() * 1000000L) / m_nSamplesPerSec;
        long nPtsWritten  = (m_nPcmBytesWritten/(m_nBitsPerSample/8)/m_nChannels*1000000L)/m_nSamplesPerSec;
        long nPtsSystem   = (System.nanoTime() / 1000L);

        m_nPtsOffs = nPts + (nPtsPlayback - nPtsWritten) - nPtsSystem;
    }




    /*----------------------------------------------------------*/
	/*! \brief service sound card
	 */
	/*----------------------------------------------------------*/
    public boolean AudioSink_Service() {
        //
        // check if audio playback buffer is running empty
        //
    	boolean ret = true;
        if ( null != m_AudioTrack ) {
            long nBufferedAudioTime = ((m_nPcmBytesWritten / (m_nBitsPerSample/8) / m_nChannels) - GetPlaybackHeadPosition());
            nBufferedAudioTime = (nBufferedAudioTime *1000)/m_nSamplesPerSec;
            if (nBufferedAudioTime <= PREROLL_MS) {
                NLog.d(null, "TS: audio running dry, ms left: " + (int)nBufferedAudioTime);
                m_AudioTrack.write(m_PcmSilence, 0,m_nPcmSilenceLen);
                m_nPcmBytesWritten += m_nPcmSilenceLen;
                ret = false;
            }

            if ( m_fPlaybackRateIs != m_fPlaybackRateReq ) {
                m_fPlaybackRateIs = m_fPlaybackRateReq;
                int nSampleRate = (int) (m_nSamplesPerSec * m_fPlaybackRateIs);
                m_AudioTrack.setPlaybackRate(nSampleRate);
                NLog.d(null, "TS: change play rate to " + m_fPlaybackRateIs + " ,Sps: " + nSampleRate);
            }
        }
        return ret;
    }




    /*----------------------------------------------------------*/
	/*! \brief get current playback head position in samples
	 */
	/*----------------------------------------------------------*/
    private long GetPlaybackHeadPosition() {
        if ( null == m_AudioTrack )
            return 0;

        //
        // head position returned by android becomes negative after
        // 3 hours and overflows after 21 hours. To avoid loss of
        // snyc, overflows are tracked
        //
        long lNewHeadPos = 0xFFFFFFFFL & m_AudioTrack.getPlaybackHeadPosition();
        if ( lNewHeadPos < m_nLastHeadPos )
            m_nHeadPosOverflow++;

        m_nLastHeadPos = lNewHeadPos;

        return  lNewHeadPos + (0xFFFFFFFFL * m_nHeadPosOverflow);
    }
}
