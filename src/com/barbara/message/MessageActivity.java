package com.barbara.message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import android.R.menu;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.barbara.dowhat.R;
import com.barbara.dowhat.utility.MyFile;
import com.barbara.dowhat.utility.SQLdb;
import com.umeng.analytics.MobclickAgent;

public class MessageActivity extends SherlockActivity {
	private Button bt_block;
	private Button bt_black;
	private EditText et_add;
	ListView lv;
	private ArrayList<String> number = new ArrayList<String>();
	private SQLdb messageDB;
	private Cursor myCursor;
	private final static String TABLE_NAME = "message";
	private MessageAdapter adapter;
	private Menu menu;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT) {
		// configActionBar();
		// }
		setContentView(R.layout.activity_message);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		bt_block = (Button) findViewById(R.id.mes_block_bt);
		bt_black = (Button) findViewById(R.id.mes_black_bt);
		et_add = (EditText) findViewById(R.id.mes_black_et);
		lv = (ListView) findViewById(R.id.mes_list);

		bt_block.setOnClickListener(new ButtonListener());
		bt_black.setOnClickListener(new ButtonListener());
		messageDB = new SQLdb(this);
		myCursor = messageDB.selectMessage();
		// messageDB.insertMessage("aaaa");
		// adapter = new SimpleCursorAdapter(this,
		// android.R.layout.simple_list_item_1, myCursor,
		// new String[] { messageDB.FIELD_NUM },
		// new int[] { android.R.id.text1 }, 0);
		adapter = new MessageAdapter(this, R.layout.message_list_item,
				myCursor, new String[] { messageDB.FIELD_NUM },
				new int[] { R.id.textView1 }, 0);
		lv.setAdapter(adapter);
	}

	// @TargetApi(11)
	// protected void configActionBar() {
	// getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	// }

	@Override
	public void onStart() {
		super.onStart();
		myCursor.requery();
		lv.invalidateViews();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private static class ViewHolder {
		public TextView numTv;
		public ImageButton delBtn;
	}

	private class MessageAdapter extends SimpleCursorAdapter {

		public MessageAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void bindView(View v, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder) v.getTag();
			holder.numTv.setText(cursor.getString(1));
			holder.delBtn.setVisibility(View.INVISIBLE);
			final int id = cursor.getInt(0);
			v.setOnTouchListener(new OnTouchListener() {

				// @Override
				// public void onClick(View v) {
				// TODO Auto-generated method stub
				// ViewHolder holder = (ViewHolder) v.getTag();
				// if(holder.delBtn.getVisibility() == View.VISIBLE){
				// holder.delBtn.setVisibility(View.INVISIBLE);
				// }else{
				// holder.delBtn.setVisibility(View.VISIBLE);
				// }
				// }
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					float DownX = 0;
					float UpX = 0;
					switch (event.getAction())// 根据动作来执行代码
					{
					case MotionEvent.ACTION_MOVE:// 滑动
						break;
					case MotionEvent.ACTION_DOWN:// 按下
						DownX = event.getX();
						break;
					case MotionEvent.ACTION_UP:// 松开
						UpX = event.getX();
						if (Math.abs(UpX - DownX) > 100) {
							ViewHolder holder = (ViewHolder) v.getTag();
							if (holder.delBtn.getVisibility() == View.VISIBLE) {
								holder.delBtn.setVisibility(View.INVISIBLE);
							} else {
								holder.delBtn.setVisibility(View.VISIBLE);
							}
						}
						break;
					default:
					}
					return true;
				}

			});

			holder.delBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					deleteOne(id);
				}
			});
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater
					.inflate(R.layout.message_list_item, parent, false);
			ViewHolder holder = new ViewHolder();
			holder.numTv = (TextView) v.findViewById(R.id.textView1);
			holder.delBtn = (ImageButton) v.findViewById(R.id.imageButton1);
			v.setTag(holder);
			return v;
		}

	}

	private class ButtonListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.mes_block_bt:
				Intent intent = new Intent();
				intent.setClass(MessageActivity.this, BlackActivity.class);
				startActivity(intent);
				break;
			case R.id.mes_black_bt:
				Intent intent2 = new Intent();
				intent2.setClass(MessageActivity.this, InboxActivity.class);
				startActivity(intent2);
				// finish();
				break;
			default:
				break;
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void addOne() {
		String num = et_add.getText().toString();
		if (num.equals("")){
			Toast.makeText(MessageActivity.this, "请输入号码", Toast.LENGTH_LONG)
			.show();
			return;
		}
		if (messageDB.queryMessage(num)) {
			Toast.makeText(MessageActivity.this, "已重复", Toast.LENGTH_LONG)
					.show();
			return;
		}
		messageDB.insertMessage(et_add.getText().toString());
		et_add.setText("");
		myCursor.requery();
		lv.invalidateViews();
		Toast.makeText(MessageActivity.this, "添加成功", Toast.LENGTH_LONG)
		.show();
	}

	@SuppressWarnings("deprecation")
	private void deleteOne(int id) {
		if (id == 0)
			return;
		messageDB.delete(id, TABLE_NAME);
		et_add.setText("");
		myCursor.requery();
		lv.invalidateViews();
		// adapter.notifyDataSetChanged();
		// myEditText.setText("");
		id = 0;
	}
	
	private void backup(){
		myCursor = messageDB.selectMessage();
		List<String> record = new ArrayList<String>();
		for(myCursor.moveToFirst();!myCursor.isAfterLast();myCursor.moveToNext())  {
			record.add(myCursor.getString(1));
		}
		Yaml yaml = new Yaml();
		String msgYml = yaml.dump(record);
		
//		Calendar calendar = Calendar.getInstance(Locale.CHINA);
//		DateFormat yamlFormat = new SimpleDateFormat("备份于"+MyFile.Yaml.shortTime);
//		menu.findItem(R.id.menu_backup).setTitle(yamlFormat.format(calendar.getTime()));
		
		MyFile.saveToYaml(getApplicationContext(), com.barbara.dowhat.constant.Constant.BACKUP_DIR, com.barbara.dowhat.constant.Constant.BACKUP_PATH, TABLE_NAME, msgYml);	
		Toast.makeText(getApplicationContext(), "备份成功", Toast.LENGTH_SHORT).show();
	}
	private void recoveryFromBackup(){
		
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File extStorageDir = new File(com.barbara.dowhat.constant.Constant.BACKUP_PATH + "/" + TABLE_NAME);// 获得路径
			if (extStorageDir.exists()) {
				InputStream is;
				List<String> records = new ArrayList<String>();
				Yaml yaml = new Yaml();
				try {
					is = new FileInputStream(extStorageDir);
					records = (List<String>)yaml.load(is);
					messageDB.clean(TABLE_NAME);
					for(String record:records){
						messageDB.insertMessage(record);
					}
//					records.addAll(record);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		myCursor.requery();
		lv.invalidateViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_message, menu);
		this.menu = menu;
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.menu_add:
			addOne();
			break;
		case R.id.menu_backup:
			backup();
			break;
		case R.id.menu_recover:		
			recoveryFromBackup();
			break;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (messageDB != null) {
			messageDB.close();
			// myCursor.close();
		}
		if (myCursor != null) {
			myCursor.close();
		}
	}
}
