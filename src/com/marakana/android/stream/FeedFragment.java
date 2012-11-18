package com.marakana.android.stream;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


import com.marakana.android.stream.db.StreamContract;
import com.marakana.android.stream.svc.RefreshService;


/**
 * FeedFragment
 */
public class FeedFragment extends ListFragment {
    private static final String TAG = "FEED";

    private static final int LOADER_ID = 47;

    static final String[] FROM = {
        StreamContract.Feed.Columns.TITLE,
        StreamContract.Feed.Columns.DESC,
        StreamContract.Feed.Columns.PUB_DATE
    };

    private static final int[] TO = {
        R.id.text_title,
        R.id.text_description,
        R.id.text_date
    };

    private static final SimpleCursorAdapter.ViewBinder VIEW_BINDER
        = new SimpleCursorAdapter.ViewBinder() {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            switch (view.getId()) {
                case R.id.text_description:
                    String description = cursor.getString(columnIndex);
                    ((TextView) view).setText(Html.fromHtml(description).toString());
                    return true;
                case R.id.text_date:
                    long timestamp = cursor.getLong(columnIndex);
                    ((TextView) view).setText(DateUtils.getRelativeTimeSpanString(timestamp));
                    return true;
                default:
                    return false;
            }
        }
    };

    // --- Loader Callbacks
    private final LoaderManager.LoaderCallbacks<Cursor> loader
        = new LoaderManager.LoaderCallbacks<Cursor>() {
        private final String[] PROJ = new String[FROM.length + 1];
        {
            PROJ[0] = StreamContract.Feed.Columns.ID;
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
            Log.d(TAG, "loader finished");
            ((SimpleCursorAdapter) getListAdapter()).swapCursor(cursor);
            setSelection(0);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> cursor) {
            Log.d(TAG, "loader reset");
            ((SimpleCursorAdapter) getListAdapter()).swapCursor(null);
        }
    };

    /**
     * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final MainActivity activity = (MainActivity) getActivity();

        setEmptyText(getString(R.string.no_feed));

        // Create the adapter
        SimpleCursorAdapter adapter
            = new SimpleCursorAdapter(activity, R.layout.list_item, null, FROM, TO, 0);
        adapter.setViewBinder(VIEW_BINDER);
        setListAdapter(adapter);

        activity.getActionBarMgr().attachFlingListener(getListView());

        // Initialize the loader
        getLoaderManager().initLoader(LOADER_ID, null, loader);

        // Start the RefreshService
        RefreshService.pollOnce(activity);

        Log.d(TAG, "created");
    }

    /**
     * @see android.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "click @" + position + ": " + id);
        Intent i = new Intent(getActivity(), PostActivity.class);
        i.putExtra(PostFragment.KEY_ID, id);
        startActivity(i);
    }
}
