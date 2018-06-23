package com.goldingmedia.contant;

import java.io.File;

/**
 * Created by Jallen on 2017/7/20 0020 10:30.
 */

public class Contant {
    public enum ContentType {
        NULL, video, audio, img, map, apk, xml, lrc,
        custom, system, music, sysdata, ebooks, windowAd, jtv, jmagazine, jmall,
        windowTop, windowMiddle, windowBottom, windowCrossbar, windowText, guangdongTv, evenAd,
        famGodGrass, financialEye, orangeFlavor, securityVideo, seaWhale, bingYan
    }

    public class Actions{
        public static final String ADS_SWITCH = "com.goldingmedia.ads.switch";
        public static final String ENG_MODE = "com.goldingmedia.eng.setting";
        public static final String PLAY = "com.golding.playmedia";
        public static final String CLOSEACTIVITY = "com.golding.CloseActivity";
        public static final String START_ADS_ACTIVITY = "com.golding.start.ads";
        public static final String APK_EXCHANGE = "com.golding.apk.exchange";
        public static final String PLAY_STATUS_REPORT = "com.golding.status.report";
        public static final String CONVERSATION_SEND = "com.golding.conversation.send";
        public static final String CONVERSATION_MSG = "com.golding.conversation.msg";

    }

    //升级，推送的下载地址
    public static  String UpgradeServer = "http://10.0.0.1:81/Upgrade/";
    public static  String MediaServer = "http://10.0.0.1:81/Media/";
    public static  String ConfigServer = "http://10.0.0.1:81/Config/";

    // 订单处理服务器
    public static String PAY_HOST = "http://119.23.145.184:9103";

    //seat info path
    public static final String SETTINGDATETXT = "sdcard/.txt/localseat.txt";
    public static final String SEATSARRANGEMENT = "sdcard/.txt/seatsarrangement.txt";
    public static final String CHATSEAT = "sdcard/.txt/listseat.txt";

    public static final String BasePath = "/mnt/sdcard/goldingmedia/";
    public static final String Launcher = "/mnt/sdcard/goldingmedia/Launcher/";
    public static final String HITV = "/mnt/sdcard/goldingmedia/HITV/";
    public static final String Ads = "/mnt/sdcard/goldingmedia/Ads/";
    public static final String Music = "/mnt/sdcard/goldingmedia/Music/";
    public static final String Photo = "/mnt/sdcard/goldingmedia/Photo/";
    public static final String AppExe = "/mnt/sdcard/goldingmedia/AppExe/";
    public static final String Docs =  "/mnt/sdcard/goldingmedia/Docs/";
    public static final String Stream =  "/mnt/sdcard/goldingmedia/Stream/";
    public static final String Game =  "/mnt/sdcard/goldingmedia/Game/";
    public static final String Elive =  "/mnt/sdcard/goldingmedia/Elive/";
    public static final String MyApp =  "/mnt/sdcard/goldingmedia/MyApp/";
    public static final String PushPath = "/mnt/sdcard/goldingmedia/push/";
    public static final String UpgradePath = "/mnt/sdcard/goldingmedia/upgrade/";
//category path
    public static final String HOTZONE_PATH = "/mnt/sdcard/goldingmedia/hotzone/";
    public static final String MOVIESSHOW_PATH = "/mnt/sdcard/goldingmedia/moviesshow/";
    public static final String GOLDING_PATH = "/mnt/sdcard/goldingmedia/golding/";
    public static final String GAME_PATH = "/mnt/sdcard/goldingmedia/game/";
    public static final String ELINE_PATH = "/mnt/sdcard/goldingmedia/elive/";
    public static final String MYAPP_PATH = "/mnt/sdcard/goldingmedia/myapp/";
    public static final String ADS_PATH = "/mnt/sdcard/goldingmedia/ads/";
//Sub Category path
    public static final String MEDIA_GOLDING = "golding";
    public static final String MEDIA_GDTV = "guangdongtv";
    public static final String MEDIA_WHALES = "whales";
    public static final String MEDIA_SDL = "shengdelin";
    public static final String GOLDING_JTV = "jtv";
    public static final String GOLDING_JMAGAZINE = "jmagazine";
    public static final String GOLDING_JMALL = "jmall";
    public static final String GAME_L = "lgame";
    public static final String GAME_M = "mgame";
    public static final String GAME_S = "sgame";
    public static final String E_MALL = "mall";
    public static final String E_HOTEL = "hotel";
    public static final String E_FOOD = "food";
    public static final String E_TRAVEL = "travel";
    public static final String MY_EBOOK = "ebook";
    public static final String MY_APP = "app";
    public static final String MY_SETTING = "setting";
    public static final String Upgrade = "upgrade";
    public static final String Push = "push";
    public final static String SeatNum = "";

