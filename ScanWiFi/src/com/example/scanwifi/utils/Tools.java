package com.example.scanwifi.utils;

import java.io.File;

import com.example.scanwifi.BaseApplication;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Tools {
    public static String bytesToHexString(byte[] src) {
	StringBuilder stringBuilder = new StringBuilder("");
	if (src == null || src.length <= 0) {
	    return null;
	}
	for (int i = 0; i < src.length; i++) {
	    int v = src[i] & 0xFF;
	    String hv = Integer.toHexString(v);
	    if (hv.length() < 2) {
		stringBuilder.append(0);
	    }
	    stringBuilder.append(hv);
	}
	return stringBuilder.toString();
    }

    public static boolean isExternalStorageWritable() {
	String state = Environment.getExternalStorageState();
	if (Environment.MEDIA_MOUNTED.equals(state)) {
	    return true;
	}
	return false;
    }

    public static boolean isExternalStorageReadable() {
	String state = Environment.getExternalStorageState();
	if (Environment.MEDIA_MOUNTED.equals(state)
		|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	    return true;
	}
	return false;
    }

    public static String getExternalAppPath() {
	if (isExternalStorageWritable()) {
	    String path = Environment.getExternalStorageDirectory()
		    .getAbsolutePath() + ConstantValue.EXTERNAL_SD_DIR_NAME;
	    File file = new File(path);
	    if (!file.exists()) {
		file.mkdirs();
	    }
	    return path;
	}
	return null;
    }

    public static void showKeyBoard(View view) {
	InputMethodManager imm = (InputMethodManager) BaseApplication
		.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
		InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static void hideKeyBoard(View view) {
	InputMethodManager imm = (InputMethodManager) BaseApplication
		.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getString(int resId) {
	return BaseApplication.getContext().getString(resId);
    }

    public static int dip2px(float dip) {
	final float scale = BaseApplication.getContext().getResources()
		.getDisplayMetrics().density;
	return (int) (dip * scale + 0.5f);
    }

    public static float px2dip(float px) {
	final float scale = BaseApplication.getContext().getResources()
		.getDisplayMetrics().density;
	return px / scale + 0.5f;
    }

    private static int screenW = -1, screenH = -1;

    public static int getScreenW() {
	if (screenW < 0) {
	    initScreenDisplayParams();
	}
	return screenW;
    }

    public static int getScreenH() {
	if (screenH < 0) {
	    initScreenDisplayParams();
	}
	return screenH;
    }

    private static void initScreenDisplayParams() {
	DisplayMetrics dm = BaseApplication.getContext().getResources()
		.getDisplayMetrics();
	screenW = dm.widthPixels;
	screenH = dm.heightPixels;
    }

    public static Resources getResources() {
	return BaseApplication.getContext().getResources();
    }
}
