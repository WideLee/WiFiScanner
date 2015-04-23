package com.example.scanwifi.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.example.scanwifi.R;
import com.example.scanwifi.R.xml;
import com.example.scanwifi.net.CentralHttpClient;
import com.example.scanwifi.object.MapInfo;
import com.example.scanwifi.utils.ConstantValue;
import com.example.scanwifi.utils.FileUtil;
import com.example.scanwifi.utils.HiThread;
import com.example.scanwifi.utils.JsonParser;
import com.example.scanwifi.utils.LanguageAdapter;
import com.example.scanwifi.view.DialogFactory;
import com.example.scanwifi.view.LoadingDialog;

public class SettingFragment extends PreferenceFragment {

    private Handler mHandler;

    private LanguageAdapter mLanguageAdapter;

    private Preference mUpdateMapPreference;
    private Preference mSelectMapPreference;
    private Preference mUpdateBeaconPreference;
    private Preference mUpdateAdPreference;
    private Preference mAboutAdPreference;
    private Preference mLanguagePreference;

    private PreferenceCategory mMapCategory;
    private PreferenceCategory mBeaconAdCategory;
    private PreferenceCategory mOtherCategory;

    private LoadingDialog mLoadingDialog;
    private ProgressDialog mProgressDialog;

    private boolean isCancel;

    class GetMessageThread extends HiThread {

	String url;
	int type;

	public GetMessageThread(String _url, int _type) {
	    url = _url;
	    type = _type;
	}

	@Override
	public void run() {
	    isCancel = false;
	    String result = CentralHttpClient.post(url);
	    if (result == null) {
		mHandler.sendEmptyMessage(ConstantValue.MSG_SETTING_GET_MESSAGE_FAIL);
	    } else {
		Message msg = new Message();
		msg.what = ConstantValue.MSG_SETTING_GET_MESSAGE_SUCCESS;
		Bundle bundle = new Bundle();
		bundle.putInt("info", type);
		bundle.putString("result", result);
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	    }
	}
    }

    class DownloadMessageThread extends HiThread {
	private static final int CACHE = 1024;
	private int map_id;
	private boolean action;

	public DownloadMessageThread(int _id, boolean _action) {
	    map_id = _id;
	    action = _action;
	}

	@Override
	public void run() {
	    String error = new String();

	    List<NameValuePair> paramList = new ArrayList<NameValuePair>();
	    paramList
		    .add(new BasicNameValuePair("id", Integer.toString(map_id)));
	    try {
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
			paramList, CentralHttpClient.CHARSET);
		HttpPost request = new HttpPost(ConstantValue.URL_DOWNLOAD_MAP);
		request.setEntity(entity);
		HttpClient client = CentralHttpClient.getHttpClient();
		HttpResponse response = client.execute(request);
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
		    Message msg = new Message();
		    msg.what = ConstantValue.MSG_SETTING_DOWNLOAD_MAP_FAIL;
		    msg.obj = mLanguageAdapter.getString("error_status_code")
			    + " " + response.getStatusLine().getStatusCode();
		    mHandler.sendMessage(msg);
		    return;
		}
		HttpEntity respEntity = response.getEntity();
		String filename = FileUtil.getFileName(response);
		if (filename == null) {
		    String data = EntityUtils.toString(respEntity,
			    CentralHttpClient.CHARSET);
		    String info = JsonParser.parseResultMessageJson(data);
		    Message msg = new Message();
		    msg.what = ConstantValue.MSG_SETTING_DOWNLOAD_MAP_FAIL;
		    if (info == null) {
			msg.obj = mLanguageAdapter.getString("unknown_error");
		    } else {
			msg.obj = info;
		    }
		    mHandler.sendMessage(msg);
		    return;
		}

		File dirFile = getActivity().getDir(
			ConstantValue.FILE_MAP_DIRECTORY, Context.MODE_PRIVATE);
		File objectFile = new File(dirFile.getAbsolutePath() + "/"
			+ filename);
		FileOutputStream outStream = new FileOutputStream(objectFile);
		InputStream inStream = respEntity.getContent();
		long length = respEntity.getContentLength();

