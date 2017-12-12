package com.meitu.preload.utl;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtils {

	protected static Toast toast;
	private static Context mContext;

	public static  void init(Context context){
		mContext = context;
	}

	public static void showToast(final String text) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			public void run() {
				if (toast == null) {
					toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
				} else {
					toast.setText(text);
				}
				toast.show();
			}
		});
	}

	public static void showToast(final int textId) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			public void run() {
				if (toast == null) {
					toast = Toast
							.makeText(mContext, textId, Toast.LENGTH_SHORT);
				} else {
					toast.setText(textId);
				}
				toast.show();
			}
		});
	}

	public static void showToastLong(Context context, final String text) {
		mContext = context;
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			public void run() {
				if (toast == null) {
					toast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
				} else {
					toast.setText(text);
				}
				toast.show();
			}
		});
	}
}
