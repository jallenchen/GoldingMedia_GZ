package com.goldingmedia.mvp.mode;

/**
 * Created by Jallen on 2017/9/21 0021 09:28.
 */

public class EventBusCMD {
    private int CmdId = 0;
    private String values = "";

    public EventBusCMD(int cmd){
        CmdId = cmd;
    }

    public int getCmdId() {
        return CmdId;
    }

    public void setCmdId(int cmdId) {
        CmdId = cmdId;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }
}
