package com.marakana.android.stream.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * DbHelper
 */
public class DbHelper extends SQLiteOpenHelper {
    static final String TABLE_FEED = "feed";
    static final String COL_ID = "id";
    static final String COL_TITLE = "title";
    static final String COL_LINK = "link";
    static final String COL_AUTHOR = "author";
    static final String COL_PUB_DATE = "pub_date";
    static final String COL_CATEGORY = "category";
    static final String COL_DESC = "description";

    private static final String TAG = "Stream-DbHelper";
    private static final String DB_NAME = "stream.db";
    private static final int DB_VERSION = 1;

    // lazily build this stuff...
    private static final class Holder {
        private Holder() {}

        private static final String CREATE_TABLE_FEED
            = "CREATE TABLE " + TABLE_FEED + " ("
                + COL_ID + " long PRIMARY KEY,"
                + COL_TITLE + " text,"
                + COL_LINK + " text,"
                + COL_AUTHOR + " text,"
                + COL_PUB_DATE + " long,"
                + COL_DESC + " text)";

        private static final String DROP_TABLE_FEED
            = "DROP TABLE IF EXISTS " + TABLE_FEED;
    }

    /**
     * @param context
     */
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate with sql: " + Holder.CREATE_TABLE_FEED);
        db.execSQL(Holder.CREATE_TABLE_FEED);
    }

    /**
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Temporary solution
        db.execSQL(Holder.DROP_TABLE_FEED);
        onCreate(db);
    }

    /**
     * @return a copy of the database
     */
    public SQLiteDatabase getDb() { return getWritableDatabase(); }
}
