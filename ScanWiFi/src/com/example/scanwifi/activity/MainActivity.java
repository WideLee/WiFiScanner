package com.example.scanwifi.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scanwifi.R;
import com.example.scanwifi.net.CentralHttpClient;
import com.example.scanwifi.object.Position;
import com.example.scanwifi.utils.ConstantValue;
import com.example.scanwifi.utils.FileUtils;
import com.example.scanwifi.utils.HiThread;
import com.example.scanwifi.utils.PreferenceUtils;
import com.example.scanwifi.view.DialogFactory;
import com.example.scanwifi.view.mapview.MapDecorator;
import com.example.scanwifi.view.mapview.MapView;

public class MainActivity extends Activity {
    
    private Switch mSwitch;
    private TextView mTextView;
    private int mScanTime;
    private boolean mScanning;
    private WifiManager mWifiManager;
    private WiFiScanReceiver mWifiScanReceiver;
    private EditText mEditText;
    private int peroid;
    private String mMacAddr;

    private MapDecorator mDecorator;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
	public void handleMessage(Message msg) {
	    switch (msg.what) {
	    case ConstantValue.MSG_ACTIVITY_REMOTE_LOCATE_RESULT:
		mDecorator.updateMyLocation((Position) msg.obj);
		break;
	    case ConstantValue.MSG_ACTIVITY_LOCAL_CHANGE_MAP:
		PreferenceUtils.saveStringValue(MainActivity.this,
			PreferenceUtils.KEY_CURRENT_MAP, msg.obj.toString());
		initMapView();
		break;
	    default:
		break;
	    }
	}
    };

    public MainActivity() {
	mDecorator = new MapDecorator();
    }

    private HiThread mScanningThread = new HiThread() {

	@Override
	public void run() {
	    while (mScanning) {
		mWifiManager.startScan();

		try {
		    Thread.sleep(peroid);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	}
    };

    private HiThread mUpdatePositionThread = new HiThread() {

	@Override
	public void run() {
	    while (true) {
		try {

		    String result = CentralHttpClient
			    .post(ConstantValue.LOCATION_RESULT_URL
				    + "?client=" + mMacAddr.replace(":", ""));
		    if (result != null) {
			JSONObject object = new JSONObject(result);
			if (object.getBoolean("success")) {
			    Position loc = new Position(Float.valueOf(object
				    .getString("x")), Float.valueOf(object
				    .getString("y")));
			    Message msg = new Message();
			    msg.what = ConstantValue.MSG_ACTIVITY_REMOTE_LOCATE_RESULT;
			    msg.obj = loc;
			    mHandler.sendMessage(msg);
			}
		    }

		    Thread.sleep(2000);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		} catch (NumberFormatException e) {
		    e.printStackTrace();
		} catch (JSONException e) {
		    e.printStackTrace();
		}
	    }
	}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	setContentView(R.layout.activity_main);
	mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	mMacAddr = mWifiManager.getConnectionInfo().getMacAddress();
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
	    public void onCheckedChanged(CompoundButton buttonView,
		    boolean isChecked) {
		if (isChecked) {
		    mScanTime = 0;
		    mTextView.setText(Integer.toString(mScanTime));

		    if (mWifiManager.isWifiEnabled()) {
			mScanning = true;
			if (mEditText.getText().toString().equals("")) {
			    peroid = 0;
			} else {
			    peroid = Integer.valueOf(mEditText.getText()
				    .toString());
			}
			timestamp = System.currentTimeMillis();
			mScanningThread.start();
			mEditText.setEnabled(false);

			mEditor.putInt("peroid", peroid);
			mEditor.commit();
		    } else {
			Toast.makeText(MainActivity.this, "Not enabled wifi",
				Toast.LENGTH_SHORT).show();
			buttonView.setChecked(false);
		    }
		} else {
		    mScanning = false;
		    mEditText.setEnabled(true);
		    mScanningThread.stop();
		}
	    }
	});

	MapView mapView = (MapView) findViewById(R.id.mapview);
	mDecorator.setMapView(mapView);

	initMapView();

    }

    public void initMapView() {

	try {
	    String filename = PreferenceUtils.getStringValue(this,
		    PreferenceUtils.KEY_CURRENT_MAP);
	    if (!filename.equals("")) {
		File file = new File(FileUtils.getAppDir() + filename);
		if (file.exists()) {
		    FileInputStream inStream = new FileInputStream(file);
		    mDecorator.initNewMap(inStream);
		} else {
		    Toast.makeText(this, "empty map!", Toast.LENGTH_SHORT)
			    .show();
		}
	    } else {
		DialogFactory.getInstance(this).getChooseMapDialog(mHandler)
			.show();
	    }
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}

    }

    @Override
    protected void onResume() {
	registerReceiver(mWifiScanReceiver, new IntentFilter(
		WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	mUpdatePositionThread.start();
	super.onResume();
    }

    @Override
    protected void onStop() {
	unregisterReceiver(mWifiScanReceiver);
	mUpdatePositionThread.stop();
	super.onStop();
    }

    private long timestamp = 0;

    public class WiFiScanReceiver extends BroadcastReceiver {

	public WiFiScanReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
	    if (mScanning) {
		mTextView
			.setText(Integer.toString(++mScanTime)
				+ "  "
				+ Long.toString(System.currentTimeMillis()
					- timestamp));
		timestamp = System.currentTimeMillis();
	    }
	}
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
	if (item.getItemId() == R.id.action_search) {
	    DialogFactory.getInstance(this).getChooseMapDialog(mHandler).show();
	}
	return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.main, menu);
	return super.onCreateOptionsMenu(menu);
    }
}
