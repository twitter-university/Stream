package com.marakana.android.stream;

import android.app.Activity;
import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.marakana.android.parser.Post;
import com.marakana.android.stream.db.StreamContract;

/**
 * PostActivity
 */
public class PostActivity extends Activity {
    /** intent key for id parameter */
    public static final String KEY_ID = "com.marakana.android.stream.ID";

    private static final String TAG = "Stream-PostActivity";

    private ActionBarMgr actionBarMgr;
    private PostFragment postFragment;
    private Post post;
    private long id;

    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Setup the action bar
        actionBarMgr = new ActionBarMgr(this, true);

        id = getIntent().getLongExtra(KEY_ID, -1);

        Uri uri = ContentUris.withAppendedId(StreamContract.Feed.URI, id);

        //!!! on the UI thread????
        post = Post.getPost(id, getContentResolver().query(uri, null, null, null, null));
        setTitle(post.getTitle());

        // Setup the post fragment
        postFragment = (PostFragment) getFragmentManager().findFragmentById(R.id.fragment_post);
        postFragment.update(post);

        Log.d(TAG, "Updated post for id: " + id);
    }

    /**
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return actionBarMgr.onCreateOptionsMenu(R.menu.activity_post, menu);
    }


    /**
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return (actionBarMgr.onOptionsItemSelected(item))
            ? true
            : super.onOptionsItemSelected(item);
    }
}
