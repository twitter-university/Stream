package com.marakana.android.stream.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * DbHelper
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DB";

    private static final String DB_NAME = "stream.db";
    private static final int DB_VERSION = 3;

    static final String TABLE_FEED = "feed";
    static final String TABLE_TAGS = "tags";

    static final String COL_ID = "id";
    static final String COL_TITLE = "title";
    static final String COL_LINK = "link";
    static final String COL_DESC = "description";

    static final String COL_TAGS_ICON = "_data";

    static final String COL_FEED_AUTHOR = "author";
    static final String COL_FEED_PUB_DATE = "pub_date";

    // lazily build this stuff...
    private static final class Feed {
        private Feed() {}

        private static final String CREATE_TABLE
            = "CREATE TABLE " + TABLE_FEED + " ("
                + COL_ID + " integer PRIMARY KEY AUTOINCREMENT,"
                + COL_TITLE + " text,"
                + COL_LINK + " text,"
                + COL_FEED_AUTHOR + " text,"
                + COL_FEED_PUB_DATE + " integer,"
                + COL_DESC + " text)";

        private static final String DROP_TABLE
            = "DROP TABLE IF EXISTS " + TABLE_FEED;
    }

    private static final class Tags {
        private Tags() {}

        private static final String CREATE_TABLE
            = "CREATE TABLE " + TABLE_TAGS + " ("
                + COL_ID + " integer PRIMARY KEY AUTOINCREMENT,"
                + COL_TITLE + " text,"
                + COL_LINK + " text,"
                + COL_DESC + " text"
                + COL_TAGS_ICON + " text)";

        private static final String DROP_TABLE
            = "DROP TABLE IF EXISTS " + TABLE_TAGS;
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
        Log.d(TAG, "onCreate with sql: " + Feed.CREATE_TABLE);
        db.execSQL(Feed.CREATE_TABLE);
        db.execSQL(Tags.CREATE_TABLE);
    }

    /**
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Temporary solution
        db.execSQL(Feed.DROP_TABLE);
        db.execSQL(Tags.DROP_TABLE);
        onCreate(db);
    }

    /**
     * @return a copy of the database
     */
    public SQLiteDatabase getDb() { return getWritableDatabase(); }
}