    public static String PushTime = "0";

    public final static int TCP_PORT_NUMBER = 6666;
    public final static int TCP_PORT_NUMBER_SEND = 6668;

    public static final int FUNC_SELECT_FILE = 1;
    public static final int FUNC_PLAY_MODE = 2;
    public static final int FUNC_TIME_POSITION = 3;
    public static final int FUNC_REPETITION = 4;
    public static final int FUNC_SERVERVERSION = 5;
    public static final int FUNC_MEDIA_PID = 6;
    public static final int FUNC_VOD_FILE_LIST = 7;
    public static final int FUNC_PLAY_STATUS = 8;
    public static final int FUNC_FOLDER_FILE_LIST_GET = 9;
    public static final int FUNC_VOD_SELECT_ADS_FILE = 10;
    public static final int FUNC_VOD_BOARDCAST = 11;

    public static final int FUNC_STOP_STREAM = 12;
    public static final int FUNC_STOP_ADS_STREAM = 13;
    public static final int FUNC_GPS_TIME_GET = 14;
    public static final int FUNC_GPS_POSITION_GET = 15;

    public static final int FUNC_NM_EXECUTE_CONFIG = 16;

    public static final int FUNC_ADS_FILE_SET = 20;
    public static final int FUNC_ADS_FILE_PLAYSTATUS = 21;
    public static final int FUNC_SYSTEM_TIME_GET = 22;



    public class StatusCode{
        public static final int PLAY_STATUS_INVALID  = -1;
        public static final int PLAY_STATUS_SUCCESS  = 0;
        public static final int PLAY_STATUS_FILEEND = 1;
        public static final int PLAY_STATUS_FAILURE	= 2;
        public static final int PLAY_STATUS_FAILURE_NO_FILE = 3;
        public static final int PLAY_STATUS_FAILURE_NO_SDCARD = 4;
    }
    /**
     * @class WindowMessageID
     * @brief 内部消息ID定义类。
     * @author Jallen
     */
    public class MsgID {

        /**
         * @brief 刷新时间。
         */
        public static final int REFLESH_TIME = 0x00;
        /**
         * @brief 刷新数据。
         */
        public static final int REFLESH_DATA = 0x01;
        /**
         * @brief 刷新UI。
         */
        public static final int REFLESH_UI = 0x02;
        /**
         * @brief 刷新广告。
         */
        public static final int REFLESH_ADS = 0x03;

        /**
         * @brief 请求出错。
         */
        public final static int ERROR = 0x04;
        // 资源更新的消息
        public final static int DOWNLOAD_MSG_V = 0x10;
        public final static int DOWNLOAD_MSG_G = 0x11;
        public final static int NET_4G_NG = 0xff;
        public final static int NET_4G_OK = 0x00;
        public final static int NET_MOST_OK = 0x12;
    }


    public static final String KEY_TOPNUM = "topnum";

