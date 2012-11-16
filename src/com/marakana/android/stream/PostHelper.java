package com.marakana.android.stream;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.marakana.android.parser.Post;
import com.marakana.android.stream.db.StreamContract;


/**
 * PostHelper
 */
public class PostHelper {

    /**
     * @param context d
     * @param id
     * @return
     *
     *  Creates a new post from a given cursor.
     */
    public static Post getPost(Context context, long id) {
        Post post = null;

        // Get the data
        Uri uri = ContentUris.withAppendedId(StreamContract.Feed.URI, id);
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);

        // Position the cursor to the the first element
        if (c.moveToFirst()) {
            post = new Post(c.getLong(c.getColumnIndex(StreamContract.Feed.Columns.ID)));
            post.setTitle(c.getString(c.getColumnIndex(StreamContract.Feed.Columns.TITLE)));
            post.setDescription(c.getString(c.getColumnIndex(StreamContract.Feed.Columns.DESC)));
            post.setLink(c.getString(c.getColumnIndex(StreamContract.Feed.Columns.LINK)));
            post.setTimestamp(c.getLong(c.getColumnIndex(StreamContract.Feed.Columns.PUB_DATE)));
            post.setLink(c.getString(c.getColumnIndex(StreamContract.Feed.Columns.LINK)));
        }

        return post;
    }
}
