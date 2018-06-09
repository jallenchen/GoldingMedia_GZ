package com.goldingmedia.most.ts_renderer;

public interface TsAudioSink {
    void AudioSink_Start(int nChannels, int nSamplesPerSec, int nBitsPerSample, long nPts);
    void AudioSink_Stop();
    long  AudioSink_GetPresentationTime();
    void AudioSink_SetPlaybackRate(float fPlaybackRate);
    float AudioSink_GetPlaybackRate();
    void AudioSink_PcmOut(byte[] Pcm, int nLen, long nPts);
    boolean AudioSink_Service();
}
