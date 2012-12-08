package com.marakana.android.stream.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.marakana.android.stream.BuildConfig;
import com.marakana.android.stream.db.dao.AuthorsDao;
import com.marakana.android.stream.db.dao.PostsDao;
import com.marakana.android.stream.db.dao.TagsDao;
import com.marakana.android.stream.db.dao.ThumbsDao;


/**
 * DbHelper
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DB";

    private static final String DB_NAME = "stream.db";
    private static final int DB_VERSION =  6;


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
        if (BuildConfig.DEBUG) { Log.d(TAG, "db create"); }

        AuthorsDao.initDb(context, db);
        ThumbsDao.initDb(context, db);
        PostsDao.initDb(context, db);
        TagsDao.initDb(context, db);
    }

    /**
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "db upgrade"); }

        TagsDao.dropTable(context, db);
        PostsDao.dropTable(context, db);
        ThumbsDao.dropTable(context, db);
        AuthorsDao.dropTable(context, db);

        onCreate(db);
    }

    /**
     * @return a copy of the database
     */
    public SQLiteDatabase getDb() { return getWritableDatabase(); }
}