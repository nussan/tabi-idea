package checkers.tabi_idea.custom.view

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.os.Build
import android.support.v4.widget.TextViewCompat
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewTreeObserver
import android.widget.TextView

class EqualWidthHeightTextView : AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val h = this.measuredHeight
        val w = this.measuredWidth
        val r = Math.max(w, h)

        setMeasuredDimension(r, r)
    }


    fun setPositionXByCenterPositionX(f: Float) {
        this.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                x = f - width / 2

                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    fun setPositionYByCenterPositionY(f: Float) {
        this.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                y = f - height / 2

                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    fun getCenterPositionX() = x + width / 2
    fun getCenterPositionY() = y + height / 2
}