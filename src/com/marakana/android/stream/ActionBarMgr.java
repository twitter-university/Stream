package com.marakana.android.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import net.callmeike.android.efx.MenuSlider;

import com.marakana.android.stream.svc.RefreshService;

final class MenuElement {
    private final String label;
    private final Class<?> target;

    public MenuElement(String label, Class<?> target) {
        this.label = label;
        this.target = target;
    }

    public Class<?> getTarget() { return target; }

    @Override public String toString() { return label; }
}

/**
 * ActionBarMgr
 */
public class ActionBarMgr extends MenuSlider<MenuElement> {
    private static final int MENU_DURATION = 300;
    private static final int MENU_OVERHANG = 128;

    private static final List<MenuElement> topMenu;
    static {
        List<MenuElement> l = new ArrayList<MenuElement>();
        topMenu = Collections.unmodifiableList(l);
    }

    private final Activity activity;

    /**
     * @param activity
     * @param enabled if true, home as up is enabled
     */
    public ActionBarMgr(Activity activity, boolean enabled) {
        super(activity, R.layout.menu, R.layout.menu_item, MENU_DURATION, MENU_OVERHANG);
        activity.getActionBar().setDisplayHomeAsUpEnabled(enabled);
        this.activity = activity;
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
                toggleSlider();
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

    /**
     * @see net.callmeike.android.efx.MenuSlider#getMenuItems()
     */
    @Override
    protected List<MenuElement> getMenuItems() { return topMenu; }

    /**
     * @see net.callmeike.android.efx.MenuSlider#handleSelection(java.lang.Object)
     */
    @Override
    protected void handleSelection(MenuElement item) {
        Intent i = new Intent(activity, item.getTarget());
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(i);
    }
}
