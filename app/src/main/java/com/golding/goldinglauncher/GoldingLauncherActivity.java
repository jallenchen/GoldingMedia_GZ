package com.golding.goldinglauncher;

/**调用JNI接口，获取Most pin的值
 * Created by Jallen on 2017/9/13 0013 17:08.
 */

public class GoldingLauncherActivity {
    public static boolean isError = false;
    public native static int open_gpio_device();
    public native static int set_mostpin_input();
    public native static int get_mostpin_value();
    public native static int close_gpio_device();
    static {
        try {
            System.loadLibrary("mostpin_jni");
            isError = false;
        } catch (UnsatisfiedLinkError e) {
            isError = true;
            e.printStackTrace();
        }
    }

    public static void openDevice(){
        if(!isError){
            open_gpio_device();
        }
    }

    public static void setInput(){
        if(!isError){
            set_mostpin_input();
        }
    }

    public static void closeDevice(){
        if(!isError){
            close_gpio_device();
        }
    }

    public static int getValue(){
        if(!isError){
            return get_mostpin_value();
        }else{
            return 1;
        }
    }
}
