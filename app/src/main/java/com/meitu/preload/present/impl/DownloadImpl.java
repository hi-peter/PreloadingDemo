package com.meitu.preload.present.impl;


import android.content.Context;
import android.support.annotation.NonNull;

import com.meitu.preload.info.DownloadInfo;
import com.meitu.preload.model.DonwloadModel;
import com.meitu.preload.model.IDownload;
import com.meitu.preload.present.IDownloadContract;
import com.meitu.preload.utl.RxJavaUtil;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author Ljq 2017/12/8
 */
public class DownloadImpl implements IDownloadContract.Presenter {

    private CompositeDisposable mCompositeDisposable;
    private IDownload mModel;
    private IDownloadContract.View mView;

    private DownloadImpl(Context context) {
        mModel = new DonwloadModel(context);
        mCompositeDisposable = new CompositeDisposable();
    }

    public static DownloadImpl bind(@NonNull Context context) {
        return new DownloadImpl(context);
    }

    public DownloadImpl setView(IDownloadContract.View view) {
        mView = view;
        mView.setPresenter(this);
        return this;
    }


    @Override
    public void download(DownloadInfo downloadInfo) {
        RxJavaUtil.run(mModel.download(downloadInfo), new Observer<DownloadInfo>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                mCompositeDisposable.add(disposable);
            }

            @Override
            public void onNext(DownloadInfo downloadInfo) {
                mView.onProgress(downloadInfo.getTotal(), downloadInfo.getDownloadLength());
            }

            @Override
            public void onError(Throwable throwable) {
                mView.onFail();
            }

            @Override
            public void onComplete() {
                mView.onSuccess();
            }
        });
    }

    @Override
    public void cancel() {
        if (mModel != null)
            mModel.cancel();
        mCompositeDisposable.clear();
    }

    @Override
    public void onRequestCountChange(int requestCount) {

    }
}
