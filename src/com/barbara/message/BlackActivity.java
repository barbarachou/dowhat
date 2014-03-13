package com.barbara.message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import com.barbara.dowhat.utility.MyFile;
import com.umeng.analytics.MobclickAgent;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.ListView;

public class BlackActivity extends SherlockListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT) {
		// configActionBar();
		// }
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File extStorageDir = new File(
					com.barbara.dowhat.constant.Constant.Message.STORE_PATH);// 获得路径
			if (extStorageDir.exists()) {
				List<String> filenames = new ArrayList<String>();
				Map<String, File> name2file = new HashMap<String, File>();// 文件名对应字符串
				for (File file : extStorageDir.listFiles()) {
					String filename = file.getName();
					filenames.add(filename);
					name2file.put(filename, file);
				}

				Collections.sort(filenames, Collections.reverseOrder());

				List<Map<String, String>> records = new ArrayList<Map<String, String>>();
				Yaml yaml = new Yaml();
				for (String filename : filenames) {
					File file = name2file.get(filename);
					InputStream is;
					try {
						is = new FileInputStream(file);
						Map<String, String> record = (Map<String, String>) yaml
								.load(is);
						records.add(record);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				BlackAdapter adapter = new BlackAdapter(this, records);
				setListAdapter(adapter);
			}
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Map<String, String> record = (Map<String, String>) getListAdapter()
				.getItem(position);
		Intent intent = new Intent(this, BlackDetailActivity.class);
		intent.putExtra(com.barbara.dowhat.constant.Constant.Message.FROM,
				record.get(com.barbara.dowhat.constant.Constant.Message.FROM));
		intent.putExtra(
				com.barbara.dowhat.constant.Constant.Message.SENT_AT,
				record.get(com.barbara.dowhat.constant.Constant.Message.SENT_AT));
		intent.putExtra(com.barbara.dowhat.constant.Constant.Message.TEXT,
				record.get(com.barbara.dowhat.constant.Constant.Message.TEXT));
		startActivity(intent);
	}

}
