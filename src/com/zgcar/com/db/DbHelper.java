package com.zgcar.com.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	private String tableName1;
	private String tableName2;

	public DbHelper(Context context, String tableName1, String tableName2,
			String dbName) {
		super(context, "tab" + dbName + "le.db", null, 1);
		this.tableName1 = tableName1;
		this.tableName2 = tableName2;
	}

	@Override
	public void onCreate(SQLiteDatabase sql) {
		Log.e("log", "´´½¨±í");
		if (!"".equals(tableName1)) {
			sql.execSQL("create table if not exists " + tableName1
					+ "(id integer primary key autoincrement,"
					+ "read_flag integer," + "voice_flag integer,"
					+ "is_send integer," + "link varchar," + "date varchar,"
					+ "time integer," + "voice_path varchar,"
					+ "nick_name varchar," + "from_ varchar)");
		}
		if (!"".equals(tableName2)) {
			sql.execSQL("create table if not exists "
					+ tableName2
					+ "(id integer primary key autoincrement,flag integer,time varchar,content varchar)");
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sql, int arg1, int arg2) {
		sql.execSQL("alter table user add column other string");
	}

}
