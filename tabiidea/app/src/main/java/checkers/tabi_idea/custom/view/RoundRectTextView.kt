package checkers.tabi_idea.custom.view

import android.content.Context
import android.graphics.*
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.view.ViewTreeObserver


class RoundRectTextView : AppCompatTextView {
    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setBackgroundColor(Color.parseColor("#00CED1"))
        elevation = 30f
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, width, height, 30f )
            }
        }
        clipToOutline = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val h = Math.max(this.measuredHeight, 150)
        val w = Math.max(this.measuredWidth, 150)
        val r = Math.min(Math.max(w, h), 300)
        setMeasuredDimension(r, r)
    }

    override fun draw(canvas: Canvas?) {
        val paint = Paint()
        paint.color = Color.parseColor("#FF00CED1")
        paint.flags = Paint.ANTI_ALIAS_FLAG
        canvas?.drawRoundRect(
                RectF(x, y, width.toFloat(), height.toFloat()),
                30f,
                30f,
                paint)
        super.draw(canvas)
    }

    override fun performClick(): Boolean {
        return super.performClick()
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