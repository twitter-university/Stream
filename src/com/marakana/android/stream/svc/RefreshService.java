package com.marakana.android.stream.svc;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

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
    private static final String TAG = "REFRESH";

    private static final URL FEED_URL;
    static {
        try { FEED_URL = new URL("http://marakana.com/s/feed.rss"); }
        catch (Exception e) { throw new RuntimeException("Can't parse feed URL!"); }
    }

    /**
     * @param ctxt
     */
    public static void pollOnce(Context ctxt) {
        ctxt.startService(new Intent(ctxt, RefreshService.class));
    }

    private class ContentValuesPostHandler implements PostHandler {
        private final SimpleDateFormat FORMATTER
                = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

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
            long t = 0;
            try { t = FORMATTER.parse(pubDate).getTime(); }
            catch (ParseException e) { }
            vals.put(StreamContract.Feed.Columns.PUB_DATE, Long.valueOf(t));
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

    private int added;
    private boolean running;

    /**
     *
     */
    public RefreshService() { super(TAG); }

    /**
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        boolean r;
        synchronized (this) {
            r = running;
            running = true;
        }
        if (r) { return; }

        added = 0;
        Log.d(TAG, "Starting parse: " + added);

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
            synchronized (this) { running = false; }
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