    //大类 ID
    public static final int CATEGORY_HOTZONE_ID= 1;
    public static final int CATEGORY_MEDIA_ID = 2;
    public static final int CATEGORY_GOLDING_ID = 3;
    public static final int CATEGORY_GAME_ID = 4;
    public static final int CATEGORY_ELIVE_ID = 5;
    public static final int CATEGORY_MYAPP_ID = 6;
    public static final int CATEGORY_ADS_ID= 7;
    public static final int CATEGORY_STATISTICS_ID = 8;
    public static final int CATEGORY_DIAGNOSE_ID = 9;
    public static final int CATEGORY_CONFIG_ID = 10;
    public static final int CATEGORY_SYSTEM_ID = 11;
    public static final int CATEGORY_LOGIN_ID = 12;
    public static final int CATEGORY_XFTP_ID = 13;
    public static final int CATEGORY_COMMAND_ID = 14;
    public static final int CATEGORY_ALL_ID = 255;
    //CATEGORY_HOTZONE_ID
    public static final int PROPERTY_HOTZONE_MEDIA_ID = 1;
   // CATEGORY_MEDIA_ID
    public static final int PROPERTY_MEDIA_GOLDING_ID	= 1;
    public static final int PROPERTY_MEDIA_GDTV_ID = 2;
    public static final int PROPERTY_MEDIA_WHALES_ID = 3;
    public static final int PROPERTY_MEDIA_SHENGDELIN_ID = 4;
    ////CATEGORY_GOLDING_ID
    public static final int PROPERTY_GOLDING_JTV_ID = 1;
    public static final int PROPERTY_GOLDING_MAGAZINE_ID = 2;
    public static final int PROPERTY_GOLDING_MALL_ID = 3;

    ////CATEGORY_GAME_ID
    public static final int PROPERTY_GAME_L_ID = 1 ;
    public static final int PROPERTY_GAME_M_ID = 2;
    public static final int PROPERTY_GAME_S_ID = 3;

    ////CATEGORY_ELIVE_ID
    public static final int PROPERTY_ELIVE_MALL_ID = 1;
    public static final int PROPERTY_ELIVE_HOTEL_ID = 2;
    public static final int PROPERTY_ELIVE_FOOD_ID = 3;
    public static final int PROPERTY_ELIVE_TRAVEL_ID = 4;
    ////CATEGORY_MYAPP_ID
    public static final int PROPERTY_MYAPP_EBOOK_ID = 1;
    public static final int PROPERTY_MYAPP_APP_ID = 2;
    public static final int PROPERTY_MYAPP_SETTING_ID = 3;

    ////CATEGORY_ADS_ID
    public static final int PROPERTY_ADS_MEDIA_ID = 1;
    public static final int PROPERTY_ADS_IMG_ID = 2;
    public static final int PROPERTY_ADS_TXT_ID = 3;

////CATEGORY_STATISTICS_ID
    public static final int PROPERTY_STATISTICS_MEDIA_ID = 1;
    public static final int PROPERTY_STATISTICS_ADS_ID = 2;
    public static final int PROPERTY_STATISTICS_PAY_ID = 3;
////CATEGORY_DIAGNOSE_ID
    public static final int PROPERTY_DIAGNOSE_DEVICE_ID = 1;
    public static final int PROPERTY_DIAGNOSE_SYSTEM_ID = 2;
////CATEGORY_CONFIG_ID
    public static final int PROPERTY_CONFIG_SYSTEM_ID = 1;
    public static final int PROPERTY_LAUNCHER_CONFIG_ID	= 2;
////CATEGORY_SYSTEM_ID
    public static final int PROPERTY_SYSTEM_INFO_ID = 1;
    public static final int PROPERTY_SYSTEM_GPS_ID = 2;
    public static final int PROPERTY_SYSTEM_VERIFACE_ID = 3;
////CATEGORY_LOGIN_ID
    public static final int PROPERTY_WEB_LOGIN_ID	 = 1;
    public static final int PROPERTY_SERVER_LOGIN_ID = 2;
    public static final int PROPERTY_MASTER_LOGIN_ID = 3;
    public static final int PROPERTY_TERMINAL_LOGIN_ID = 4;

////CATEGORY_XFTP_ID
    public static final int PROPERTY_XFTP_PUSH_ID	 = 1;
    public static final int PROPERTY_XFTP_UPGRADE_ID = 2;

////CATEGORY_COMMAND_ID
    public static final int PROPERTY_CMMD_RESPONSE_ID = 1;
    public static final int PROPERTY_CMMD_REQUEST_ID = 2;
    public static final int PROPERTY_CMMD_CUSTOM_ID = 3;
    public static final int PROPERTY_CMMD_RESOURSE_ID = 4;

////ADS_EXTEND_TYPE
    public static final int ADS_EXTEND_TYPE_ADS = 1;
    public static final int ADS_EXTEND_TYPE_SECURITY = 2;
    public static final int ADS_EXTEND_TYPE_WINDOW = 3;
    public static final int ADS_EXTEND_TYPE_EVEN = 4;
    public static final int ADS_EXTEND_TYPE_MGR = 5;
    //add for wendi Guangzhou
    public static final int ADS_EXTEND_TYPE_FIRST = 101;
    public static final int ADS_EXTEND_TYPE_SCREENOFF = 102;
    public static final int ADS_EXTEND_TYPE_PROMPT = 103;
    public static final int ADS_EXTEND_TYPE_FILMON= 104;
    public static final int ADS_EXTEND_TYPE_FILEPAUSE= 105;
    public static final int ADS_EXTEND_TYPE_HOMECOLUMN = 106;
    public static final int ADS_EXTEND_TYPE_MOVICEAREA = 107;
    public static final int ADS_EXTEND_TYPE_GAMEAREA = 108;



////ADS_WINDOW_ORIENT For windows
    public static final int ADS_WINDOW_ORIENT_All= 0;
    public static final int ADS_WINDOW_ORIENT_TOP = 1;
    public static final int ADS_WINDOW_ORIENT_MIDDLE = 2;
    public static final int ADS_WINDOW_ORIENT_BOTTOM = 3;

