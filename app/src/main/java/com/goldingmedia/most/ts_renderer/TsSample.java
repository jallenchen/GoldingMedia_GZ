package com.goldingmedia.most.ts_renderer;

import com.goldingmedia.utils.NLog;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;


/*----------------------------------------------------------*/
/*!
 * \brief  Holds a elementary stream packet (=sample)
 */
/*----------------------------------------------------------*/
public class TsSample {
    private static final int FREXT_HP   		= 100;
    private static final int FREXT_Hi10P   		= 110;
    private static final int FREXT_Hi422   		= 122;
    private static final int FREXT_Hi444   		= 244;
    private static final int FREXT_CAVLC444		= 44;
    private static final int YUV444 			= 3;
    private static final int MAX_SAMPLE_SIZE 	= (256*1024);
    private enum MIME_TYPE {AUDIO_MPEG_L1,AUDIO_MPEG_L2,AUDIO_MPEG,VIDEO_AVC,AUDIO_AAC,UNKNOWN}

    private static final String[] MIME_NAMES = {"audio/mpeg-L1", "audio/mpeg-L2", "audio/mpeg", "video/avc", "audio/mp4a-latm"};
    private static final ConcurrentLinkedQueue<TsSample> m_OldBuffers = new ConcurrentLinkedQueue<>();

    private long m_nPts;
    private int m_nWidth;
    private int m_nHeight;
    private int m_nSpsLen;
    private int m_nAudioSampleRate;
    private int m_nAudioChannels;
    private byte[] m_Esds;
    private boolean m_bDiscontinuity;
    private MIME_TYPE m_nMime;
    private final ByteBuffer m_ByteBuffer = ByteBuffer.allocate(MAX_SAMPLE_SIZE);
    private TsBitStream m_BitStream = new TsBitStream();




    /*----------------------------------------------------------*/
	/*! \brief constructs a new elementary stream packet
	 *
	 *  \param nMaxSize - maximum size of sample in bytes
	 */
	/*----------------------------------------------------------*/
    private TsSample() {
        SetEmpty();
    }




    /*----------------------------------------------------------*/
	/*! \brief creates a new sample instamce
	 */
	/*----------------------------------------------------------*/
    public static TsSample CreateSample() {
        TsSample s = m_OldBuffers.poll();

        if ( null != s ) {
            s.SetEmpty();
            return s;
        }

        return new TsSample();
    }




    /*----------------------------------------------------------*/
	/*! \brief hands back unused sample for re-usage
	 */
	/*----------------------------------------------------------*/
    static public void Recycle(TsSample s) { if ( null != s) m_OldBuffers.offer(s); }




    /*----------------------------------------------------------*/
	/*! \brief gets the sample's data array
	 *
	 * \return byte array containing sample data
	 */
	/*----------------------------------------------------------*/
    public byte[] GetArray() { return m_ByteBuffer.array(); }




    /*----------------------------------------------------------*/
	/*! \brief get number of audio channels
	 *
	 * \return number of audio channels
	 */
	/*----------------------------------------------------------*/
    public int GetAudioChannelCount() {
        return m_nAudioChannels;
    }




    /*----------------------------------------------------------*/
	/*! \brief get number of audio samples per second
	 *
	 * \return number of audio samples per second
	 */
	/*----------------------------------------------------------*/
    public int GetAudioSampleRate() {
        return m_nAudioSampleRate;
    }




    /*----------------------------------------------------------*/
	/*! \brief gets ESDS byte array (valid in case of AAC audio)
	 *
	 * \return ByteBuffer containing ESDS if available,
	 *         null otherwise
	 */
	/*----------------------------------------------------------*/
    public ByteBuffer GetEsds() {
        if (MIME_TYPE.AUDIO_AAC != m_nMime)
            return null;

        return ByteBuffer.wrap(m_Esds);
    }




    /*----------------------------------------------------------*/
	/*! \brief tells if sample is first after a discontinuity
	 *
	 * \return true if a discontinuity occurred
	 */
	/*----------------------------------------------------------*/
    public boolean GetDiscontinuity() { return m_bDiscontinuity; }




