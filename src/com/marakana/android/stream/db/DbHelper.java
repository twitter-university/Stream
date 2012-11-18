package com.marakana.android.stream.db;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;


/**
 * DbHelper
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DB";

    private static final String DB_NAME = "stream.db";
    private static final int DB_VERSION = 4;

    static final String TABLE_FEED = "feed";
    static final String TABLE_TAGS = "tags";

    static final String COL_ID = "id";
    static final String COL_TITLE = "title";
    static final String COL_LINK = "link";
    static final String COL_DESC = "description";

    static final String COL_TAGS_ICON = "_data";
    static final String COL_TAGS_LOCAL = "local";

    static final String COL_FEED_AUTHOR = "author";
    static final String COL_FEED_PUB_DATE = "pub_date";

    static final String ASSET_TAGS = "tags.csv";

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
                + COL_DESC + " text,"
                + COL_TAGS_ICON + " text,"
                + COL_TAGS_LOCAL + " integer,"
                + "CHECK(" + COL_TAGS_LOCAL + "==0 OR "  + COL_TAGS_LOCAL + "==1))";

        private static final String DROP_TABLE
            = "DROP TABLE IF EXISTS " + TABLE_TAGS;
    }

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
        Log.d(TAG, "onCreate with sql: " + Feed.CREATE_TABLE);
        db.execSQL(Feed.CREATE_TABLE);
        createTagsDb(db);
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

    private void createTagsDb(SQLiteDatabase db) {
        Log.d(TAG, "onCreate with sql: " + Tags.CREATE_TABLE);
        db.execSQL(Tags.CREATE_TABLE);

        final Integer local = Integer.valueOf(1);
        BufferedReader in = null;
        ContentValues vals = new ContentValues();
        try {
            in = new BufferedReader(new InputStreamReader(context.getAssets().open(ASSET_TAGS)));
            for (String line = ""; line != null; line = in.readLine()) {
                String[] fields = line.split(",");
                if ((4 > fields.length) || TextUtils.isEmpty(fields[0])) { continue; }

                vals.clear();
                vals.put(COL_TITLE, fields[0]);

                if (!TextUtils.isEmpty(fields[3])) {
                    vals.put(COL_TAGS_LOCAL, local);
                    vals.put(COL_TAGS_ICON, fields[3]);
                }

                if (!TextUtils.isEmpty(fields[1])) { vals.put(COL_LINK, fields[1]); }
                if (!TextUtils.isEmpty(fields[2])) { vals.put(COL_DESC, fields[2]); }

                db.insert(TABLE_TAGS, null, vals);
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Failed initializing DB");
        }
        finally {
            if (null != in) {
                try { in.close(); } catch (Exception e) { }
            }
        }
    }
}
