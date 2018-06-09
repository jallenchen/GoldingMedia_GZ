package com.goldingmedia;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.goldingmedia.utils.NLog;

/**
 * Created by Jallen on 2017/10/8 0008 10:57.
 */

class ActivityLifecycleListener implements Application.ActivityLifecycleCallbacks {
    private int refCount = 0;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        refCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        NLog.e("ActivityLifecycleListener","onActivityResumed:"+refCount);
            Intent intent = new Intent("com.goldingmedia.backkey");
            intent.putExtra("backkey",refCount);
            GDApplication.getmInstance().sendBroadcast(intent);
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        refCount--;
        NLog.e("ActivityLifecycleListener","onActivityStopped:"+refCount);
        if(refCount == 0){
            Intent intent = new Intent("com.goldingmedia.backkey");
            intent.putExtra("backkey",0);
            GDApplication.getmInstance().sendBroadcast(intent);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
