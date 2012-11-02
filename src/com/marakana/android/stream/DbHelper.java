package com.marakana.android.stream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	private static final String TAG = "Stream-DbHelper";
	private static final String DB_NAME = "stream.db";
	private static final int DB_VERSION = 1;
	public static final String TABLE = "feed";

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = String.format("create table %s ( %s int primary key,"
				+ "%s text, %s text, %s text, %s int, %s text)", TABLE,
				StreamContract.Columns._ID, StreamContract.Columns.TITLE,
				StreamContract.Columns.LINK, StreamContract.Columns.AUTHOR,
				StreamContract.Columns.PUB_DATE,
				StreamContract.Columns.DESCRIPTION);
		Log.d(TAG, "onCreate with sql: " + sql);
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Temporary solution
		db.execSQL("drop table if exists " + TABLE);
		onCreate(db);
	}

}
