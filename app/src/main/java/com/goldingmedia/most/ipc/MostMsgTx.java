package com.goldingmedia.most.ipc;




/*----------------------------------------------------------*/
/*!
 * \brief  class holding a MOST message which needs to be
 *         sent
 */
/*----------------------------------------------------------*/
public class MostMsgTx extends MostMsg {
    public interface IMessageSent {void OnMessageSent(boolean bSucceeded, MostMsgTx Msg);}

    private final MsgAddr m_Addr;
    private final int m_nTimeoutMs;
    private final IMessageSent m_MessageSent;
    private final Class <?>m_UserContext;




    /*----------------------------------------------------------*/
	/*! \brief constructs a new message
	 *
	 * \param Addr - Target address
	 * \param nFBlock - Function Block ID
	 * \param nInst - Instance ID
	 * \param nFunc - Function ID
	 * \param nOpType - Operation
	 * \param nPayloadLen - length of payload in bytes
	 * \param Payload - Payload
	 * \param nTimeoutMs - Timeout for sending in Milliseconds
	 * \param MessageSent - Handler called, after message was sent
	 * \param UserContext - a class passed to the handler
	 */
	/*----------------------------------------------------------*/
    public MostMsgTx(MsgAddr Addr, int nFBlock, int nInst, int nFunc, byte nOpType, int nPayloadLen, byte[] Payload, int nTimeoutMs, IMessageSent MessageSent, Class <?>UserContext) {
        super(nFBlock, nInst, nFunc, nOpType, nPayloadLen, Payload);

        m_Addr        = Addr;
        m_nTimeoutMs  = nTimeoutMs;
        m_MessageSent = MessageSent;
        m_UserContext = UserContext;
    }




    /*----------------------------------------------------------*/
	/*! \brief calls message handler with the send result
	 */
	/*----------------------------------------------------------*/
    public void MsgSent(boolean bSuccess) {
        if ( null != m_MessageSent )
            m_MessageSent.OnMessageSent(bSuccess,this);
    }




    /*----------------------------------------------------------*/
	/*! \brief get target address
	 */
	/*----------------------------------------------------------*/
    public MsgAddr GetAddr() {return m_Addr;}




    /*----------------------------------------------------------*/
	/*! \brief get user context
	 */
	/*----------------------------------------------------------*/
    public Class <?>GetUserContext() { return m_UserContext;}
}
