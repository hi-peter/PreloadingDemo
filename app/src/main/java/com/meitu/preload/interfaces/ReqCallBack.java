package com.meitu.preload.interfaces;

import java.io.IOException;

import okhttp3.Call;

/**
 * $desc$
 *
 * @author Ljq $date$
 */

public interface ReqCallBack<T> {

    void onSuccess(String result);

    void onFail(Call call, IOException e);
}
