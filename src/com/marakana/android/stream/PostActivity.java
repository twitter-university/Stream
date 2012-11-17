package com.marakana.android.stream;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * PostActivity
 */
public class PostActivity extends Activity {
    //private static final String TAG = "POST";

    private ActionBarMgr actionBarMgr;

    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Setup the action bar
        actionBarMgr = new ActionBarMgr(this, true);

        ((PostFragment) getFragmentManager().findFragmentById(R.id.fragment_post))
            .loadPost(getIntent().getExtras());
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
