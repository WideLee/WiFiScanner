package com.example.scanwifi.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;

import com.example.scanwifi.utils.ConstantValue;
import com.example.scanwifi.utils.FileUtils;

public class DialogFactory {
    private static DialogFactory mInstance;

    private Context mContext;

    public static synchronized DialogFactory getInstance(Context context) {
	if (mInstance == null) {
	    mInstance = new DialogFactory(context);
	}
	mInstance.setmContext(context);
	return mInstance;
    }

    private DialogFactory(Context context) {
	mContext = context;
    }

    private void setmContext(Context mContext) {
	this.mContext = mContext;
    }

    public Dialog getChooseMapDialog(final Handler handler) {
	final String[] files = FileUtils.getMapfileList(mContext);
	final Message msg = new Message();
	msg.what = ConstantValue.MSG_ACTIVITY_LOCAL_CHANGE_MAP;

	AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
	builder.setTitle("Choose Map");

	if (files == null || files.length == 0) {
	    builder.setMessage("Empty Map Set!");
	} else {
	    builder.setSingleChoiceItems(files, -1, new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
		    msg.obj = files[which];
		}
	    });
	    builder.setPositiveButton("OK", new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
		    if (msg.obj != null) {
			handler.sendMessage(msg);
		    }
		}
	    });
	}
	builder.setNegativeButton("Cancel", null);
	return builder.create();
    }
}
