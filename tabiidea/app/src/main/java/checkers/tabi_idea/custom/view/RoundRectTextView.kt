package checkers.tabi_idea.custom.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.max
import kotlin.math.min


class RoundRectTextView : AppCompatTextView {
    private var mPaint: Paint = Paint()
    private var mColorInt: Int = Color.parseColor("#00CED1")
    private var mStrokeColor: Int = mColorInt
    private var mLike: Boolean = false

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        setBackgroundColor(mColorInt)
        elevation = 10f
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, width, height, 30f)
            }
        }
        maxLines = 1
        gravity = Gravity.CENTER
        clipToOutline = true

        //        Log.d("favorite" ,drawable.toString())
//        val bmp = Bitmap.createBitmap(drawable!!.intrinsicWidth/20,drawable.intrinsicHeight/20, Bitmap.Config.ARGB_8888)
//        Log.d("favorite" , bmp.toString())
//        val canvas = Canvas()
//        val paint = Paint()
//        canvas.drawBitmap(bmp, 0f, 0f, paint)
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
        if (push) strokeDrawable.setStroke(13, mStrokeColor)
        this.background = strokeDrawable
    }

    override fun onDraw(canvas: Canvas?) {
        drawLikeCircle(canvas!!)
        super.onDraw(canvas)
    }

    private fun drawLikeCircle(canvas: Canvas) {
        mPaint.style = Paint.Style.STROKE
        mPaint.color = mStrokeColor
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = 5f

        val rad = 20f
        val cx = this.width - 32f
        val cy = 32f
        if (mLike) {
            canvas.drawCircle(cx, cy, rad, mPaint)
        } else {
            mPaint.color = mColorInt
            canvas.drawCircle(cx, cy, rad, mPaint)
        }
    }

    fun setLike(like: Boolean) {
        this.mLike = like
    }

    fun setColor(colorInt: Int) {
        this.mColorInt = colorInt
        setStrokeColor(colorInt)
    }

    private fun setStrokeColor(colorInt: Int) {
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
        this.mStrokeColor = Color.parseColor("#$colorString")
    }
}