package com.meitu.preload.manager;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.meitu.preload.application.PreloadApplication;
import com.meitu.preload.info.DownloadInfo;
import com.meitu.preload.present.IDownloadContract;
import com.meitu.preload.present.impl.DownloadImpl;
import com.meitu.preload.utl.ToastUtils;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @author Ljq 2017/12/8
 */
public class PreloadingManager implements IDownloadContract.View {

    private static final String DEST_FILE_DIR = Environment.getExternalStorageState();
    private LinkedList<DownloadInfo> mData;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int requestCount = 0;
    private Timer mTimer;
    private IDownloadContract.Presenter mPresenter;
    private static PreloadingManager mInstance;

    private PreloadingManager() {

    }

    public static PreloadingManager getInstance() {
        if (mInstance == null) {
            synchronized (PreloadingManager.class) {
                if (mInstance == null) {
                    mInstance = new PreloadingManager();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onSuccess() {
        mData.removeFirst();

        if (!mData.isEmpty())
            mPresenter.download(mData.getFirst());
    }

    @Override
    public void onFail() {

    }

    @Override
    public void onProgress(long total, long current) {

    }

    @Override
    public void setPresenter(IDownloadContract.Presenter presenter) {
        mPresenter = presenter;
    }


    public class MyTimeTask extends TimerTask {

        @Override
        public void run() {
            if (requestCount == 0) {
                mPresenter.download(mData.getFirst());
            }
        }
    }

    public void setView(Context context, IDownloadContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public void preDownload(Context context, LinkedList<DownloadInfo> data) {
        if (mPresenter == null)
            DownloadImpl.bind(context).setView(this);
        if (data == null || data.isEmpty()) {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
            return;
        }
        mData = data;
        mTimer.schedule(new MyTimeTask(), 2000, 2000);
    }


    public void addRequestCount() {
        synchronized (PreloadingManager.this) {
            requestCount++;
            if (mPresenter != null) {
                mPresenter.cancel();
                mPresenter.onRequestCountChange(requestCount);
            }
        }
    }

    public void reduceRequestCount() {
        synchronized (PreloadingManager.this) {
            requestCount--;
            if (mPresenter != null) {
                mPresenter.onRequestCountChange(requestCount);
            }
        }
    }
}
