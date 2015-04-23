package com.example.scanwifi.activity;

import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;

public class SettingActivity extends PreferenceActivity {

    private SettingFragment mSettingFragment;
    private MainHandler mHandler;

    static class MainHandler extends Handler {
	private WeakReference<SettingActivity> mActivity;

	public MainHandler(SettingActivity activity) {
	    mActivity = new WeakReference<SettingActivity>(activity);
	}

	@Override
	public void handleMessage(Message msg) {
	    SettingActivity activity = mActivity.get();
	    if (activity != null) {
		activity.handleMessage(msg);
	    }
	}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	mHandler = new MainHandler(this);

	mSettingFragment = new SettingFragment(mHandler);

	getFragmentManager().beginTransaction()
		.replace(android.R.id.content, mSettingFragment).commit();
    }

    public void handleMessage(Message msg) {
	mSettingFragment.handleMessage(msg);
    }
}
