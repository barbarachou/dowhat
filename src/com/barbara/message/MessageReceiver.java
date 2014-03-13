package com.barbara.message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.damazio.notifier.event.receivers.mms.EncodedStringValue;
import org.damazio.notifier.event.receivers.mms.PduHeaders;
import org.damazio.notifier.event.receivers.mms.PduParser;
import org.yaml.snakeyaml.Yaml;

import com.barbara.dowhat.utility.MyFile;
import com.barbara.dowhat.utility.SQLdb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsMessage;
import android.util.Log;

public class MessageReceiver extends BroadcastReceiver {
	private static final String TAG = "MessageReceiver";
	private static final String MMS_DATA_TYPE = "application/vnd.wap.mms-message";

	protected static ArrayList<String> contactNumbers;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle uinf = intent.getExtras();
		
		//彩信
		if (MMS_DATA_TYPE.equalsIgnoreCase(intent.getType())) {
			PduParser parser = new PduParser();
			PduHeaders headers = parser.parseHeaders(intent
					.getByteArrayExtra("data"));
			if (headers == null) {
				Log.e(TAG, "Couldn't parse headers for WAP PUSH.");
				return;
			}

			int messageType = headers.getMessageType();
			// Log.v(TAG, "WAP PUSH message type: 0x" +
			// Integer.toHexString(messageType));

			// Check if it's a MMS notification
			if (messageType == PduHeaders.MESSAGE_TYPE_NOTIFICATION_IND) {
				String from = null;
				EncodedStringValue encodedFrom = headers.getFrom();
				if (encodedFrom != null) {
					from = encodedFrom.getString();//sender's number
				}

				// Log.d(TAG, "mms from " + from);
				// Log.d(TAG, "text: ");

				if (null != from) {

					if (isSpam(from, context)) {
						// Log.d(TAG, "filtered mms from " + from);

						// headers don't contain a `sent at` time
						Date date = new Date();
						persistMessage(from, "mms message notification",
								date.getTime(), context);

						abortBroadcast();
					}
				}
			} else {
				Log.d(TAG, "other type of mms: " + messageType);
			}

			// ---
			// SMS
			// ---
		} else {
			// boolean _isSpam = false;
			Object[] pdus = (Object[]) uinf.get("pdus");
			if (null != pdus) {
				String from = null;
				Long timestamp = null;
//				boolean matchedRule = false;
				StringBuffer msgBodyBuffer = new StringBuffer();

				for (Object bytes : pdus) {
					SmsMessage sms = SmsMessage.createFromPdu((byte[]) bytes);
					msgBodyBuffer.append(sms.getMessageBody());

					if (null == from) {
						from = sms.getOriginatingAddress();
					}

					if (null == timestamp) {
						timestamp = sms.getTimestampMillis();
					}
				}

				String text = msgBodyBuffer.toString();
//				matchedRule = isSpam(from, context);

				if (isSpam(from, context)) {
					persistMessage(from, text, timestamp, context);

					abortBroadcast();

					// Log.i("> w <", String.format("blocked: [%s] %s", from,
					// text));
				} // else {
					// Log.i("> w <", String.format("passed: [%s] %s", from,
					// text));
					// }
			}
		}
	} // onReceive

	protected static boolean isSpam(String from, Context context) {
//		if (null == contactNumbers) {
//			loadContacts(context);
//		}
		SQLdb messageDB = new SQLdb(context);
//		Cursor myCursor = messageDB.selectMessage();
		if (messageDB.queryMessage(from)){
			messageDB.close();
			return true;
		}else{
			messageDB.close();
			return false;
		}

//		from = from.replaceAll("[ +_-]", "");
//		String matchedRule = SenderFilter.isSpam(from, context);
//
//		if (null == matchedRule) {
//			for (String contactNumber : contactNumbers) {
//				if (from.endsWith(contactNumber)) {
//					return null;
//				}
//			}
//		}
//
//		if (null == matchedRule) {
//			matchedRule = KeywordFilter.isSpam(text, context);
//		}
//		

//		return matchedRule;
	} // isSpam

	protected static void loadContacts(Context context) {
		Uri contactDataUri = Phone.CONTENT_URI;

		Cursor c = context.getContentResolver().query(contactDataUri,
				new String[] { Phone.NUMBER }, Phone.MIMETYPE + " = ?",
				new String[] { Phone.CONTENT_ITEM_TYPE }, null);

		contactNumbers = new ArrayList<String>();
		while (c.moveToNext()) {
			String contactNumber = c.getString(0).replaceAll("[ +_-]", "");
			contactNumbers.add(contactNumber);
		}
	}

//来源，正文，时间，规则
	private void persistMessage(String from, String text, Long timestamp, Context context) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		DateFormat yamlFormat = new SimpleDateFormat(MyFile.Yaml.DateTimeFormat);

		Map<String, String> record = new HashMap<String, String>();
		record.put(com.barbara.dowhat.constant.Constant.Message.FROM, from);
		record.put(com.barbara.dowhat.constant.Constant.Message.SENT_AT,
				yamlFormat.format(calendar.getTime()));
		record.put(com.barbara.dowhat.constant.Constant.Message.TEXT, text);
//		record.put(MyFile.Message.MATCHED_RULE, matchedRule);

		Yaml yaml = new Yaml();
		String msgYml = yaml.dump(record);

		DateFormat filenameFormat = new SimpleDateFormat(
				"yyyy-MM-dd_HHmm_ss_SSS");
		String filename = String.format("%s_%s.yml",
				filenameFormat.format(calendar.getTime()), from);
		
		MyFile.saveToYaml(context, com.barbara.dowhat.constant.Constant.Message.STORE_DIR, com.barbara.dowhat.constant.Constant.Message.STORE_PATH, filename, msgYml);

//		File bakDir = null;//存储路径
//		if (Environment.MEDIA_MOUNTED.equals(Environment
//				.getExternalStorageState())) {
//			// We can read and write the media
//			File extStorageDir = new File(MyFile.Message.STORE_PATH);
//			if (extStorageDir.exists() || extStorageDir.mkdirs()) {
//				bakDir = extStorageDir;
//			}
//		}
//
//		if (null == bakDir) {
//			bakDir = context.getDir(MyFile.Message.STORE_DIR,
//					Context.MODE_PRIVATE);
//		}
//
//		try {
//			FileOutputStream fos = new FileOutputStream(new File(bakDir,
//					filename));
//			fos.write(msgYml.getBytes());
//			fos.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	} // persistMessage

}

