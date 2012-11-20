/* $Id: $
   Copyright 2012, G. Blake Meike

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.marakana.android.stream.efx;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class LoadingDrawable
    extends Drawable implements LoaderManager.LoaderCallbacks<BitmapDrawable>
{
    private static final String TAG = "LOADINGDRAWABLE";

    /**
     * BitmapLoader
     */
    private static class BitmapLoader extends AsyncTaskLoader<BitmapDrawable> {
        private volatile boolean loaded;
        private final Uri uri;

        /**
         * @param context
         * @param uri
         */
        public BitmapLoader(Context context, Uri uri) {
            super(context);
            this.uri = uri;
        }

        /**
         * @see android.content.AsyncTaskLoader#loadInBackground()
         */
        @Override
        public BitmapDrawable loadInBackground() {
            InputStream in = null;
            Bitmap icon = null;
            try {
                icon = BitmapFactory.decodeStream(
                        getContext().getContentResolver().openInputStream(uri));
            }
            catch (FileNotFoundException e) {
                Log.w(TAG, "Failed loading icon: " + uri, e);
            }
            finally {
                if (null != in) {
                    try { in.close(); } catch (Exception e) { }
                }
            }

            BitmapDrawable iconDrawable = null;
            if (null != icon) {
                iconDrawable = new BitmapDrawable(getContext().getResources(), icon);
                loaded = true;
            }

            return iconDrawable;
        }

        /**
         * @see android.content.Loader#onStartLoading()
         *
         * see bug: http://code.google.com/p/android/issues/detail?id=14944
         */
        @Override
        protected void onStartLoading() {
            if (!loaded) { forceLoad(); }
        }
    }

    private final Context ctxt;
    private final Uri uri;
    private BitmapDrawable icon;

    /**
     * @param ctxt
     * @param uri
     */
    public LoadingDrawable(Context ctxt, Uri uri) {
        this.ctxt = ctxt;
        this.uri = uri;
    }

    /**
     * @see android.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
     */
    @Override
    public Loader<BitmapDrawable> onCreateLoader(int id, Bundle args) {
        return new BitmapLoader(ctxt, uri);
    }

    /**
     * @see android.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.content.Loader, java.lang.Object)
     */
    @Override
    public void onLoadFinished(Loader<BitmapDrawable> ldr, BitmapDrawable bitmap) {
        icon = bitmap;
        invalidateSelf();
    }

    /**
     * @see android.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.content.Loader)
     */
    @Override
    public void onLoaderReset(Loader<BitmapDrawable> ldr) {

    }

    /**
     * @see android.graphics.drawable.Drawable#draw(android.graphics.Canvas)
     */
    @Override
    public void draw(Canvas canvas) {
        if (null != icon) { icon.draw(canvas); }
    }

    /**
     * @see android.graphics.drawable.Drawable#getOpacity()
     */
    @Override
    public int getOpacity() {
        return ((null == icon) || (!icon.isVisible()))
            ? PixelFormat.TRANSPARENT
            : icon.getOpacity();
    }

    /**
     * @see android.graphics.drawable.Drawable#setAlpha(int)
     */
    @Override
    public void setAlpha(int alpha) {
        if (null != icon) { icon.mutate().setAlpha(alpha); }
    }

    /**
     * @see android.graphics.drawable.Drawable#setColorFilter(android.graphics.ColorFilter)
     */
    @Override
    public void setColorFilter(ColorFilter cf) {
        if (null != icon) { icon.mutate().setColorFilter(cf); }
    }
}