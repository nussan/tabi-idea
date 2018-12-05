package checkers.tabi_idea.custom.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.ImageView
import checkers.tabi_idea.R
import checkers.tabi_idea.data.MindMapObject
import checkers.tabi_idea.fragment.TravelMindMapFragment
import kotlinx.android.synthetic.main.list_event_row.view.*
import kotlinx.android.synthetic.main.notification_action_tombstone.view.*


class ZoomableLayout :
        ConstraintLayout,
        ScaleGestureDetector.OnScaleGestureListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {
    var scale = 1.0f
        private set
    private var scaleFactor = 1.0f
    private var lastScaleFactor = 0f

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    var lineDrawer: LineDrawer? = null
    var tapListener: TapListener? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        (lineDrawer as? TravelMindMapFragment)?.drawLines(canvas, scaleFactor)
    }

    interface LineDrawer {
        fun drawLines(canvas: Canvas?, scale: Float)
    }

    fun addView(child: View?, mmo: MindMapObject) {
        addView(child)
        (child as? RoundRectTextView)?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                child.x = if (mmo.viewIndex == 0) width.toFloat() / 2 + mmo.positionX - child.width / 2 else findViewWithTag<RoundRectTextView>(mmo.parent).x + mmo.positionX
                child.y = if (mmo.viewIndex == 0) height.toFloat() / 2 + mmo.positionY - child.height / 2 else findViewWithTag<RoundRectTextView>(mmo.parent).y + mmo.positionY
                child.scaleX = 0f
                child.scaleY = 0f
                val set = AnimatorSet()
                set.duration = 200
                set.playTogether(
                        ObjectAnimator.ofFloat(child, "scaleX", scale),
                        ObjectAnimator.ofFloat(child, "scaleY", scale))
                set.start()
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })


    }

    private fun updateListener(context: Context) {
        val scaleGestureDetector = ScaleGestureDetector(context, this)
        val gestureDetector = GestureDetector(context, this)

        this.setOnTouchListener { _, motionEvent ->
            scaleGestureDetector.onTouchEvent(motionEvent)
            gestureDetector.onTouchEvent(motionEvent)
            true
        }
    }

    private fun init(context: Context) {
        updateListener(context)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onScaleBegin(scaleDetector: ScaleGestureDetector): Boolean {
//        Log.i(TAG, "onScaleBegin: ${scaleDetector.scaleFactor}")
        return true
    }

    override fun onScale(scaleDetector: ScaleGestureDetector): Boolean {
        scaleFactor = scaleDetector.scaleFactor
//        Log.i(TAG, "onScale$scaleFactor")
        if (scale in MIN_ZOOM..MAX_ZOOM) Log.i(TAG, "onScale: true")
        else if ((scale < MIN_ZOOM && scaleDetector.scaleFactor > 1.0f) || (scale > MAX_ZOOM && scaleDetector.scaleFactor < 1.0f)) Log.i(TAG, "onScale: true")
        else return false

        if (lastScaleFactor == 0f || Math.signum(scaleFactor) == Math.signum(lastScaleFactor)) {
            scale *= scaleFactor
            lastScaleFactor = scaleFactor
            applyScale(scaleDetector.focusX, scaleDetector.focusY)
            invalidate()
        } else {
            lastScaleFactor = 0f
        }
        return true
    }

    override fun onScaleEnd(scaleDetector: ScaleGestureDetector) {
//        Log.i(TAG, "onScaleEnd")
    }

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        tapListener?.onTap(e, (width / 2).toFloat(), (height / 2).toFloat(), scaleFactor)
        tapListener = null
        return true
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
//        Log.d(TAG, "$velocityX, $velocityY")
        return true
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        Log.d(TAG, "onScroll")
        applyTranslation(distanceX, distanceY)
        invalidate()
        return true
    }

    private fun applyTranslation(distanceX: Float, distanceY: Float) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.translationX -= distanceX
            child.translationY -= distanceY
        }
        invalidate()
    }

    private fun applyScale(pivotX: Float, pivotY: Float) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val matrix = child.matrix
            matrix.setScale(scaleFactor, scaleFactor, pivotX - child.x - child.width / 2, pivotY - child.y - child.height / 2)
//            Log.d(TAG, "${child.id} : ${matrix.toShortString()}")
            val m = FloatArray(9)
            matrix.getValues(m)
            child.translationX += m[Matrix.MTRANS_X]
            child.translationY += m[Matrix.MTRANS_Y]
            child.scaleX = scale
            child.scaleY = scale
        }
    }

    override fun onLongPress(e: MotionEvent?) {
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
//        scale = if (scale == MAX_ZOOM) MAX_ZOOM else MIN_ZOOM
//        applyScale()
//        invalidate()
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        return true
    }

    companion object {
        private const val TAG = "ZoomableLayout"
        private const val MIN_ZOOM = 0.3f
        private const val MAX_ZOOM = 2.0f
    }

    interface TapListener {
        fun onTap(e: MotionEvent, centerX: Float, centerY: Float, scale: Float)
    }
}