    /*----------------------------------------------------------*/
	/*! \brief extracts sample's video format
	 *
	 * \return true if video format was found
	 */
	/*----------------------------------------------------------*/
    public boolean GetHasAudioFormat() {
        //
        // check for audio header sync word
        //
        m_BitStream.setSource(m_ByteBuffer.array(),0);

        int sync_word = m_BitStream.getU(12);

        if ( 0xFFF != sync_word )
            return false;

        int id          = m_BitStream.getU(1);
        int layer       = m_BitStream.getU(2);
        int protection  = m_BitStream.getU(1);

        if (1 == id)        // MPEG-2  (usually MP3)
        {
            int bit_rate    = m_BitStream.getU(4);
            int sample_freq = m_BitStream.getU(2);
            int padding     = m_BitStream.getU(1);
            int priv        = m_BitStream.getU(1);
            int mode        = m_BitStream.getU(2);

            m_nMime =
                    (3 == layer) ? MIME_TYPE.AUDIO_MPEG_L1 :
                    (2 == layer) ? MIME_TYPE.AUDIO_MPEG_L2 :
                    (1 == layer) ? MIME_TYPE.AUDIO_MPEG    : MIME_TYPE.UNKNOWN;

            m_nAudioSampleRate =
                    (0 == sample_freq) ? 44100 :
                    (1 == sample_freq) ? 48000 :
                    (2 == sample_freq) ? 32000 : 0;

            m_nAudioChannels =
                    (3 == mode) ? 1 : 2;
        }
        else                // MPEG-4  (usually AAC)
        {
            int profile     = m_BitStream.getU(2);
            int srate       = m_BitStream.getU(4);
                              m_BitStream.getU(1);
            int channel     = m_BitStream.getU(3);

            m_nMime         = MIME_TYPE.AUDIO_AAC;

            m_nAudioSampleRate =
                    ( 0 == srate) ? 96000 :
                    ( 1 == srate) ? 88200 :
                    ( 2 == srate) ? 64000 :
                    ( 3 == srate) ? 48000 :
                    ( 4 == srate) ? 44100 :
                    ( 5 == srate) ? 32000 :
                    ( 6 == srate) ? 24000 :
                    ( 7 == srate) ? 22050 :
                    ( 8 == srate) ? 16000 :
                    ( 9 == srate) ? 12000 :
                    (10 == srate) ? 11025 :
                    (11 == srate) ?  8000 :
                    (12 == srate) ?  7350 : 0;

            m_nAudioChannels =
                    (1 == channel) ? 1 :
                    (2 == channel) ? 2 : 0;

            m_Esds = new byte[] {
                    (byte) (profile << 4 | srate >> 1),
                    (byte) ((srate & 0x01) << 7 | channel << 3) };
        }

        return true;
    }



    /*----------------------------------------------------------*/
	/*! \brief gets stream format as mime string
	 *
	 * \return mime type as string
	 */
	/*----------------------------------------------------------*/
    String GetMimeType() {
        return MIME_TYPE.AUDIO_MPEG_L1 == m_nMime ? MIME_NAMES[0] :
               MIME_TYPE.AUDIO_MPEG_L2 == m_nMime ? MIME_NAMES[1] :
               MIME_TYPE.AUDIO_MPEG    == m_nMime ? MIME_NAMES[2] :
               MIME_TYPE.VIDEO_AVC     == m_nMime ? MIME_NAMES[3] :
               MIME_TYPE.AUDIO_AAC     == m_nMime ? MIME_NAMES[4] : null;
    }




    /*----------------------------------------------------------*/
	/*! \brief extracts sample's video format
	 *
	 * \return true if video format was found
	 */
	/*----------------------------------------------------------*/
    public boolean GetHasVideoFormat() {
        //
        // find NAL unit containing SPS(=sequence parameter set)
        //
        int i;
        for (i=0; i<m_ByteBuffer.position()-3; i++)
            if ( 0 == m_ByteBuffer.get(i) && 0 == m_ByteBuffer.get(i+1) && 1 == m_ByteBuffer.get(i+2) && 0x67 == m_ByteBuffer.get(i+3) )
                break;

        if ( i >= m_ByteBuffer.position()-3 )
            return false;

        i += 4;

        //
        // decode SPS
        //
        m_BitStream.setSource(m_ByteBuffer.array(), i);

        int profile_idc = m_BitStream.getU(8);				// profile_idc
        m_BitStream.getU(1);								// constraint_set0_flag
        m_BitStream.getU(1);								// constraint_set1_flag
        m_BitStream.getU(1);								// constraint_set2_flag
        m_BitStream.getU(1);								// constraint_set3_flag
        m_BitStream.getU(1);								// constraint_set4_flag
        m_BitStream.getU(3);								// reserved_zero_3bits
        m_BitStream.getU(8);								// level idc
        m_BitStream.uev();								    // sequence_parameter_set_id

        if (    FREXT_HP       == profile_idc ||
                FREXT_Hi10P    == profile_idc ||
                FREXT_Hi422    == profile_idc ||
                FREXT_Hi444    == profile_idc ||
                FREXT_CAVLC444 == profile_idc )
        {
            int chroma_format_idc  = m_BitStream.uev();

            if( chroma_format_idc == YUV444) {
                m_BitStream.getU(1);						// separate_colour_plane_flag
            }

            m_BitStream.uev();								// bit_depth_luma_minus8
            m_BitStream.uev();								// bit_depth_chroma_minus8
            m_BitStream.getU(1);							// lossless_qpprime_flag
            int seq_scaling_matrix_present_flag = m_BitStream.getU(1);
            if (0 != seq_scaling_matrix_present_flag )
            {
                int n_ScalingList = (chroma_format_idc != YUV444) ? 8 : 12;
                for(i=0; i<n_ScalingList; i++)
                {
                    int seq_scaling_list_present_flag = m_BitStream.getU(1);
                    if( 0 != seq_scaling_list_present_flag ) {
                        NLog.e("TsSample", "TS: scaling list not supported ");
                        return false;
                    }
                }
            }
        }

        m_BitStream.uev();									// log2_max_frame_num_minus4
        int pict_order_cnt_type = m_BitStream.uev();
        if (pict_order_cnt_type == 0) {
            m_BitStream.uev();
        } else if (pict_order_cnt_type == 1) {
            m_BitStream.getU(1);
            m_BitStream.sev();
            m_BitStream.sev();
            int n = m_BitStream.uev();
            for (int x = 0; x < n; x++)
                m_BitStream.sev();
        }
        m_BitStream.uev();									// num_ref_frames
        m_BitStream.getU(1);
        m_nWidth = (m_BitStream.uev() + 1) * 16;
        m_nHeight = (m_BitStream.uev() + 1) * 16;

        int nPos = m_BitStream.getPos();
        m_nSpsLen = nPos/8 + ( (0 == nPos%8) ? 0 : 1 );

        m_nMime = MIME_TYPE.VIDEO_AVC;

        return true;
    }




