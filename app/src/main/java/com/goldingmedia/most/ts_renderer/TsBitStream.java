package com.goldingmedia.most.ts_renderer;




/*----------------------------------------------------------*/
/*!
 * \brief  helper to decode non byte aligned values from a
 *         bit stream
 */
/*----------------------------------------------------------*/
class TsBitStream {
    private int pos;
    private byte[] data;




    /*----------------------------------------------------------*/
	/*! \brief sets source of bit stream
	 *
	 *  \param pData - byte array containing bitstream
	 *  \param nOffs - offset to start of bitstream in bytes
	 */
	/*----------------------------------------------------------*/
    public void setSource(byte[] pData, int nOffs) {
        data = pData;
        pos  = nOffs*8;
    }




    /*----------------------------------------------------------*/
	/*! \brief reads next bit
	 */
	/*----------------------------------------------------------*/
    int getBit() {
        final int mask = 1 << (7 - (pos & 7));
        final int idx = pos >> 3;
        pos++;
        return ((data[idx] & mask) == 0) ? 0 : 1;
    }




    /*----------------------------------------------------------*/
	/*! \brief gets current read position in bits
	 */
	/*----------------------------------------------------------*/
    int getPos() {
        return pos;
    }




    /*----------------------------------------------------------*/
	/*! \brief extracts unsigned value with "bits" bits
	 */
	/*----------------------------------------------------------*/
    int getU(int bits) {
        int result = 0;
        for (int i = 0; i < bits; i++) {
            result = result * 2 + getBit();
        }
        return result;
    }




    /*----------------------------------------------------------*/
	/*! \brief extracts value with variable length. the length
	 *         is determined by counting ones.
	 */
	/*----------------------------------------------------------*/
    int ev(final boolean signed) {
        int nBitCount = 0;
        while ( 0 == getBit() ) {
            nBitCount++;
        }
        int result = 1;
        for (int i = 0; i < nBitCount; i++) {
            int b = getBit();
            result = result * 2 + b;
        }
        result--;
        if (signed)
            result = (result + 1) / 2 * (result % 2 == 0 ? -1 : 1);
        return result;
    }




    /*----------------------------------------------------------*/
	/*! \brief extracts unsigned value with variable length
	 */
	/*----------------------------------------------------------*/
    int uev() {
        return ev(false);
    }




    /*----------------------------------------------------------*/
	/*! \brief extracts signed value with variable length
	 */
	/*----------------------------------------------------------*/
    int sev() {
        return ev(true);
    }
}
