package com.marakana.android.stream;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;

@SuppressLint("SetJavaScriptEnabled")
public class PostFragment extends WebViewFragment {
	private static final String TAG = "Stream-PostFragment";
	private static final String MIME_TYPE = "text/html";
	private static final String ENCODING = "utf-8";
	private static long id = -1;
	private static final String PREFIX = "<html><body>\n";
	private static final String STYLE = "<style type=\"text/css\">p {font-size:1.5em;}</style>";
	private static final String SUFIX = "\n</body></html>";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getWebView().setWebViewClient(WEBVIEW_CLIENT);
		
	    // Enable JavaScript
	    WebSettings webSettings = getWebView().getSettings();
	    webSettings.setJavaScriptEnabled(true);
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
      String title = cursor.getString(cursor.getColumnIndex(StreamContract.Columns.TITLE));
			String description = cursor.getString(cursor.getColumnIndex(StreamContract.Columns.DESCRIPTION));
      String link = cursor.getString(cursor.getColumnIndex(StreamContract.Columns.LINK));

			// Update the activity title and link
			getActivity().setTitle(title);
			((PostActivity)getActivity()).setLink(link);
			
			// Update the webview
			getWebView().loadData(PREFIX+STYLE+description+SUFIX, MIME_TYPE, ENCODING);

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
