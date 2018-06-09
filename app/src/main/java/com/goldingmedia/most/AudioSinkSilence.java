package com.goldingmedia.most;


import com.goldingmedia.most.ts_renderer.TsAudioSink;

/**
 * Created by Thorsten Kummermehr on 03.12.15.
 */
public class AudioSinkSilence implements TsAudioSink {
    private long m_nPtsOffset;
    private long m_tAudioSinkStart;
    private static AudioSinkSilence staticInstance = null;

    protected AudioSinkSilence() {}
    public static AudioSinkSilence GetInstance(){
        if (staticInstance == null)
            staticInstance = new AudioSinkSilence();
        return staticInstance;
    }

    @Override
    public void AudioSink_Start(int nChannels, int nSamplesPerSec, int nBitsPerSample, long nPts) {
        m_nPtsOffset = nPts;
        m_tAudioSinkStart = System.nanoTime() + ((long)2*(long)1000*(long)1000*(long)1000);
    }

    @Override
    public void AudioSink_Stop() {}

    @Override
    public long AudioSink_GetPresentationTime() {
        long l = System.nanoTime() - m_tAudioSinkStart;
        return ( (l)/1000L) + m_nPtsOffset;
    }

    @Override
    public void AudioSink_SetPlaybackRate(float fPlaybackRate) {
    }

    @Override
    public float AudioSink_GetPlaybackRate() {
        return 1.0f;
    }

    @Override
    public void AudioSink_PcmOut(byte[] Pcm, int nLen, long nPts) {
        //
        // update PTS tracking
        //
        m_nPtsOffset = nPts;
        m_tAudioSinkStart = System.nanoTime();
    }

    @Override
    public boolean AudioSink_Service() {
    	return true;
    }

    /*----------------------------------------------------------*/
	/*! \brief receive updated PTS from Master player
	 */
	/*----------------------------------------------------------*/
    public void OnPtsUpdate(long pts) {
        m_nPtsOffset = pts;
        m_tAudioSinkStart = System.nanoTime();
    }
}
