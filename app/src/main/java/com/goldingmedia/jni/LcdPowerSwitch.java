package com.goldingmedia.jni;

import com.goldingmedia.utils.NLog;
import com.goldingmedia.utils.Utils;

/**
 * Created by Jallen on 2017/12/7 0007 16:43.
 */

public class LcdPowerSwitch {
    public static boolean isError = false;
    static {
            try {
                System.loadLibrary("lcdpowerset_jni");
                isError = false;
            }catch (UnsatisfiedLinkError e) {
                isError = true;
                e.printStackTrace();
            }
    }

    public native static int on();
    public native static int off();
    public native static int get();

    public static int state = 1;

    public static int lcdOn(){
        if(!isError){
            return   on();
        }
        state = 1;
        return state;
    }

    public static int lcdOff(){
        if(!isError){
            return   off();
        }
        state = 0;
        return state;
    }

    public static int lcdGet(){
        if(!isError){
            return   get();
        }
        return state;
    }
}
