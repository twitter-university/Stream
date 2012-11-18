package com.marakana.android.stream;

import android.app.Activity;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import net.callmeike.android.efx.WindowSlider;

import com.marakana.android.stream.svc.RefreshService;
import com.marakana.android.stream.efx.FlingDetector;
import com.marakana.android.stream.efx.FlingDetector.Direction;


/**
 * ActionBarMgr
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class ActionBarMgr extends WindowSlider implements FlingDetector.FlingListener {
    private static final int MENU_DURATION = 300;
    private static final int MENU_OVERHANG = 128;


    private final Activity activity;

    /**
     * @param activity
     * @param enabled if true, home as up is enabled
     */
    public ActionBarMgr(Activity activity, boolean enabled) {
        super(activity, R.layout.menu, MENU_DURATION, MENU_OVERHANG);
        this.activity = activity;

        activity.getActionBar().setDisplayHomeAsUpEnabled(enabled);

        attachFlingListener(findViewById(R.id.tree_menu));
    }

    /**
     * @param v the view
     */
    public void attachFlingListener(View v) {
        final GestureDetector flingDetector
            = new GestureDetector(activity, new FlingDetector(this));
        v.setOnTouchListener(
            new View.OnTouchListener() {
                @Override public boolean onTouch(View view, MotionEvent event) {
                    return flingDetector.onTouchEvent(event);
                } });
    }

    /**
     * @see com.marakana.android.stream.efx.FlingDetector.FlingListener#onFling(com.marakana.android.stream.efx.FlingDetector.Direction)
     */
    @Override
    public boolean onFling(Direction dir) { return toggleSlider(dir); }

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
                setVisible(!getVisible());
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

    private boolean toggleSlider(Direction dir) {
        boolean opening = (dir == FlingDetector.Direction.RIGHT);
        if (getVisible() == opening) { return false; }
        setVisible(opening);
        return true;
    }
}
