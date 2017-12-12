package com.meitu.preload.present;


import com.meitu.preload.info.DownloadInfo;

/**
 * $desc$
 *
 * @author Ljq $date$
 */

public interface IDownloadContract {

    interface View{

        void onSuccess();

        void onFail();

        void onProgress(long total, long progress);

        void setPresenter(Presenter presenter);


    }

    interface Presenter {

        void download(DownloadInfo downloadInfo);

        void cancel();

        void onRequestCountChange(int requestCount);
    }
}
