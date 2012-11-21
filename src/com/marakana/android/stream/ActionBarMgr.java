package com.marakana.android.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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


/**
 * ActionBarMgr
 *
 */
public class ActionBarMgr extends WindowSlider implements FlingDetector.FlingListener {
    private static final String TAG = "BARMGR";

    private static final int MENU_DURATION = 300;
    private static final int MENU_OVERHANG = 128;

    final static class MenuElement {
        private final String label;
        private final Class<?> target;

        public MenuElement(String label, Class<?> target) {
            this.label = label;
            this.target = target;
        }

        public Class<?> getTarget() { return target; }

        @Override public String toString() { return label; }
    }

    private static final List<MenuElement> menuItems;
    static {
        List<MenuElement> l = new ArrayList<MenuElement>();
        l.add(new MenuElement("Home", null));
        l.add(new MenuElement("Feed", MainActivity.class));
        l.add(new MenuElement("Tags", null));
        l.add(new MenuElement("Authors", null));
        menuItems = Collections.unmodifiableList(l);
    }


    private final Activity activity;

    /**
     * @param activity
     * @param enabled if true, home as up is enabled
     */
    public ActionBarMgr(Activity activity, boolean enabled) {
        super(activity, R.layout.menu, MENU_DURATION, MENU_OVERHANG);
        this.activity = activity;

        activity.getActionBar().setDisplayHomeAsUpEnabled(enabled);

        ListView menuView = (ListView) findViewById(R.id.menu_list);
        final ArrayAdapter<MenuElement> adapter
            = new ArrayAdapter<MenuElement>(activity, R.layout.menu_item);
        adapter.addAll(menuItems);

        menuView.setAdapter(adapter);
        menuView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> row, View v, int pos, long id) {
                handleMenuSelection(adapter.getItem(pos));
            } });

        attachFlingListener(menuView);
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
                toggleSlider(!getVisible());
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

    void handleMenuSelection(MenuElement item) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "menu selected: " + item); }
        Class<?> target = item.getTarget();
        if (null == target) { return; }
        Intent i = new Intent(activity, target);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(i);
        reset();
    }

    private boolean toggleSlider(Direction dir) {
        return toggleSlider(dir == FlingDetector.Direction.RIGHT);
    }

    private boolean toggleSlider(boolean opening) {
        if (getVisible() == opening) { return false; }
        setVisible(opening);
        return true;
    }
}
