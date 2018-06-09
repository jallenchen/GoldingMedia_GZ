package com.goldingmedia.most.ipc;

import com.goldingmedia.utils.NLog;

import java.util.zip.CRC32;

/*----------------------------------------------------------*/
/*!
 * \brief  class holding a MOST message
 */
/*----------------------------------------------------------*/
public class MostMsg {
    public static final int MAX_PAYLOAD_LENGTH = 64 *1024;
    public static final byte OP_SET            = 0x0;
    public static final byte OP_GET            = 0x1;
    public static final byte OP_SETGET         = 0x2;
    public static final byte OP_INC            = 0x3;
    public static final byte OP_DEC            = 0x4;
    public static final byte OP_GETINTERFACE   = 0x5;
    public static final byte OP_STATUS         = 0xC;
    public static final byte OP_INTERFACE      = 0xE;
    public static final byte OP_ERROR          = 0xF;
    public static final byte OP_START          = 0x0;
    public static final byte OP_ABORT          = 0x1;
    public static final byte OP_STARTRESULT    = 0x2;
    public static final byte OP_STARTRESULTACK = 0x6;
    public static final byte OP_ABORTACK       = 0x7;
    public static final byte OP_STARTACK       = 0x8;
    public static final byte OP_ERRORACK       = 0x9;
    public static final byte OP_PROCESSINGACK  = 0xA;
    public static final byte OP_PROCESSING     = 0xB;
    public static final byte OP_RESULT         = 0xC;
    public static final byte OP_RESULTACK      = 0xD;
    public static final byte OP_REPORTS        = 0x9;


    private boolean m_isValid;
    private int m_nFBlock;
    private int m_nFunc;
    private int m_nInst;
    private byte m_nOpType;
    private int m_nPayloadLen;
    private byte[] m_Payload;
    private int m_nParsePos;
    private long m_nHeaderCrc;
    private long m_nPayloadCrc;
    private byte[] m_zHeader;


    /*----------------------------------------------------------*/
    /*! \brief constructs a new message, which is initial not valid.
     *  \note It has to fed by the Parse method until IsValid method reports true.
     */
    /*----------------------------------------------------------*/
    MostMsg()
    {
        m_isValid = false;
        m_nParsePos = 0;
        m_zHeader = new byte[16];
    }



    /*----------------------------------------------------------*/
	/*! \brief constructs a new message which is fully set up and valid
	 */
	/*----------------------------------------------------------*/
    MostMsg(int nFBlock, int nInst, int nFunc, byte nOpType, int nPayloadLen, byte[] Payload) {
        m_isValid     = true;
        m_nFBlock     = nFBlock;
        m_nInst       = nInst;
        m_nFunc       = nFunc;
        m_nOpType     = nOpType;
        m_nPayloadLen = nPayloadLen;
        m_Payload     = Payload;
        m_nParsePos   = -1;
    }




    /*----------------------------------------------------------*/
	/*! \brief serializes a message to an byte array
	 */
	/*----------------------------------------------------------*/
    public byte[] ToByteArray() {
        byte[] b = new byte[ToByteArrayLen()];
        b[0] = ( byte )0x49;
        b[1] = ( byte )0x72;
        b[2] = ( byte )0x16;
        b[3] = ( byte )0x25;
        b[4] = ( byte )( m_nFBlock & 0xFF );
        b[5] = ( byte )( m_nInst & 0xFF );
        b[6] = ( byte )( ( m_nFunc >> 8 ) & 0xFF );
        b[7] = ( byte )( ( m_nFunc )&0xFF );
        b[8] = ( byte )( m_nOpType );
        b[9] = ( byte )0x0;
        b[10] = ( byte )0x0;
        b[11] = ( byte )0x0;
        b[12] = ( byte )( ( m_nPayloadLen >> 24 ) & 0xFF );
        b[13] = ( byte )( ( m_nPayloadLen >> 16 ) & 0xFF );
        b[14] = ( byte )( ( m_nPayloadLen >> 8 ) & 0xFF );
        b[15] = ( byte )( m_nPayloadLen & 0xFF );

        CRC32 hCrc = new CRC32();
        hCrc.update(b, 0, 16);
        long headerCrc = hCrc.getValue();

        b[16] = ( byte )( ( headerCrc >> 24 ) & 0xFF );
        b[17] = ( byte )( ( headerCrc >> 16 ) & 0xFF );
        b[18] = ( byte )( ( headerCrc >> 8 ) & 0xFF );
        b[19] = ( byte )( headerCrc & 0xFF );
        
        if (m_nPayloadLen != 0 && m_Payload != null) {
            System.arraycopy(m_Payload, 0, b, 20, m_nPayloadLen);

            CRC32 pCrc = new CRC32();
            pCrc.update(b, 20, m_nPayloadLen);
            long payloadCrc = pCrc.getValue();

            b[20 + m_nPayloadLen] = ( byte )( ( payloadCrc >> 24 ) & 0xFF );
            b[21 + m_nPayloadLen] = ( byte )( ( payloadCrc >> 16 ) & 0xFF );
            b[22 + m_nPayloadLen] = ( byte )( ( payloadCrc >> 8 ) & 0xFF );
            b[23 + m_nPayloadLen] = ( byte )( payloadCrc & 0xFF );
        }
        return b;
    }




    public int ToByteArrayLen() {
        int len = 20;
        if (0 != m_nPayloadLen)
            len += m_nPayloadLen + 4;  //If there is payload, it will be CRC checked
        return len;
    }



