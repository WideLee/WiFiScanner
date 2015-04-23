package com.example.scanwifi.view;

import java.util.ArrayList;

import com.example.scanwifi.R;
import com.example.scanwifi.R.array;
import com.example.scanwifi.object.MapInfo;
import com.example.scanwifi.utils.ConstantValue;
import com.example.scanwifi.utils.LanguageAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

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

    public Dialog getAboutMessageDialog(String message) {
	AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
	builder.setTitle(
		LanguageAdapter.getInstance(mContext).getString(
			"setting_about_title")).setMessage(message);
	builder.setPositiveButton(LanguageAdapter.getInstance(mContext)
		.getString("positive"), null);
	return builder.create();
    }

    public Dialog getSelectMapListDialog(final Handler handler,
	    final ArrayList<MapInfo> maplist, int cur_index) {
	AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
	builder.setTitle(LanguageAdapter.getInstance(mContext).getString(
		"dialog_select_map"));
	String[] list = new String[maplist.size()];
	for (int i = 0; i < maplist.size(); i++) {
	    MapInfo info = maplist.get(i);
	    list[i] = info.getMap_id() + "-" + info.getMap_name();
	}
	builder.setSingleChoiceItems(list, cur_index, null);
	builder.setPositiveButton(LanguageAdapter.getInstance(mContext)
		.getString("positive"), new OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		int selectedPosition = ((AlertDialog) dialog).getListView()
			.getCheckedItemPosition();
		MapInfo info = maplist.get(selectedPosition);
		Message message = new Message();
		message.what = ConstantValue.MSG_SETTING_MAP_LIST_SELECTED;
		Bundle data = new Bundle();
		data.putInt("map_id", info.getMap_id());
		data.putString("map_name", info.getMap_name());
		message.setData(data);
		handler.sendMessage(message);
	    }
	});
	builder.setNegativeButton(LanguageAdapter.getInstance(mContext)
		.getString("negative"), null);
	return builder.create();
    }

    public Dialog getLanguageChoiceDialog(final Handler handler) {
	final LanguageAdapter languageAdapter = LanguageAdapter
		.getInstance(mContext);
	AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
	builder.setTitle(languageAdapter.getIdWithLanguageAdaptation("string",
		"title_select_language"));
	final DialogInterface.OnClickListener choiceListener = new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		languageAdapter.setCurrentLanguage(which);
	    }
	};
	builder.setSingleChoiceItems(R.array.language,
		languageAdapter.getLanguage(), choiceListener);

	DialogInterface.OnClickListener btnListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialogInterface, int which) {
		handler.sendEmptyMessage(ConstantValue.MSG_SETTING_UPDATE_LANGUAGE);
	    }
	};
	builder.setPositiveButton(languageAdapter.getIdWithLanguageAdaptation(
		"string", "positive"), btnListener);
	return builder.create();
    }
}
