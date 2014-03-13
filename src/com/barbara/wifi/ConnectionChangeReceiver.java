package com.barbara.wifi;

import javax.security.auth.PrivateCredentialPermission;

import com.barbara.dowhat.utility.SQLdb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

/**
 * @author barbara
 *
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {
	boolean success = false;
	SQLdb db;
	private int mode = 0;
	private static int[] ringMode = { 0,
			AudioManager.RINGER_MODE_NORMAL | AudioManager.VIBRATE_SETTING_OFF,
			AudioManager.RINGER_MODE_VIBRATE,
			AudioManager.RINGER_MODE_NORMAL | AudioManager.VIBRATE_SETTING_ON,
			AudioManager.RINGER_MODE_SILENT };
	private Mode modeFunc;
	private interface Func {
		  void function();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		final AudioManager volMgr = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		db = new SQLdb(context);
		String action = intent.getAction();
		modeFunc = new Mode();
		Func[] func = new Func[] {
				  new Func() { public void function() { modeFunc.keep(volMgr); } },
				  new Func() { public void function() { modeFunc.ring(volMgr); } },
				  new Func() { public void function() { modeFunc.vibrate(volMgr); } },
				  new Func() { public void function() { modeFunc.ringAndVibrate(volMgr); } },
				  new Func() { public void function() { modeFunc.noRingAndVibrate(volMgr); } }				  
				};
		
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			Log.v("wifi_state", "BroadcastReceiver");
			// 获得网络连接服务
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			String ssidString = isWifi(connectivityManager, context);
			if (!ssidString.equals("")) {
				mode = db.queryWIFI(ssidString);
				mode = mode==5?0:mode;
				Log.v("wifi_state", "正使用wifi:" + ssidString + "mode=" + mode);
				success = true;
			}
			if (isGPRS(connectivityManager)) {
				mode = db.queryWIFI("GPRS");
				mode = mode==5?0:mode;
				Log.v("wifi_state", "正使用GPRS:" + "mode=" + mode);
				success = true;
			}
			if (!success) {
				mode = db.queryWIFI("无连接状态");
				mode = mode==5?0:mode;
				Log.v("wifi_state", "无连接状态:" + "mode=" + mode);
			}
//			if(mode != 0){
//				volMgr.setRingerMode(ringMode[mode]);
				func[mode].function();
//			}			
		}
		db.close();
	}

	private String isWifi(ConnectivityManager connectivityManager,
			Context context) {
		String ssid = "";
		NetworkInfo info = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (info != null && info.getState() == NetworkInfo.State.CONNECTED
				|| info.getState() == NetworkInfo.State.CONNECTING) {
			Log.v("wifi_state", "正使用wifi");
			// success = true;
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo connectionInfo = wifiManager.getConnectionInfo();
			if (connectionInfo != null
					&& !(connectionInfo.getSSID().equals(""))) {
				// if (connectionInfo != null &&
				// !StringUtil.isBlank(connectionInfo.getSSID())) {
				ssid = connectionInfo.getSSID();
				// mode = db.queryWIFI(ssid);
				// if((mode&7) != 0){
				// volMgr.setRingerMode(ringMode[mode]);
				// }
				// Log.v("wifi_state", "正使用wifi:" + ssid + "mode=" + mode );
			}

		}
		return ssid;
	}

	private boolean isGPRS(ConnectivityManager connectivityManager) {
		NetworkInfo mobileInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (mobileInfo != null
				&& mobileInfo.getState() == NetworkInfo.State.CONNECTED
				|| mobileInfo.getState() == NetworkInfo.State.CONNECTING) {
			return true;
			// mode = db.queryWIFI("GPRS");
			// Log.v("wifi_state", "正使用gpgs" + "mode=" + mode);
			// if((mode&7) != 0){
			// volMgr.setRingerMode(ringMode[mode]);
			// }

			// if(volMgr.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE){
			// volMgr.setRingerMode( AudioManager.RINGER_MODE_NORMAL);
			// }
			// success = true;
		} else {
			return false;
		}
	}
	

//	audio.adjustVolume(AudioManager.ADJUST_RAISE, 0);音量调节
//	audio.adjustVolume(AudioManager.ADJUST_LOWER, 0); 
	
}
