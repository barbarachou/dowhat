package com.barbara.wifi;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.barbara.dowhat.R;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

public class InfoActivity extends SherlockActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT) {
		// configActionBar();
		// }
		setContentView(R.layout.layout_wifi);
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
