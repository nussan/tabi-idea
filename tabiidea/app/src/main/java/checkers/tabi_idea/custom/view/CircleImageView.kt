package checkers.tabi_idea.custom.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

class CircleImageView : ImageView {
    private var canvasSize: Int = 0
    private val image: Bitmap? = null
    private lateinit var paint: Paint

    constructor(context: Context) : super(context, null) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        paint = Paint()
        paint.isAntiAlias = true
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun onDraw(canvas: Canvas) {
        val drawable = drawable as? BitmapDrawable ?: return

        if (width == 0 || height == 0) return

        val srcBmp = drawable.bitmap ?: return

        val image = getSquareBitmap(srcBmp)

        canvasSize = canvas.width
        if (canvas.height < canvasSize)
            canvasSize = canvas.height

        val shader = BitmapShader(Bitmap.createScaledBitmap(image, canvasSize, canvasSize, false), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader

        val circleCenter = canvasSize / 2
        canvas.drawCircle(circleCenter.toFloat(), circleCenter.toFloat(), (circleCenter - 1).toFloat(), paint)
    }

    private fun getSquareBitmap(srcBmp: Bitmap): Bitmap {
        if (srcBmp.width == srcBmp.height) return srcBmp

        //Rectangle to square. Equivarent to ScaleType.CENTER_CROP
        val dim = Math.min(srcBmp.width, srcBmp.height)
        val dstBmp = Bitmap.createBitmap(dim, dim, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(dstBmp)
        val left = (if (srcBmp.width > dim) (dim - srcBmp.width) / 2 else 0).toFloat()
        val top = (if (srcBmp.height > dim) (dim - srcBmp.height) / 2 else 0).toFloat()
        canvas.drawBitmap(srcBmp, left, top, null)

        return dstBmp
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = measureWidth(widthMeasureSpec)
        val height = measureHeight(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    private fun measureWidth(measureSpec: Int): Int {
        var result = 0
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)

        if (specMode == View.MeasureSpec.EXACTLY) {
            // The parent has determined an exact size for the child.
            result = specSize
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize
        } else {
            // The parent has not imposed any constraint on the child.
            result = canvasSize
        }

        return result
    }

    private fun measureHeight(measureSpecHeight: Int): Int {
        var result = 0
        val specMode = View.MeasureSpec.getMode(measureSpecHeight)
        val specSize = View.MeasureSpec.getSize(measureSpecHeight)

        if (specMode == View.MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = canvasSize
        }

        return result + 2
    }
}