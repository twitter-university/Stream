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

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
class TagsDao {
    private static final String TAG = "FEED-DAO";

    private static final String DEFAULT_SORT = StreamContract.Feed.Columns.PUB_DATE + " DESC";

    private static final String PK_CONSTRAINT = DbHelper.COL_ID + "=";

    private static final Map<String, ColumnDef> COL_MAP;
    static {
        Map<String, ColumnDef> m = new HashMap<String, ColumnDef>();
        m.put(
            StreamContract.Tags.Columns.ID,
            new ColumnDef(DbHelper.COL_ID, ColumnDef.Type.LONG));
        m.put(
            StreamContract.Tags.Columns.TITLE,
            new ColumnDef(DbHelper.COL_TITLE, ColumnDef.Type.STRING));
        m.put(
            StreamContract.Tags.Columns.LINK,
            new ColumnDef(DbHelper.COL_LINK, ColumnDef.Type.STRING));
        m.put(
            StreamContract.Tags.Columns.DESC,
            new ColumnDef(DbHelper.COL_DESC, ColumnDef.Type.STRING));
        m.put(
            DbHelper.COL_TAGS_ICON,
            new ColumnDef(DbHelper.COL_TAGS_ICON, ColumnDef.Type.STRING));
        COL_MAP = Collections.unmodifiableMap(m);
    }

    private static final Map<String, String> COL_AS_MAP;
    static {
        Map<String, String> m = new HashMap<String, String>();
        m.put(
            StreamContract.Tags.Columns.ID,
            DbHelper.COL_ID + " AS " + StreamContract.Tags.Columns.ID);
        m.put(
            StreamContract.Tags.Columns.TITLE,
            DbHelper.COL_TITLE + " AS " + StreamContract.Tags.Columns.TITLE);
        m.put(
            StreamContract.Tags.Columns.DESC,
            DbHelper.COL_DESC + " AS " + StreamContract.Tags.Columns.DESC);
        m.put(
            StreamContract.Tags.Columns.LINK,
            DbHelper.COL_LINK + " AS " + StreamContract.Tags.Columns.LINK);
        m.put(DbHelper.COL_TAGS_ICON, DbHelper.COL_TAGS_ICON);
        COL_AS_MAP = Collections.unmodifiableMap(m);
    }

    private final DbHelper dbHelper;
    private final StreamProvider provider;

    public TagsDao(StreamProvider provider, DbHelper dbHelper) {
        this.provider = provider;
        this.dbHelper = dbHelper;
    }


    public long insert(ContentValues vals) {
        long pk = -1;
        vals = StreamProvider.translateCols(COL_MAP, vals);
        try { pk = dbHelper.getDb().insert(DbHelper.TABLE_TAGS, null, vals); }
        catch (SQLException e) { Log.w(TAG, "insert failed: ", e); }
        return pk;
    }

    public Cursor query(String[] proj, String sel, String[] selArgs, String ord, long pk) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setStrict(true);

        qb.setProjectionMap(COL_AS_MAP);

        qb.setTables(DbHelper.TABLE_FEED);

        if (0 <= pk) { qb.appendWhere(PK_CONSTRAINT + pk); }

        if (TextUtils.isEmpty(ord)) { ord = DEFAULT_SORT; }

        return qb.query(dbHelper.getDb(), proj, sel, selArgs, null, null, ord);
    }

    public ParcelFileDescriptor openFile(Uri uri) throws FileNotFoundException {
        return provider.openData(uri, "r");
    }
}