    /*----------------------------------------------------------*/
    /*! \brief Fills out an empty CMostMsg (created with default constructor)
     *  \return true, if parsing was valid. false, parser error, no way to continue.
     *  \note Check with IsValid method if parsing is finished.
     */
    /*----------------------------------------------------------*/
    public boolean Parse(byte receivedByte)
    {
        if( -1 == m_nParsePos )
        {
            NLog.d(null, "Parse was called, but the parse state is invalid");
            return false;
        }
        boolean valid = true;
        if( m_nParsePos <= 19 )
        {
            if( m_nParsePos <= 15 )
                m_zHeader[m_nParsePos] = receivedByte;

            //Check Header
            switch( m_nParsePos )
            {
                case 0:
                    if( 0x49 != receivedByte )
                        valid = false;
                    break;
                case 1:
                    if( 0x72 != receivedByte )
                        valid = false;
                    break;
                case 2:
                    if( 0x16 != receivedByte )
                        valid = false;
                    break;
                case 3:
                    if( 0x25 != receivedByte )
                        valid = false;
                    break;
                case 4:
                    m_nFBlock = receivedByte;
                    break;
                case 5:
                    m_nInst = receivedByte;
                    break;
                case 6:
                    m_nFunc = ((int)receivedByte & 0xFF) << 8;
                    break;
                case 7:
                    m_nFunc += (int)receivedByte & 0xFF;
                    break;
                case 8:
                    m_nOpType = receivedByte;
                    break;
                case 9:
                case 10:
                case 11:
                    //Reserved for future use cases
                    break;
                case 12:
                    m_nPayloadLen = ((int)receivedByte & 0xFF) << 24;
                    break;
                case 13:
                    m_nPayloadLen += ((int)receivedByte & 0xFF) << 16;
                    break;
                case 14:
                    m_nPayloadLen += ((int)receivedByte & 0xFF) << 8;
                    break;
                case 15:
                    m_nPayloadLen += ((int)receivedByte & 0xFF);
                    break;
                case 16:
                    m_nHeaderCrc = ((long)receivedByte & 0xFF) << 24;
                    break;
                case 17:
                    m_nHeaderCrc += ((long)receivedByte & 0xFF) << 16;
                    break;
                case 18:
                    m_nHeaderCrc += ((long)receivedByte & 0xFF) << 8;
                    break;
                case 19:
                    m_nHeaderCrc += (long)receivedByte & 0xFF;
                    CRC32 hCrc = new CRC32();
                    hCrc.update(m_zHeader, 0, 16);
                    long headerCrc = hCrc.getValue();
                    if( m_nHeaderCrc == headerCrc )
                    {
                        if( 0 != m_nPayloadLen )
                        {
                            //Continue to parse the payload
                            m_Payload = new byte[m_nPayloadLen];
                        }
                        else
                        {
                            //No payload, we are finished
                            m_isValid = true;
                            m_nParsePos = -1;
                        }
                    }
                    else
                    {
                        NLog.d(null, "MostMsg::Protocol error, header CRC does not match");
                        valid = false;
                    }
                    break;
            }
        }
        else
        {
            if( m_nParsePos < ( m_nPayloadLen + 20 ) )
            {
                //Check Payload and store it
                if( null != m_Payload )
                {
                    m_Payload[m_nParsePos - 20] = receivedByte;
                }
                else
                {
                    NLog.d(null, "MostMsg::Protocol deserializing error, payload buffer is NULL" );
                    valid = false;
                    m_nPayloadLen = 0;
                }
            }
            else
            {
                //Finally check CRC of payload
                int crcState = m_nParsePos - m_nPayloadLen - 20;
                switch( crcState )
                {
                    case 0:
                        m_nPayloadCrc = ((long)receivedByte & 0xFF) << 24;
                        break;
                    case 1:
                        m_nPayloadCrc += ((long)receivedByte & 0xFF) << 16;
                        break;
                    case 2:
                        m_nPayloadCrc += ((long)receivedByte & 0xFF) << 8;
                        break;
                    case 3:
                        m_nPayloadCrc += (long)receivedByte & 0xFF;
                        CRC32 pCrc = new CRC32();
                        pCrc.update(m_Payload, 0, m_nPayloadLen);
                        long payloadCrc = pCrc.getValue();

                        if( m_nPayloadCrc == payloadCrc )
                        {
                            //Payload is successfully received
                            m_isValid = true;
                            m_nParsePos = -1;
                        }
                        else
                        {
                            NLog.d(null, "MostMsg::Protocol deserializing error, Payload CRC mismatch" );
                            valid = false;
                        }
                        break;
                    default:
                        NLog.d(null, "MostMsg::Protocol deserializing error, Payload CRC state is out of range" );
                        valid = false;
                }
            }
        }

        if( valid )
        {
            if (m_nParsePos >= 0)
                ++m_nParsePos;
        }
        else
        {
            NLog.d(null, "CMostMsg::Parse failed at parse position " +  m_nParsePos );
            m_nParsePos = -1;
        }
        return valid;
    }


    public boolean IsValid() { return m_isValid; }

    public int GetFBlock() { return m_nFBlock; }

    public int GetFunc() { return m_nFunc; }

    public int GetInst() {return m_nInst;}

    public byte GetOpType() { return m_nOpType;}

    public byte[] GetPayload () {return m_Payload;}

    public int GetPayloadLen () {return m_nPayloadLen;}
}