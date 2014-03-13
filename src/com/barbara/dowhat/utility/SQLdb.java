package com.barbara.dowhat.utility;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import android.R.integer;
import android.R.string;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class SQLdb extends SQLiteOpenHelper {
	private final static String DATABASE_NAME = "doWhat_db";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_NAME = "birthday";
	private final static String TABLE_NAME2 = "message";
	private final static String TABLE_NAME3 = "wifi";

	public final static String FIELD_id = "_id";
	public final static String FIELD_NAME = "name";
	public final static String FIELD_DAY = "day";
	private final static String FIELD_ALARM = "alarm";

	public final static String FIELD_id2 = "_id";
	public final static String FIELD_NUM = "number";

	public final static String FIELD_id3 = "_id";
	public final static String FIELD_WIFI = "wifi";
	public final static String FIELD_MODE = "mode";// keep:0 music:1 shock:2
													// mix:3 silence:4

	public SQLdb(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + TABLE_NAME + " (" + FIELD_id
				+ " INTEGER primary key autoincrement, " + " " + FIELD_NAME
				+ " text," + " " + FIELD_ALARM + " long," + " " + FIELD_DAY
				+ " text)";
		db.execSQL(sql);

		String sql2 = "CREATE TABLE " + TABLE_NAME2 + " (" + FIELD_id2
				+ " INTEGER primary key autoincrement, " + " " + FIELD_NUM
				+ " text)";
		db.execSQL(sql2);

		String sql3 = "CREATE TABLE " + TABLE_NAME3 + " (" + FIELD_id3
				+ " INTEGER primary key autoincrement, " + " " + FIELD_WIFI
				+ " text," + " " + FIELD_MODE + " INTEGER)";
		db.execSQL(sql3);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}

	/**
	 * 选择表
	 * 
	 * @return
	 */
	public Cursor selectBirthday() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db
				.query(TABLE_NAME, null, null, null, null, null, null);
		// Cursor cursor = db.query("birthday", new
		// String[]{"name","day"},null,null,null,null, "num desc", "0,10");
		return cursor;
	}

	public Cursor selectMessage() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME2, null, null, null, null, null,
				null);
		// Cursor cursor = db.query("birthday", new
		// String[]{"name","day"},null,null,null,null, "num desc", "0,10");
		return cursor;
	}

	public Cursor selectWifi() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME3, null, null, null, null, null,
				null);
		// Cursor cursor = db.query("birthday", new
		// String[]{"name","day"},null,null,null,null, "num desc", "0,10");
		return cursor;
	}

	/**
	 * 删除表
	 * 
	 * @param tablename
	 */
	public void clean(String tablename) {
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "delete from " + tablename;
		db.execSQL(sql);
	}

	/**
	 * 生日表插入数据
	 * 
	 * @param name名字
	 * @param day日期
	 * @param alarm闹钟提醒时间
	 * @return 插入到某行
	 */
	public long insertBirthday(String name, String day, long alarm) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(FIELD_NAME, name);
		cv.put(FIELD_ALARM, alarm);
		cv.put(FIELD_DAY, day);
		long row = db.insert(TABLE_NAME, null, cv);
		return row;
	}

	/**
	 * 黑名单表插入数据
	 * 
	 * @param number黑名单号码
	 * @return
	 */
	public long insertMessage(String number) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(FIELD_NUM, number);
		long row = db.insert(TABLE_NAME2, null, cv);
		return row;
	}
	
	/**
	 * WIFI表插入数据
	 * @param wifi 名字
	 * @param mode 模式
	 * @return
	 */
	public long insertWifi(String wifi, int mode) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(FIELD_WIFI, wifi);
		cv.put(FIELD_MODE, mode);
		long row = db.insert(TABLE_NAME3, null, cv);
		return row;
	}

	/**
	 * 黑名单查找某号码
	 * 
	 * @param num
	 * @return 是否存在
	 */
	public boolean queryMessage(String num) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_NAME2, null, "number== ?",
				new String[] { num + "" }, null, null, null, null);
		if (cursor.moveToFirst()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * WIFI查找WIFI
	 * @param wifi
	 * @return
	 */
	public int queryWIFI(String wifi) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_NAME3, new String[] {"mode"}, "wifi== ?",
				new String[] { wifi + "" }, null, null, null, null);
		if (cursor.moveToFirst()) {
			return cursor.getInt(0);
		} else {
			return 5;
		}
	}
//	 private final static String[] PROJECTION_DEFAULT = new String[] {
//	 "title",
//	 "content", "sub", "info" };
//	 private static final String WHERE_SUB = " sub == ? ";
//	 private static final String ORDER_BY_NUM_DESC = " num desc ";
//	 public Cursor getNewsBySub(int sub) {
//		 SQLiteDatabase db = getReadableDatabase();
//		 // FIXME limt should use as pass parameters
//		 Cursor cursor = db.query(TABLE_NAME, PROJECTION_DEFAULT, WHERE_SUB,
//		 new String[] { sub + "" }, null, null, ORDER_BY_NUM_DESC, null);
//		 return cursor;
//		 }
	
	/**
	 * 黑名单删除号码
	 * 
	 * @param num
	 */
	public void deleteMessage(String num) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = FIELD_NUM + " = ?";
		String[] whereValue = { num };
		db.delete(TABLE_NAME2, where, whereValue);
	}

	/**
	 * 根据ID删除某条记录
	 * 
	 * @param id
	 * @param tablename
	 */
	public void delete(int id, String tablename) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = FIELD_id + " = ?";
		String[] whereValue = { Integer.toString(id) };
		db.delete(tablename, where, whereValue);
	}

	/**
	 * 更新生日表（未用到
	 * 
	 * @param id
	 * @param name
	 * @param day
	 */
	public void update(int id, String name, String day) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = FIELD_id + " = ?";
		String[] whereValue = { Integer.toString(id) };
		ContentValues cv = new ContentValues();
		cv.put(FIELD_NAME, name);
		cv.put(FIELD_DAY, day);
		db.update(TABLE_NAME, cv, where, whereValue);
	}

	/**
	 * 更新WIFI表的模式
	 * @param wifi
	 * @param mode
	 */
	public void updateWIFI(String wifi, int mode) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = FIELD_WIFI + " = ?";
		String[] whereValue = { wifi };
		ContentValues cv = new ContentValues();
//		cv.put(FIELD_WIFI, wifi);
		cv.put(FIELD_MODE, mode);
		db.update(TABLE_NAME3, cv, where, whereValue);
	}
	
}
