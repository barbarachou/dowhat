package com.barbara.message;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.barbara.dowhat.R;
import com.barbara.dowhat.utility.MyFile;
import com.umeng.analytics.MobclickAgent;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.widget.TextView;

public class BlackDetailActivity extends SherlockActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_black_detail);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT){
		// configActionBar();
		// }

		TextView fromV = (TextView) findViewById(R.id.bd_from);
		TextView timeV = (TextView) findViewById(R.id.bd_time);
		// TextView matchedRuleV = (TextView)findViewById(R.id.matched_rule);
		TextView textV = (TextView) findViewById(R.id.bd_text);

		Intent intent = getIntent();
		fromV.setText("from: " + intent.getStringExtra(com.barbara.dowhat.constant.Constant.Message.FROM));
		timeV.setText(intent.getStringExtra(com.barbara.dowhat.constant.Constant.Message.SENT_AT));
		textV.setText(intent.getStringExtra(com.barbara.dowhat.constant.Constant.Message.TEXT));
		// matchedRuleV.setText("caught by: " +
		// intent.getStringExtra(MyFile.Message.MATCHED_RULE));
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	// @TargetApi(11)
	// protected void configActionBar() {
	// getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	// }

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// getMenuInflater().inflate(R.menu.activity_black_detail, menu);
	// return true;
	// }

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
