package com.marakana.android.stream.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.marakana.android.stream.BuildConfig;


/**
 * DbHelper
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DB";

    private static final String DB_NAME = "stream.db";
    private static final int DB_VERSION = 5;


    private final Context context;

    /**
     * @param context
     */
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    /**
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        FeedDao.initDb(context, db);
        TagsDao.initDb(context, db);
    }

    /**
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "db upgrade"); }
        FeedDao.dropTable(context, db);
        TagsDao.dropTable(context, db);
        onCreate(db);
    }

    /**
     * @return a copy of the database
     */
    public SQLiteDatabase getDb() { return getWritableDatabase(); }
}