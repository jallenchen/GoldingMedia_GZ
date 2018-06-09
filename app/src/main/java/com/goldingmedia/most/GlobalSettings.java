package com.goldingmedia.most;


import com.goldingmedia.GDApplication;
import com.goldingmedia.most.ts_renderer.TsAudioSink;
import com.goldingmedia.most.ts_renderer.TsSource;

public class GlobalSettings {
    public static final boolean CdevEnabled = true;
    public static final boolean UdpEnabled = false;
    public static final boolean UdpUsesMulticast = true;
    public static final String UdpMulticastChannel = "239.255.42.98";

    //May be adjusted by customer:
    public static TsAudioSink GetAudioSink()
    {
        //return TsAudioSinkMostAmp.GetInstance();
        return AudioSinkLocal.GetInstance();
    }

    public static TsSource GetSource()
    {
        return SourceMostUdp.GetInstance(GDApplication.getmInstance());
    }
}
