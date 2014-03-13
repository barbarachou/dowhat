package com.barbara.dowhat.adapter;

import java.util.Calendar;
import java.util.Locale;

import com.barbara.dowhat.R;
import com.barbara.dowhat.activity.BirthdayActivity;
import com.barbara.dowhat.utility.SQLdb;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class BirthdayAdapter extends SimpleCursorAdapter {
	private SQLdb birthdayDB;
	private int id;
	private Context ctx;

	public BirthdayAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags, SQLdb myDB) {
		super(context, layout, c, from, to, flags);
		ctx=context;
		birthdayDB = myDB;
	}

	@Override
	public void bindView(View v, Context context, Cursor cursor) {

		ViewHolder holder = (ViewHolder) v.getTag();
		holder.delBtn.setVisibility(View.INVISIBLE);
		id = cursor.getInt(0);
		if (isViewPassed(cursor)) {
			setViewPassed(cursor, v);
		} else {
			setViewUnpassed(cursor, v);
		}
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewHolder holder = (ViewHolder) v.getTag();
				if (holder.delBtn.getVisibility() == View.VISIBLE) {
					holder.delBtn.setVisibility(4);
				} else {
					holder.delBtn.setVisibility(0);
				}
			}
		});

		holder.delBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteTodo();
				BirthdayActivity.myCursor.requery();
				BirthdayActivity.lv.invalidateViews();
			}
		});

	}
	
	private void deleteTodo() {
		if (id == 0)
			return;
		AlarmManager alarmManager = (AlarmManager) ctx
				.getSystemService(Context.ALARM_SERVICE);
		Intent alarmIntent = new Intent(com.barbara.dowhat.constant.Constant.ALARM_ACTION_NAME);
		PendingIntent alarmPI = PendingIntent.getBroadcast(ctx, id,
				alarmIntent, 0);
		if (alarmManager != null) {
			alarmManager.cancel(alarmPI);
		}
		birthdayDB.delete(id, com.barbara.dowhat.constant.Constant.BIRTHDAY_TABLE_NAME);
		Toast.makeText(ctx, "删除成功", Toast.LENGTH_SHORT).show();
	}

	private static class ViewHolder {
		public TextView name;
		public TextView day;
		public ImageButton delBtn;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.birthday_item, parent, false);
		ViewHolder holder = new ViewHolder();
		holder.name = (TextView) v.findViewById(R.id.tv_name);
		holder.day = (TextView) v.findViewById(R.id.tv_day);
		holder.delBtn = (ImageButton) v.findViewById(R.id.bt_del);
		v.setTag(holder);
		return v;
	}

	protected void setViewPassed(Cursor cursor, View v) {
		ViewHolder holder = (ViewHolder) v.getTag();
		holder.name.setText(cursor.getString(1));
		holder.day.setText(cursor.getString(3));
		holder.name.setTextColor(holder.name.getTextColors().withAlpha(130));
		holder.day.setTextColor(holder.day.getTextColors().withAlpha(130));
	}

	protected void setViewUnpassed(Cursor cursor, View v) {
		ViewHolder holder = (ViewHolder) v.getTag();
		holder.name.setText(cursor.getString(1));
		holder.day.setText(cursor.getString(3));
		holder.name.setTextColor(holder.name.getTextColors().withAlpha(255));
		holder.day.setTextColor(holder.day.getTextColors().withAlpha(255));
	}

	protected boolean isViewPassed(Cursor cursor) {
		long time = cursor.getLong(2);
		Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
		long now = dateAndTime.getTimeInMillis();
		return time < now;

	}
}
