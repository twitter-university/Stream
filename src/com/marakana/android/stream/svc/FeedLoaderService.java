package com.marakana.android.stream.svc;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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
public class FeedLoaderService extends IntentService {
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
        ctxt.startService(new Intent(ctxt, FeedLoaderService.class));
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
                System.currentTimeMillis() + FeedLoaderService.POLL_INTERVAL,
                FeedLoaderService.POLL_INTERVAL,
                PendingIntent.getService(
                        ctxt,
                        INTENT_TAG,
                        new Intent(ctxt, FeedLoaderService.class),
                        PendingIntent.FLAG_UPDATE_CURRENT));
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "peroidic polling started @" + FeedLoaderService.POLL_INTERVAL);
        }
    }

    private class ContentValuesPostHandler implements AtomFeedParser.PostHandler {
        // Atom format: "yyyy-MM-dd'T'HH:mm:ssZ"
        // RSS date format: "EEE, dd MMM yyyy-MM-ddHH:mm:ss Z"
        private final SimpleDateFormat FORMATTER
                = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);

        private final ContentValues authorVals = new ContentValues();
        private final ContentValues postVals = new ContentValues();
        private final ContentValues tmpVals = new ContentValues();
        private final StringBuilder tags = new StringBuilder();

        public ContentValuesPostHandler() { }

        @Override
        public void finish() {
            if (0 < tags.length()) {
                postVals.put(StreamContract.Posts.Columns.TAGS, tags.toString());
                tags.setLength(0);
            }

            writeAuthor(authorVals);
            authorVals.clear();

            writePost(postVals);
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
            postVals.put(StreamContract.Posts.Columns.AUTHOR, uri);
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
        public void setThumb(String thumb, String type) {
            tmpVals.clear();
            tmpVals.put(StreamContract.Thumbs.Columns.LINK, thumb);
            tmpVals.put(StreamContract.Thumbs.Columns.TYPE, type);
            writeThumb(tmpVals);
            postVals.put(StreamContract.Posts.Columns.THUMB, thumb);
        }

        @Override
        public void setContent(String link, String type) {
            postVals.put(StreamContract.Posts.Columns.TYPE, type);
            postVals.put(StreamContract.Posts.Columns.CONTENT, link);
        }

        @Override
        public void addCategory(String label, String term) {
            tmpVals.clear();
            tmpVals.put(StreamContract.Tags.Columns.TITLE, label);
            tmpVals.put(StreamContract.Tags.Columns.DESC, term);
            writeTag(tmpVals);

            if (0 < tags.length()) { tags.append(","); }
            tags.append(label);
        }
    }

    private static final AtomicBoolean mutex = new AtomicBoolean();


    private List<ContentValues> posts = new ArrayList<ContentValues>();

    /**
     *
     */
    public FeedLoaderService() { super(TAG); }

    /**
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "starting poll"); }

        URL url = null;
        InputStream feed = null;
        if (mutex.getAndSet(true)) { return; }

        int added = 0;
        try {
            url = getFeedUrl();
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.addRequestProperty(HEAD_ACCEPT, MIME_TYPE);

            posts.clear();
            parseFeed(c.getInputStream());
            added = getContentResolver().bulkInsert(
                    StreamContract.Posts.URI,
                    posts.toArray(new ContentValues[posts.size()]));
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

    void writeThumb(ContentValues vals) {
        getContentResolver().insert(StreamContract.Thumbs.URI, vals);
    }

    void writeTag(ContentValues vals) {
        getContentResolver().insert(StreamContract.Tags.URI, vals);
    }

    void writeAuthor(ContentValues vals) {
        getContentResolver().insert(StreamContract.Authors.URI, vals);
    }

    void writePost(ContentValues vals) {
        posts.add(new ContentValues(vals));
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
