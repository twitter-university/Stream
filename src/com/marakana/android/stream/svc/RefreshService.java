package com.marakana.android.stream.svc;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.marakana.android.parser.FeedParser;
import com.marakana.android.parser.FeedParser.PostHandler;
import com.marakana.android.stream.db.StreamContract;


/**
 * RefreshService
 */
public class RefreshService extends IntentService {
    private static final String TAG = "Stream-RefreshService";

    private static final URL FEED_URL;
    static {
        try { FEED_URL = new URL("http://marakana.com/s/feed.rss"); }
        catch (Exception e) { throw new RuntimeException("Can't parse feed URL!"); }
    }

    private class ContentValuesPostHandler implements PostHandler {
        private final ContentValues vals = new ContentValues();

        public ContentValuesPostHandler() {}

        @Override
        public void finish() { writePost(vals); }

        @Override
        public void setTitle(String title) {
            vals.put(StreamContract.Feed.Columns.TITLE, title);
        }

        @Override public void setAuthor(String author) { }

        @Override
        public void setPubDate(String pubDate) {
            vals.put(StreamContract.Feed.Columns.PUB_DATE, pubDate);
        }

        @Override
        public void setDescription(String desc) {
            vals.put(StreamContract.Feed.Columns.DESC, desc);
        }

        @Override
        public void setLink(String link) {
            vals.put(StreamContract.Feed.Columns.LINK, link);
        }
    }

    /**
     * @param ctxt
     */
    public static void pollOnce(Context ctxt) {
        ctxt.startService(new Intent(ctxt, RefreshService.class));
    }


    private int added;

    /**
     *
     */
    public RefreshService() { super(TAG); }

    /**
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        added = 0;

        InputStream feed = null;
        try {
            HttpURLConnection c = (HttpURLConnection) FEED_URL.openConnection();
            c.addRequestProperty("Accept", "application/rss+xml");
            parseFeed(c.getInputStream());
        }
        catch (IOException e) {
            Log.w(TAG, "Failed opening connection to: " + FEED_URL);
        }
        finally {
            Log.d(TAG, "Inserted records: " + added);
            if (null != feed) {
                try { feed.close(); } catch (IOException e) { }
            }
        }

        stopSelf();
    }

    void writePost(ContentValues values) {
        if (null != getContentResolver().insert(StreamContract.Feed.URI, values)) { added++; }
    }

    private void parseFeed(InputStream feed) {
        FeedParser parser = new FeedParser();
        try { parser.parse(feed, new ContentValuesPostHandler()); }
        catch (Exception e) {
            Log.w(TAG, "Parse failed", e);
        }
    }
}
