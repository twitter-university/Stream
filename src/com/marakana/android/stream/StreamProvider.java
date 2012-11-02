package com.marakana.android.stream;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class StreamProvider extends ContentProvider {
	private static final String TAG = "Stream-StreamProvider";
	private DbHelper dbHelper;

	private static final int POST_ITEM = 1;
	private static final int POST_DIR = 2;
	private static final UriMatcher uriMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		uriMatcher.addURI(StreamContract.AUTHORITY, StreamContract.PATH,
				POST_DIR);
		uriMatcher.addURI(StreamContract.AUTHORITY, StreamContract.PATH + "/#",
				POST_ITEM);
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DbHelper(this.getContext());
		return (dbHelper == null) ? false : true;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case POST_ITEM:
			return StreamContract.CONTENT_TYPE_ITEM;
		case POST_DIR:
			return StreamContract.CONTENT_TYPE_DIR;
		default:
			return null;
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(DbHelper.TABLE);

		// Item or Directory query?
		switch (uriMatcher.match(uri)) {
		case POST_DIR:
			break;
		case POST_ITEM:
			qb.appendWhere(StreamContract.Columns._ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		// Sort order provided?
		sortOrder = TextUtils.isEmpty(sortOrder) ? StreamContract.DEFAULT_SORT
				: sortOrder;

		// Do the query
		Cursor cursor = qb.query(db, projection, selection, selectionArgs,
				null, null, sortOrder);

		// Notify the uri has changed
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		Log.d(TAG, "query got records: "+cursor.getCount());
		
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// Valid Uri?
		if (uriMatcher.match(uri) != POST_DIR) {
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		// Insert into db
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long id = db.insertWithOnConflict(DbHelper.TABLE, null, values,
				SQLiteDatabase.CONFLICT_IGNORE);

		// Check if insert succeeded
		if (id > 0) {
			Uri ret = ContentUris.withAppendedId(uri, id);
			// Notify of change to uri
			getContext().getContentResolver().notifyChange(ret, null);
			return ret;
		} else {
			return null;
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
