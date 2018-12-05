package checkers.tabi_idea.custom.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager


class HoldableViewPager : ViewPager {
    var isEnable = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onTouchEvent(ev: MotionEvent?): Boolean = when (isEnable) {
        true -> super.onTouchEvent(ev)
        else -> false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean = when (isEnable) {
        true -> super.onInterceptTouchEvent(ev)
        else -> false
    }
}