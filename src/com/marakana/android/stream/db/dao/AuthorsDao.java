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
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class AuthorsDao {
    private static final String TAG = "AUTHORS-DAO";

    static final String TABLE = "authors";
    static final String COL_ID = "id";

    private static final String COL_NAME = "name";
    private static final String COL_LINK = "link";

    private static final String CREATE_TABLE
        = "CREATE TABLE " + TABLE + " ("
            + COL_ID + " integer PRIMARY KEY AUTOINCREMENT,"
            + COL_NAME + " text,"
            + COL_LINK + " text)";

    private static final String DROP_TABLE
        = "DROP TABLE IF EXISTS " + TABLE;

    private static final String DEFAULT_SORT = StreamContract.Posts.Columns.PUB_DATE + " DESC";
    private static final String PK_CONSTRAINT = COL_ID + "=";

    private static final Map<String, ColumnDef> COL_MAP;
    static {
        Map<String, ColumnDef> m = new HashMap<String, ColumnDef>();
        m.put(
            StreamContract.Authors.Columns.ID,
            new ColumnDef(COL_ID, ColumnDef.Type.LONG));
        m.put(
            StreamContract.Authors.Columns.NAME,
            new ColumnDef(COL_NAME, ColumnDef.Type.STRING));
        m.put(
            StreamContract.Authors.Columns.LINK,
            new ColumnDef(COL_LINK, ColumnDef.Type.STRING));
        COL_MAP = Collections.unmodifiableMap(m);
    }

    private static final Map<String, String>  COL_AS_MAP;
    static {
        Map<String, String> m = new HashMap<String, String>();
        m.put(
            StreamContract.Authors.Columns.ID,
            COL_ID + " AS " + StreamContract.Authors.Columns.ID);
        m.put(
            StreamContract.Authors.Columns.NAME,
            COL_NAME + " AS " + StreamContract.Authors.Columns.NAME);
        m.put(
            StreamContract.Authors.Columns.LINK,
            COL_LINK + " AS " + StreamContract.Authors.Columns.LINK);
         COL_AS_MAP = Collections.unmodifiableMap(m);
    }

    /**
     * @param context
     * @param db
     */
    public static void dropTable(Context context, SQLiteDatabase db) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "drop authors db: " + DROP_TABLE); }
        db.execSQL(DROP_TABLE);
    }

    /**
     * @param context
     * @param db
     */
    public static void initDb(Context context, SQLiteDatabase db) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "create authors db: " + CREATE_TABLE); }
        db.execSQL(CREATE_TABLE);
    }

    private final DbHelper dbHelper;

    /**
     * @param dbHelper
     */
    public AuthorsDao(DbHelper dbHelper) { this.dbHelper = dbHelper; }

    /**
     * @param vals
     * @return pk for inserted row
     */
    public long insert(ContentValues vals) {
        long pk = -1;
        vals = StreamProvider.translateCols(COL_MAP, vals);
        try { pk = dbHelper.getDb().insert(TABLE, null, vals); }
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
}
