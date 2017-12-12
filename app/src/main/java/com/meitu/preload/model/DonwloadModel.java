package com.meitu.preload.model;


import android.content.Context;
import android.util.Log;

import com.meitu.preload.info.DownloadInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * $desc$
 *
 * @author Ljq $date$
 */

public class DonwloadModel implements IDownload {

    private static final String TAG = DonwloadModel.class.getSimpleName();
    private OkHttpClient mClient;
    private Call mDownCall;
    private final Context mContext;

    public DonwloadModel(Context context) {
        mClient = new OkHttpClient.Builder().build();
        mContext = context.getApplicationContext();
    }

    @Override
    public Observable<DownloadInfo> download(final DownloadInfo downloadInfo) {
        return Observable.create(new DownloadSubscribe(downloadInfo));
    }

    @Override
    public void cancel() {
        if (mDownCall != null && !mDownCall.isCanceled()) {
            mDownCall.cancel();
            mDownCall = null;
        }
    }

    private class DownloadSubscribe implements ObservableOnSubscribe<DownloadInfo> {
        private DownloadInfo downloadInfo;

        public DownloadSubscribe(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void subscribe(ObservableEmitter<DownloadInfo> emitter) throws Exception {
            String url = downloadInfo.getUrl();
            long downloadLength = downloadInfo.getDownloadLength();//已经下载好的长度
            long contentLength = downloadInfo.getTotal();//文件的总长度
            //初始进度信息
            emitter.onNext(downloadInfo);
            Request.Builder builder = new Request.Builder().url(url);
            if (contentLength > 0) {
                builder.addHeader("RANGE", "bytes=" + downloadLength + "-" + contentLength);
            }
            Request request = builder.build();
            mDownCall = mClient.newCall(request);
            Response response = mDownCall.execute();

            File file = new File(mContext.getFilesDir(), downloadInfo.getFileID());
            InputStream is = null;
            FileOutputStream fileOutputStream = null;
            try {
                is = response.body().byteStream();
                if (contentLength <= 0) {
                    downloadInfo.setTotal(response.body().contentLength());
                }
                fileOutputStream = new FileOutputStream(file, true);
                byte[] buffer = new byte[2048];//缓冲数组2kB
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                    downloadLength += len;
                    downloadInfo.setDownloadLength(downloadLength);
                    emitter.onNext(downloadInfo);
                }
                fileOutputStream.flush();
            } catch (Exception e) {
                emitter.onError(e);
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }

            }
            emitter.onComplete();//完成
        }
    }
}