    ////ADS_WINDOW_ORIENT For prompt
    public static final int ADS_PROMPT_TOP= 1;
    public static final int ADS_PROMPT_BOTTOM = 2;

    public static final int ADS_ACTION_NULL = 0;
    public static final int ADS_ACTION_URL = 1;
    public static final int ADS_ACTION_SUB = 2;

    ////DEV_TYPE
    public static final int DEV_TYPE_MASTER = 1;
    public static final int DEV_TYPE_TERMINAL = 2;
    public static final int DEV_TYPE_SERVER = 3;

/* Message transfer route node */
    public static final int MESSAGE_ROUTE_TERMINAL = 1;
    public static final int MESSAGE_ROUTE_MASTER = 2;
    public static final int MESSAGE_ROUTE_SERVER = 3;
    public static final int MESSAGE_ROUTE_WEB	 = 4;

    public static final int MEDIA_TYPE_MOVIE = 1;
    public static final int MEDIA_TYPE_TVSHOW	 = 2;
    public static final int MEDIA_TYPE_CARTOON	 = 3;
    public static final int MEDIA_TYPE_SPORT = 4;
    public static final int MEDIA_TYPE_DRAMA	 = 5;
    public static final int MEDIA_TYPE_MTV	 = 6;
    public static final int MEDIA_TYPE_MUSIC	 = 7;

    public static final int CMD_REQUEST_MEDIA_PAY =1;
    public static final int REQUEST_MEDIA_DELETE = 2;
    public static final int CMD_REQUEST_MEDIA_GET = 3;
    public static final int CMD_REQUEST_MEDIA_STOP = 4;
    public static final int CMD_REQUEST_MEDIA_PLAY	= 5;
    public static final int CMD_REQUEST_MEDIA_SHOW	= 6;
    public static final int CMD_REQUEST_MASTER_NUMBER = 7;

    ///////////////////////////DB////////////////////////////////////
    public final static String TABLE_NAME_CATEGORY = "table_category";
    public final static String TABLE_NAME_HOTZONE = "table_hotzone";
    public final static String TABLE_NAME_MOVIESSHOW = "table_moviesshow";
    public final static String TABLE_NAME_GOLDING = "table_golding";
    public final static String TABLE_NAME_GAME = "table_game";
    public final static String TABLE_NAME_ELIVE = "table_elive";
    public final static String TABLE_NAME_MYAPP = "table_myapp";
    public final static String TABLE_NAME_ADS = "table_ads";
    public final static String TABLE_NAME_STATISTICS = "table_statistics";

    public final static int VERSION = 15;
   // public final static String DATABASE_NAME= "goldingmedia_sc.db";  //四川版本
    public final static String DATABASE_NAME= "goldingmedia_yg.db"; //粤港版本
   // public final static String DATABASE_NAME= "goldingmedia_hlj.db"; //黑龙江版本

