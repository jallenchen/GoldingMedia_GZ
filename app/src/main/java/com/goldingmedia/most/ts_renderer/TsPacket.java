package com.goldingmedia.most.ts_renderer;




/*----------------------------------------------------------*/
/*!
 * \brief  Handles a transport stream packet of 188 bytes
 */
/*----------------------------------------------------------*/
class TsPacket {
    public  static final int LENGTH                               = 188;
    public  static final int PID_INVALID                          = 0xFFFF;
    private static final int ADAPTION_FIELD_CONTROL__RESERVED     = (0x00 << 4);
    private static final int ADAPTION_FIELD_CONTROL__PAYLOAD_ONLY = (0x01 << 4);
    private static final int ADAPTION_FIELD_CONTROL__NO_PAYLOAD   = (0x02 << 4);
    private static final int ADAPTION_FIELD_CONTROL__BOTH         = (0x03 << 4);
    private static final int program_stream_map					  = 0xBC;
    private static final int padding_stream						  = 0xBE;
    private static final int private_stream_2					  = 0xBF;
    private static final int ECM								  = 0xF0;
    private static final int EMM								  = 0xF1;
    private static final int DSMCC_stream						  = 0xF2;
    private static final int H222_stream						  = 0xF8;
    private static final int program_stream_directory			  = 0xFF;
    private static final int PROGRAM_ASSOSICIATION_SECTION        = 0x00;
    private static final int PROGRAM_MAP_SECTION                  = 0x02;
    private static final int ISO_IEC_11172_AUDIO                  = 0x03;
    private static final int ISO_IEC_13818_2_Video                = 0x02;
    private static final int ISO_IEC_13818_3_AUDIO                = 0x04;
    private static final int ISO_IEC_13818_1_RESERVED             = 0x1B;




    /*----------------------------------------------------------*/
	/*! \brief extract continuity counter
	 *
	 *  \param pTs - byte array containing TS packet
	 */
	/*----------------------------------------------------------*/
    static int GetContinuityCounter(byte[] pTs) {
        return (pTs[3] & 0xF);
    }




    /*----------------------------------------------------------*/
	/*! \brief get array index where ES payload
	 *
	 *  \param pTs - byte array containing TS packet
	 */
	/*----------------------------------------------------------*/
    static int GetElementaryPayloadStart(byte[] pTs) {

        int nRet = GetPayloadStart(pTs);					// index to TS payload

        if ( GetHasPesHeader(pTs) ) {
            nRet += 3 + 1 + 2 + 1 + 1;  					// packet start(3) + stream_id(1) + PES_packet_length(2) + PES_scampling...(1) + PTS_DTS_flags...(1)
            nRet += (pTs[nRet] & 0xFF) + 1;					// PES_header_data_length
        }

        return nRet;
    }




    /*----------------------------------------------------------*/
	/*! \brief check if there is a discontinuity in the stream
	 *
	 *  \param pTs - byte array containing TS packet
	 *  \param nLastDiscontinuityCounter - preceding counter
	 */
	/*----------------------------------------------------------*/
    static boolean GetHasDiscontinuity(byte[] pTs, int nLastDiscontinuityCounter) {
        nLastDiscontinuityCounter = (nLastDiscontinuityCounter+1) & 0xF;

        return (pTs[3] & 0xF) != nLastDiscontinuityCounter;
    }




    /*----------------------------------------------------------*/
	/*! \brief check if a TS packet has the given PID
	 *
	 *  \param pTs - byte array containing TS packet
	 *  \param nPid - PID to check for
	 *
	 *  \return true if PID is equal
	 */
	/*----------------------------------------------------------*/
    static boolean GetHasPid(byte[] pTs, int nPid) {
        return nPid == GetPid(pTs);
    }




    /*----------------------------------------------------------*/
	/*! \brief check if a TS packet contains a PAT
	 *
	 *  \param pTs - byte array containing TS packet
	 *
	 *  \return true if PAT is contained
	 */
	/*----------------------------------------------------------*/
    static boolean GetIsPat(byte[] pTs) {
        if ( !GetHasPid(pTs,0) )
            return false;

        int nPat = GetPayloadStart(pTs);                        // start of PSI
        if (nPat < pTs.length) {
            nPat += 1 + (0xFF & pTs[nPat]);                         // pointer_field
            return (pTs[nPat] == PROGRAM_ASSOSICIATION_SECTION);   // table_id
        }
        return false;
    }


    /*----------------------------------------------------------*/
	/*! \brief check if a TS packet is a stuffing packet
	 *
	 *  \param pTs - byte array containing TS packet
	 *
	 *  \return true if this packet is only stuffing data (no audio / video)
	 */
	/*----------------------------------------------------------*/
    static boolean GetIsStuffing(byte[] pTs) {
        return 0x1FFF == GetPid(pTs);
    }




