package com.marakana.android.stream;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;

public class PostFragment extends WebViewFragment {
	private static final String TAG = "Stream-PostFragment";
	private static final String MIME_TYPE = "text/html";
	private static final String ENCODING = "utf-8";
	private static long id = -1;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getWebView().setWebViewClient(WEBVIEW_CLIENT);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (id > 0)
			this.updatePost(id);
	}

	/** Gets the post with the given ID and displays it. */
	public void updatePost(long id) {

		// Get the data
		Uri uri = ContentUris.withAppendedId(StreamContract.CONTENT_URI, id);
		Log.d(TAG, "updatePost uri: " + uri);
		Cursor cursor = getActivity().getContentResolver().query(uri, null,
				null, null, null);

		// Position the cursor to the the first element
		if (cursor.moveToFirst()) {
			String description = cursor.getString(5);

			// Update the webview
			super.getWebView().loadData(description, MIME_TYPE, ENCODING);

			this.id = id;
		}
	}

	private static final WebViewClient WEBVIEW_CLIENT = new WebViewClient() {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return false;
		}

	};
}
