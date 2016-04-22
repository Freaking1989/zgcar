package com.zgcar.com.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zgcar.com.account.model.MessageInfos;

public class DbManager implements Manager {

	private DbHelper dbHelper;
	private SQLiteDatabase db;
	private String tableName1, tableName2;

	/**
	 * @param context
	 * @param tableName1
	 *            语音信息表名
	 * @param tableName2
	 *            话费信息表名
	 * @param dbName
	 *            db文件名
	 */
	public DbManager(Context context, String imei, String dbName) {
		this.tableName1 = "vo" + imei + "ice";
		this.tableName2 = "ph" + imei + "one";
		dbHelper = new DbHelper(context, this.tableName1, this.tableName2,
				dbName);
		db = dbHelper.getWritableDatabase();
		dbHelper.onCreate(db);
	}

	@Override
	public synchronized ArrayList<MessageInfos> query() {
		ArrayList<MessageInfos> arrayList = new ArrayList<MessageInfos>();
		Cursor csCursor = db.rawQuery("select * from " + tableName2
				+ " order by id asc", null);
		if (csCursor != null) {
			while (csCursor.moveToNext()) {
				MessageInfos info = new MessageInfos();
				info.setFlag(csCursor.getInt(csCursor.getColumnIndex("flag")));
				info.setId(csCursor.getInt(csCursor.getColumnIndex("id")));
				info.setTime(csCursor.getString(csCursor.getColumnIndex("time")));
				info.setMessage(csCursor.getString(csCursor
						.getColumnIndex("content")));
				info.setFlag(csCursor.getInt(csCursor.getColumnIndex("flag")));
				arrayList.add(info);
			}
		}
		csCursor.close();
		return arrayList;
	}

	@Override
	public synchronized void insert(MessageInfos infos) {
		db.beginTransaction();
		db.execSQL(
				"insert into " + tableName2 + " values(null,?,?,?)",
				new Object[] { infos.getFlag(), infos.getTime(),
						infos.getMessage() });
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	// ***************************以下是数据库操作*********************************
	@Override
	public void closeDb() {
		if (db != null) {
			if (db.isOpen()) {
				db.close();
			}
			db = null;
		}
	}

	@Override
	public void deletTable(String imei) {
		String voiceTable = "vo" + imei + "ice";
		String phoneTable = "ph" + imei + "one";
		db.beginTransaction();
		db.execSQL("delete from " + voiceTable);
		db.execSQL("delete from " + phoneTable);
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}

	@Override
	public void deletPhoneTable(String imei) {
		String phoneTable = "ph" + imei + "one";
		db.beginTransaction();
		db.execSQL("delete from " + phoneTable);
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/* **************************以下是接口******************************** */

}
