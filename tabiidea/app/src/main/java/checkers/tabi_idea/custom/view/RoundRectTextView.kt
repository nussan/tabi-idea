package checkers.tabi_idea.custom.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.drawable.DrawableCompat
import checkers.tabi_idea.data.MindMapObject
import kotlin.math.max
import kotlin.math.min


class RoundRectTextView : AppCompatTextView {
    private var mPaint: Paint = Paint()
    private var mColorInt: Int = Color.parseColor("#00CED1")
    private var mStrokeColor: Int? = null
    private var mLike: Boolean = false
    private var mHighLight = false
    private var mFlag = false

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        setBackgroundColor(mColorInt)

        includeFontPadding = false
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

    fun drawStroke(push: Boolean) {
        var strokeDrawable = GradientDrawable()
        strokeDrawable.setColor(mColorInt)
        if (push) strokeDrawable.setStroke(13, mStrokeColor!!)
        this.background = strokeDrawable
    }

    override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)
            drawLikeHart(canvas!!)
    }

    fun drawLikeHart(canvas: Canvas) {
        mPaint.textSize = 50f

        val x = this.width - 60f
        val y = 50f
        if (mLike && !mHighLight) {
            canvas.drawText("❤️", x, y, mPaint)
        }
    }

    fun setLike(like: Boolean) {
        this.mLike = like
    }

    fun setColor(colorInt: Int) {
        this.mColorInt = colorInt
        this.mStrokeColor = setStrokeColor(mColorInt)
    }

    private fun setStrokeColor(colorInt: Int): Int {
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

        return Color.parseColor("#${colorString}")
    }

    fun setHighLight(highRight: Boolean){
        this.mHighLight = highRight
    }

    fun setFlag(flag: Boolean){
        this.mFlag = flag
    }

    fun drawHighRight(highLight: Boolean, flag: Boolean) {
        if (highLight) {
            //ハートを消す
            drawLikeHart(Canvas())
            invalidate()
            if (flag) {
                this.background.setColorFilter(Color.parseColor("#55ffffff"), PorterDuff.Mode.OVERLAY)
            } else {
                this.background.setColorFilter(Color.parseColor("#66000000"), PorterDuff.Mode.DARKEN)
                this.setTextColor(Color.parseColor("#66000000"))
            }
        } else {
            this.background = ColorDrawable(mColorInt)
            this.setTextColor(Color.WHITE)
        }
    }
}