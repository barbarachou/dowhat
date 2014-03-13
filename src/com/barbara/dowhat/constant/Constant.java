package com.barbara.dowhat.constant;

import android.os.Environment;

public class Constant {
	public final static String BIRTHDAY_TABLE_NAME = "birthday";
	public static final String ALARM_ACTION_NAME = "com.barbara.dowhat.broadcast.ALARM";

	public static final String PACKAGE = "dowhat";
	public static final String FILE_PATH = "/data/data/com.barbara.dowhat/files/";

	public static final String DB_DIR = "database";

	public static final String BACKUP_DIR = "backup_info";
	public static final String BACKUP_PATH = String.format("%s/%s/%s",
			Environment.getExternalStorageDirectory().getPath(), PACKAGE,
			BACKUP_DIR);

	public static final class Message {
		public static final String FROM = "from";
		public static final String SENT_AT = "sent_at";
		public static final String TEXT = "text";
		
		public static final String STORE_DIR = "blocked_messages";
		public static final String STORE_PATH = String.format("%s/%s/%s",
				Environment.getExternalStorageDirectory().getPath(), PACKAGE,
				STORE_DIR);

	}
}