   // public final static String PROTOBUFFER_NAME= "pushServiceAll_sc.bin";  //四川版本
   public final static String PROTOBUFFER_NAME= "pushServiceAll_yg.bin"; //粤港版本
    // public final static String PROTOBUFFER_NAME= "pushServiceAll_hlj.bin"; //黑龙江版本


    public final static String TABLE_NAME_MEDIAMETA= "table_mediameta";
    public final static String _ID = "_id";
    public final static String CATEGORY_ID = "category_id";
    public final static String CATEGORY_SUB_ID = "category_sub_id";
    public final static String TRUCK_SUB_INDEX = "truck_sub_index";
    public final static String TRUCK_SUB_DESC = "truck_sub_desc";
    public final static String TRUCK_FORMAT = "truck_format";

    public final static String UUID = "uuid";
    public final static String TRUCK_TITLE = "truck_title";
    public final static String TRUCK_IMAGE = "truck_image";
    public final static String TRUCK_FILENAME = "truck_filename";
    public final static String TRUCK_DESC = "truck_desc";
    public final static String TRUCK_PROVIDER = "truck_provider";
    public final static String TRUCK_EXTRA = "truck_extra";
    public final static String TRUCK_PERIOD = "truck_period";
    public final static String TRUCK_FAVOR = "truck_favor";
    public final static String TRUCK_INDEX = "truck_index";
    public final static String TRUCK_SHOW = "truck_show";
    public final static String TRUCK_VALID = "truck_valid";
    public final static String TRUCK_MEDIA_TYPE_ID = "truck_media_type_id";
    public final static String TRUCK_MEDIA_TYPE_THEME = "truck_media_type_theme";
    public final static String TRUCK_PUSH_NUM = "truck_push_num";
    public final static String TRUCK_TIMESTAMP= "truck_timestamp";

    public final static String TRUCK_EXTEND_TYPE= "truck_extend_type";
    public final static String TRUCK_WND_STYLE= "truck_wnd_style";
    public final static String TRUCK_WND_ORIENT = "truck_wnd_orient";
    public final static String TRUCK_PLAY_AREA= "truck_play_area";
    public final static String TRUCK_PLAY_PLAN= "truck_play_plan";

    //for update ads action
    public final static String TRUCK_ADS_ACTION = "truck_ads_action";
    public final static String TRUCK_ADS_URL = "truck_ads_url";
    public final static String TRUCK_PLAY_ORDER = "truck_play_order";
    public final static String TRUCK_ADS_ACTION_CATEGORY_ID = "truck_action_cate_id";
    public final static String TRUCK_ADS_ACTION_CATEGORY_SUB_ID = "truck_action_cate_subid";
    public final static String TRUCK_ADS_ACTION_CATEGORY_FILENAME = "truck_action_filename";

    public final static String TABLE_NAME_PAYMETA= "table_paymeta";
    public final static String PAY_TYPE = "pay_type";
    public final static String PAY_RESULT = "pay_result";
    public final static String PAY_STATE = "pay_state";
    public final static String PAY_FREE_TIME = "pay_free_time";
    public final static String PAY_VALUE = "pay_value";
    public final static String PAY_UNIT = "pay_unit";
    public final static String PAY_COUNTER = "pay_counter";
    public final static String PAY_INTERFACE = "pay_interface";
    public final static String PAY_MODE = "pay_mode";
    public final static String PAY_TIME = "pay_time";

