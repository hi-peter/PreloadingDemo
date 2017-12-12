package com.meitu.preload.utl;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Ljq 2017/12/1
 */
public class RxJavaUtil {

    public static <T extends Object> Disposable run(Observable<T> ob, Consumer<T> cs) {
        return ob.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cs);
    }

    public static <T extends Object> void run(Observable<T> ob, Observer<T> cs) {
        ob.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cs);
    }
}
