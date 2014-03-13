package com.barbara.dowhat.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.barbara.dowhat.R;
import com.barbara.dowhat.adapter.BirthdayAdapter;
import com.barbara.dowhat.utility.MyFile;
import com.barbara.dowhat.utility.SQLdb;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.c;

public class BirthdayActivity extends SherlockActivity {
	private Context ctx;
	public static ListView lv;
	private SQLdb birthdayDB;
	public static Cursor myCursor;
	private SimpleCursorAdapter adapter;
	private Button btnTime, btnDay;
	private EditText etContent;
	private Calendar nowTime = Calendar.getInstance(Locale.CHINA);
	private Calendar setTime = (Calendar) nowTime.clone();
	private TimePickerDialog timePickerDialog;
	private DatePickerDialog datePickerDialog;
	private Menu menu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		setContentView(R.layout.birthday_activity);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getExtras();
		initView();
		setListener();
		initData();
		initViewData();
	}

	private void getExtras() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		}
	}

	private void initView() {
		lv = (ListView) findViewById(R.id.bithdat_lv);
		btnTime = (Button) findViewById(R.id.birthday_btn_time);
		btnDay = (Button) findViewById(R.id.birthday_btn_day);
		etContent = (EditText) findViewById(R.id.birthday_et_content);
	}

	private void setListener() {
		btnTime.setOnClickListener(new ButtonListener());
		btnDay.setOnClickListener(new ButtonListener());
	}

	private void initData() {
		birthdayDB = new SQLdb(ctx);
		myCursor = birthdayDB.selectBirthday();
	}

	private void initViewData() {
		setButtonText(nowTime);
		adapter = new BirthdayAdapter(this, R.layout.birthday_item, myCursor,
				new String[] { birthdayDB.FIELD_NAME, birthdayDB.FIELD_DAY },
				new int[] { R.id.tv_name, R.id.tv_day }, 0, birthdayDB);
		lv.setAdapter(adapter);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private class ButtonListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.birthday_btn_day:
				openDatePickerDialog();
				break;
			case R.id.birthday_btn_time:
				openTimePickerDialog(true);
				break;
			default:
				break;
			}
		}
	}

	private void openDatePickerDialog() {
		datePickerDialog = new DatePickerDialog(ctx, onDateSetListener,
				nowTime.get(Calendar.YEAR), nowTime.get(Calendar.MONTH),
				nowTime.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.setTitle("日期");
		datePickerDialog.show();
	}

	OnDateSetListener onDateSetListener = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			setTime.set(Calendar.YEAR, year);
			setTime.set(Calendar.MONTH, monthOfYear);
			setTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			setButtonText(setTime);
		}
	};

	public void openTimePickerDialog(boolean is24r) {
		timePickerDialog = new TimePickerDialog(ctx, onTimeSetListener,
				nowTime.get(Calendar.HOUR_OF_DAY),
				nowTime.get(Calendar.MINUTE) + 1, is24r);
		timePickerDialog.setTitle("时间");
		timePickerDialog.show();
	}

	OnTimeSetListener onTimeSetListener = new OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			setTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
			setTime.set(Calendar.MINUTE, minute);
			setTime.set(Calendar.SECOND, 0);
			setTime.set(Calendar.MILLISECOND, 0);
			if (setTime.compareTo(nowTime) <= 0) {
				setTime.add(Calendar.DATE, 1);
			}
			setButtonText(setTime);
		}
	};

	public void setButtonText(Calendar targetCal) {
		int year = targetCal.get(Calendar.YEAR);
		int month = targetCal.get(Calendar.MONTH) + 1;
		int day = targetCal.get(Calendar.DAY_OF_MONTH);
		int hours = targetCal.get(Calendar.HOUR_OF_DAY);
		int mnts = targetCal.get(Calendar.MINUTE);
		btnDay.setText(year + " - " + month + " - " + day);
		btnTime.setText(hours + " : " + mnts);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_birthday, menu);
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
		case R.id.menu_add: {
			addTodo();
			myCursor.requery();
			lv.invalidateViews();
			Toast.makeText(ctx, "添加成功", Toast.LENGTH_SHORT).show();
			break;
		}
		case R.id.menu_backup:
			backup();
			break;
		case R.id.menu_recover:
			recoveryFromBackup();
			break;
		}
		return true;
	}

	@SuppressLint("SimpleDateFormat")
	public void addTodo() {
		String content = etContent.getText().toString();
		if (content.equals("")) {
			Toast.makeText(ctx, "请输入姓名", Toast.LENGTH_SHORT).show();
			return;
		}
		long alarmTime = setTime.getTimeInMillis();
		Date tasktime = setTime.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		String currentDateandTime = sdf.format(tasktime);
		birthdayDB.insertBirthday(content, currentDateandTime, alarmTime);
		myCursor.moveToLast();
		int alarmID = myCursor.getInt(0);
		com.barbara.dowhat.utility.Common.startAlarm(ctx, alarmID, alarmTime,
				content);
	}

	private void backup() {
		myCursor = birthdayDB.selectBirthday();
		List<Map<String, Object>> record = new ArrayList<Map<String, Object>>();
		for (myCursor.moveToFirst(); !myCursor.isAfterLast(); myCursor
				.moveToNext()) {
			Map<String, Object> mMap = new HashMap<String, Object>();
			mMap.put("name", myCursor.getString(1));
			mMap.put("day", myCursor.getString(3));
			mMap.put("alarm", myCursor.getLong(2));
			record.add(mMap);
		}
		Yaml yaml = new Yaml();
		String msgYml = yaml.dump(record);

		MyFile.saveToYaml(getApplicationContext(), com.barbara.dowhat.constant.Constant.BACKUP_DIR,
				com.barbara.dowhat.constant.Constant.BACKUP_PATH,
				com.barbara.dowhat.constant.Constant.BIRTHDAY_TABLE_NAME,
				msgYml);
		Toast.makeText(getApplicationContext(), "备份成功", Toast.LENGTH_SHORT)
				.show();
	}

	private void recoveryFromBackup() {

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File extStorageDir = new File(com.barbara.dowhat.constant.Constant.BACKUP_PATH + "/"
					+ com.barbara.dowhat.constant.Constant.BIRTHDAY_TABLE_NAME);// 获得路径
			if (extStorageDir.exists()) {
				InputStream is;
				List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
				Yaml yaml = new Yaml();
				try {
					is = new FileInputStream(extStorageDir);
					records = (List<Map<String, Object>>) yaml.load(is);
					birthdayDB
							.clean(com.barbara.dowhat.constant.Constant.BIRTHDAY_TABLE_NAME);
					for (Map<String, Object> record : records) {
						birthdayDB.insertBirthday(record.get("name") + "",
								record.get("day") + "",
								com.barbara.dowhat.utility.Common
										.getLong(record.get("alarm")));
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		myCursor.requery();
		lv.invalidateViews();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (birthdayDB != null) {
			birthdayDB.close();
		}
		if (myCursor != null) {
			myCursor.close();
		}
	}

}
