package com.meitu.preload;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * $desc$
 *
 * @author Ljq $date$
 */

public class ManagerService extends Service {

    private static final String TAG = "ManagerService";
    private ServiceBinder binder = new ServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        createFloatView();
    }

    /**
     * 创建浮动窗口布局
     */
    private void createFloatView() {

    }


    public void setSpeed(String str) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, ManagerService.class));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY_COMPATIBILITY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    public class ServiceBinder extends Binder {
        public ManagerService getService() {
            return ManagerService.this;
        }
    }
}