    public final static String TABLE_NAME_PLAYERMETA= "table_playermeta";
    public final static String TOTAL_TIME = "total_time";
    public final static String CURRENT_TIME = "current_time";
    public final static String PLAY_COUNT = "play_count";
    public final static String PLAY_DELAY = "play_delay";
    public final static String PLAY_INTERVAL = "play_interval";
    public final static String PLAY_PRIOTITY = "play_priority";
    public final static String PLAY_STATE = "play_state";
//push db
    public final static String TABLE_NAME_PUSH= "table_pushservice";
    public final static String PUSH_VERSION = "version";
    public final static String PUSH_SOP = "sop";
    public final static String PUSH_GZURI = "gzUri";
    public final static String PUSH_MD5 = "md5";
    public final static String PUSH_TSURI = "tsUri";
    public final static String PUSH_TIMESTAMP = "times";
    public final static String PUSH_UPMODE = "upMode";
    public final static String PUSH_DONE = "isDone";
//upgrade db
    public final static String TABLE_NAME_UPGRADE= "table_upgradeservice";
    public final static String UPGRADE_URI = "uri";
    public final static String UPGRADE_MD5 = "md5";
    public final static String UPGRADE_FILENAME = "filename";
    public final static String UPGRADE_VERSION = "ver";
    public final static String UPGRADE_TYPE = "type";
    public final static String UPGRADE_TIMESTAMP = "times";
    public final static String UPGRADE_UPMODE = "upMode";
    public final static String UPGRADE_OVERWRITE = "overwrite";
    public final static String UPGRADE_DEVICE = "device";
    public final static String UPGRADE_DONE = "isDone";

    public static final String STATISTICS_ID = "id";                         //
    public static final String STATISTICS_DEV_TYPE = "dev_type";                       //
    public static final String STATISTICS_DEAT_NUMBER = "seat_number";                       //
    public static final String STATISTICS_DEVUCE_ID = "device_id";                       //
    public static final String STATISTICS_MASTER_ID = "master_id";                       //
    public static final String STATISTICS_TIMESTAMP = "timestamp";                       //
    public static final String STATISTICS_CARRIER_NUMBER = "carrier_number";                       //
    public static final String STATISTICS_MEDIA_TYPE = "media_type";                       //
    public static final String STATISTICS_MEDIA_UUID = "media_uuid";                       //
    public static final String STATISTICS_MEDIA_NAME = "media_name";                       //
    public static final String STATISTICS_START_TIME = "start_time";                       //
    public static final String STATISTICS_END_TIME = "end_time";                       //
    public static final String STATISTICS_PLAY_AREA = "play_area";                       //
    public static final String STATISTICS_PLAY_DURATION = "play_duration";                       //
    public static final String STATISTICS_CLICK_COUNT = "click_count";                       //
    public static final String STATISTICS_PAY_STATE = "pay_state";                       //
    public static final String STATISTICS_PAY_RESULT = "pay_result";                       //
    
	////1:full,2:Add,3:delete,4:update
    public final static int SOP_FULL = 1 ;
    public final static int SOP_ADD = 2 ;
    public final static int SOP_DEL= 3 ;
    public final static int SOP_UPDATE= 4 ;

    public  static String MasterId = "";
    public  static String UpgradeVersion = "";
    public  static int UpgradeDone = 0;

    public static final String REBOOT = "/system/bin/sh  /system/etc/reboot.sh";
    public static final String SWITCH_SPEAK = "/system/bin/sh /system/etc/switchspeak.sh";
    public static final String SWITCH_HEADPHONE = "/system/bin/sh /system/etc/switchheadphone.sh";

