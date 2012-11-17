package com.marakana.android.stream;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.marakana.android.stream.svc.RefreshService;


/**
 * MainActivity
 */
public class MainActivity extends Activity {
    private ActionBarMgr actionBarMgr;

    /**
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return actionBarMgr.onCreateOptionsMenu(R.menu.activity_main, menu);
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

    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup the action bar
        getActionBar().setDisplayHomeAsUpEnabled(false);

        // Refresh the data
        RefreshService.pollOnce(this);

        actionBarMgr = new ActionBarMgr(this, true);
    }

    /**
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        actionBarMgr.reset();
    }
}
