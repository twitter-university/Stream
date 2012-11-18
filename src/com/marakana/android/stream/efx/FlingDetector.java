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

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class FlingDetector extends GestureDetector.SimpleOnGestureListener {
    private static final String TAG = "FLING";

    /** Fling direction */
    public static enum Direction { /** right */ RIGHT,  /** left */ LEFT; }

    /** FlingListener */
    public static interface FlingListener {
        /**
         * @param dir
         * @return true if consumed
         */
        boolean onFling(Direction dir);
    }


    private final FlingListener listener;

    /**
     * @param listener
     */
    public FlingDetector(FlingListener listener) { this.listener = listener; }

    /**
     * @see android.view.GestureDetector.SimpleOnGestureListener#onDown(android.view.MotionEvent)
     */
    @Override
    public boolean onDown(MotionEvent e) { return true; }

    /**
     * @see android.view.GestureDetector.OnGestureListener#onFling(android.view.MotionEvent, android.view.MotionEvent, float, float)
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY) {
        Log.d(TAG, "fling: " + vX + ", " + vY);
        if (Math.abs(vX) < 2 * Math.abs(vY)) { return false; }
        return listener.onFling((0 < Math.signum((double) vX)) ? Direction.RIGHT : Direction.LEFT);
    }
}
