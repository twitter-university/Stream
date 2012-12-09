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
public class AuthorsDao extends BaseDao {
    private static final String TAG = "AUTHORS-DAO";

    static final String TABLE = "authors";
    static final String COL_ID = "id";
    static final String COL_URI = "uri";
    static final String COL_NAME = "name";

    private static final String CREATE_TABLE
        = "CREATE TABLE " + TABLE + " ("
            + COL_ID + " integer PRIMARY KEY AUTOINCREMENT,"
            + COL_URI + " text UNIQUE,"
            + COL_NAME + " text)";

    private static final String DROP_TABLE
        = "DROP TABLE IF EXISTS " + TABLE;

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

    /**
     * @param dbHelper
     */
    public AuthorsDao(DbHelper dbHelper) {
        super(
            TAG,
            dbHelper,
            TABLE,
            COL_ID,
            StreamContract.Posts.Columns.PUB_DATE + " DESC",
            new ColumnMap.Builder()
                .addColumn(StreamContract.Authors.Columns.ID, COL_ID, ColumnMap.Type.LONG)
                .addColumn(StreamContract.Authors.Columns.LINK, COL_URI, ColumnMap.Type.STRING)
                .addColumn(StreamContract.Authors.Columns.NAME, COL_NAME, ColumnMap.Type.STRING)
                .build(),
            new ProjectionMap.Builder()
                .addColumn(StreamContract.Authors.Columns.ID, COL_ID)
                .addColumn(StreamContract.Authors.Columns.LINK, COL_URI)
                .addColumn(StreamContract.Authors.Columns.NAME, COL_NAME)
                .build());
    }
}
