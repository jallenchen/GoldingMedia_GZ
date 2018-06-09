package com.goldingmedia;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.goldingmedia.jni.LcdPowerSwitch;
import com.goldingmedia.mvp.view.ui.Anticlockwise;

public class BlackActivity extends BaseActivity {
    private Anticlockwise mTimerV;
    private ConvenientBanner mBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black);
        mTimerV = (Anticlockwise) findViewById(R.id.timer);
        mBanner = (ConvenientBanner) findViewById(R.id.img_cb_full);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(LcdPowerSwitch.lcdGet() == 1){
            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }
}
