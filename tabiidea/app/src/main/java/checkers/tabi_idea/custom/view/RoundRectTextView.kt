package checkers.tabi_idea.custom.view

import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import checkers.tabi_idea.R
import kotlin.math.max
import kotlin.math.min


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

    fun drawStroke(colorInt: Int, push: Boolean) {
        var strokeDrawable = GradientDrawable()
        strokeDrawable.setColor(colorInt)
        val r = Integer.parseInt(Integer.toHexString(colorInt).substring(2, 4), 16)
        val g = Integer.parseInt(Integer.toHexString(colorInt).substring(4, 6), 16)
        val b = Integer.parseInt(Integer.toHexString(colorInt).substring(6, 8), 16)

        val maxD = max(max(r, g), b)
        val minD = min(min(r, g), b)

        val newR = maxD + minD - r
        val newG = maxD + minD - g
        val newB = maxD + minD - b

        val strR = String.format("%02X", newR)
        val strG = String.format("%02X", newG)
        val strB = String.format("%02X", newB)

        val colorString = strR + strG + strB
//        Log.d("RoundRectTextView", "$r, $g, $b, $strR, $strG, $strB, $colorString")
        val strokeColor = Color.parseColor("#$colorString")

        if (push) strokeDrawable.setStroke(13, strokeColor)
        this.background = strokeDrawable
    }
}