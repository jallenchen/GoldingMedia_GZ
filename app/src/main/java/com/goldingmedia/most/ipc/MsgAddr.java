package com.goldingmedia.most.ipc;

import java.net.InetAddress;


public class MsgAddr {

    public enum IpcProtocol_t
    {
        IpcUdp_V2_0,
        IpcTcp_V2_0
    }
    private final InetAddress m_Addr;
    private final int m_nPort;
    private final IpcProtocol_t m_protocol;

    public MsgAddr(InetAddress Addr, int nPort) {
        m_Addr = Addr;
        m_nPort = nPort;
        m_protocol = IpcProtocol_t.IpcUdp_V2_0;
    }

    public MsgAddr(InetAddress Addr, int nPort, IpcProtocol_t protocol) {
        m_Addr = Addr;
        m_nPort = nPort;
        m_protocol = protocol;
    }

    public InetAddress GetInetAddress() {return m_Addr;}

    public int GetPort() {return m_nPort;}

    public IpcProtocol_t GetProtocol() {return m_protocol;}
}
