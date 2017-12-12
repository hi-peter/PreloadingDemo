package com.meitu.preload.model;


import com.meitu.preload.info.DownloadInfo;

import io.reactivex.Observable;

/**
 * $desc$
 *
 * @author Ljq $date$
 */

public interface IDownload {

    Observable<DownloadInfo> download(DownloadInfo url);

    void cancel();
}
