package com.example.scanwifi.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;

public class LoadingDialog {

    private ProgressDialog mProgressDialog;
    private Context mContext;
    private boolean isShowDialog = true;
    private OnDismissListener mOnDismissListener;
    private OnCancelListener mOnCancelListener;

    public LoadingDialog(Context context) {
	this.mContext = context;
	initProgressDialog();
    }

    private void initProgressDialog() {
	mProgressDialog = new ProgressDialog(mContext,
		ProgressDialog.STYLE_SPINNER);
	mProgressDialog.setMessage("Loading...");
	mProgressDialog.setCanceledOnTouchOutside(false);

	mProgressDialog.setOnDismissListener(mOnDismissListener);
	mProgressDialog.setOnCancelListener(mOnCancelListener);
    }

    public void setOnDismissListener(OnDismissListener mOnDismissListener) {
	this.mOnDismissListener = mOnDismissListener;
	mProgressDialog.setOnDismissListener(mOnDismissListener);
    }

    public void setOnCancelListener(OnCancelListener mOnCancelListener) {
	this.mOnCancelListener = mOnCancelListener;
	mProgressDialog.setOnCancelListener(mOnCancelListener);
    }

    public boolean getIsShowDialog() {
	return isShowDialog;
    }

    public ProgressDialog setIsShowDialog(boolean isShowDialog) {
	this.isShowDialog = isShowDialog;
	return getProgressDialog();
    }

    public ProgressDialog getProgressDialog() {
	return mProgressDialog;
    }

    public Context getContext() {
	return mContext;
    }

    public void setMessage(String msg) {
	if (getProgressDialog() != null) {
	    getProgressDialog().setMessage(msg);
	}
    }

    public void setContext(Context context) {
	this.mContext = context;
    }

    public void show() {
	if (mProgressDialog == null) {
	    initProgressDialog();
	}
	mProgressDialog.show();
    }

    public void dismiss() {
	mProgressDialog.dismiss();
    }

}
