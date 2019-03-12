package edu.campusvirtual.tzitlacalli

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager

/**
 * Created by jonathan on 3/7/18.
 */
class WrapContentHeightViewPager : ViewPager {
    private var _enabled: Boolean = true

    constructor(context: Context) : super(context) {
        _enabled = true
    }

    constructor(arg0: Context, arg1: AttributeSet) : super(arg0, arg1) { // System required constructor
        _enabled = true
    }

    protected override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        var height = 0
        for (i in 0 until getChildCount()) {
            val child = getChildAt(i)
            child.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
            val h = child.getMeasuredHeight()
            if (h > height) height = h
        }

        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (this._enabled) {
            super.onTouchEvent(event)
        } else false

    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (this._enabled) {
            super.onInterceptTouchEvent(event)
        } else false

    }

    fun setPagingEnabled(enabled: Boolean) {
        this._enabled = enabled
    }

}