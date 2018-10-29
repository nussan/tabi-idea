package checkers.tabi_idea.custom.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewTreeObserver
import checkers.tabi_idea.data.MindMapObject
import checkers.tabi_idea.fragment.TravelMindMapFragment


class ZoomableLayout :
        ConstraintLayout,
        ScaleGestureDetector.OnScaleGestureListener {


    private var mode = Mode.NONE
    var scale = 1.0f
    private var lastScaleFactor = 0f
    private var mmoCount = 0
    // 各テキストビューの座標情報
    var coordinates: MutableList<Coordinates> = mutableListOf()

    var centerX = 0f
    var centerY = 0f

    private enum class Mode {
        NONE,
        DRAG,
        ZOOM
    }

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
                child.x = if (mmo.parent == 0) width.toFloat() / 2 - mmo.positionX else getChildAt(mmo.parent).x - mmo.positionX
                child.y = if (mmo.parent == 0) height.toFloat() / 2 - mmo.positionY else getChildAt(mmo.parent).y - mmo.positionY
                Log.d("aaaaa", "${mmo.viewIndex}, ${coordinates.size}, $childCount, ${child.x}, ${child.y}")
                coordinates.add(mmo.viewIndex, Coordinates(mmo.viewIndex, child.x, child.y))
                updateListener(context)
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun updateListener(context: Context) {
        val scaleDetector = ScaleGestureDetector(context, this)

        this.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    mode = Mode.DRAG

                    coordinates.forEach {
                        it.startX = motionEvent.x / scale - it.prevDx
                        it.startY = motionEvent.y / scale - it.prevDy
                    }
                }

                MotionEvent.ACTION_MOVE ->
                    if (mode == Mode.DRAG) {
//                        Log.e("MOVE", "Drag")
                        coordinates.forEach {
                            it.dx = motionEvent.x /scale - it.startX
                            it.dy = motionEvent.y /scale - it.startY
                        }
                    }
                MotionEvent.ACTION_POINTER_DOWN -> mode = Mode.ZOOM

                MotionEvent.ACTION_POINTER_UP -> {
                    mode = Mode.NONE
//                    Log.e("ACTION_POINTER_UP", "Drag")
                }

                MotionEvent.ACTION_UP -> {
//                    Log.i(TAG, "UP")
//                    Log.e("ACTION_UP", "None")
                    mode = Mode.NONE

                    coordinates.forEach {
                        it.prevDx = it.dx
                        it.prevDy = it.dy
                    }
                }
            }

            scaleDetector.onTouchEvent(motionEvent)

            if (mode == Mode.DRAG && scale >= MIN_ZOOM || mode == Mode.ZOOM) {
                parent.requestDisallowInterceptTouchEvent(true)

                coordinates.forEach{
                    applyScaleAndTranslation(it.index)
                }
            }

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

    private fun applyScaleAndTranslation(index: Int) {
        getChildAt(index).pivotX = width / 2 - getChildAt(index).x
        getChildAt(index).pivotY = height / 2 - getChildAt(index).y
        getChildAt(index).scaleX = scale
        getChildAt(index).scaleY = scale
        getChildAt(index).translationX = coordinates[index].dx
        getChildAt(index).translationY = coordinates[index].dy
        invalidate()
    }

    companion object {
        private val TAG = "ZoomableLayout"
        private val MIN_ZOOM = 0.0f
        private val MAX_ZOOM = 100f
    }

    data class Coordinates(
            var index: Int,
            var dx: Float,
            var dy: Float,
            var prevDx: Float = dx,
            var prevDy: Float = dy,
            var startX: Float = 0f,
            var startY: Float = 0f,
            val matrix: Matrix = Matrix())
}