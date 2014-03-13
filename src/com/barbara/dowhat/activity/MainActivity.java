package com.barbara.dowhat.activity;

import cn.sharesdk.framework.ShareSDK;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.barbara.dowhat.R;
import com.barbara.dowhat.R.id;
import com.barbara.dowhat.R.layout;
import com.barbara.dowhat.R.menu;
import com.barbara.message.MessageActivity;
import com.barbara.social.SocialActivity;
import com.barbara.wifi.WifiActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends SherlockActivity {
	private FeedbackAgent agent;
	private Button btn01, btn02, btn03, btn04;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		UmengUpdateAgent.update(this);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		ShareSDK.initSDK(this);

		btn01 = (Button) findViewById(R.id.main_btn_01);
		btn02 = (Button) findViewById(R.id.main_btn_02);
		btn03 = (Button) findViewById(R.id.main_btn_03);
		btn04 = (Button) findViewById(R.id.main_btn_04);
		btn01.setOnClickListener(new ButtonListener());
		btn02.setOnClickListener(new ButtonListener());
		btn03.setOnClickListener(new ButtonListener());
		btn04.setOnClickListener(new ButtonListener());

		agent = new FeedbackAgent(this);
		// agent.sync();//无法通知用户反馈回复
	}

	private class ButtonListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			switch (arg0.getId()) {
			case R.id.main_btn_01:
				intent.setClass(MainActivity.this, BirthdayActivity.class);
				break;
			case R.id.main_btn_02:
				intent.setClass(MainActivity.this, MessageActivity.class);
				break;
			case R.id.main_btn_03:
				intent.setClass(MainActivity.this, WifiActivity.class);
				break;
			case R.id.main_btn_04:
				intent.setClass(MainActivity.this, SocialActivity.class);
				break;
			default:
				break;
			}
			startActivity(intent);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ShareSDK.stopSDK(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			agent.startFeedbackActivity();
			break;
		}
		return true;
	}

	// private List<Map<String, Object>> getData() {
	// List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	//
	// Map<String, Object> map = new HashMap<String, Object>();
	// map.put("image1", R.drawable.list);
	// map.put("text1", "备忘    ");
	// map.put("image2", R.drawable.notice);
	// map.put("text2", "提醒");
	// list.add(map);
	//
	// Map<String, Object> map2 = new HashMap<String, Object>();
	// map2.put("image1", R.drawable.mail);
	// map2.put("text1", "黑名单");
	// map2.put("image2", R.drawable.denied);
	// map2.put("text2", "短信屏蔽");
	// list.add(map2);
	//
	// Map<String, Object> map3 = new HashMap<String, Object>();
	// map3.put("image1", R.drawable.rsss);
	// map3.put("text1", "WIFI    ");
	// map3.put("image2", R.drawable.flower);
	// map3.put("text2", "情景模式");
	// list.add(map3);
	//
	// Map<String, Object> map4 = new HashMap<String, Object>();
	// map4.put("image1", R.drawable.rsss);
	// map4.put("text1", "发状态");
	// map4.put("image2", R.drawable.flower);
	// map4.put("text2", "社交");
	// list.add(map4);
	//
	// return list;
	// }

	// @Override
	// protected void onListItemClick(ListView l, View v, int position, long id)
	// {
	// super.onListItemClick(l, v, position, id);
	// Intent intent = new Intent();
	// switch (position) {
	// case 0:
	// intent.setClass(MainActivity.this, BirthdayActivity.class);
	// break;
	// case 1:
	// intent.setClass(MainActivity.this, MessageActivity.class);
	// break;
	// case 2:
	// intent.setClass(MainActivity.this, WifiActivity.class);
	// break;
	// case 3:
	// intent.setClass(MainActivity.this, SocialActivity.class);
	// break;
	// default:
	// break;
	// }
	// startActivity(intent);
	// }
}
