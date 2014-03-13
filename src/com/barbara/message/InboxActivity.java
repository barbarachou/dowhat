package com.barbara.message;

import java.util.ArrayList;
import java.util.HashMap;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.barbara.dowhat.R;
import com.barbara.dowhat.utility.MyFile;
import com.barbara.dowhat.utility.SQLdb;
import com.umeng.analytics.MobclickAgent;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.NavUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ListView;

public class InboxActivity extends SherlockListActivity {
	protected int _colThreadId = 0;
	protected int _colAddress = 0;
	protected int _colSubject = 0;
	protected int _colBody = 0;

	public static final String NUMBER = "number";
	public static final String CONTENT = "content";
	public static final String BLOCKED = "blocked";

	protected InboxAdapter _adapter;
	SQLdb myDB;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT) {
		// configActionBar();
		// }
		setContentView(R.layout.activity_inbox);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		Cursor c = getContentResolver().query(
				MyFile.SMS.INBOX_URI,
				new String[] { MyFile.SMS.THREAD_ID, MyFile.SMS.ADDRESS,
						MyFile.SMS.SUBJECT, MyFile.SMS.BODY },
				// exclude threads from contacts
				"person is null", null, null);

		if (null != c) {
			discoverColumnNames(c);

			SparseArray<ArrayList<String>> threads = new SparseArray<ArrayList<String>>(
					c.getCount());

			while (c.moveToNext()) {

				int threadId = c.getInt(_colThreadId);

				ArrayList<String> record = threads.get(threadId);
				if (null == record) {
					record = new ArrayList<String>();
					record.add(c.getString(_colAddress));
					threads.put(threadId, record);
				}

				record.add(c.getString(_colBody));
			}
			myDB = new SQLdb(this);

			// Sender sender = Sender.getInst(this);
			ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
			for (int i = threads.size() - 1; i >= 0; i--) {
				ArrayList<String> thread = threads.valueAt(i);
				HashMap<String, String> map = new HashMap<String, String>();
				String number = thread.get(0);
				map.put(NUMBER, number);
				thread.remove(0);
				map.put(CONTENT, join(thread, "\n"));
				// map.put(BLOCKED, "tttt");
				map.put(BLOCKED,
						myDB.queryMessage(number) ? getString(R.string.inbox_blocked)
								: getString(R.string.inbox_unblocked));

				data.add(map);
			}

			_adapter = new InboxAdapter(this, data, R.layout.inbox_list_item,
					new String[] { NUMBER, CONTENT, BLOCKED }, new int[] {
							R.id.title, R.id.content, R.id.blocked });

			setListAdapter(_adapter);
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

	// @TargetApi(11)
	// protected void configActionBar() {
	// getActionBar().setDisplayHomeAsUpEnabled(true);
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_inbox, menu);
		return true;
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

	protected void onListItemClick(ListView l, View v, int position, long id) {
		_adapter.toggleBlocked(v, position);
	}

	protected void discoverColumnNames(Cursor c) {
		_colThreadId = c.getColumnIndex(MyFile.SMS.THREAD_ID);
		_colAddress = c.getColumnIndex(MyFile.SMS.ADDRESS);
		_colSubject = c.getColumnIndex(MyFile.SMS.SUBJECT);
		_colBody = c.getColumnIndex(MyFile.SMS.BODY);
	}

	protected String join(ArrayList<String> s, String glue) {
		int k = s.size();
		if (k == 0)
			return null;
		StringBuilder out = new StringBuilder();
		out.append(s.get(0));
		for (int x = 1; x < k; ++x)
			out.append(glue).append(s.get(x));
		return out.toString();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (myDB != null) {
			myDB.close();
		}
	}

}
