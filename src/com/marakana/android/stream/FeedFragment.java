package com.marakana.android.stream;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


import com.marakana.android.stream.db.StreamContract;
import com.marakana.android.stream.efx.IconMgr;
import com.marakana.android.stream.svc.FeedLoaderService;


/**
 * FeedFragment
 */
public class FeedFragment extends ListFragment {
    private static final String TAG = "FEED";

    private static final int LOADER_ID = 47;

    static final String[] FROM = {
        StreamContract.Feed.Columns.THUMB,
        StreamContract.Feed.Columns.TITLE,
        StreamContract.Feed.Columns.SUMMARY,
        StreamContract.Feed.Columns.AUTHOR,
        StreamContract.Feed.Columns.PUB_DATE,
        StreamContract.Feed.Columns.TAGS
    };

    private static final int[] TO = {
        R.id.feed_thumb,
        R.id.feed_title,
        R.id.feed_summary,
        R.id.feed_author,
        R.id.feed_date,
        R.id.feed_tags,
    };

    private final SimpleCursorAdapter.ViewBinder VIEW_BINDER
        = new SimpleCursorAdapter.ViewBinder() {
        @Override
        public boolean setViewValue(View view, Cursor cur, int idx) {
            switch (view.getId()) {
                case R.id.feed_thumb:
//                    ((ImageView) view).setImageDrawable(
//                            iconMgr.getIcon(StreamContract.Thumbs.URI.buildUpon()
//                                    .appendPath(cur.getString(idx)).build()));
                    break;
                case R.id.feed_date:
                    long timestamp = cur.getLong(idx);
                    ((TextView) view).setText(DateUtils.formatDateTime(
                            FeedFragment.this.getActivity(),
                            timestamp,
                            DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_YEAR));
                    break;
                default:
                    return false;
            }
            return true;
        }
    };

    // --- Loader Callbacks
    private final LoaderManager.LoaderCallbacks<Cursor> loader
        = new LoaderManager.LoaderCallbacks<Cursor>() {
        private final String[] PROJ = new String[FROM.length + 1];
        {
            PROJ[0] = StreamContract.Posts.Columns.ID;
            System.arraycopy(FROM, 0, PROJ, 1, FROM.length);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(
                getActivity(),
                StreamContract.Feed.URI,
                PROJ,
                null,
                null,
                null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> ldr, Cursor cursor) {
            if (BuildConfig.DEBUG) { Log.d(TAG, "loader finished"); }
            ((SimpleCursorAdapter) getListAdapter()).swapCursor(cursor);
            setSelection(0);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> cursor) {
            if (BuildConfig.DEBUG) { Log.d(TAG, "loader reset"); }
            ((SimpleCursorAdapter) getListAdapter()).swapCursor(null) ;
        }
    };

    IconMgr iconMgr;

    /**
     * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final FeedActivity activity = (FeedActivity) getActivity();
        iconMgr = new IconMgr(activity);

        setEmptyText(getString(R.string.no_feed));

        // Create the adapter
        SimpleCursorAdapter adapter
            = new SimpleCursorAdapter(activity, R.layout.list_item, null, FROM, TO, 0);
        adapter.setViewBinder(VIEW_BINDER);
        setListAdapter(adapter);

        ListView list = getListView();
        list.setDivider(getResources().getDrawable(R.drawable.feed_divider));
        list.setDividerHeight(1);

        activity.getActionBarMgr().attachFlingListener(getListView());

        // Initialize the loader
        getLoaderManager().initLoader(LOADER_ID, null, loader);

        // Start the RefreshService
        FeedLoaderService.pollOnce(activity);

        if (BuildConfig.DEBUG) { Log.d(TAG, "created"); }
    }

    /**
     * @see android.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "click @" + position + ": " + id); }
        Intent i = new Intent(getActivity(), PostActivity.class);
        i.putExtra(PostFragment.KEY_ID, id);
        startActivity(i);
    }
}
