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

import android.view.GestureDetector;
import android.view.MotionEvent;

import net.callmeike.android.efx.MenuSlider;



/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class SwipeDetector extends GestureDetector.SimpleOnGestureListener {
    private final MenuSlider<?> slider;

    /**
     * @param slider
     */
    public SwipeDetector(MenuSlider<?> slider) { this.slider = slider; }

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
        if ((2 * Math.abs(vY)) > Math.abs(vX)) { return false; }

        boolean open = (0 < Math.signum((double) vX));
        boolean opened = slider.getState();

        if (open == opened) { return false; }

        slider.toggleSlider();
        return true;
    }
}
