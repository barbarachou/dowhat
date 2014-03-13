package com.barbara.dowhat.utility;

import java.util.Calendar;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Common {
	public static Long getLong(Object obj) {
		if (obj == null)
			return 0L;
		return Long.valueOf(obj + "");
	}

	public static void startAlarm(Context ctx, int id, long targetCal,
			String name) {
		Calendar now = Calendar.getInstance(Locale.CHINA);
		long nowTime = now.getTimeInMillis();
		if (nowTime > targetCal) {
			return;
		}
		AlarmManager alarmManager = (AlarmManager) ctx
				.getSystemService(Context.ALARM_SERVICE);
		Intent alarmIntent = new Intent(
				com.barbara.dowhat.constant.Constant.ALARM_ACTION_NAME);
		alarmIntent
				.putExtra(com.barbara.dowhat.constant.BundleKey.ALARM_ID, id);
		alarmIntent.putExtra(com.barbara.dowhat.constant.BundleKey.ALARM_NAME,
				name);

		PendingIntent alarmPI = PendingIntent.getBroadcast(ctx, id,
				alarmIntent, 0);

		alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal, alarmPI);

	}
}
