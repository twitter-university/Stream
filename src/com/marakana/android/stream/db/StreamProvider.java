package com.marakana.android.stream.db;

import java.io.FileNotFoundException;
import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.marakana.android.stream.BuildConfig;


/**
 * StreamProvider
 */
public class StreamProvider extends ContentProvider {
    private static final String TAG = "DB";

    private static final int FEED_ITEM = 1;
    private static final int FEED_DIR = 2;
    private static final int TAG_ITEM = 3;
    private static final int TAG_DIR = 4;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(
            StreamContract.AUTHORITY,
            StreamContract.Feed.TABLE,
            FEED_DIR);
        uriMatcher.addURI(
            StreamContract.AUTHORITY,
            StreamContract.Feed.TABLE + "/#",
            FEED_ITEM);
        uriMatcher.addURI(
            StreamContract.AUTHORITY,
            StreamContract.Tags.TABLE,
            TAG_DIR);
        uriMatcher.addURI(
            StreamContract.AUTHORITY,
            StreamContract.Tags.TABLE + "/#",
            TAG_ITEM);
    }

    /**
     * @param colMap
     * @param vals
     * @return content values for actual table
     */
    public static ContentValues translateCols(Map<String, ColumnDef> colMap, ContentValues vals) {
        ContentValues newVals = new ContentValues();
        for (String colName : vals.keySet()) {
            ColumnDef colDef = colMap.get(colName);
            if (null == colDef) {
                throw new IllegalArgumentException( "Unrecognized column: " + colName);
            }
            colDef.copy(colName, vals, newVals);
        }

        return newVals;
    }


    private DbHelper dbHelper;
    private FeedDao feed;
    private TagsDao tags;

    /**
     * @see android.content.ContentProvider#onCreate()
     */
    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(this.getContext());
        if (null == dbHelper) { return false; }
        feed = new FeedDao(this, dbHelper);
        tags = new TagsDao(this, dbHelper);
        return true;
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
            case TAG_ITEM:
                return StreamContract.Tags.CONTENT_TYPE_ITEM;
            case TAG_DIR:
                return StreamContract.Tags.CONTENT_TYPE_DIR;
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
        long pk;
        switch (uriMatcher.match(uri)) {
            case FEED_DIR:
                pk = feed.insert(vals);
                break;

            default:
                throw new UnsupportedOperationException("Unrecognized URI: " + uri);
        }

        if (0 > pk) { uri = null; }
        else {
            uri = uri.buildUpon().appendPath(String.valueOf(pk)).build();
            getContext().getContentResolver().notifyChange(uri, null);
        }
        if (BuildConfig.DEBUG) { Log.d(TAG, "inserted @" + uri + ": " + vals); }

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
                cur = feed.query(proj, sel, selArgs, ord, pk);
                break;

            case TAG_ITEM:
                pk = ContentUris.parseId(uri);
            case TAG_DIR:
                cur = tags.query(proj, sel, selArgs, ord, pk);
                break;

            default:
                throw new UnsupportedOperationException("Unrecognized URI: " + uri);
        }

        int count = -1;
        if (null != cur) {
            cur.setNotificationUri(getContext().getContentResolver(), uri);
            count = cur.getCount();
        }

        if (BuildConfig.DEBUG) { Log.d(TAG, "query @" + uri + ": " + count); }
        return cur;
    }

    /**
     * @see android.content.ContentProvider#openFile(android.net.Uri, java.lang.String)
     */
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
        throws FileNotFoundException
    {
        ParcelFileDescriptor fd;

        switch (uriMatcher.match(uri)) {
            case TAG_ITEM:
                if (!"r".equals(mode)) {
                    throw new SecurityException("Write access forbidden");
                }
                fd = tags.openFile(uri);
                break;

            /// maybe just ignore it?
            default:
                throw new UnsupportedOperationException("Unrecognized URI: " + uri);
        }

        if (BuildConfig.DEBUG) { Log.d(TAG, "file: " + fd); }
        return fd;
    }

    /**
     * Expose the openFileHelper
     *
     * @param uri
     * @param mode
     * @return open file descriptor for the file named in the _data column
     * @throws FileNotFoundException
     */
    public ParcelFileDescriptor openData(Uri uri, String mode) throws FileNotFoundException {
        return openFileHelper(uri, mode);
    }
}
