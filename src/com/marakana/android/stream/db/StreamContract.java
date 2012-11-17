package com.marakana.android.stream.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * StreamContract
 */
public final class StreamContract {
    private StreamContract() {}

    /** Authority */
    public static final String AUTHORITY = "com.marakana.android.stream.provider";

    /** Base URI */
    public static final Uri URI_BASE = new Uri.Builder()
    .scheme(ContentResolver.SCHEME_CONTENT)
    .authority(AUTHORITY)
    .build();

    /** The feed table */
    public static final class Feed {
        private Feed() {}

        /** Feed table */
        public static final String TABLE = "feed";

        /** Feed table URI */
        public static final Uri URI = URI_BASE.buildUpon().appendPath(TABLE).build();

        /** Feed table DIR type */
        public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.marakana.post";
        /** Feed table ITME type */
        public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd.marakana.post";

        /** Standard RSS column names. */
        public static final class Columns {
            private Columns() {}

            /** article pk */
            public static final String ID = BaseColumns._ID;
            /** article title */
            public static final String TITLE = "title";
            /** article author */
            public static final String AUTHOR = "author";
            /** article publication data */
            public static final String PUB_DATE = "pub_date";
            /** article description */
            public static final String DESC = "description";
            /** article link */
            public static final String LINK = "link";
        }
    }
}
