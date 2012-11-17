package com.marakana.android.stream;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.marakana.android.stream.svc.RefreshService;


/**
 * ActionBarMgr
 */
public class ActionBarMgr {
    private final Activity activity;

    /**
     * @param activity
     * @param enabled if true, home as up is enabled
     */
    public ActionBarMgr(Activity activity, boolean enabled) {
        this.activity = activity;
        activity.getActionBar().setDisplayHomeAsUpEnabled(enabled);

    }

    /**
     * @param rez
     * @param menu
     * @return true iff menu changed
     */
    public boolean onCreateOptionsMenu(int rez, Menu menu) {
        activity.getMenuInflater().inflate(rez, menu);
        return true;
    }

    /**
     * @param item
     * @return true iff event consumed
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            // called when the Home (Up) button is pressed in the Action Bar.
            case android.R.id.home:
                i = new Intent(activity, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(i);
                activity.finish();
                break;
            case R.id.menu_refresh:
                RefreshService.pollOnce(activity);
                break;
            // these should be direct actions...
            case R.id.menu_link:
//                i = new Intent(Intent.ACTION_VIEW, Uri.parse(post.getLink().toString()));
//                activity.startActivity(i);
                break;
            case R.id.menu_share:
                i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
//                i.putExtra(Intent.EXTRA_SUBJECT, post.getTitle());
//                i.putExtra(Intent.EXTRA_TEXT, post.getTitle() + ": " + post.getLink());
//                activity.startActivity(Intent.createChooser(share, "Share this post"));
                break;
            default:
                return false;
        }
        return true;
    }
}
