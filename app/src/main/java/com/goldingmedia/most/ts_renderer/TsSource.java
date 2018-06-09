package com.goldingmedia.most.ts_renderer;

import android.content.Context;

import java.net.DatagramPacket;

public interface TsSource {
    DatagramPacket TsSource_Read();
    void SetContext(Context c);
    void Close_Read();
}
