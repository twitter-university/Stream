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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import com.marakana.android.stream.BuildConfig;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
class TagsDao {
    private static final String TAG = "TAGS-DAO";

    private static final String TABLE_TAGS = "tags";

    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_LINK = "link";
    private static final String COL_DESC = "description";
    private static final String COL_TAGS_ICON = "_data";

    private static final String ASSET_TAGS = "tags.csv";

    private static final String CREATE_TABLE
        = "CREATE TABLE " + TABLE_TAGS + " ("
            + COL_ID + " integer PRIMARY KEY AUTOINCREMENT,"
            + COL_TITLE + " text,"
            + COL_LINK + " text,"
            + COL_DESC + " text,"
            + COL_TAGS_ICON + " text)";

    private static final String DROP_TABLE
        = "DROP TABLE IF EXISTS " + TABLE_TAGS;

    private static final String DEFAULT_SORT = StreamContract.Feed.Columns.PUB_DATE + " DESC";

    private static final String PK_CONSTRAINT = COL_ID + "=";

    private static final Map<String, ColumnDef> COL_MAP;
    static {
        Map<String, ColumnDef> m = new HashMap<String, ColumnDef>();
        m.put(
                StreamContract.Tags.Columns.ID,
                new ColumnDef(COL_ID, ColumnDef.Type.LONG));
        m.put(
                StreamContract.Tags.Columns.TITLE,
                new ColumnDef(COL_TITLE, ColumnDef.Type.STRING));
        m.put(
                StreamContract.Tags.Columns.LINK,
                new ColumnDef(COL_LINK, ColumnDef.Type.STRING));
        m.put(
                StreamContract.Tags.Columns.DESC,
                new ColumnDef(COL_DESC, ColumnDef.Type.STRING));
        m.put(
                COL_TAGS_ICON,
                new ColumnDef(COL_TAGS_ICON, ColumnDef.Type.STRING));
        COL_MAP = Collections.unmodifiableMap(m);
    }

    private static final Map<String, String> COL_AS_MAP;
    static {
        Map<String, String> m = new HashMap<String, String>();
        m.put(
                StreamContract.Tags.Columns.ID,
                COL_ID + " AS " + StreamContract.Tags.Columns.ID);
        m.put(
                StreamContract.Tags.Columns.TITLE,
                COL_TITLE + " AS " + StreamContract.Tags.Columns.TITLE);
        m.put(
                StreamContract.Tags.Columns.DESC,
                COL_DESC + " AS " + StreamContract.Tags.Columns.DESC);
        m.put(
                StreamContract.Tags.Columns.LINK,
                COL_LINK + " AS " + StreamContract.Tags.Columns.LINK);
        m.put(COL_TAGS_ICON, COL_TAGS_ICON);
        COL_AS_MAP = Collections.unmodifiableMap(m);
    }

    static void dropTable(@SuppressWarnings("unused") Context context, SQLiteDatabase db) {
        db.execSQL(DROP_TABLE);
    }

    static void initDb(Context context, SQLiteDatabase db) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "create tags db: " + CREATE_TABLE); }
        db.execSQL(CREATE_TABLE);

        AssetManager assets = context.getAssets();

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(assets.open(ASSET_TAGS)));
            readIcons(context, assets, in, db);
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

    static void readIcons(Context ctxt, AssetManager assets, BufferedReader in, SQLiteDatabase db)
            throws IOException
    {
        final ContentValues vals = new ContentValues();
        for (String line = ""; line != null; line = in.readLine()) {
            String[] fields = line.split(",");
            if ((4 > fields.length) || TextUtils.isEmpty(fields[0])) { continue; }

            vals.clear();
            vals.put(COL_TITLE, fields[0]);
            if (BuildConfig.DEBUG) { Log.d(TAG, "adding local icon: " + fields[0]); }

            if (!TextUtils.isEmpty(fields[3])) {
                vals.put(COL_TAGS_ICON, fields[3]);
                copyAsset(assets, fields[3], new File(ctxt.getFilesDir(), fields[3]));
            }

            if (!TextUtils.isEmpty(fields[1])) { vals.put(COL_LINK, fields[1]); }
            if (!TextUtils.isEmpty(fields[2])) { vals.put(COL_DESC, fields[2]); }

            db.insert(TABLE_TAGS, null, vals);
        }
    }

    @SuppressWarnings("resource")
    private static void copyAsset(AssetManager assets, String asset, File dst) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(assets.open(asset));
            out = new BufferedOutputStream(new FileOutputStream(dst));
            int n = copyFile(in, out);
            Log.d(TAG, "asset: " + asset + " @" + n);
        }
        catch(IOException e) {
            Log.e("tag", "Failed to copy asset: " + asset, e);
        }
        finally {
            if (null != in) {
                try { in.close(); } catch (Exception e) { }
            }
            if (null != out) {
                try { out.close(); } catch (Exception e) { }
            }
        }
    }

    private static int copyFile(InputStream in, OutputStream out) throws IOException {
        int length = 0;
        int n;
        byte[] buffer = new byte[1024];
        while (0 <= (n = in.read(buffer))) {
            out.write(buffer, 0, n);
            length += n;
        }
        return length;
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
        try { pk = dbHelper.getDb().insert(TABLE_TAGS, null, vals); }
        catch (SQLException e) { Log.w(TAG, "Insert failed: ", e); }
        return pk;
    }

    public Cursor query(String[] proj, String sel, String[] selArgs, String ord, long pk) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setStrict(true);

        qb.setProjectionMap(COL_AS_MAP);

        qb.setTables(TABLE_TAGS);

        if (0 <= pk) { qb.appendWhere(PK_CONSTRAINT + pk); }

        if (TextUtils.isEmpty(ord)) { ord = DEFAULT_SORT; }

        return qb.query(dbHelper.getDb(), proj, sel, selArgs, null, null, ord);
    }

    public ParcelFileDescriptor openFile(Uri uri) throws FileNotFoundException {
        long pk = ContentUris.parseId(uri);
        if (0 > pk) { throw new IllegalArgumentException("Malformed URI: " + uri); }

        String fName = null;
        Cursor c = null;
        try {
            c = dbHelper.getDb().query(
                    TABLE_TAGS,
                    new String[] { COL_TAGS_ICON },
                    PK_CONSTRAINT + pk,
                    null,
                    null,
                    null,
                    null);

            if (1 != c.getCount()) { throw new FileNotFoundException("No tag for: " + uri); }
            c.moveToFirst();

            fName = c.getString(c.getColumnIndex(COL_TAGS_ICON));
        }
        catch (Exception e) {
            Log.e(TAG, "WTF?", e);
        }
        finally {
            if (null != c) {
                try { c.close(); } catch (Exception e) { }
            }
        }

        Log.d(TAG, "Opening: " + fName);
        ParcelFileDescriptor fd = null;
        try {
            fd = ParcelFileDescriptor.open(
                    new File(provider.getContext().getFilesDir(), fName),
                    ParcelFileDescriptor.MODE_READ_ONLY);
        }
        catch (Exception e) {
            throw new FileNotFoundException("Failed opening : " + fName);
        }
        Log.d(TAG, "Opened file: " + fd);

        return fd;
    }
}
