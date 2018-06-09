package com.goldingmedia.most.ipc;




/*----------------------------------------------------------*/
/*!
 * \brief  filters messages and dispatches result to a
 *         handler
 */
/*----------------------------------------------------------*/
public class MsgFilter {
    private final int m_nFBlock;
    private final int m_nFunc;
    private final byte m_nOpType;
    private final IMsgHandler m_MsgHandler;

    public interface IMsgHandler {void OnMsg(MsgAddr Addr, MostMsg Msg);}


    /*----------------------------------------------------------*/
	/*! \brief construct a message filter
	 */
	/*----------------------------------------------------------*/
    public MsgFilter(int nFBlock, int nFunc, byte nOpType,IMsgHandler MsgHandler) {
        m_nFBlock    = nFBlock;
        m_nFunc      = nFunc;
        m_nOpType    = nOpType;
        m_MsgHandler = MsgHandler;
    }


    /*----------------------------------------------------------*/
	/*! \brief filter an incoming messagek
	 */
	/*----------------------------------------------------------*/
    public void Filter(MsgAddr Addr, MostMsg Msg) {
        if ( null != m_MsgHandler )
            if ( Msg.GetFBlock() == m_nFBlock && Msg.GetFunc() == m_nFunc && Msg.GetOpType() == m_nOpType )
                m_MsgHandler.OnMsg(Addr,Msg);
    }
}
