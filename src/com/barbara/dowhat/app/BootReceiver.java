package com.barbara.dowhat.app;

import com.barbara.dowhat.utility.SQLdb;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

public class BootReceiver extends BroadcastReceiver {
	private SQLdb myDB;
	private Cursor myCursor;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			myDB = new SQLdb(context);
			myCursor = myDB.selectBirthday();
			if (myCursor.moveToFirst()) {
				while (myCursor.moveToNext()) {
					long alarmTime = myCursor.getLong(2);
					int alarmID = myCursor.getInt(0);
					String name = myCursor.getString(1);
					com.barbara.dowhat.utility.Common.startAlarm(context,
							alarmID, alarmTime, name);
				}
			}
			myDB.close();
			if (myCursor != null) {
				myCursor.close();
			}
		}
	}

}