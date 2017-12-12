package com.meitu.preload.interfaces;

/**
 * $desc$
 *
 * @author Ljq $date$
 */
public interface ReqProgressCallBack<T>  extends ReqCallBack<T>{
    /**
     * 响应进度更新
     */
    void onProgress(long total, long current);
}