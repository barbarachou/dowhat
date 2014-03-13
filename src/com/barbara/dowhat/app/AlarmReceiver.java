package com.barbara.dowhat.app;

import com.barbara.dowhat.R;
import com.barbara.dowhat.entity.NotifyUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (com.barbara.dowhat.constant.Constant.ALARM_ACTION_NAME
				.equals(intent.getAction())) {

			NotifyUtil.addAppNotify(context, R.drawable.note48,
					intent.getStringExtra(com.barbara.dowhat.constant.BundleKey.ALARM_NAME),
					intent.getIntExtra(com.barbara.dowhat.constant.BundleKey.ALARM_ID, -1));

		}
	}
}