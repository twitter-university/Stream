package com.marakana.android.stream;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;

import com.marakana.android.stream.db.StreamContract;


/**
 * PostFragment
 *
 * !!! Getting error:
 *     E/webview(9263): Error: WebView.destroy() called while still attached!
 */
public class PostFragment extends WebViewFragment {
    /** intent key for id parameter */
    public static final String KEY_ID = "com.marakana.android.stream.ID";

    private static final String MIME_TYPE = "text/html";
    private static final String ENCODING = "utf-8";
    private static final String PREFIX = "<html><body>\n";
    private static final String STYLE = "<style type=\"text/css\">p {font-size:1.1em;}</style>";
    private static final String SUFFIX = "\n</body></html>";

    private static final int LOADER_ID = 47;

    private static final String TAG = "POST";


    private final WebViewClient webviewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // If it's one of our pages, load it within our app
            if (Uri.parse(url).getHost().equals("marakana.com")
                || Uri.parse(url).getHost().equals("mrkn.co"))
            {
                return false;
            }

            // Otherwise, use the default browser to handle this URL
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            getActivity().startActivity(intent);
            return true;
        }
    };

    // --- Loader Callbacks
    private final LoaderManager.LoaderCallbacks<Cursor> loader
        = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return (null == args)
                ? null
                : new CursorLoader(
                    getActivity(),
                    ContentUris.withAppendedId(StreamContract.Feed.URI, args.getLong(KEY_ID)),
                    new String[] { StreamContract.Feed.Columns.DESC },
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> ldr, Cursor cursor) { setWebContent(cursor); }

        @Override
        public void onLoaderReset(Loader<Cursor> ldr) { setWebContent(null); }
    };

    /**
     * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PostActivity activity = (PostActivity) getActivity();

        // Initialize the loader
        getLoaderManager().initLoader(LOADER_ID, null, loader);

        getWebView().setWebViewClient(webviewClient);

        // Enable JavaScript
        WebSettings webSettings = getWebView().getSettings();
        webSettings.setJavaScriptEnabled(true);

        // add the fling opener
        activity.getActionBarMgr().attachFlingListener(getWebView());

        Log.d(TAG, "created");
    }

    /**
     * Gets the post with the given ID and displays it.
     *
     * @param args
     */
    void loadPost(Bundle args) {
        getLoaderManager().restartLoader(LOADER_ID, args, loader);
    }

    void setWebContent(Cursor cursor) {
        WebView view = getWebView();
        if (null == view) { return; }

        StringBuilder content = new StringBuilder(PREFIX).append(STYLE);
        content.append(
                ((null != cursor) && cursor.moveToNext())
                ? cursor.getString(0)
                        : "<p>Empty post.</p>");
        content.append(SUFFIX);

        view.loadData(content.toString(), MIME_TYPE, ENCODING);
    }
}