    /*----------------------------------------------------------*/
	/*! \brief gets the PMT PID from the n-th program of a PAT
	 *         packet
	 *
	 *  \param pTs - byte array containing TS packet
	 *  \param nPrg - number of program
	 *
	 *  \return true if PAT is contained
	 */
	/*----------------------------------------------------------*/
    static int GetPmtPidFromPat(byte[] pTs, int nPrg) {
        int nPat = GetPayloadStart(pTs);                        // PSI
        nPat += 1 + (0xFF & pTs[nPat]);                         // pointer_field

        return ((0x1F & pTs[nPat + 10 +nPrg*4]) << 8) +         // program_map_PID (high byte)
                (0xFF & pTs[nPat + 10 +nPrg*4 + 1]);            // program_map_PID (low byte)
    }




    /*----------------------------------------------------------*/
	/*! \brief gets the audio PID from a PMT packet
	 *
	 *  \param pTs - byte array containing TS packet
	 *
	 *  \return PID of 1st audio channel
	 */
	/*----------------------------------------------------------*/
    static int GetAudioPidFromPmt(byte[] pTs) {
        try {
            int nPmt = GetPayloadStart(pTs);                        // PSI
            nPmt += 1 + (0xFF & pTs[nPmt]);                         // pointer_field

            if (PROGRAM_MAP_SECTION != pTs[nPmt])                 // check table_id
                return PID_INVALID;

            int section_length = ((0x0F & pTs[nPmt + 1]) << 8) + (0xFF & pTs[nPmt + 2]);
            int program_info_length = ((0x0F & pTs[nPmt + 10]) << 8) + (0xFF & pTs[nPmt + 11]);

            int n = nPmt + 12 + program_info_length;

            while (n < section_length + nPmt + 2) {                        // run through all streams
                int stream_type = pTs[n];
                int elementary_pid = ((0x1F & pTs[n + 1]) << 8) + (0xFF & pTs[n + 2]);
                int ES_info_length = ((0x0F & pTs[n + 3]) << 8) + (0xFF & pTs[n + 4]);

                if (ISO_IEC_11172_AUDIO == stream_type || ISO_IEC_13818_3_AUDIO == stream_type) {
                    return elementary_pid;
                }

                n += 5 + ES_info_length;                            // switch to next stream
            }

            return PID_INVALID;                                     // no audio stream found
        } catch (Exception ex) {
            return PID_INVALID;
        }
    }




    /*----------------------------------------------------------*/
	/*! \brief gets the audio PID from a PMT packet
	 *
	 *  \param pTs - byte array containing TS packet
	 *
	 *  \return PID of 1st audio channel
	 */
	/*----------------------------------------------------------*/
    static int GetVideoPidFromPmt(byte[] pTs) {
        try {
            int nPmt = GetPayloadStart(pTs);                        // PSI
            nPmt += 1 + (0xFF & pTs[nPmt]);                         // pointer_field

            if (PROGRAM_MAP_SECTION != pTs[nPmt])                 // check table_id
                return PID_INVALID;

            int section_length = ((0x0F & pTs[nPmt + 1]) << 8) + (0xFF & pTs[nPmt + 2]);
            int program_info_length = ((0x0F & pTs[nPmt + 10]) << 8) + (0xFF & pTs[nPmt + 11]);

            int n = nPmt + 12 + program_info_length;

            while (n < section_length + nPmt + 2) {                        // run through all streams
                int stream_type = pTs[n];
                int elementary_pid = ((0x1F & pTs[n + 1]) << 8) + (0xFF & pTs[n + 2]);
                int ES_info_length = ((0x0F & pTs[n + 3]) << 8) + (0xFF & pTs[n + 4]);

                if (ISO_IEC_13818_2_Video == stream_type || ISO_IEC_13818_1_RESERVED == stream_type) {
                    return elementary_pid;
                }

                n += 5 + ES_info_length;                            // switch to next stream
            }

            return PID_INVALID;                                     // no audio stream found
        } catch (Exception ex) {
            return PID_INVALID;
        }
    }




    /*----------------------------------------------------------*/
	/*! \brief check if TS packet contains a PES header
	 *
	 *  \param pTs - byte array containing TS packet
	 */
	/*----------------------------------------------------------*/
    static boolean GetHasPesHeader(byte[] pTs) {
        if ( !GetIsPayloadUnitStart(pTs) )
            return false;

        try {
            int nPes = GetPayloadStart(pTs);

            return !(0 != pTs[nPes] ||
                    0 != pTs[nPes + 1] ||
                    1 != pTs[nPes + 2] ||    // is it a PES Header?
                    program_stream_map == pTs[nPes + 3] ||
                    padding_stream == pTs[nPes + 3] ||
                    private_stream_2 == pTs[nPes + 3] ||
                    ECM == pTs[nPes + 3] ||
                    EMM == pTs[nPes + 3] ||
                    program_stream_directory == pTs[nPes + 3] ||
                    DSMCC_stream == pTs[nPes + 3] ||
                    H222_stream == pTs[nPes + 3] ||
                    0 == (0x80 & pTs[nPes + 7]));
        } catch (Exception ex) {
            return false;
        }
    }




