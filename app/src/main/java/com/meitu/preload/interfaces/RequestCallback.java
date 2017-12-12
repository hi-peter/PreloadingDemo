package com.meitu.preload.interfaces;

/**
 * 网络请求回调
 *
 * @author Ljq 2017/12/7
 */
public interface RequestCallback {

    void onSuccess(String result);

    void onFail(int errorCode, String message);
}
