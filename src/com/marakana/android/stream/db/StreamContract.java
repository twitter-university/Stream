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

    /** The posts table */
    public static final class Posts {
        private Posts() {}

        /** Posts table */
        public static final String TABLE = "posts";

        /** Posts table URI */
        public static final Uri URI = URI_BASE.buildUpon().appendPath(TABLE).build();

        /** Posts table DIR type */
        public static final String CONTENT_TYPE_DIR
            = ContentResolver.CURSOR_DIR_BASE_TYPE + " + /vnd.marakana.stream.post";
        /** Posts table ITEM type */
        public static final String CONTENT_TYPE_ITEM
            = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.marakana.stream.post";

        /** Posts table columns. */
        public static final class Columns {
            private Columns() {}

            /** article pk */
            public static final String ID = BaseColumns._ID;
            /** article uri */
            public static final String LINK = "link";
            /** article title */
            public static final String TITLE = "title";
            /** article author */
            public static final String AUTHOR = "author";
            /** article publication data */
            public static final String PUB_DATE = "pub_date";
            /** article description */
            public static final String SUMMARY = "summary";
            /** ref to article content */
            public static final String CONTENT = "content";
            /** ref to article thumbnail */
            public static final String THUMB = "thumb";

            /** Special column */
            public static final String MAX_PUB_DATE = "max_pub_date";
        }
    }

    /** The authors table */
    public static final class Authors {
        private Authors() {}

        /** Authors table */
        public static final String TABLE = "authors";

        /** Authors table URI */
        public static final Uri URI = URI_BASE.buildUpon().appendPath(TABLE).build();

        /** Authors table DIR type */
        public static final String CONTENT_TYPE_DIR
            = ContentResolver.CURSOR_DIR_BASE_TYPE + " + /vnd.marakana.stream.author";
        /** Authors table ITME type */
        public static final String CONTENT_TYPE_ITEM
            = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.marakana.stream.author";

        /** Authors table columns. */
        public static final class Columns {
            private Columns() {}
            /** author pk */
            public static final String ID = BaseColumns._ID;
            /** author uri */
            public static final String LINK = "link";
            /** author name */
            public static final String NAME = "name";
        }
    }

    /**
     * Tags
     */
    public static final class Tags {
        private Tags() {}

        /** Tags table */
        public static final String TABLE = "tags";

        /** Tags table URI */
        public static final Uri URI = URI_BASE.buildUpon().appendPath(TABLE).build();

        /** Tags table DIR type */
        public static final String CONTENT_TYPE_DIR
            = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.marakana.stream.tag";
        /** Feed table ITME type */
        public static final String CONTENT_TYPE_ITEM
            = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.marakana.stream.tag";

        /** Standard RSS column names. */
        public static final class Columns {
            private Columns() {}

            /** tag pk */
            public static final String ID = BaseColumns._ID;
            /** tag uri */
            public static final String LINK = "link";
            /** tag title */
            public static final String TITLE = "title";
            /** tag description */
            public static final String DESC = "description";
        }
    }
}
