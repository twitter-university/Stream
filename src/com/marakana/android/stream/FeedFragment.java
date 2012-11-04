package com.marakana.android.stream;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class FeedFragment extends ListFragment {
  private static final String TAG = "Stream-FeedFragment";
  private static final String[] FROM = { StreamContract.Columns.TITLE,
      StreamContract.Columns.DESCRIPTION };
  private static final int[] TO = { R.id.text_title, R.id.text_description };
  private static final int LOADER_ID = 47;
  private static final String MIME_TYPE = "text/html";
  private static final String ENCODING = "utf-8";

  private SimpleCursorAdapter adapter;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    super.setEmptyText("No data yet...");

    // Create the adapter
    adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item, null,
        FROM, TO, 0);
    adapter.setViewBinder(VIEW_BINDER);

    super.setListAdapter(adapter);

    // Initialize the loader
    super.getLoaderManager().initLoader(LOADER_ID, null, loader);

    // Start the RefreshService
    Log.d(TAG, "onActivityCreated");
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    Log.d(TAG, "onListItemClick'd for pos: " + position + " id: " + id);
    ((MainActivity) getActivity()).showPost(id);
  }

  // --- Loader Callbacks
  private final LoaderManager.LoaderCallbacks<Cursor> loader = new LoaderManager.LoaderCallbacks<Cursor>() {
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
      return new CursorLoader(getActivity(), StreamContract.CONTENT_URI, null,
          null, null, StreamContract.DEFAULT_SORT);
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

  private static final ViewBinder VIEW_BINDER = new ViewBinder() {

    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
      if (view.getId() != R.id.text_description)
        return false;
      
      String description = cursor.getString(columnIndex);
//      WebView webView = ((WebView)view);
//      webView.loadData(description, MIME_TYPE, ENCODING);
      TextView textView = (TextView)view;
      textView.setText( Html.fromHtml(description) );
      
      return true;
    }

  };
}
