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

import java.util.Map;
import java.util.WeakHashMap;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class IconMgr {
    private final Map<Uri, LoadingDrawable> cache = new WeakHashMap<Uri, LoadingDrawable>();

    private final Activity ctxt;

    /**
     * @param ctxt
     */
    public IconMgr(Activity ctxt) { this.ctxt = ctxt; }

    /**
     * @param uri
     * @return a drawable that will, eventually, show the icon
     */
    public Drawable getIcon(Uri uri) {
        LoadingDrawable icon;
        boolean load = false;
        synchronized (this) {
            icon = cache.get(uri);
            if (null == icon) {
                icon = new LoadingDrawable(ctxt, uri);
                cache.put(uri, icon);
                load = true;
            }
        }

        if (load) { ctxt.getLoaderManager().initLoader(icon.hashCode(), null, icon); }

        Log.d("ICON", "Got icon: " + icon + ": " + uri);

        return icon;
    }
}
