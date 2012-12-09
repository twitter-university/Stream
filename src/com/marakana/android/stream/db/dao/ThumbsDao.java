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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.marakana.android.stream.BuildConfig;
import com.marakana.android.stream.db.ProjectionMap;
import com.marakana.android.stream.db.ColumnMap;
import com.marakana.android.stream.db.DbHelper;
import com.marakana.android.stream.db.StreamContract;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class ThumbsDao extends BaseDao {
    private static final String TAG = "THUMBS-DAO";

    static final String TABLE = "thumbs";
    static final String COL_ID = "id";
    static final String COL_URI = "uri";

    private static final String COL_DATA = "_data";
    private static final String COL_TYPE = "type";
    private static final String COL_LAST_USE = "last_use";

    private static final String CREATE_TABLE
        = "CREATE TABLE " + TABLE + " ("
            + COL_ID + " integer PRIMARY KEY AUTOINCREMENT,"
            + COL_URI + " text UNIQUE,"
            + COL_DATA + " text UNIQUE,"
            + COL_TYPE + " text,"
            + COL_LAST_USE + " integer)";

    private static final String DROP_TABLE
        = "DROP TABLE IF EXISTS " + TABLE;

//    private static final SecureRandom random = new SecureRandom();
//
//    private static String makeFileName() {
//        return "thumb-" + new BigInteger(130, random).toString(32);
//    }

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

    /**
     * @param dbHelper
     */
    public ThumbsDao(DbHelper dbHelper) {
        super(
            TAG,
            dbHelper,
            TABLE,
            COL_ID,
            null,
            new ColumnMap.Builder()
                .addColumn(StreamContract.Thumbs.Columns.ID, COL_ID, ColumnMap.Type.LONG)
                .addColumn(StreamContract.Thumbs.Columns.LINK, COL_URI, ColumnMap.Type.STRING)
                .addColumn(StreamContract.Thumbs.Columns.TYPE, COL_TYPE, ColumnMap.Type.STRING)
                .build(),
            new ProjectionMap.Builder()
                .addColumn(StreamContract.Thumbs.Columns.ID, COL_ID)
                .addColumn(StreamContract.Thumbs.Columns.LINK, COL_URI)
                .build());
    }
}
