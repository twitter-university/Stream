package com.marakana.android.stream.svc;

import java.util.List;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.marakana.android.parser.FeedParser;
import com.marakana.android.parser.Post;
import com.marakana.android.stream.db.StreamContract;


/**
 * RefreshService
 */
public class RefreshService extends IntentService {
    private static final String TAG = "Stream-RefreshService";

    private static final String FEED_URL = "http://marakana.com/s/feed.rss";

    /**
     * @param ctxt
     */
    public static void pollOnce(Context ctxt) {
        ctxt.startService(new Intent(ctxt, RefreshService.class));
    }


    private final FeedParser parser;

    /**
     *
     */
    public RefreshService() {
        super(TAG);
        parser = FeedParser.getParser(FEED_URL, FeedParser.Type.ANDROID_SAX);
    }

    /**
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        List<Post> posts;

        try { posts = parser.parse(); }
        catch (Exception e) {
            Log.w(TAG, "Failed parsing: " + FEED_URL, e);
            return;
        }

        if (null == posts) {
            Log.w(TAG, "No posts in feed: " + FEED_URL);
            return;
        }

        int i = 0;
        ContentValues values = new ContentValues();
        ContentResolver resolver = getContentResolver();
        for (Post post : posts) {
            values.clear();
            values.put(StreamContract.Feed.Columns.ID, Long.valueOf(post.hashCode()));
            values.put(StreamContract.Feed.Columns.TITLE, post.getTitle());
            values.put(StreamContract.Feed.Columns.DESC, post.getDescription());
            values.put(StreamContract.Feed.Columns.LINK, post.getLinkString());
            values.put(StreamContract.Feed.Columns.PUB_DATE, Long.valueOf(post.getTimestamp()));

            // Insert into the content provider
            if (null != resolver.insert(StreamContract.Feed.URI, values)) { i++ ; }
        }

        Log.d(TAG, "Inserted records: " + i);
    }
}
