package com.golding.goldinglauncher;

public class flashActivity {
    public static boolean isError = false;
    static{
        try {
            System.loadLibrary("flash81118_jni");
            isError = false;
        } catch (UnsatisfiedLinkError e) {
            isError = true;
            e.printStackTrace();
        }
    }
    public native static int init();
    public native static int flash();

    public static void onInit(){
        if(!isError){
              init();
        }
    }

    public static int onFlash(){
        if(!isError){
           return flash();
        }else{
            return -1;
        }
    }
}
