package com.marakana.android.stream;

import android.net.Uri;
import android.provider.BaseColumns;

public final class StreamContract {
	private StreamContract() {}
	
	// content://com.marakana.android.stream.provider/feed
	
	public static final String AUTHORITY = "com.marakana.android.stream.provider";
	public static final String PATH = DbHelper.TABLE;
	public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY + "/" + PATH );
	public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.marakana.post";
	public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd.marakana.post";

	public static final String DEFAULT_SORT = Columns.PUB_DATE + " DESC";
	
	/** Standard RSS column names. */
	public static final class Columns implements BaseColumns {
		private Columns() {}
		public static final String TITLE = "title";
		public static final String LINK = "link";
		public static final String AUTHOR = "author";
		public static final String PUB_DATE = "pub_date";
		public static final String CATEGORY = "category";
		public static final String DESCRIPTION = "description";
	}
}
