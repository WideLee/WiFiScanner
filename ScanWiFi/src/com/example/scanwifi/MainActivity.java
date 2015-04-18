package com.example.scanwifi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Switch mSwitch;
	private TextView mTextView;
	private int mScanTime;
	private boolean mScanning;
	private WifiManager mWifiManager;
	private WiFiScanReceiver mWifiScanReceiver;
	private EditText mEditText;
	private int peroid;

	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor mEditor;

	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			mWifiManager.startScan();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_main);
		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		mScanning = false;
		mWifiScanReceiver = new WiFiScanReceiver();

		mSharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();

		mTextView = (TextView) findViewById(R.id.tv_edit_times);
		mSwitch = (Switch) findViewById(R.id.scan_switch);
		mEditText = (EditText) findViewById(R.id.et_peroid);
		mScanTime = 0;
		peroid = mSharedPreferences.getInt("peroid", 0);
		mTextView.setText(Integer.toString(mScanTime));
		mEditText.setText(Integer.toString(peroid));
		mSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					mScanTime = 0;
					mTextView.setText(Integer.toString(mScanTime));

					if (mWifiManager.isWifiEnabled()) {
						mScanning = true;
						mHandler.post(mRunnable);
						mEditText.setEnabled(false);
						if (mEditText.getText().toString().equals("")) {
							peroid = 0;
						} else {
							peroid = Integer.valueOf(mEditText.getText().toString());
						}
						mEditor.putInt("peroid", peroid);
						mEditor.commit();
					} else {
						Toast.makeText(MainActivity.this, "未开启wifi", Toast.LENGTH_SHORT).show();
					}
				} else {
					mScanning = false;
					mEditText.setEnabled(true);
					mHandler.removeCallbacks(mRunnable);
				}
			}
		});
	}

	@Override
	protected void onStart() {
		registerReceiver(mWifiScanReceiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		super.onStart();
	}

	@Override
	protected void onStop() {
		unregisterReceiver(mWifiScanReceiver);
		super.onStop();
	}

	public class WiFiScanReceiver extends BroadcastReceiver {

		public WiFiScanReceiver() {
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if (mScanning) {
				mTextView.setText(Integer.toString(++mScanTime));
				mHandler.postDelayed(mRunnable, peroid);
			}
		}
	}
}