		long count_len = 0;
		byte[] buffer = new byte[CACHE];
		int ch = 0;
		while ((ch = inStream.read(buffer)) != -1) {
		    outStream.write(buffer, 0, ch);
		    count_len += ch;
		    Message msg = new Message();
		    msg.what = ConstantValue.MSG_SETTING_DOWNLOAD_MAP_ING;
		    Bundle data = new Bundle();
		    data.putLong("total_length", length);
		    data.putLong("cur_length", count_len);
		    msg.setData(data);
		    mHandler.sendMessage(msg);
		}
		inStream.close();
		outStream.flush();
		outStream.close();
		boolean zipRes = FileUtil.unZip(objectFile.getPath(),
			objectFile.getParent() + "/" + map_id + "/");
		if (!zipRes) {
		    Message msg = new Message();
		    msg.what = ConstantValue.MSG_SETTING_DOWNLOAD_MAP_FAIL;
		    msg.obj = mLanguageAdapter.getString("error_zip");
		    mHandler.sendMessage(msg);
		    return;
		}
		objectFile.delete();
		boolean handleRes = FileUtil.handleNewMapData(getActivity(),
			map_id);
		if (!handleRes) {
		    Message msg = new Message();
		    msg.what = ConstantValue.MSG_SETTING_DOWNLOAD_MAP_FAIL;
		    msg.obj = mLanguageAdapter.getString("unknown_error");
		    mHandler.sendMessage(msg);
		    return;
		}

		if (action) {
		    String map_name = FileUtil.getMapNameByMapID(getActivity(),
			    map_id);
		    Message message = new Message();
		    message.what = ConstantValue.MSG_SETTING_MAP_INFO_PREPARED;
		    Bundle data = new Bundle();
		    data.putInt("map_id", map_id);
		    data.putString("map_name", map_name);
		    message.setData(data);
		    mHandler.sendMessage(message);
		} else {
		    Message msg = new Message();
		    msg.what = ConstantValue.MSG_SETTING_DOWNLOAD_MAP_SUCCESS;
		    msg.arg1 = map_id;
		    mHandler.sendMessage(msg);
		}
		return;
	    } catch (UnsupportedEncodingException e) {
		e.printStackTrace();
		error = e.toString();
	    } catch (ClientProtocolException e) {
		e.printStackTrace();
		error = e.toString();
	    } catch (IOException e) {
		e.printStackTrace();
		error = e.toString();
	    }