    /*----------------------------------------------------------*/
	/*! \brief checks if sample is empty yet
	 *
	 * \return true if sample is empty
	 */
	/*----------------------------------------------------------*/
    public boolean GetIsEmpty() {
        return (0 == m_ByteBuffer.position());
    }




    /*----------------------------------------------------------*/
	/*! \brief get PTS of sample in 45 kHz units
	 *
	 * \return PTS if available, 0 otherwise
	 */
	/*----------------------------------------------------------*/
    public long GetPts() {
        return m_nPts;
    }




    /*----------------------------------------------------------*/
	/*! \brief gets SPS byte array
	 *
	 * \return ByteBuffer containing SPS if available,
	 *         null otherwise
	 */
	/*----------------------------------------------------------*/
    public ByteBuffer GetSps() {
        if ( !GetHasVideoFormat() || 0 == m_nSpsLen)
            return null;
        //
        // find NAL unit containing SPS(=sequence parameter set)
        //
        int i;
        for (i=0; i<m_ByteBuffer.position()-3; i++)
            if ( 0 == m_ByteBuffer.get(i) && 0 == m_ByteBuffer.get(i+1) && 1 == m_ByteBuffer.get(i+2) && 0x67 == m_ByteBuffer.get(i+3) )
                break;
        i += 4;
        if ( i >= m_ByteBuffer.position()-3 )
            return null;

        return ByteBuffer.wrap(m_ByteBuffer.array(), i, m_nSpsLen);
    }




    /*----------------------------------------------------------*/
	/*! \brief get sample's size
	 *
	 * \return sample's size in bytes
	 */
	/*----------------------------------------------------------*/
    public int GetSize() {
        return m_ByteBuffer.position();
    }




    /*----------------------------------------------------------*/
	/*! \brief get sample's video height
	 *
	 * \return sample's video height in pixel
	 */
	/*----------------------------------------------------------*/
    public int GetVideoHeigth() {
        return m_nHeight;
    }



    /*----------------------------------------------------------*/
	/*! \brief get sample's video width
	 *
	 * \return sample's video width in pixel
	 */
	/*----------------------------------------------------------*/
    public int GetVideoWidth() {
        return m_nWidth;
    }




    /*----------------------------------------------------------*/
	/*! \brief set samples discontinuity flag
	 */
	/*----------------------------------------------------------*/
    public void SetDiscontinuity(boolean bDiscontinuity) {
        m_bDiscontinuity = bDiscontinuity;
    }




    /*----------------------------------------------------------*/
	/*! \brief skip already received sample data
	 */
	/*----------------------------------------------------------*/
    public void SetEmpty() {
        m_nPts    			= 0;
        m_nHeight 			= 0;
        m_nWidth  			= 0;
        m_nSpsLen 			= 0;
        m_nAudioSampleRate 	= 0;
        m_nAudioChannels   	= 0;
        m_Esds              = null;
        m_bDiscontinuity      = false;
        m_nMime               = MIME_TYPE.UNKNOWN;
        m_ByteBuffer.rewind();
    }




    /*----------------------------------------------------------*/
	/*! \brief set PTS of sample
	 *
	 * \param nPts - PTS in 45kHz units
	 */
	/*----------------------------------------------------------*/
    public void SetPts(long nPts) {
        m_nPts = nPts;
    }




    /*----------------------------------------------------------*/
	/*! \brief adds payload data to the sample
	 *
	 * \param pTs - byte buffer containing payload
	 * \param nOffs - offset from buffer start in bytes
	 * \param nLen - length of payload chunk
	 */
	/*----------------------------------------------------------*/
    public void WritePayload(byte[] pTs, int nOffs, int nLen) {
        if ( m_ByteBuffer.position() + nLen > MAX_SAMPLE_SIZE ) {
            NLog.e("TsSample", "TS: sample buffer overflow");
            SetEmpty();
            return;
        }

        m_ByteBuffer.put(pTs, nOffs, nLen);
    }
}
