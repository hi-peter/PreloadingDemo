package com.meitu.preload.application;

import android.app.Application;

import com.meitu.preload.utl.ToastUtils;

/**
 * $desc$
 *
 * @author Ljq $date$
 */

public class PreloadApplication extends Application{

    private static PreloadApplication mApplication;

    public static PreloadApplication getInstance(){
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        ToastUtils.init(this);
    }
}
