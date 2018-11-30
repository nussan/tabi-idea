package checkers.tabi_idea.custom.view

import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider


class RoundRectTextView : AppCompatTextView {
    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setBackgroundColor(Color.parseColor("#00CED1"))
        elevation = 10f
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, width, height, 30f)
            }
        }
        maxLines = 1
        gravity = Gravity.CENTER
        clipToOutline = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val h = Math.max(this.measuredHeight, MIN_SIZE)
        val w = Math.max(this.measuredWidth, MIN_SIZE)
//        val r = Math.min(Math.max(w, h), MAX_SIZE)
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    companion object {
        const val MAX_SIZE = 400
        const val MIN_SIZE = 200
    }
}