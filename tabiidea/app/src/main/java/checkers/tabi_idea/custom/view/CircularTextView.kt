package checkers.tabi_idea.custom.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.ViewTreeObserver


class CircularTextView : AppCompatTextView {
    var strokeWidth: Float = 0f
        set(dp) {
            val scale = context.resources.displayMetrics.density
            dp * scale
        }
    var strokeColor: Int = 0
    internal var solidColor: Int = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        elevation = 5f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val h = Math.max(this.measuredHeight, 150)
        val w = Math.max(this.measuredWidth, 150)
        val r = Math.min(Math.max(w, h), 300)
        setPadding(20,20,20,20)
        setMeasuredDimension(r, r)
    }

    override fun draw(canvas: Canvas) {

        val circlePaint = Paint()
        circlePaint.color = solidColor
        circlePaint.flags = Paint.ANTI_ALIAS_FLAG

        val strokePaint = Paint()
        strokePaint.color = strokeColor
        strokePaint.flags = Paint.ANTI_ALIAS_FLAG

        val h = this.height
        val w = this.width

        val diameter = if (h > w) h else w
        val radius = diameter / 2

        canvas.drawCircle((diameter / 2).toFloat(), (diameter / 2).toFloat(), radius.toFloat(), strokePaint)

        canvas.drawCircle((diameter / 2).toFloat(), (diameter / 2).toFloat(), radius.toFloat() - strokeWidth, circlePaint)

        super.draw(canvas)
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