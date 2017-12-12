package com.meitu.preload.task;

import android.os.Handler;
import android.util.Log;

import com.meitu.preload.interfaces.ReqProgressCallBack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 
 * @author Ljq 2017/12/8
 */
public class DownCallback implements Callback{

    private static final String TAG = DownCallback.class.getSimpleName();
    private File mFile;
    private ReqProgressCallBack mReqCallBack;
    private Handler mHandler;

    public DownCallback(File file, final ReqProgressCallBack reqCallBack, Handler hndler){
        mFile = file;
        mReqCallBack = reqCallBack;
        this.mHandler = hndler;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        Log.e(TAG, e.toString() + "");
        mReqCallBack.onFail(call,  e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            long total = response.body().contentLength();
            Log.e(TAG, "total------>" + total);
            long current = 0;
            is = response.body().byteStream();
            fos = new FileOutputStream(mFile);
            while ((len = is.read(buf)) != -1) {
                current += len;
                fos.write(buf, 0, len);
                Log.e(TAG, "current------>" + current);
                mReqCallBack.onProgress(total, current);
            }
            fos.flush();
            mReqCallBack.onSuccess(mFile.getPath());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
//            failedCallBack("下载失败", callBack);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}
