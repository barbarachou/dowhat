package com.barbara.wifi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.barbara.dowhat.R;
import com.barbara.dowhat.utility.SQLdb;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class WifiActivity extends SherlockActivity {

	private SQLdb WIFIdb;
	private Cursor myCursor;
	public static WifiAdapter adapter;
	public static ListView lv;
	private int layout = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT) {
		// configActionBar();
		// }
		setContentView(R.layout.activity_wifi);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		WIFIdb = new SQLdb(this);
		myCursor = WIFIdb.selectWifi();
		lv = (ListView) findViewById(R.id.wifi_lv);

		adapter = new WifiAdapter(this, getData(), WIFIdb);
		lv.setAdapter(adapter);
		// setListAdapter(adapter);

	}

	// @TargetApi(11)
	// protected void configActionBar() {
	// getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	// }
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.clear();
		Log.v("wifi", "connect");
		Map<String, Object> map1 = new HashMap<String, Object>();
		int mode1 = WIFIdb.queryWIFI("无连接状态");
		if (mode1 == 5) {
			WIFIdb.insertWifi("无连接状态", 0);
			mode1 = 0;
		}
		map1.put("mode", mode1);
		map1.put("name", "无连接状态");
		list.add(map1);

		Map<String, Object> map0 = new HashMap<String, Object>();
		int mode0 = WIFIdb.queryWIFI("GPRS");
		if (mode0 == 5) {
			WIFIdb.insertWifi("GPRS", 0);
			mode0 = 0;
		}
		map0.put("mode", mode0);
		map0.put("name", "GPRS");
		list.add(map0);

		final WifiManager wifiManager = (WifiManager) this
				.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
		// String textStatus = "";
		for (WifiConfiguration config : configs) {
			Map<String, Object> map = new HashMap<String, Object>();
			String wifi = config.SSID.replace("\"", "");
			int mode = WIFIdb.queryWIFI(wifi);
			map.put("name", wifi);
			if (mode == 5) {// 数据列表无此WIFI
				WIFIdb.insertWifi(wifi, 0);
				mode = 0;
			}
			map.put("mode", mode);
			list.add(map);
			// textStatus = config.SSID;
			// Log.v("wifi",textStatus);
		}

		return list;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_wifi, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			setContentView(R.layout.activity_wifi);

			break;
		case R.id.menu_wifi:
			Intent intent = new Intent().setClass(WifiActivity.this,
					InfoActivity.class);
			startActivity(intent);
			break;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (WIFIdb != null) {
			WIFIdb.close();
		}
		if (myCursor != null) {
			myCursor.close();
		}
	}

}
