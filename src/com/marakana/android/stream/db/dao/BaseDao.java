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

import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.marakana.android.stream.BuildConfig;
import com.marakana.android.stream.db.ColumnMap;
import com.marakana.android.stream.db.DbHelper;
import com.marakana.android.stream.db.ProjectionMap;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public abstract class BaseDao {
    private final String tag;
    private final DbHelper dbHelper;
    private final String table;
    private final String primaryKey;
    private final String defaultSort;
    private final ColumnMap colMap;
    private final ProjectionMap projMap;

    /**
     * @param tag
     * @param dbHelper
     * @param table
     * @param primaryKey
     * @param defaultSort
     * @param colMap
     * @param projMap
     */
    public BaseDao(
            String tag,
            DbHelper dbHelper,
            String table,
            String primaryKey,
            String defaultSort,
            ColumnMap colMap,
            ProjectionMap projMap)
    {
        this.tag = tag;
        this.dbHelper = dbHelper;
        this.table = table;
        this.primaryKey = primaryKey;
        this.defaultSort = defaultSort;
        this.colMap = colMap;
        this.projMap = projMap;
    }

    /**
     * @return the DB
     */
    protected SQLiteDatabase getDb() { return dbHelper.getDb(); }

    /**
     * @return the projection map
     */
    protected Map<String, String> getProjectionMap() { return projMap.getProjectionMap(); }

    /**
     * @param vals virtual cols
     * @return actual cols
     */
    protected ContentValues translateCols(ContentValues vals) {
        return colMap.translateCols(vals);
    }

    /**
     * @param vals
     * @return pk for inserted row
     */
    public long insert(ContentValues vals) {
        if (BuildConfig.DEBUG) { Log.d(tag, "insert: " + vals); }
        long pk = -1;
        try {
            pk = getDb().insertWithOnConflict(
                    table,
                    null,
                    translateCols(vals),
                    SQLiteDatabase.CONFLICT_IGNORE);
        }
        catch (SQLException e) { Log.w(tag, "insert failed: ", e); }
        return pk;
    }

    /**
     * @param proj
     * @param sel
     * @param args
     * @param ord
     * @param pk
     * @return cursor
     */
    public Cursor query(String[] proj, String sel, String[] args, String ord, long pk) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setStrict(true);

        qb.setProjectionMap(getProjectionMap());

        qb.setTables(table);

        if (0 <= pk) { qb.appendWhere(primaryKey + "=" + pk); }

        if (TextUtils.isEmpty(ord)) { ord = defaultSort; }

        return qb.query(getDb(), proj, sel, args, null, null, ord);
    }
}