    /*----------------------------------------------------------*/
	/*! \brief check if TS packet contains a PTS
	 *
	 *  \param pTs - byte array containing TS packet
	 */
	/*----------------------------------------------------------*/
    static boolean GetHasPts(byte[] pTs) {
        if ( !GetHasPesHeader(pTs) )
            return false;

        int nPes = GetPayloadStart(pTs);

        return ( 0 != ((pTs[nPes+7] & 0x80)) );					// PTS_DTS_flags is 0x2 or 0x3
    }




    /*----------------------------------------------------------*/
	/*! \brief check if a TS packet has sync byte
	 *
	 *  \param pTs - byte array containing TS packet
	 */
	/*----------------------------------------------------------*/
    static boolean GetHasSyncByte(byte[] pTs) {
        return 0x47 == pTs[0];
    }




    /*----------------------------------------------------------*/
	/*! \brief check if a TS packet has sync byte
	 *
	 *  \param pTs - byte array containing TS packet
	 *  \param offs - offset to sync byte
	 */
	/*----------------------------------------------------------*/
    static boolean GetHasSyncByte(byte[] pTs, final int offset) {
        if (null == pTs || offset < 0 || offset >= pTs.length)
            return false;
        return 0x47 == pTs[offset];
    }




    /*----------------------------------------------------------*/
	/*! \brief find first TS Sync Byte in a buffer
	 *
	 *  \param buffer - byte array searched
	 *  \param length - length of bytes searched
	 */
	/*----------------------------------------------------------*/
    static int GetSyncByteOffset(byte []b, int bSize)
    {
        int offset =  0;
        int packetsFound = 0;
        boolean synced = false;

        while( !synced && offset < 188 && offset < bSize && offset < b.length) {
            if (b[offset] == 0x47) {
                synced = true;
                int j = offset + 188;
                while (synced && packetsFound < 5 && j < bSize && j < b.length)
                {
                    if(b[j] != 0x47) {
                        synced = false;
                        packetsFound = 0;
                    } else {
                        j += 188;
                        ++packetsFound;
                    }
                }
            }
            if (!synced)
                offset++;
        }
        return synced ? offset : -1;
    }




    /*----------------------------------------------------------*/
	/*! \brief check if TS packet starts a new payload unit
	 *
	 *  \param pTs - byte array containing TS packet
	 */
	/*----------------------------------------------------------*/
    static boolean GetIsPayloadUnitStart(byte[] pTs) {
        return ( 0 != (pTs[1] & 0x40));
    }




    /*----------------------------------------------------------*/
	/*! \brief get arry index of payload start in packet
	 *
	 *  \param pTs - byte array containing TS packet
	 */
	/*----------------------------------------------------------*/
    private static int GetPayloadStart(byte[] pTs) {
        switch ( (pTs[3] & 0x30) ) {
            case ADAPTION_FIELD_CONTROL__PAYLOAD_ONLY:
                return 4;

            case ADAPTION_FIELD_CONTROL__BOTH:
                return 4 + 1 + (pTs[4] & 0xFF);

            case ADAPTION_FIELD_CONTROL__RESERVED:
            case ADAPTION_FIELD_CONTROL__NO_PAYLOAD:
                return 188;
        }

        return 188;
    }




    /*----------------------------------------------------------*/
	/*! \brief get PES packet length from packet
	 *
	 *  \param pTs - byte array containing TS packet
	 *  \return length of PES packet in bytes
	 */
	/*----------------------------------------------------------*/
    static int GetPesPacketLength(byte[] pTs) {
        int nPes = GetPayloadStart(pTs);
        return ((pTs[nPes+4] & 0xFF)<<8) + (pTs[nPes+5] & 0xFF);
    }




    /*----------------------------------------------------------*/
	/*! \brief extract PID from a TS packet
	 *
	 *  \param pTs - byte array containing TS packet
	 */
	/*----------------------------------------------------------*/
    private static int GetPid(byte[] pTs) {
        return ((pTs[1] & 0x1F) << 8) + (pTs[2] & 0xFF);
    }




    /*----------------------------------------------------------*/
	/*! \brief extract PTS from a TS packet in 45 kHz units
	 *
	 *  \param pTs - byte array containing TS packet
	 */
	/*----------------------------------------------------------*/
    static long GetPts(byte[] pTs) {
        int nPes = GetPayloadStart(pTs);

        return 	((long)(pTs[nPes+ 9] & 0x0E)<<28) +
                ((pTs[nPes+10] & 0xFF)<<21) +
                ((pTs[nPes+11] & 0xFE)<<13) +
                ((pTs[nPes+12] & 0xFF)<< 6) +
                ((pTs[nPes+13] & 0xFF)>> 2);
    }
}
