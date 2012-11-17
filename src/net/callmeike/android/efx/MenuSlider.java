package net.callmeike.android.efx;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;


/**
 * MenuSlider
 *
 * When I use this, I frequently see the error:
 *     10-06 18:11:45.746: E/AndroidRuntime(701): java.lang.RuntimeException:
 *     Unable to instantiate application android.app.Application: java.lang.NullPointerException
 * Word has it that this is an Eclipse problem.  It is being addressed here:
 *     http://code.google.com/p/android/issues/detail?id=25869
 *
 * @param <T> menu item type
 */
public abstract class MenuSlider<T> {
    final Activity activity;

    boolean animating;

    private final int viewLayoutId;
    private final int itemLayoutId;
    private final int duration;
    private final int overhang;

    private final Rect viewRect = new Rect();
    private final Point bounds = new Point();
    private ListView menuView;

    /**
     * @param activity
     * @param viewLayoutId
     * @param itemLayoutId
     * @param duration
     * @param overhang
     */
    public MenuSlider(
            Activity activity,
            int viewLayoutId,
            int itemLayoutId,
            int duration,
            int overhang)
    {
        this.activity = activity;
        this.viewLayoutId = viewLayoutId;
        this.itemLayoutId = itemLayoutId;
        this.duration = duration;
        this.overhang = overhang;
        activity.getActionBar().setHomeButtonEnabled(true);
    }

    /**
     * @return a list of menu items
     */
    protected abstract List<T> getMenuItems();

    /**
     * @param item
     */
    protected abstract void handleSelection(T item);

    /**
     *
     */
    public void toggleSlider() {
        Log.d("SLIDER", "toggled: " + animating);
        if (animating) { return; }

        setBounds();

        if (null == menuView) { menuView = show(); }
        else { hideMenuView(true); }
    }

    /**
     *
     */
    public void reset() {
        bounds.set(0, 0);
        if (animating) { menuView = null; }
        else if (null != menuView) { hideMenuView(false); }
    }

    void handleMenuSelection(T item) {
        if (animating) { return; }
        handleSelection(item);
    }

    void onShowComplete(ListView menu) {
        if (null == menuView) { hide(false, menu); }
        animating = false;
    }

    void onHideComplete(ListView menu) {
        ((FrameLayout) activity.getWindow().getDecorView()).removeView(menu);
        animating = false;
    }

    private void hideMenuView(boolean animate) {
        ListView menu = menuView;
        menuView = null;
        hide(animate, menu);
    }

    private void hide(boolean animate, final ListView menu) {
        animating = animate;

        moveFrame(
                bounds.y,
                0,
                (!animating) ? null : new AnimationListener() {
                    @Override public void onAnimationEnd(Animation a) { onHideComplete(menu); }
                    @Override public void onAnimationRepeat(Animation a) { }
                    @Override public void onAnimationStart(Animation a) { }
                });

        if (!animating) { onHideComplete(menu); }
    }

    private ListView show() {
        animating = true;

        final ListView menu = getMenu();

        LinearLayout actionBarFrame = moveFrame(
                -bounds.y,
                bounds.y,
                new AnimationListener() {
                    @Override public void onAnimationEnd(Animation a) { onShowComplete(menu); }
                    @Override public void onAnimationRepeat(Animation a) { }
                    @Override public void onAnimationStart(Animation a) { }
                });

        FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();
        decorView.addView(menu);
        decorView.bringChildToFront(actionBarFrame);

        return menu;
    }

    private LinearLayout moveFrame(int delta, int to, AnimationListener animationListener) {
        LinearLayout actionBarFrame
            = (LinearLayout) activity.findViewById(android.R.id.content).getParent();

        FrameLayout.LayoutParams params
            = (FrameLayout.LayoutParams) actionBarFrame.getLayoutParams();
        params.setMargins(to, 0, -to, 0);
        actionBarFrame.setLayoutParams(params);

        if (null != animationListener) { animate(actionBarFrame, delta, animationListener); }

        return actionBarFrame;
    }

    private void setBounds() {
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(viewRect);
        bounds.set(viewRect.top, viewRect.right - overhang);
    }

    private void animate(View v, int delta, AnimationListener animationListener) {
        TranslateAnimation ta = new TranslateAnimation(delta, 0, 0, 0);
        ta.setInterpolator(new DecelerateInterpolator());
        ta.setAnimationListener(animationListener);
        ta.setDuration(duration);
        v.startAnimation(ta);
    }

    private ListView getMenu() {
        LayoutInflater inflater
            = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ListView menu = (ListView) inflater.inflate(viewLayoutId, null);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                Gravity.LEFT);
        params.setMargins(0, bounds.x, 0, 0);
        menu.setLayoutParams(params);

        final ArrayAdapter<T> adapter = new ArrayAdapter<T>(activity, itemLayoutId);
        adapter.addAll(getMenuItems());
        menu.setAdapter(adapter);

        menu.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> row, View v, int pos, long id) {
                handleMenuSelection(adapter.getItem(pos));
            } });

        return menu;
    }
}
