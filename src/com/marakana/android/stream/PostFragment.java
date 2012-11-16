
package com.marakana.android.stream;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;

import com.marakana.android.parser.Post;

/**
 * PostFragment
 */
public class PostFragment extends WebViewFragment {
    //private static final String TAG = "Stream-PostFragment";
    private static final String MIME_TYPE = "text/html";
    private static final String ENCODING = "utf-8";
    private static final String PREFIX = "<html><body>\n";
    private static final String STYLE = "<style type=\"text/css\">p {font-size:1.1em;}</style>";
    private static final String SUFIX = "\n</body></html>";


    private final WebViewClient webviewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // If it's one of our pages, load it within our app
            if (Uri.parse(url).getHost().equals("marakana.com")
                || Uri.parse(url).getHost().equals("mrkn.co")) {
                return false;
            }

            // Otherwise, use the default browser to handle this URL
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            getActivity().startActivity(intent);
            return true;
        }
    };

    /**
     * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getWebView().setWebViewClient(webviewClient);

        // Enable JavaScript
        WebSettings webSettings = getWebView().getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    /**
     * @see android.webkit.WebViewFragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        // if (id > 0)
        // this.updatePost(id);
    }

    /**
     * @param post
     *
     * Gets the post with the given ID and displays it.
     */
    public void update(Post post) {
        String content = "<p>No data for this post.</p>";
        if (post != null) { content = post.getDescription(); }

        // Update the webview
        getWebView() .loadData(PREFIX + STYLE + content + SUFIX, MIME_TYPE, ENCODING);
    }
}
