package com.goldingmedia.most;

import com.goldingmedia.utils.NLog;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


/*----------------------------------------------------------*/
/*!
 * \brief  outputs pcm data to a MOST amplifier
 */
/*----------------------------------------------------------*/
public class AudioSinkMostAmp extends AudioSinkSilence {
    private static AudioSinkMostAmp staticInstance = null;
    private static final ArrayList<String> m_DevListAmp = new ArrayList<>();
    private FileOutputStream m_CharDevPcmOut = null;
    private byte[] alignBuffer = new byte[512];
    private int bufferPos = 0;




    /*----------------------------------------------------------*/
	/*! \brief get the single instance
	 */
	/*----------------------------------------------------------*/
    public static AudioSinkMostAmp GetInstance()
    {
        if (null == staticInstance)
            staticInstance = new AudioSinkMostAmp();
        return staticInstance;
    }




    /*----------------------------------------------------------*/
	/*! \brief constructor
	 */
	/*----------------------------------------------------------*/
    private AudioSinkMostAmp() {
        super();
        //AMP CDEVs for D-REV USB INIC
        for (int i = 0; i < 5; i++)
            m_DevListAmp.add("/dev/mdev" + i + "-ep04");
        //AMP CDEVs for C-REV USB INIC
        for (int i = 0; i < 5; i++)
            m_DevListAmp.add("/dev/mdev" + i + "-ep0a");
        //AMP CDEVs for MLB INIC
        for (int i = 0; i < 5; i++)
            m_DevListAmp.add("/dev/mdev" + i + "-ca14");
    }




    /*----------------------------------------------------------*/
	/*! \brief called by TS renderer to initialize audio sink
	 */
	/*----------------------------------------------------------*/
    @Override
    public void AudioSink_Start(int nChannels, int nSamplesPerSec, int nBitsPerSample, long nPts) {
        super.AudioSink_Start(nChannels, nSamplesPerSec, nBitsPerSample, nPts);

        //
        // open PCM output channel on MOST
        //
        Iterator<String> it = m_DevListAmp.iterator();

        for (; (null == m_CharDevPcmOut) && (it.hasNext()); ) {
            String szCharDev = it.next();
            if (new java.io.File(szCharDev).exists()) {
                try {
                    NLog.d(null, "SP: Opening PCM out char dev: '" + szCharDev + "'");
                    m_CharDevPcmOut = new FileOutputStream(szCharDev);
                    break;
                } catch (Exception e) {
                    NLog.e("", "SP: unable to open PCM output: " + szCharDev + ", message:" + e.getMessage());
                    m_CharDevPcmOut = null;
                }
            }
        }
    }




    /*----------------------------------------------------------*/
	/*! \brief called by TS renderer to stop audio sink
	 */
	/*----------------------------------------------------------*/
    @Override
    public void AudioSink_Stop() {
        if (null != m_CharDevPcmOut) {
            //Send silence before closing
            for (int i = 0; i < alignBuffer.length; i++)
                alignBuffer[i] = 0;

            try {
                m_CharDevPcmOut.write(alignBuffer, 0, alignBuffer.length);
                m_CharDevPcmOut.close();
                m_CharDevPcmOut = null;
            } catch (IOException e) {
                NLog.e("", "SP: failed to output PCM");
            }
        }
    }


    /*----------------------------------------------------------*/
	/*! \brief called by TS renderer to hand over a chunk of
	 *         PCM audio
	 */
	/*----------------------------------------------------------*/
    @Override
    public void AudioSink_PcmOut(byte[] Pcm, int nLen, long nPts) {
        super.AudioSink_PcmOut(Pcm, nLen, nPts);
        if ( null == m_CharDevPcmOut )
            return;

        //
        // push PCM data into MOST channel, make sure to send always 512 Byte with one write call
        //
        int offset = 0;
        do {
            while ((bufferPos < alignBuffer.length) && (offset < nLen)) {
                //Also swap byte order (little to big endian)
                alignBuffer[bufferPos+1] = Pcm[offset++];
                alignBuffer[bufferPos] = Pcm[offset++];
                bufferPos += 2;
            }

            if (bufferPos < alignBuffer.length)
                break;
            bufferPos = 0;

            //
            // writing PCM data out to MOST char device
            //
            try {
                m_CharDevPcmOut.write(alignBuffer, 0, alignBuffer.length);
            } catch (IOException e) {
                NLog.e("","SP: failed to output PCM");
            }
        } while (offset < nLen);
    }
}