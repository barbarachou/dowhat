package com.barbara.dowhat.entity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class NotifyUtil {
	
	@SuppressWarnings("deprecation")
	public static void addAppNotify(Context context, int iconID, String name, int alarmID){
//		Intent notificationIntent = new Intent(context, BirthdayActivity.class);
//		  PendingIntent contentIntent = PendingIntent.getActivity(context,
//				  alarmID, notificationIntent,
//				  PendingIntent.FLAG_ONE_SHOT);
		
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); 
		Notification notification = new Notification(); 
		notification.icon = iconID; //通知图标
		notification.tickerText = "new notice"; //通知的内容
		notification.defaults=Notification.DEFAULT_SOUND; //通知的铃声
		notification.audioStreamType= android.media.AudioManager.ADJUST_LOWER; 
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent intent = new Intent(); 
		intent.setComponent(new ComponentName("com.barbara.Alarm","com.barbara.Alarm.BirthdayActivity"));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, alarmID, intent, PendingIntent.FLAG_ONE_SHOT); 
		// 点击状态栏的图标出现的提示信息设置 
		notification.setLatestEventInfo(context,"提醒",name, pendingIntent);
		manager.notify(alarmID, notification);
	}
}
