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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.marakana.android.stream.BuildConfig;
import com.marakana.android.stream.db.DbHelper;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class ThumbsDao {
    private static final String TAG = "THUMBS-DAO";

    static final String TABLE = "thumbs";
    static final String COL_ID = "id";
    static final String COL_URI = "uri";

    private static final String COL_DATA = "_data";
    private static final String COL_LAST_USE = "last_use";

    private static final String CREATE_TABLE
        = "CREATE TABLE " + TABLE + " ("
            + COL_ID + " integer PRIMARY KEY AUTOINCREMENT,"
            + COL_DATA + " text,"
            + COL_URI + " text,"
            + COL_LAST_USE + " integer)";

    private static final String DROP_TABLE
        = "DROP TABLE IF EXISTS " + TABLE;

    private static final String PK_CONSTRAINT = COL_ID + "=";

    /**
     * @param context
     * @param db
     */
    public static void dropTable(Context context, SQLiteDatabase db) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "drop thumbs db: " + DROP_TABLE); }
        db.execSQL(DROP_TABLE);
    }

    /**
     * @param context
     * @param db
     */
    public static void initDb(Context context, SQLiteDatabase db) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "create thumbs db: " + CREATE_TABLE); }
        db.execSQL(CREATE_TABLE);
    }

    private final DbHelper dbHelper;

    /**
     * @param dbHelper
     */
    public ThumbsDao(DbHelper dbHelper) { this.dbHelper = dbHelper; }

    /**
     * @param vals
     * @return pk for inserted row
     */
    public long insert(ContentValues vals) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "insert thumb: " + vals); }
        long pk = -1;
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

        qb.setTables(TABLE);

        if (0 <= pk) { qb.appendWhere(PK_CONSTRAINT + pk); }

        return qb.query(dbHelper.getDb(), proj, sel, selArgs, null, null, ord);
    }
}
