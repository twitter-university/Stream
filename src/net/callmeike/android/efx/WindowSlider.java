package net.callmeike.android.efx;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.marakana.android.stream.BuildConfig;


/**
 * MenuSlider
 *
 * When I use this, I frequently see the error:
 *     10-06 18:11:45.746: E/AndroidRuntime(701): java.lang.RuntimeException:
 *     Unable to instantiate application android.app.Application: java.lang.NullPointerException
 * Word has it that this is an Eclipse problem.  It is being addressed here:
 *     http://code.google.com/p/android/issues/detail?id=25869
 */
public abstract class WindowSlider {
    private static final String TAG = "SLIDER";

     private boolean animating;

    final Activity activity;

    private final int duration;
    private final float overhang;

    private final Rect viewRect = new Rect();
    private final Point bounds = new Point();

    private final FrameLayout menuView;

    GestureDetector gestureDetector;
    private boolean visible;

    /**
     * @param activity
     * @param viewLayoutId
     * @param duration
     * @param overhang
     */
    public WindowSlider(
        Activity activity,
        int viewLayoutId,
        int duration,
        float overhang)
    {
        this.activity = activity;
        this.duration = duration;
        this.overhang = overhang;
        this.menuView = getMenu(viewLayoutId);
        activity.getActionBar().setHomeButtonEnabled(true);
    }

    /**
     * @param id
     * @return the sub-view
     */
    public View findViewById(int id) { return menuView.findViewById(id); }

    /**
     * @return boolean true if menu is visible
     */
    public boolean getVisible() { return visible; }

    /**
     * @param vis
     */
    public void setVisible(boolean vis) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "set visible: " + visible + "(" + animating + ")"); }

        if (vis == visible) { return; }
        visible = vis;

        if (animating) { return; }

        setBounds();

        if (visible) { show(); }
        else { hideMenuView(true); }
    }

    /**
     *
     */
    public void reset() {
        bounds.set(0, 0);
        if (animating) { visible = false; }
        else if (visible) { hideMenuView(false); }
    }

    void onShowComplete() {
        if (!visible) { hide(false); }
        animating = false;
    }

    void onHideComplete() {
        ((FrameLayout) activity.getWindow().getDecorView()).removeView(menuView);
        animating = false;
    }

    private void hideMenuView(boolean animate) {
        visible = false;
        hide(animate);
    }

    private void hide(boolean animate) {
        animating = animate;

        moveFrame(
            bounds.y,
            0,
            (!animating) ? null : new AnimationListener() {
                @Override public void onAnimationEnd(Animation a) { onHideComplete(); }
                @Override public void onAnimationRepeat(Animation a) { }
                @Override public void onAnimationStart(Animation a) { }
            });

        if (!animating) { onHideComplete(); }
    }

    private void show() {
        animating = true;

        LinearLayout actionBarFrame = moveFrame(
            -bounds.y,
            bounds.y,
            new AnimationListener() {
                @Override public void onAnimationEnd(Animation a) { onShowComplete(); }
                @Override public void onAnimationRepeat(Animation a) { }
                @Override public void onAnimationStart(Animation a) { }
            });

        FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                Gravity.LEFT);

        int oh = Math.round((viewRect.right - viewRect.left) * overhang);

        params.setMargins(0, bounds.x, oh, 0);
        menuView.setLayoutParams(params);

        decorView.addView(menuView);
        decorView.bringChildToFront(actionBarFrame);

        visible = true;
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
        int x = activity.getActionBar().getHeight(); //
        int oh = Math.round((viewRect.right - viewRect.left) * overhang);
        bounds.set(viewRect.top, viewRect.right - oh);
    }

    private void animate(View v, int delta, AnimationListener animationListener) {
        TranslateAnimation ta = new TranslateAnimation(delta, 0, 0, 0);
        ta.setInterpolator(new DecelerateInterpolator());
        ta.setAnimationListener(animationListener);
        ta.setDuration(duration);
        v.startAnimation(ta);
    }

    private FrameLayout getMenu(int id) {
        return (FrameLayout)
            ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(id, null);
    }
}
