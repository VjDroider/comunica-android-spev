package edu.campusvirtual.comunica.library

import com.google.android.gms.maps.model.Marker
import android.view.MotionEvent
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.Log
import android.view.View


class OnInfoWidnowElemTouchListener(val view: View, val bgDrawableNormal: Drawable , val bgDrawablePressed: Drawable, val listener: OnClickListenerConfirmed): View.OnTouchListener {

    var handler = Handler()
    var marker: Marker? = null
    var pressed = false;

    fun setmarker(marker: Marker) {
        this.marker = marker;
    }


    override fun onTouch(vv: View, event: MotionEvent): Boolean {
        Log.d("ONTOUCH", "si")
        if (0 <= event.getX() && event.getX() <= view.getWidth() && 0 <= event.getY() && event.getY() <= view.getHeight()) {
            when (event.getActionMasked()) {
                MotionEvent.ACTION_DOWN -> {
                    startPress()
                }

            // We need to delay releasing of the view a little so it shows the
            // pressed state on the screen
                MotionEvent.ACTION_UP -> {
                    handler.postDelayed(confirmClickRunnable, 150);
                }

                MotionEvent.ACTION_CANCEL -> {
                    endPress()
                }
            }
        } else {
            // If the touch goes outside of the view's area
            // (like when moving finger out of the pressed button)
            // just release the press
            endPress();
        }
        return false;
    }

    fun startPress() {
        if (!pressed) {
            pressed = true;
            handler.removeCallbacks(confirmClickRunnable);
            view.setBackgroundDrawable(bgDrawablePressed);
            if (marker != null)
                marker!!.showInfoWindow();
        }
    }

    fun endPress(): Boolean {
        if (pressed) {
            this.pressed = false;
            handler.removeCallbacks(confirmClickRunnable);
            view.setBackgroundDrawable(bgDrawableNormal);
            if (marker != null)
                marker!!.showInfoWindow();
            return true;
        } else
            return false;
    }

    private val confirmClickRunnable = Runnable {
        if (endPress()) {
            listener.onClickConfirmed(view, marker!!)
        }
    }

    interface OnClickListenerConfirmed {
        fun onClickConfirmed(v: View, marker: Marker)
    }


}