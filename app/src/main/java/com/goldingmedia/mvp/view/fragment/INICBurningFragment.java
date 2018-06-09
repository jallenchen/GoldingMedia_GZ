package com.goldingmedia.mvp.view.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.golding.goldinglauncher.flashActivity;
import com.goldingmedia.LauncherActivity;
import com.goldingmedia.R;
import com.goldingmedia.WelcomeActivity;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.utils.HandlerUtils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class INICBurningFragment extends Fragment implements HandlerUtils.OnReceiveMessageListener{

    private Context mContext;
    private int mFlashStatus = -1;
    private TextView mMsgText;
    private Button mBtn;
    private HandlerUtils.HandlerHolder handlerHolder;
    private Timer timer = new Timer();

    private TimerTask task = new TimerTask( ) {
        public void run ( ) {
            Message message = new Message( );
            message.what = 1;
            handlerHolder.sendMessage(message);
        }
    };

    private Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Log.e("","=====Start flash 81118 inic thread");
                flashActivity.onInit();
                mFlashStatus = flashActivity.onFlash();
                Message message = new Message( );
                message.what = 2;
                handlerHolder.sendMessage(message);

        }
    });

    @Override
    public void handlerMessage(Message msg) {
        switch (msg.what) {
            case 1:
                CancelDialog();
                timer.cancel();
                mMsgText.setVisibility(View.VISIBLE);
                mBtn.setVisibility(View.GONE);
                mMsgText.setText(R.string.timeout_inic);
                break;
            case 2:
                CancelDialog();
                timer.cancel();
                if(mFlashStatus == 0){ //烧录成功
                    mBtn.setVisibility(View.GONE);
                    mMsgText.setText(R.string.finish_inic);
                    mMsgText.setTextColor(0xffffffff);

                    File destDir = new File("sdcard/inicFlag");
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                    }
                    Intent intent = new Intent("com.goldingmedia.system.load.script");
                    intent.putExtra("scriptpath", Contant.REBOOT);
                    getActivity().sendBroadcast(intent);
                }
                mMsgText.setVisibility(View.VISIBLE);
                break;
        }
    }

    public INICBurningFragment() {
    }

    @SuppressLint("ValidFragment")
    public INICBurningFragment(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inic, container, false);
        mMsgText = (TextView)view.findViewById(R.id.msg);
        mBtn = (Button)view.findViewById(R.id.btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                File destDir = new File("sdcard/inicFlag");
                if (destDir.exists()) {
                    destDir.delete();
                }

                start();
            }
        });
        handlerHolder = new HandlerUtils.HandlerHolder(this);
        return view;
    }

    private AlertDialog mDialog = null;
    void ShowDialog() {
        if (mDialog == null) {
            AlertDialog.Builder b = new AlertDialog.Builder(mContext);
            mDialog = b.create();
            mDialog.setCancelable(false);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.inicdialog, null);
            WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
            params.x = WindowManager.LayoutParams.FILL_PARENT;
            params.y = WindowManager.LayoutParams.FILL_PARENT;
            mDialog.getWindow().setAttributes(params);
            mDialog.setView(view, 0, -50, 0, 0);
        }
        if(mDialog.isShowing() == false){
            mDialog.show();
        }

    }

    void CancelDialog() {
        if (mDialog != null) {
            if (mDialog.isShowing()) {
                mDialog.cancel();
                mDialog = null;
            }
        }
    }



    public void start() {
        ShowDialog();
        mFlashStatus = -1;
        timer.schedule(task, 500000); //5分钟后退出
        mThread.start();
    }
}
