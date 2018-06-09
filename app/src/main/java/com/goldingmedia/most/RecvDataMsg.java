package com.goldingmedia.most;

import com.goldingmedia.GDApplication;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.MediaMetaProtos;
import com.goldingmedia.goldingcloud.PayMetaProtos;
import com.goldingmedia.goldingcloud.PlayerMetaProtos;
import com.goldingmedia.goldingcloud.PushServiceProtos;
import com.goldingmedia.utils.NLog;
import com.goldingmedia.utils.NToast;
import com.goldingmedia.utils.ProtoDataParse;
import com.goldingmedia.utils.Utils;

/**
 * Created by Jallen on 2017/8/19 0019 11:27.
 */

public class RecvDataMsg {
    private static final String TAG = "RecvDataMsg";
    private final static int DATA_MAX_LEN = (1024 * 128);
    private byte[] data_buff = new byte[DATA_MAX_LEN];
    private int data_size = 0;
    private static int DATA_HEAD_LEN = 3;
    private static final int PROTOBUFF_HEAD_LEN = 8;
    private static final int PROTOBUFF_ECC_LEN = 4;
    private static final int PROTOBUFF_EXTRA_LEN = 12; //PROTOBUFF_HEAD_LEN+PROTOBUFF_ECC_LEN

    public boolean onParseDataMsg(byte[] buff,int len)  {
        int i = 0,j = 0;
        boolean bFind = false;
        for(i = 0; i < len; i++) {
            if(Utils.toHex(buff[i]).equals("eb") && Utils.toHex(buff[i+1]).equals("eb")  && Utils.toHex(buff[i+2]).equals("90")) {
                bFind = true;
                break;
            }
            j++;
        }
        System.arraycopy(data_buff,j,data_buff,0,data_size-j);
        data_size -= j;
        if(false == bFind) {
            return false;
        }

        if(data_size < PROTOBUFF_HEAD_LEN)
            return false;


        int frame_size = 0;
        byte[] protosize = new byte[2];
        protosize[0] = data_buff[6];
        protosize[1] = data_buff[7];
        frame_size = Utils.getShort(protosize);
        if(frame_size <= 0) {
            /* skip protobuf data size is 0 */
            data_size -= PROTOBUFF_EXTRA_LEN;
            System.arraycopy(data_buff,PROTOBUFF_EXTRA_LEN,data_buff,0,data_size);
            return false;
        }

        if((frame_size + PROTOBUFF_EXTRA_LEN) >  data_size)
            return false;

        /* parse protobuf frame data */
        try {
            onRmRecvDataMsg(data_buff,frame_size+PROTOBUFF_EXTRA_LEN);
        } catch (Exception e) {
            data_size = 0;
            e.printStackTrace();
            return false;
        }

        data_size -= PROTOBUFF_EXTRA_LEN+frame_size;
        System.arraycopy(data_buff,PROTOBUFF_EXTRA_LEN+frame_size,data_buff,0,data_size);
        return true;
    }

    public  void onRecvDataMsg(byte[] msg,int len) throws Exception{
        if(data_size >= DATA_MAX_LEN) {
            data_size = 0;
        }
        System.arraycopy(msg,0,data_buff,data_size,len);
        data_size += len;

        while(data_size > PROTOBUFF_HEAD_LEN) {
            if(!onParseDataMsg(data_buff,data_size))
                break;
        }
    }


    public void onRmRecvDataMsg(byte[] data,int protoSize) throws Exception{
        byte[] proto_buff = new byte[protoSize-PROTOBUFF_EXTRA_LEN];
        System.arraycopy(data,PROTOBUFF_HEAD_LEN,proto_buff,0,protoSize-PROTOBUFF_EXTRA_LEN);
        byte[] checkSum = new byte[PROTOBUFF_ECC_LEN];
        System.arraycopy(data,protoSize-PROTOBUFF_ECC_LEN,checkSum,0,PROTOBUFF_ECC_LEN);

        int categoryId = data[3];
        int categorySubId = data[4];
        NLog.d(TAG,"categoryId:"+categoryId+"--categorySubId:"+categorySubId);

        GDApplication.post2UIRunnable(new Runnable() {
            @Override
            public void run() {
                NToast.longToast(GDApplication.getmInstance(),"接收到主机消息："+categoryId);
            }
        });



        //TODO arlt32 校验
        if(Utils.intAdler32CheckSum(proto_buff) != Utils.getInt(checkSum)){
            NLog.e(TAG,"Adler32CheckSum Error");
            return;
        }

        ProtoDataParse protoDataParse = new ProtoDataParse(GDApplication.getmInstance());
        protoDataParse.ParseProto(categoryId,categorySubId,proto_buff);
    }

}
