package com.goldingmedia.most.ts_renderer;



/*----------------------------------------------------------*/
/*!
 * \brief  Handles a elementary stream in a multiplexed
 *         transport stream
 */
/*----------------------------------------------------------*/
class TsElementaryStream {
    private int m_nPid = TsPacket.PID_INVALID;
    private int m_nErrSync = 0;
    private int m_nErrDiscontinuity = 0;
    private int m_nContinuityCounter = -1;
    private TsSample m_Sample;




    /*----------------------------------------------------------*/
	/*! \brief constructs a new elementary stream
	 *
	 *  \param nVPid - PID of elementary stream
	 */
	/*----------------------------------------------------------*/
    public TsElementaryStream() {
        m_Sample = TsSample.CreateSample();
    }




    /*----------------------------------------------------------*/
	/*! \brief gets number of errors due to missing sync byte
	 */
	/*----------------------------------------------------------*/
    public int GetErrSync() {
        return m_nErrSync;
    }




    /*----------------------------------------------------------*/
	/*! \brief gets number of stream discontinuities
	 */
	/*----------------------------------------------------------*/
    public int GetErrDiscontinuity() {
        return m_nErrDiscontinuity;
    }




    /*----------------------------------------------------------*/
	/*! \brief gets the elementary stream's PID
	 *
	 *  \return PID of elementary stream
	 */
	/*----------------------------------------------------------*/
    public int GetPid() { return m_nPid; }




    /*----------------------------------------------------------*/
	/*! \brief adds received data to the elementary stream
	 *
	 *  \param pTs - 188 byte transport stream packet
	 */
	/*----------------------------------------------------------*/
    public TsSample PushTs(byte[] pTs) {
        if ( !TsPacket.GetHasSyncByte(pTs)) {
            m_nErrSync++;
            return null;
        }

        if ( !TsPacket.GetHasPid(pTs, m_nPid))
            return null;

        if ( TsPacket.GetHasDiscontinuity(pTs, m_nContinuityCounter) ) {
            //Reenable this code, if you want to restart decoder after discontinutiy
            m_Sample.SetEmpty();
            m_Sample.SetDiscontinuity(true);
            m_nErrDiscontinuity++;
        }

        m_nContinuityCounter = TsPacket.GetContinuityCounter(pTs);

        if ( m_Sample.GetIsEmpty() && !TsPacket.GetIsPayloadUnitStart(pTs)) {
            return null;
        }

        TsSample Ret = null;
        if ( TsPacket.GetIsPayloadUnitStart(pTs) ) {
                Ret = m_Sample;                             // keep old sample to return for rendering

                m_Sample = TsSample.CreateSample();
                if ( null != Ret )
                    if ( Ret.GetIsEmpty() ) {
                        if (Ret.GetDiscontinuity())
                            m_Sample.SetDiscontinuity(true);

                        TsSample.Recycle(Ret);
                        Ret = null;
                    }

                if (TsPacket.GetHasPts(pTs))
                    m_Sample.SetPts(TsPacket.GetPts(pTs));
        }

        int nEsPayloadStart = TsPacket.GetElementaryPayloadStart(pTs);

        if ( nEsPayloadStart < 188 ) {
            m_Sample.WritePayload(pTs, nEsPayloadStart, 188 - nEsPayloadStart);
        }

        return Ret;
    }




    /*----------------------------------------------------------*/
	/*! \brief sets the elementary stream's PID
	 *
	 *  \param nPid - PID of elementary stream
	 */
	/*----------------------------------------------------------*/
    public void SetPid(int nPid) { m_nPid = nPid; }
}




