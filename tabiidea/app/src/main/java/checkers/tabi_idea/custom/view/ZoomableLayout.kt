package checkers.tabi_idea.custom.view

import android.content.Context
import android.graphics.Canvas
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.*
import checkers.tabi_idea.data.MindMapObject
import checkers.tabi_idea.fragment.TravelMindMapFragment


class ZoomableLayout :
        ConstraintLayout,
        ScaleGestureDetector.OnScaleGestureListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{

    private var scale = 1.0f
    private var lastScaleFactor = 0f

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    var lineDrawer: LineDrawer? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        (lineDrawer as? TravelMindMapFragment)?.drawLines(canvas, scale)
    }

    interface LineDrawer {
        fun drawLines(canvas: Canvas?, scale: Float)
    }

    fun addView(child: View?, mmo: MindMapObject) {
        addView(child, mmo.viewIndex)

        (child as? RoundRectTextView)?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                child.x = if (mmo.parent == 0) width.toFloat() / 2 + mmo.positionX else getChildAt(mmo.parent).x + mmo.positionX
                child.y = if (mmo.parent == 0) height.toFloat() / 2 + mmo.positionY else getChildAt(mmo.parent).y + mmo.positionY

                updateListener(context)

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
//        Log.i(TAG, "onScaleBegin")
        return true
    }

    override fun onScale(scaleDetector: ScaleGestureDetector): Boolean {
        val scaleFactor = scaleDetector.scaleFactor
//        Log.i(TAG, "onScale$scaleFactor")
        if (lastScaleFactor == 0f || Math.signum(scaleFactor) == Math.signum(lastScaleFactor)) {
            scale *= scaleFactor
            scale = Math.max(MIN_ZOOM, Math.min(scale, MAX_ZOOM))
            lastScaleFactor = scaleFactor
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
        return true
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        Log.d(TAG, "$velocityX, $velocityY")
        return true
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.translationX = child.x - distanceX / scale
            child.translationY = child.y - distanceY / scale
            child.pivotX = width / 2 - child.x
            child.pivotY = height / 2 - child.y
            child.scaleX = scale
            child.scaleY = scale
            invalidate()
        }
        return true
    }

    override fun onLongPress(e: MotionEvent?) {
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        // とりあえずダブルタップで暴れないように
        // ダブルタップで拡大縮小できるようにしてもいいかも
        scale = if (scale == MIN_ZOOM) MAX_ZOOM else MIN_ZOOM
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.pivotX = width / 2 - child.x
            child.pivotY = height / 2 - child.y
            child.scaleX = scale
            child.scaleY = scale
            invalidate()
        }
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        return true
    }

    companion object {
        private val TAG = "ZoomableLayout"
        private const val MIN_ZOOM = 0.3f
        private const val MAX_ZOOM = 1.5f
    }
}