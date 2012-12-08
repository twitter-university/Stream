package com.marakana.android.stream.svc;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.marakana.android.parser.AtomFeedParser;
import com.marakana.android.stream.BuildConfig;
import com.marakana.android.stream.db.StreamContract;


/**
 * RefreshService
 */
public class RefreshService extends IntentService {
    private static final String TAG = "REFRESH";

    /** Base URI for the Marakana stream */
    public static final Uri FEED_URI = Uri.parse("http://marakana.com/s");
    /** URL query time constraint */
    public static final String FEED_TIME_CONSTRAINT = "publishedSince.millis";
    /** Accept: header */
    public static final String HEAD_ACCEPT = "Accept";
    /** Feed MIME type */
    public static final String MIME_TYPE = "application/atom+xml";  //"application/rss+xml";

    /** Poll interval: !!! should be a preference */
    public static final long POLL_INTERVAL = 5 * 60 * 1000;

    /** Intent tag for new feed count */
    public static final String TAG_NEW_FEED_COUNT = "feed.count";

    private static final int INTENT_TAG = 42;

    private static final long TIME_KLUDGE = 1001L;

    /**
     * Poll just once.
     *
     * @param ctxt
     */
    public static void pollOnce(Context ctxt) {
        ctxt.startService(new Intent(ctxt, RefreshService.class));
    }

    /**
     * Start periodic polling.
     *
     * @param ctxt
     */
    public static void startPolling(Context ctxt) {
        AlarmManager am = (AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(
                AlarmManager.RTC,
                System.currentTimeMillis() + RefreshService.POLL_INTERVAL,
                RefreshService.POLL_INTERVAL,
                PendingIntent.getService(
                        ctxt,
                        INTENT_TAG,
                        new Intent(ctxt, RefreshService.class),
                        PendingIntent.FLAG_UPDATE_CURRENT));
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "peroidic polling started @" + RefreshService.POLL_INTERVAL);
        }
    }

    private class ContentValuesPostHandler implements AtomFeedParser.PostHandler {
        private final SimpleDateFormat FORMATTER
                = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

        private final ContentValues postVals = new ContentValues();
        private final ContentValues authorVals = new ContentValues();

        public ContentValuesPostHandler() {}

        @Override
        public void finish() {
            writePost(postVals, authorVals);
            postVals.clear();
        }

        @Override
        public void setId(String uri) {
            postVals.put(StreamContract.Posts.Columns.LINK, uri);
        }

        @Override
        public void setTitle(String title) {
            postVals.put(StreamContract.Posts.Columns.TITLE, title);
        }

        @Override
        public void setAuthorName(String name) {
            authorVals.put(StreamContract.Authors.Columns.NAME, name);
        }

        @Override
        public void setAuthorUri(String uri) {
            authorVals.put(StreamContract.Authors.Columns.LINK, uri);
        }

        @Override
        public void setPubDate(String date) {
            long t = 0;
            try { t = FORMATTER.parse(date).getTime(); }
            catch (ParseException e) { }
            postVals.put(StreamContract.Posts.Columns.PUB_DATE, Long.valueOf(t));
        }

        @Override
        public void setSummary(String summary) {
            postVals.put(StreamContract.Posts.Columns.SUMMARY, summary);
        }

        @Override
        public void setThumb(String thumb) {
            postVals.put(StreamContract.Posts.Columns.THUMB, thumb);
        }

        @Override
        public void setContent(String link) {
            postVals.put(StreamContract.Posts.Columns.CONTENT, link);
        }

        @Override
        public void addCategory(String label, String term) {
            // Ignore it for now...
        }
    }

    private static final AtomicBoolean mutex = new AtomicBoolean();
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
        if (BuildConfig.DEBUG) { Log.d(TAG, "starting poll"); }

        URL url = null;
        InputStream feed = null;
        if (mutex.getAndSet(true)) { return; }
        try {
            added = 0;

            url = getFeedUrl();
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.addRequestProperty(HEAD_ACCEPT, MIME_TYPE);

            parseFeed(c.getInputStream());
        }
        catch (IOException e) {
            Log.w(TAG, "Failed opening connection to: " + url);
        }
        finally {
            mutex.set(false);
            if (BuildConfig.DEBUG) { Log.d(TAG, "poll complete: " + added); }
            if (null != feed) {
                try { feed.close(); } catch (IOException e) { }
            }
        }

        // !!! Virgil D. seems to recommend this.  Really?
        stopSelf();
    }

    void writePost(ContentValues post, ContentValues author) {
        //ContentResolver resolver = getContentResolver();
        //resolver.insert(StreamContract.Authors.URI, author);
        //if (null != resolver.insert(StreamContract.Posts.URI, post)) { added++; }
        Log.d(TAG, "Post: " + post);
        Log.d(TAG, "Author: " + author);
    }

    private URL getFeedUrl() throws MalformedURLException {
        return new URL(
            FEED_URI.buildUpon().appendQueryParameter(
                FEED_TIME_CONSTRAINT,
                String.valueOf(getLatestPostTime() + TIME_KLUDGE))
            .build().toString());
    }

    private void parseFeed(InputStream feed) {
        AtomFeedParser parser = new AtomFeedParser();
        try { parser.parse(feed, new ContentValuesPostHandler()); }
        catch (Exception e) { Log.w(TAG, "Parse failed", e); }
    }

    private long getLatestPostTime() {
        Cursor c = getContentResolver().query(
            StreamContract.Posts.URI,
            new String[] { StreamContract.Posts.Columns.MAX_PUB_DATE },
            null,
            null,
            null);
        try { return (c.moveToNext()) ? c.getLong(0) : Long.MIN_VALUE; }
        finally { c.close(); }
    }
}
