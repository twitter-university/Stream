package com.marakana.android.stream;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class FeedFragment extends ListFragment {
	private static final String TAG = "Stream-FeedFragment";
	private static final String[] FROM = { StreamContract.Columns.TITLE };
	private static final int[] TO = { android.R.id.text1 };
	private static final int LOADER_ID = 47;
	private SimpleCursorAdapter adapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		super.setEmptyText("No data yet...");

		// Create the adapter
		adapter = new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_list_item_1, null, FROM, TO, 0);

		super.setListAdapter(adapter);

		// Initialize the loader
		super.getLoaderManager().initLoader(LOADER_ID, null, loader);

		Log.d(TAG, "onActivityCreated");
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(TAG, "onListItemClick'd for pos: " + position + " id: " + id);
		PostFragment postFragment = (PostFragment) getFragmentManager()
				.findFragmentById(R.id.fragment_post);
		postFragment.updatePost(id);
	}

	// --- Loader Callbacks
	private final LoaderManager.LoaderCallbacks<Cursor> loader = new LoaderManager.LoaderCallbacks<Cursor>() {
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new CursorLoader(getActivity(), StreamContract.CONTENT_URI,
					null, null, null, StreamContract.DEFAULT_SORT);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			adapter.swapCursor(cursor);
			setSelection(0);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> cursor) {
			adapter.swapCursor(null);
		}
	};
}
