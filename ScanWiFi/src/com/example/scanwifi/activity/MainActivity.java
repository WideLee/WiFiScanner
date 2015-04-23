package com.example.scanwifi.activity;

import java.util.List;

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
import com.example.scanwifi.R.id;
import com.example.scanwifi.R.layout;
import com.example.scanwifi.R.menu;
import com.example.scanwifi.net.CentralHttpClient;
import com.example.scanwifi.object.FloorMap;
import com.example.scanwifi.object.MapInfo;
import com.example.scanwifi.object.Position;
import com.example.scanwifi.utils.ConstantValue;
import com.example.scanwifi.utils.FileUtil;
import com.example.scanwifi.utils.HiThread;
import com.example.scanwifi.utils.JsonParser;
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

    private MapDecorator mDecorator;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private Handler mHandler = new Handler() {
	public void handleMessage(Message msg) {
	    switch (msg.what) {
	    case ConstantValue.MSG_ACTIVITY_REMOTE_LOCATE_RESULT:
		mDecorator.updateMyLocation((Position) msg.obj);
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
		String result = CentralHttpClient
			.post(ConstantValue.LOCATION_RESULT_URL);
		MapInfo info = FileUtil
			.getCurrentMapInfoFromFile(MainActivity.this);
		if (result != null && info != null) {
		    String[] coor = result.replaceAll(" ", "").split(",");
		    Position loc = new Position(Float.valueOf(coor[0]),
			    Float.valueOf(coor[1]), info.getMap_id());
		    Message msg = new Message();
		    msg.what = ConstantValue.MSG_ACTIVITY_REMOTE_LOCATE_RESULT;
		    msg.obj = loc;
		    mHandler.sendMessage(msg);
		}
		try {
		    Thread.sleep(2000);
		} catch (InterruptedException e) {
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
	MapInfo info = FileUtil.getCurrentMapInfoFromFile(this);
	if (info != null) {
	    long map_id = info.getMap_id();

	    long cur_id = mDecorator.getCurrentMapFloorId();
	    if (map_id != cur_id) {

		List<FloorMap> maps = mDecorator.getFloorList();

		int index = -1;
		for (int i = 0; i < maps.size(); i++) {
		    FloorMap map = maps.get(i);
		    if (map.getInfo().getMap_id() == info.getMap_id()) {
			index = i;
			break;
		    }
		}

		FloorMap newMap = null;
		if (index == -1) {
		    String json = FileUtil.getMapJsonByID(this,
			    info.getMap_id());
		    if (json != null) {
			newMap = JsonParser.parseMapFloorJson(json);
			mDecorator.addFloorMap(newMap);

		    }
		} else {
		    newMap = maps.get(index);
		}
		if (newMap != null) {
		    mDecorator.changeFloor(newMap);
		}
	    }
	}
    }

    @Override
    protected void onStart() {
	registerReceiver(mWifiScanReceiver, new IntentFilter(
		WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	mUpdatePositionThread.start();
	super.onStart();
    }

    @Override
    protected void onStop() {
	unregisterReceiver(mWifiScanReceiver);
	mUpdatePositionThread.stop();
	super.onStop();
    }

    public class WiFiScanReceiver extends BroadcastReceiver {

	public WiFiScanReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
	    if (mScanning) {
		mTextView.setText(Integer.toString(++mScanTime));
	    }
	}
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
	if (item.getItemId() == R.id.action_settings) {
	    Intent i = new Intent(MainActivity.this, SettingActivity.class);
	    startActivityForResult(i, ConstantValue.ACTION_UPDATE_DATA);
	}
	return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	switch (requestCode) {
	case ConstantValue.ACTION_UPDATE_DATA:
	    if (resultCode == RESULT_OK) {
		initMapView();
	    }
	    break;
	default:
	    break;
	}
	super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.main, menu);
	return super.onCreateOptionsMenu(menu);
    }
}
