/* $Id: $
   Copyright 2012, G. Blake Meike

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.marakana.android.stream.db.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.marakana.android.stream.BuildConfig;
import com.marakana.android.stream.db.ColumnDef;
import com.marakana.android.stream.db.DbHelper;
import com.marakana.android.stream.db.StreamContract;
import com.marakana.android.stream.db.StreamProvider;


/**
 * Using URIs as foreign keys is really convenient, but pretty expensive, space-wise.
 *   Might have to fix that, at some point in the future.
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class PostsDao {
    private static final String TAG = "POSTS-DAO";

    static final String TABLE = "posts";
    static final String COL_ID = "id";

    private static final String COL_URI = "uri";
    private static final String COL_TITLE = "title";
    private static final String COL_AUTHOR = "author";
    private static final String COL_DATE = "pub_date";
    private static final String COL_TAGS = "tags";
    private static final String COL_SUMMARY = "summary";
    private static final String COL_CONTENT = "content";
    private static final String COL_TYPE = "type";
    private static final String COL_THUMB = "thumb";

    private static final String CREATE_TABLE
        = "CREATE TABLE " + TABLE + " ("
            + COL_ID + " integer PRIMARY KEY AUTOINCREMENT,"
            + COL_URI + " text UNIQUE,"
            + COL_TITLE + " text,"
            + COL_AUTHOR + " text REFERENCES " + AuthorsDao.TABLE + "(" + AuthorsDao.COL_URI + "),"
            + COL_DATE + " integer,"
            + COL_TAGS + " text,"
            + COL_SUMMARY + " text,"
            + COL_THUMB + " text REFERENCES " + ThumbsDao.TABLE + "(" + ThumbsDao.COL_URI + "),"
            + COL_TYPE + " text,"
            + COL_CONTENT + " text)";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE;

    private static final String DEFAULT_SORT = StreamContract.Posts.Columns.PUB_DATE + " DESC";
    private static final String PK_CONSTRAINT = TABLE + "." + COL_ID + "=";

    private static final Map<String, ColumnDef> COL_MAP;
    static {
        Map<String, ColumnDef> m = new HashMap<String, ColumnDef>();
        m.put(
            StreamContract.Posts.Columns.ID,
            new ColumnDef(COL_ID, ColumnDef.Type.LONG));
        m.put(
            StreamContract.Posts.Columns.LINK,
            new ColumnDef(COL_URI, ColumnDef.Type.STRING));
        m.put(
            StreamContract.Posts.Columns.TITLE,
            new ColumnDef(COL_TITLE, ColumnDef.Type.STRING));
        m.put(
            StreamContract.Posts.Columns.AUTHOR,
            new ColumnDef(COL_AUTHOR, ColumnDef.Type.STRING));
        m.put(
            StreamContract.Posts.Columns.PUB_DATE,
            new ColumnDef(COL_DATE, ColumnDef.Type.LONG));
        m.put(
            StreamContract.Posts.Columns.TAGS,
            new ColumnDef(COL_TAGS, ColumnDef.Type.STRING));
        m.put(
            StreamContract.Posts.Columns.SUMMARY,
            new ColumnDef(COL_SUMMARY, ColumnDef.Type.STRING));
        m.put(
            StreamContract.Posts.Columns.THUMB,
            new ColumnDef(COL_THUMB, ColumnDef.Type.STRING));
        m.put(
            StreamContract.Thumbs.Columns.TYPE,
            new ColumnDef(COL_TYPE, ColumnDef.Type.STRING));
        m.put(
            StreamContract.Posts.Columns.CONTENT,
            new ColumnDef(COL_CONTENT, ColumnDef.Type.STRING));
         COL_MAP = Collections.unmodifiableMap(m);
    }

    private static final Map<String, String>  COL_AS_MAP;
    static {
        Map<String, String> m = new HashMap<String, String>();
        m.put(
            StreamContract.Posts.Columns.ID,
            COL_ID + " AS " + StreamContract.Posts.Columns.ID);
        m.put(
            StreamContract.Posts.Columns.TITLE,
            COL_TITLE + " AS " + StreamContract.Posts.Columns.TITLE);
        m.put(
            StreamContract.Posts.Columns.AUTHOR,
            COL_AUTHOR + " AS " + StreamContract.Posts.Columns.AUTHOR);
        m.put(
            StreamContract.Posts.Columns.PUB_DATE,
            COL_DATE + " AS " + StreamContract.Posts.Columns.PUB_DATE);
        m.put(
            StreamContract.Posts.Columns.SUMMARY,
            COL_SUMMARY + " AS " + StreamContract.Posts.Columns.SUMMARY);
        m.put(
            StreamContract.Posts.Columns.TAGS,
            COL_TAGS + " AS " + StreamContract.Posts.Columns.TAGS);
        m.put(
            StreamContract.Posts.Columns.CONTENT,
            COL_CONTENT + " AS " + StreamContract.Posts.Columns.CONTENT);
        m.put(
            StreamContract.Posts.Columns.THUMB,
            COL_THUMB + " AS " + StreamContract.Posts.Columns.THUMB);
        m.put(StreamContract.Posts.Columns.MAX_PUB_DATE,
            "MAX(" + StreamContract.Posts.Columns.PUB_DATE + ") AS "
                + StreamContract.Posts.Columns.PUB_DATE);
         COL_AS_MAP = Collections.unmodifiableMap(m);
    }

    private static final String FEED_TABLE
        = TABLE + " INNER JOIN " + AuthorsDao.TABLE
            + " ON(" + TABLE + "." + COL_AUTHOR
                + "=" + AuthorsDao.TABLE + "." + AuthorsDao.COL_URI + ")"
            + " LEFT OUTER JOIN " + ThumbsDao.TABLE
                + " ON(" + TABLE + "." + COL_THUMB
                    + "=" + ThumbsDao.TABLE + "." + ThumbsDao.COL_URI + ")";

    private static final Map<String, String> FEED_COL_AS_MAP;
    static {
        Map<String, String> m = new HashMap<String, String>();
        m.put(
            StreamContract.Feed.Columns.ID,
            TABLE + "." + COL_ID + " AS " + StreamContract.Feed.Columns.ID);
        m.put(
            StreamContract.Feed.Columns.TITLE,
            TABLE + "." + COL_TITLE + " AS " + StreamContract.Feed.Columns.TITLE);
        m.put(
            StreamContract.Feed.Columns.AUTHOR,
            AuthorsDao.TABLE + "." + AuthorsDao.COL_NAME
                + " AS " + StreamContract.Feed.Columns.AUTHOR);
        m.put(
            StreamContract.Feed.Columns.PUB_DATE,
            TABLE + "." + COL_DATE + " AS " + StreamContract.Feed.Columns.PUB_DATE);
        m.put(
            StreamContract.Feed.Columns.SUMMARY,
            TABLE + "." + COL_SUMMARY + " AS " + StreamContract.Feed.Columns.SUMMARY);
        m.put(
            StreamContract.Feed.Columns.TAGS,
            TABLE + "." + COL_TAGS + " AS " + StreamContract.Feed.Columns.TAGS);
        m.put(
            StreamContract.Feed.Columns.CONTENT,
            TABLE + "." + COL_CONTENT + " AS " + StreamContract.Feed.Columns.CONTENT);
        m.put(
            StreamContract.Feed.Columns.THUMB,
            ThumbsDao.TABLE + "." + ThumbsDao.COL_ID + " AS " + StreamContract.Feed.Columns.THUMB);
        FEED_COL_AS_MAP = Collections.unmodifiableMap(m);
    }

    /**
     * @param context
     * @param db
     */
    public static void dropTable(Context context, SQLiteDatabase db) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "drop posts db: " + DROP_TABLE); }
        db.execSQL(DROP_TABLE);
    }

    /**
     * @param context
     * @param db
     */
    public static void initDb(Context context, SQLiteDatabase db) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "create posts db: " + CREATE_TABLE); }
        db.execSQL(CREATE_TABLE);
    }


    private final DbHelper dbHelper;

    /**
     * @param dbHelper
     */
    public PostsDao(DbHelper dbHelper) { this.dbHelper = dbHelper; }

    /**
     * @param vals
     * @return pk for inserted row
     */
    public long insert(ContentValues vals) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "insert post: " + vals); }
        long pk = -1;
        vals = StreamProvider.translateCols(COL_MAP, vals);
        try {
            pk = dbHelper.getDb().insertWithOnConflict(
               TABLE,
               null,
               vals,
               SQLiteDatabase.CONFLICT_IGNORE);
        }
        catch (SQLException e) { Log.w(TAG, "insert failed: ", e); }
        return pk;
    }

    /**
     * @param proj
     * @param sel
     * @param selArgs
     * @param ord
     * @param pk
     * @return cursor
     */
    public Cursor query(String[] proj, String sel, String[] selArgs, String ord, long pk) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setStrict(true);

        qb.setProjectionMap(COL_AS_MAP);

        qb.setTables(TABLE);

        if (0 <= pk) { qb.appendWhere(PK_CONSTRAINT + pk); }

        if (TextUtils.isEmpty(ord)) { ord = DEFAULT_SORT; }

        return qb.query(dbHelper.getDb(), proj, sel, selArgs, null, null, ord);
    }

    /**
     * @param proj
     * @param sel
     * @param selArgs
     * @param ord
     * @param pk
     * @return cursor
     */
    public Cursor queryFeed(String[] proj, String sel, String[] selArgs, String ord, long pk) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setStrict(true);

        qb.setProjectionMap(FEED_COL_AS_MAP);

        qb.setTables(FEED_TABLE);

        if (0 <= pk) { qb.appendWhere(PK_CONSTRAINT + pk); }

        if (TextUtils.isEmpty(ord)) { ord = DEFAULT_SORT; }

        return qb.query(dbHelper.getDb(), proj, sel, selArgs, null, null, ord);
    }
}
