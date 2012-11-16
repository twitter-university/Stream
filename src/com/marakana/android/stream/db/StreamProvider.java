package com.marakana.android.stream.db;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


/**
 * StreamProvider
 */
public class StreamProvider extends ContentProvider {
    private static final String TAG = "Stream-StreamProvider";

    private static final String DEFAULT_SORT = StreamContract.Feed.Columns.PUB_DATE + " DESC";

    private static final String FEED_PK_CONSTRAINT = DbHelper.COL_ID + "=";

    private static final int FEED_ITEM = 1;
    private static final int FEED_DIR = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(
            StreamContract.AUTHORITY,
            StreamContract.Feed.TABLE, FEED_ITEM);
        uriMatcher.addURI(
            StreamContract.AUTHORITY,
            StreamContract.Feed.TABLE + "/#", FEED_DIR);
    }

    private static final Map<String, ColumnDef> FEED_COL_MAP;
    static {
        Map<String, ColumnDef> m = new HashMap<String, ColumnDef>();
        m.put(
            StreamContract.Feed.Columns.ID,
            new ColumnDef(DbHelper.COL_ID, ColumnDef.Type.LONG));
        m.put(
            StreamContract.Feed.Columns.TITLE,
            new ColumnDef(DbHelper.COL_TITLE, ColumnDef.Type.STRING));
        m.put(
            StreamContract.Feed.Columns.LINK,
            new ColumnDef(DbHelper.COL_LINK, ColumnDef.Type.STRING));
        m.put(
            StreamContract.Feed.Columns.AUTHOR,
            new ColumnDef(DbHelper.COL_AUTHOR, ColumnDef.Type.STRING));
        m.put(
            StreamContract.Feed.Columns.PUB_DATE,
            new ColumnDef(DbHelper.COL_PUB_DATE, ColumnDef.Type.LONG));
        m.put(
            StreamContract.Feed.Columns.CATEGORY,
            new ColumnDef(DbHelper.COL_CATEGORY, ColumnDef.Type.STRING));
        m.put(
            StreamContract.Feed.Columns.DESC,
            new ColumnDef(DbHelper.COL_DESC, ColumnDef.Type.STRING));
        FEED_COL_MAP = Collections.unmodifiableMap(m);
    }

    private static final Map<String, String> FEED_COL_AS_MAP;
    static {
        Map<String, String> m = new HashMap<String, String>();
        m.put(
            StreamContract.Feed.Columns.ID,
            DbHelper.COL_ID + " AS " + StreamContract.Feed.Columns.ID);
        m.put(
            StreamContract.Feed.Columns.TITLE,
            DbHelper.COL_TITLE + " AS " + StreamContract.Feed.Columns.TITLE);
        m.put(
            StreamContract.Feed.Columns.LINK,
            DbHelper.COL_LINK + " AS " + StreamContract.Feed.Columns.LINK);
        m.put(
            StreamContract.Feed.Columns.AUTHOR,
            DbHelper.COL_AUTHOR + " AS " + StreamContract.Feed.Columns.AUTHOR);
        m.put(
            StreamContract.Feed.Columns.PUB_DATE,
            DbHelper.COL_PUB_DATE + " AS " + StreamContract.Feed.Columns.PUB_DATE);
        m.put(
            StreamContract.Feed.Columns.CATEGORY,
            DbHelper.COL_CATEGORY + " AS " + StreamContract.Feed.Columns.CATEGORY);
        m.put(
            StreamContract.Feed.Columns.DESC,
            DbHelper.COL_DESC + " AS " + StreamContract.Feed.Columns.DESC);
        FEED_COL_AS_MAP = Collections.unmodifiableMap(m);
    }


    private DbHelper dbHelper;

    /**
     * @see android.content.ContentProvider#onCreate()
     */
    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(this.getContext());
        return dbHelper != null;
    }

    /**
     * @see android.content.ContentProvider#getType(android.net.Uri)
     */
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case FEED_ITEM:
                return StreamContract.Feed.CONTENT_TYPE_ITEM;
            case FEED_DIR:
                return StreamContract.Feed.CONTENT_TYPE_DIR;
            default:
                return null;
        }
    }

    /**
     * @see android.content.ContentProvider#update(android.net.Uri,
     *      android.content.ContentValues, java.lang.String,
     *      java.lang.String[])
     */
    @Override
    public int update(Uri uri, ContentValues vals, String sel, String[] selArgs) {
        throw new UnsupportedOperationException("Update not supported");
    }

    /**
     * @see android.content.ContentProvider#delete(android.net.Uri,
     *      java.lang.String, java.lang.String[])
     */
    @Override
    public int delete(Uri uri, String sel, String[] selArgs) {
        throw new UnsupportedOperationException("Update not supported");
    }

    /**
     * @see android.content.ContentProvider#insert(android.net.Uri,
     *      android.content.ContentValues)
     */
    @Override
    public Uri insert(Uri uri, ContentValues vals) {
        vals = translateCols(vals);

        long pk;
        switch (uriMatcher.match(uri)) {
            case FEED_DIR:
                pk = insertFeed(vals);
                break;

            default:
                throw new UnsupportedOperationException("Unrecognized URI: " + uri);
        }

        if (0 > pk) { uri = null; }
        else {
            uri = uri.buildUpon().appendPath(String.valueOf(pk)).build();
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return uri;
    }

    /**
     * @see android.content.ContentProvider#query(android.net.Uri,
     *      java.lang.String[], java.lang.String, java.lang.String[],
     *      java.lang.String)
     */
    @SuppressWarnings("fallthrough")
    @Override
    public Cursor query(Uri uri, String[] proj, String sel, String[] selArgs, String ord) {
        Cursor cur;

        long pk = -1;
        switch (uriMatcher.match(uri)) {
            case FEED_ITEM:
                pk = ContentUris.parseId(uri);
            case FEED_DIR:
                cur = queryFeed(proj, sel, selArgs, ord, pk);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized URI: " + uri);
        }

        int count = -1;
        if (null != cur) {
            cur.setNotificationUri(getContext().getContentResolver(), uri);
            count = cur.getCount();
        }

        Log.d(TAG, "query got records: " + count);
        return cur;
    }

    private long insertFeed(ContentValues vals) {
        return dbHelper.getDb().insert(DbHelper.TABLE_FEED, null, vals);
    }

    private Cursor queryFeed(String[] proj, String sel, String[] selArgs, String ord, long pk) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setStrict(true);

        qb.setProjectionMap(FEED_COL_AS_MAP);

        qb.setTables(DbHelper.TABLE_FEED);

        if (0 <= pk) { qb.appendWhere(FEED_PK_CONSTRAINT + pk); }

        if (TextUtils.isEmpty(ord)) { ord = DEFAULT_SORT; }

        return qb.query(dbHelper.getDb(), proj, sel, selArgs, null, null, ord);
    }

    private ContentValues translateCols(ContentValues vals) {
        ContentValues newVals = new ContentValues();
        for (String colName : vals.keySet()) {
            ColumnDef colDef = FEED_COL_MAP.get(colName);
            if (null == colDef) {
                throw new IllegalArgumentException( "Unrecognized column: " + colName);
            }
            colDef.copy(colName, vals, newVals);
        }

        return newVals;
    }
}