	    Message msg = new Message();
	    msg.what = ConstantValue.MSG_SETTING_DOWNLOAD_MAP_FAIL;
	    msg.obj = error;
	    mHandler.sendMessage(msg);
	}
    }

    public SettingFragment(Handler handler) {
	mHandler = handler;
	isCancel = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	addPreferencesFromResource(R.xml.activity_setting);

	getActivity().setResult(Activity.RESULT_CANCELED);
	mLanguageAdapter = LanguageAdapter.getInstance(getActivity());

	initViews();
	initLanguage();
	initListeners();
    }

    private void initViews() {
	mAboutAdPreference = findPreference("about");
	mSelectMapPreference = findPreference("change_map");
	mUpdateAdPreference = findPreference("update_ad");
	mUpdateBeaconPreference = findPreference("update_beacon");
	mUpdateMapPreference = findPreference("update_cur_map");
	mLanguagePreference = findPreference("language");

	mMapCategory = (PreferenceCategory) findPreference("map_relate");
	mBeaconAdCategory = (PreferenceCategory) findPreference("beacon_ad");
	mOtherCategory = (PreferenceCategory) findPreference("other");

	mLoadingDialog = new LoadingDialog(getActivity());
	mLoadingDialog.setOnCancelListener(new OnCancelListener() {

	    @Override
	    public void onCancel(DialogInterface dialog) {
		isCancel = true;
		Toast.makeText(getActivity(),
			mLanguageAdapter.getString("request_cancel"),
			Toast.LENGTH_SHORT).show();
	    }
	});
	mProgressDialog = new ProgressDialog(getActivity());
	mProgressDialog.setIndeterminate(true);
	mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	mProgressDialog.setCancelable(false);

	getPreferenceScreen().removePreference(mBeaconAdCategory);
	getPreferenceScreen().removePreference(mOtherCategory);
    }

    private void initLanguage() {
	getActivity().getActionBar().setTitle(
		mLanguageAdapter.getString("action_settings"));

	mMapCategory.setTitle(mLanguageAdapter.getString("setting_map_relate"));
	mBeaconAdCategory.setTitle(mLanguageAdapter
		.getString("setting_beacon_ad"));
	mOtherCategory.setTitle(mLanguageAdapter.getString("setting_other"));

	long ad_ts = FileUtil.getTimestampFromFile(getActivity(),
		ConstantValue.FILE_AD_INFO_NAME);
	if (ad_ts == 0) {
	    mUpdateAdPreference.setSummary(mLanguageAdapter
		    .getString("setting_update_ad_summary")
		    + mLanguageAdapter.getString("never_update"));
	} else {
	    mUpdateAdPreference.setSummary(mLanguageAdapter
		    .getString("setting_update_ad_summary") + new Date(ad_ts));
	}
	mUpdateAdPreference.setTitle(mLanguageAdapter
		.getString("setting_update_ad_title"));

	long beacon_ts = FileUtil.getTimestampFromFile(getActivity(),
		ConstantValue.FILE_BEACON_INFO_NAME);
	if (beacon_ts == 0) {
	    mUpdateBeaconPreference.setSummary(mLanguageAdapter
		    .getString("setting_update_beacon_summary")
		    + mLanguageAdapter.getString("never_update"));
	} else {
	    mUpdateBeaconPreference.setSummary(mLanguageAdapter
		    .getString("setting_update_beacon_summary")
		    + new Date(beacon_ts));
	}
	mUpdateBeaconPreference.setTitle(mLanguageAdapter
		.getString("setting_update_beacon_title"));

	MapInfo info = FileUtil.getCurrentMapInfoFromFile(getActivity());
	if (info == null) {
	    mUpdateMapPreference.setEnabled(false);
	} else {
	    mUpdateMapPreference.setEnabled(true);
	}
	long map_ts = FileUtil.getCurMapUpdateTimestamp(getActivity());
	if (map_ts == 0) {
	    mUpdateMapPreference.setSummary(mLanguageAdapter
		    .getString("setting_update_beacon_summary")
		    + mLanguageAdapter.getString("never_update"));
	} else {
	    mUpdateMapPreference.setSummary(mLanguageAdapter
		    .getString("setting_update_beacon_summary")
		    + new Date(map_ts));
	}
	mUpdateMapPreference.setTitle(mLanguageAdapter
		.getString("setting_update_cur_map_title"));

	if (info == null) {
	    mSelectMapPreference.setSummary(mLanguageAdapter
		    .getString("setting_change_map_summary")
		    + mLanguageAdapter.getString("never_update"));
	} else {
	    String curMapString = info.getMap_id() + "-" + info.getMap_name();
	    mSelectMapPreference.setSummary(mLanguageAdapter
		    .getString("setting_change_map_summary") + curMapString);
	}
	mSelectMapPreference.setTitle(mLanguageAdapter
		.getString("setting_change_map_title"));

	mLanguagePreference.setSummary(mLanguageAdapter
		.getString("setting_language_summary")
		+ " "
		+ mLanguageAdapter.getLanguageString());
	mLanguagePreference.setTitle(mLanguageAdapter
		.getString("title_select_language"));

	mAboutAdPreference.setSummary(mLanguageAdapter
		.getString("setting_about_summary"));
	mAboutAdPreference.setTitle(mLanguageAdapter
		.getString("setting_about_title"));

	mProgressDialog.setMessage(mLanguageAdapter
		.getString("dialog_downing_map"));
    }

    private void initListeners() {
	mAboutAdPreference
		.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		    @Override
		    public boolean onPreferenceClick(Preference preference) {
			mLoadingDialog.show();
			new GetMessageThread(
				ConstantValue.URL_GER_ABOUT_MESSAGE,
				ConstantValue.MSG_ABOUT_MESSAGE).start();
			return true;
		    }
		});

	mUpdateAdPreference
		.setOnPreferenceClickListener(new OnPreferenceClickListener() {

		    @Override
		    public boolean onPreferenceClick(Preference preference) {
			mLoadingDialog.show();
			new GetMessageThread(ConstantValue.URL_GET_AD_MESSAGE,
				ConstantValue.MSG_AD_MESSAGE).start();
			getActivity().setResult(Activity.RESULT_OK);
			return true;
		    }
		});

	mUpdateBeaconPreference
		.setOnPreferenceClickListener(new OnPreferenceClickListener() {

		    @Override
		    public boolean onPreferenceClick(Preference preference) {
			mLoadingDialog.show();
			new GetMessageThread(
				ConstantValue.URL_GET_BEACON_MESSAGE,
				ConstantValue.MSG_BEACON_MESSAGE).start();
			getActivity().setResult(Activity.RESULT_OK);
			return true;
		    }
		});

	mUpdateMapPreference
		.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		    @Override
		    public boolean onPreferenceClick(Preference preference) {
			MapInfo info = FileUtil
				.getCurrentMapInfoFromFile(getActivity());
			if (info != null) {
			    initProgressDialog();
			    new DownloadMessageThread(info.getMap_id(), false)
				    .start();
			}
			getActivity().setResult(Activity.RESULT_OK);
			return true;
		    }
		});

	mSelectMapPreference
		.setOnPreferenceClickListener(new OnPreferenceClickListener() {

		    @Override
		    public boolean onPreferenceClick(Preference preference) {
			mLoadingDialog.show();
			new GetMessageThread(
				ConstantValue.URL_GET_MAP_LIST_MESSAGE,
				ConstantValue.MSG_MAP_LIST_MESSAGE).start();
			getActivity().setResult(Activity.RESULT_OK);
			return true;
		    }
		});

	mLanguagePreference
		.setOnPreferenceClickListener(new OnPreferenceClickListener() {

		    @Override
		    public boolean onPreferenceClick(Preference preference) {
			DialogFactory.getInstance(getActivity())
				.getLanguageChoiceDialog(mHandler).show();
			Intent intent = new Intent();
			intent.putExtra("change_language", true);
			getActivity().setResult(Activity.RESULT_OK, intent);
			return true;
		    }
		});
    }

    private void initProgressDialog() {
	mProgressDialog.setProgress(0);
	mProgressDialog.show();
    }

    private void updateProgressDialog(long cur_len, long tot_len) {
	mProgressDialog.setMax((int) (tot_len / 1024));
	mProgressDialog.setIndeterminate(false);
	mProgressDialog.setProgress((int) (cur_len / 1024));
    }

    public void handleMessage(Message msg) {
	switch (msg.what) {
	case ConstantValue.MSG_SETTING_GET_MESSAGE_FAIL:
	    if (!isCancel) {
		mLoadingDialog.dismiss();
		Toast.makeText(getActivity(),
			mLanguageAdapter.getString("request_fail"),
			Toast.LENGTH_SHORT).show();
	    }
	    break;
	case ConstantValue.MSG_SETTING_GET_MESSAGE_SUCCESS:
	    if (!isCancel) {
		Bundle bundle = msg.getData();
		String data = bundle.getString("result");
		int info = bundle.getInt("info");
		if (info == ConstantValue.MSG_ABOUT_MESSAGE) {
		    handleAboutMessage(data);
		} else if (info == ConstantValue.MSG_BEACON_MESSAGE) {
		    // handleBeaconMessage(data);
		} else if (info == ConstantValue.MSG_AD_MESSAGE) {
		    // handleADMessage(data);
		} else if (info == ConstantValue.MSG_MAP_LIST_MESSAGE) {
		    handleMapListMessage(data);
		}
	    }
	    break;
	case ConstantValue.MSG_SETTING_MAP_LIST_SELECTED:
	    Bundle bundle = msg.getData();
	    int map_id = bundle.getInt("map_id");
	    String map_name = bundle.getString("map_name");
	    ArrayList<MapInfo> infos = FileUtil
		    .getLocalMapListFromFile(getActivity());
	    MapInfo info = new MapInfo(map_id, map_name);
	    if (infos.contains(info)) {
		Message newMessage = new Message();
		newMessage.what = ConstantValue.MSG_SETTING_MAP_INFO_PREPARED;
		newMessage.setData(bundle);
		mHandler.sendMessage(newMessage);
	    } else {
		initProgressDialog();
		new DownloadMessageThread(map_id, true).start();
	    }
	    break;
	case ConstantValue.MSG_SETTING_MAP_INFO_PREPARED:
	    Bundle data = msg.getData();
	    int id = data.getInt("map_id");
	    String name = data.getString("map_name");
	    boolean result = FileUtil.setCurrentMapInfo(getActivity(),
		    new MapInfo(id, name));
	    if (result) {
		Toast.makeText(getActivity(),
			mLanguageAdapter.getString("change_success"),
			Toast.LENGTH_SHORT).show();
		String curMapString = id + "-" + name;
		mSelectMapPreference
			.setSummary(mLanguageAdapter
				.getString("setting_change_map_summary")
				+ curMapString);
		MapInfo map_info = FileUtil
			.getCurrentMapInfoFromFile(getActivity());
		if (map_info == null) {
		    mUpdateMapPreference.setEnabled(false);
		} else {
		    mUpdateMapPreference.setEnabled(true);
		}
		long map_ts = FileUtil.getCurMapUpdateTimestamp(getActivity());
		if (map_ts == 0) {
		    mUpdateMapPreference.setSummary(mLanguageAdapter
			    .getString("setting_update_beacon_summary")
			    + mLanguageAdapter.getString("never_update"));
		} else {
		    mUpdateMapPreference.setSummary(mLanguageAdapter
			    .getString("setting_update_beacon_summary")
			    + new Date(map_ts));
		}
	    } else {
		Toast.makeText(getActivity(),
			mLanguageAdapter.getString("unknown_error"),
			Toast.LENGTH_SHORT).show();
	    }
	    mProgressDialog.dismiss();
	    break;
	case ConstantValue.MSG_SETTING_DOWNLOAD_MAP_FAIL:
	    String error = msg.obj.toString();
	    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
	    mProgressDialog.dismiss();
	    break;
	case ConstantValue.MSG_SETTING_DOWNLOAD_MAP_SUCCESS:
	    long map_ts = FileUtil.getCurMapUpdateTimestamp(getActivity());
	    if (map_ts == 0) {
		mUpdateMapPreference.setSummary(mLanguageAdapter
			.getString("setting_update_beacon_summary")
			+ mLanguageAdapter.getString("never_update"));
	    } else {
		mUpdateMapPreference.setSummary(mLanguageAdapter
			.getString("setting_update_beacon_summary")
			+ new Date(map_ts));
	    }
	    mProgressDialog.dismiss();
	    Toast.makeText(getActivity(),
		    mLanguageAdapter.getString("update_map_success"),
		    Toast.LENGTH_SHORT).show();
	    break;
	case ConstantValue.MSG_SETTING_DOWNLOAD_MAP_ING:
	    Bundle progress = msg.getData();
	    long tot_len = progress.getLong("total_length");
	    long cur_len = progress.getLong("cur_length");
	    updateProgressDialog(cur_len, tot_len);
	    break;
	case ConstantValue.MSG_SETTING_UPDATE_LANGUAGE:
	    initLanguage();
	    break;
	default:
	    break;
	}
    }

    private void handleMapListMessage(String data) {
	ArrayList<MapInfo> infos = JsonParser.parseMapInfoResultJson(data);
	if (infos != null && !infos.isEmpty()) {
	    MapInfo info = FileUtil.getCurrentMapInfoFromFile(getActivity());
	    int cur_index = 0;
	    if (info != null) {
		cur_index = infos.indexOf(info);
	    }
	    Dialog selectMapDialog = DialogFactory.getInstance(getActivity())
		    .getSelectMapListDialog(mHandler, infos, cur_index);
	    mLoadingDialog.dismiss();
	    selectMapDialog.show();
	} else if (infos.isEmpty()) {
	    mLoadingDialog.dismiss();
	    Toast.makeText(getActivity(),
		    mLanguageAdapter.getString("empty_map_set"),
		    Toast.LENGTH_SHORT).show();
	} else {
	    mLoadingDialog.dismiss();
	    Toast.makeText(getActivity(), JsonParser.getErrorMessage(),
		    Toast.LENGTH_SHORT).show();
	}
    }

    private void handleAboutMessage(String data) {
	mLoadingDialog.dismiss();
	DialogFactory.getInstance(getActivity()).getAboutMessageDialog(data)
		.show();
    }
}