    /**
     * 获取大类，子类的路径
     * @param categoryId
     * @param categorySubId
     * @param folder
     * @return
     */
    public static  String getTruckMetaNodePath(int categoryId,int categorySubId,String folder,boolean isCreate){
        StringBuilder metaPathBuilder = new StringBuilder();
        switch(categoryId){
            case CATEGORY_HOTZONE_ID:
                metaPathBuilder.append(HOTZONE_PATH);
                createDir(metaPathBuilder.toString(),isCreate);
                break;
            case CATEGORY_MEDIA_ID:
                metaPathBuilder.append(MOVIESSHOW_PATH);
                createDir(metaPathBuilder.toString(),isCreate);
                if(PROPERTY_MEDIA_GOLDING_ID == categorySubId){
                    metaPathBuilder.append(MEDIA_GOLDING);
                }else if(PROPERTY_MEDIA_GDTV_ID == categorySubId){
                    metaPathBuilder.append(MEDIA_GDTV);
                }else if(PROPERTY_MEDIA_WHALES_ID == categorySubId){
                    metaPathBuilder.append(MEDIA_WHALES);
                }else if(PROPERTY_MEDIA_SHENGDELIN_ID == categorySubId){
                    metaPathBuilder.append(MEDIA_SDL);
                }
                break;
            case CATEGORY_GOLDING_ID:
                metaPathBuilder.append(GOLDING_PATH);
                createDir(metaPathBuilder.toString(),isCreate);
                if(categorySubId == PROPERTY_GOLDING_JTV_ID){
                    metaPathBuilder.append(GOLDING_JTV);
                }else  if(categorySubId == PROPERTY_GOLDING_MAGAZINE_ID){
                    metaPathBuilder.append(GOLDING_JMAGAZINE);
                } else  if(categorySubId == PROPERTY_GOLDING_MALL_ID){
                    metaPathBuilder.append(GOLDING_JMALL);
                }
                break;
            case CATEGORY_GAME_ID:
                metaPathBuilder.append(GAME_PATH);
                createDir(metaPathBuilder.toString(),isCreate);
                if(categorySubId == PROPERTY_GAME_L_ID){
                    metaPathBuilder.append(GAME_L);
                }else  if(categorySubId == PROPERTY_GAME_M_ID){
                    metaPathBuilder.append(GAME_M);
                } else  if(categorySubId == PROPERTY_GAME_S_ID){
                    metaPathBuilder.append(GAME_S);
                }
                break;
            case CATEGORY_ELIVE_ID:
                metaPathBuilder.append(ELINE_PATH);
                createDir(metaPathBuilder.toString(),isCreate);
                if(categorySubId == PROPERTY_ELIVE_MALL_ID){
                    metaPathBuilder.append(E_MALL);
                }else  if(categorySubId == PROPERTY_ELIVE_HOTEL_ID){
                    metaPathBuilder.append(E_HOTEL);
                } else  if(categorySubId == PROPERTY_ELIVE_FOOD_ID){
                    metaPathBuilder.append(E_FOOD);
                } else if(categorySubId == PROPERTY_ELIVE_TRAVEL_ID){
                    metaPathBuilder.append(E_TRAVEL);
                }

                break;
            case CATEGORY_MYAPP_ID:
                metaPathBuilder.append(MYAPP_PATH);
                createDir(metaPathBuilder.toString(),isCreate);
                if(categorySubId == PROPERTY_MYAPP_SETTING_ID){
                    metaPathBuilder.append(MY_SETTING);
                }else  if(categorySubId == PROPERTY_MYAPP_APP_ID){
                    metaPathBuilder.append(MY_APP);
                } else  if(categorySubId == PROPERTY_MYAPP_EBOOK_ID){
                    metaPathBuilder.append(MY_EBOOK);
                }
                break;

            case CATEGORY_ADS_ID:
                metaPathBuilder.append(ADS_PATH);
                createDir(metaPathBuilder.toString(),isCreate);
                switch (categorySubId) {
                    case PROPERTY_ADS_MEDIA_ID:
                        metaPathBuilder.append("av");
                        break;
                    case PROPERTY_ADS_IMG_ID:
                        metaPathBuilder.append("img");
                        break;
                    case PROPERTY_ADS_TXT_ID:
                        metaPathBuilder.append("txt");
                        break;
                }
                break;
            default:
                break;
        }
        createDir(metaPathBuilder.toString(),isCreate);
       return metaPathBuilder.append("/"+folder).toString();
    }
    /**
     * 在SD卡上创建目录
     * @return 文件目录
     */
    public static void createDir(String path,boolean isCreate){
        if(!isCreate){
            return;
        }
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdir();
        }
    }

    public static String getTabNameByCategoryId(int categoryid){
        switch (categoryid){
            case CATEGORY_HOTZONE_ID:
                return TABLE_NAME_HOTZONE;
            case CATEGORY_MEDIA_ID:
                return TABLE_NAME_MOVIESSHOW;
            case CATEGORY_GOLDING_ID:
                return TABLE_NAME_GOLDING;
            case CATEGORY_GAME_ID:
                return TABLE_NAME_GAME;
            case CATEGORY_ELIVE_ID:
                return TABLE_NAME_ELIVE;
            case CATEGORY_MYAPP_ID:
                return TABLE_NAME_MYAPP;
            case CATEGORY_ADS_ID:
                return TABLE_NAME_ADS;
            default:
                return "";
        }
    }

}
