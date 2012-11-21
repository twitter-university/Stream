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
package com.marakana.android.stream.db;

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


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
class FeedDao {
    private static final String TAG = "FEED-DAO";

    private static final String TABLE_FEED = "feed";

    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_LINK = "link";
    private static final String COL_FEED_AUTHOR = "author";
    private static final String COL_FEED_PUB_DATE = "pub_date";
    private static final String COL_ICON = "icon";
    private static final String COL_DESC = "description";

    private static final String CREATE_TABLE
        = "CREATE TABLE " + TABLE_FEED + " ("
            + COL_ID + " integer PRIMARY KEY AUTOINCREMENT,"
            + COL_TITLE + " text,"
            + COL_LINK + " text,"
            + COL_FEED_AUTHOR + " text,"
            + COL_FEED_PUB_DATE + " integer,"
            + COL_DESC + " text,"
            + COL_ICON + " integer)";

    private static final String DROP_TABLE
        = "DROP TABLE IF EXISTS " + TABLE_FEED;

    private static final String DEFAULT_SORT = StreamContract.Feed.Columns.PUB_DATE + " DESC";
    private static final String PK_CONSTRAINT = COL_ID + "=";

    private static final Map<String, ColumnDef> COL_MAP;
    static {
        Map<String, ColumnDef> m = new HashMap<String, ColumnDef>();
        m.put(
            StreamContract.Feed.Columns.ID,
            new ColumnDef(COL_ID, ColumnDef.Type.LONG));
        m.put(
            StreamContract.Feed.Columns.TITLE,
            new ColumnDef(COL_TITLE, ColumnDef.Type.STRING));
        m.put(
            StreamContract.Feed.Columns.LINK,
            new ColumnDef(COL_LINK, ColumnDef.Type.STRING));
        m.put(
            StreamContract.Feed.Columns.AUTHOR,
            new ColumnDef(COL_FEED_AUTHOR, ColumnDef.Type.STRING));
        m.put(
            StreamContract.Feed.Columns.PUB_DATE,
            new ColumnDef(COL_FEED_PUB_DATE, ColumnDef.Type.LONG));
        m.put(
            StreamContract.Feed.Columns.ICON,
            new ColumnDef(COL_ICON, ColumnDef.Type.LONG));
        m.put(
            StreamContract.Feed.Columns.DESC,
            new ColumnDef(COL_DESC, ColumnDef.Type.STRING));
         COL_MAP = Collections.unmodifiableMap(m);
    }

    private static final Map<String, String>  COL_AS_MAP;
    static {
        Map<String, String> m = new HashMap<String, String>();
        m.put(
            StreamContract.Feed.Columns.ID,
            COL_ID + " AS " + StreamContract.Feed.Columns.ID);
        m.put(
            StreamContract.Feed.Columns.TITLE,
            COL_TITLE + " AS " + StreamContract.Feed.Columns.TITLE);
        m.put(
            StreamContract.Feed.Columns.LINK,
            COL_LINK + " AS " + StreamContract.Feed.Columns.LINK);
        m.put(
            StreamContract.Feed.Columns.AUTHOR,
            COL_FEED_AUTHOR + " AS " + StreamContract.Feed.Columns.AUTHOR);
        m.put(
            StreamContract.Feed.Columns.PUB_DATE,
            COL_FEED_PUB_DATE + " AS " + StreamContract.Feed.Columns.PUB_DATE);
        m.put(
            StreamContract.Feed.Columns.ICON,
            COL_ICON + " AS " + StreamContract.Feed.Columns.ICON);
        m.put(
            StreamContract.Feed.Columns.DESC,
            COL_DESC + " AS " + StreamContract.Feed.Columns.DESC);
        m.put(StreamContract.Feed.Columns.MAX_PUB_DATE,
            "MAX(" + StreamContract.Feed.Columns.PUB_DATE + ") AS "
                + StreamContract.Feed.Columns.PUB_DATE);
         COL_AS_MAP = Collections.unmodifiableMap(m);
    }

    static void dropTable(@SuppressWarnings("unused") Context context, SQLiteDatabase db) {
        db.execSQL(DROP_TABLE);
    }

    static void initDb(@SuppressWarnings("unused") Context context, SQLiteDatabase db) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "create feed db: " + CREATE_TABLE); }
        db.execSQL(CREATE_TABLE);
    }


    private final DbHelper dbHelper;
    @SuppressWarnings("unused")
    private final StreamProvider provider;

    public FeedDao(StreamProvider provider, DbHelper dbHelper) {
        this.provider = provider;
        this.dbHelper = dbHelper;
    }

    public long insert(ContentValues vals) {
        long pk = -1;
        vals = StreamProvider.translateCols(COL_MAP, vals);
        try { pk = dbHelper.getDb().insert(TABLE_FEED, null, vals); }
        catch (SQLException e) { Log.e(TAG, "insert failed: ", e); }
        return pk;
    }

    public Cursor query(String[] proj, String sel, String[] selArgs, String ord, long pk) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setStrict(true);

        qb.setProjectionMap(COL_AS_MAP);

        qb.setTables(TABLE_FEED);

        if (0 <= pk) { qb.appendWhere(PK_CONSTRAINT + pk); }

        if (TextUtils.isEmpty(ord)) { ord = DEFAULT_SORT; }

        return qb.query(dbHelper.getDb(), proj, sel, selArgs, null, null, ord);
    }
}
