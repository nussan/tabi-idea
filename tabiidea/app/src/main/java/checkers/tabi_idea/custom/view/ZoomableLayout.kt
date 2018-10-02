package checkers.tabi_idea.custom.view

import android.content.Context
import android.graphics.Matrix
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector


class ZoomableLayout : ConstraintLayout, ScaleGestureDetector.OnScaleGestureListener {
    private var mode = Mode.NONE
    private var scale = 1.0f
    private var lastScaleFactor = 0f

    // 各テキストビューの座標情報
    private var coordinates: Array<Coordinates?> = arrayOf()

    var centerX = 0f
    var centerY = 0f

    private enum class Mode {
        NONE,
        DRAG,
        ZOOM
    }

    constructor(context: Context): this(context, null)


    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    private fun init(context: Context) {
        val scaleDetector = ScaleGestureDetector(context, this)

        this.setOnTouchListener { _, motionEvent ->
            if (coordinates.isEmpty()) {
                coordinates = arrayOfNulls(childCount)
                for (i in 0 until childCount) {
                    coordinates[i] = Coordinates(getChildAt(i).x, getChildAt(i).y)
                }
            }


            when (motionEvent.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    mode = Mode.DRAG

                    for (i in 0 until childCount) {
                        coordinates[i]?.startX = motionEvent.x / scale - coordinates[i]!!.prevDx
                        coordinates[i]?.startY = motionEvent.y / scale - coordinates[i]!!.prevDy
                    }
                }

                MotionEvent.ACTION_MOVE ->
                    if (mode == Mode.DRAG) {
                        Log.e("MOVE", "Drag")
                        for (i in 0 until childCount) {
                            coordinates[i]!!.dx = motionEvent.x / scale - coordinates[i]?.startX!!
                            coordinates[i]!!.dy = motionEvent.y / scale - coordinates[i]?.startY!!
                        }
                    }
                MotionEvent.ACTION_POINTER_DOWN -> mode = Mode.ZOOM

                MotionEvent.ACTION_POINTER_UP -> {
                    mode = Mode.NONE
                    Log.e("ACTION_POINTER_UP", "Drag")
                }

                MotionEvent.ACTION_UP -> {
                    Log.i(TAG, "UP")
                    Log.e("ACTION_UP", "None")
                    mode = Mode.NONE

                    for (i in 0 until childCount) {
                        coordinates[i]!!.prevDx = coordinates[i]!!.dx
                        coordinates[i]!!.prevDy = coordinates[i]!!.dy
                    }
                }
            }

            scaleDetector.onTouchEvent(motionEvent)

            if (mode == Mode.DRAG && scale >= MIN_ZOOM || mode == Mode.ZOOM) {
                parent.requestDisallowInterceptTouchEvent(true)
                applyScaleAndTranslation()
            }

            true
        }
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onScaleBegin(scaleDetector: ScaleGestureDetector): Boolean {
        Log.i(TAG, "onScaleBegin")
        return true
    }

    override fun onScale(scaleDetector: ScaleGestureDetector): Boolean {
        val scaleFactor = scaleDetector.scaleFactor
        Log.i(TAG, "onScale$scaleFactor")
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
        Log.i(TAG, "onScaleEnd")
    }

    private fun applyScaleAndTranslation() {
        for (i in 0 until childCount) {
            getChildAt(i).pivotX = centerX - getChildAt(i).x
            getChildAt(i).pivotY = centerY - getChildAt(i).y
            getChildAt(i).scaleX = scale
            getChildAt(i).scaleY = scale
            getChildAt(i).translationX = coordinates[i]?.dx!!
            getChildAt(i).translationY = coordinates[i]?.dy!!
        }
    }

    companion object {
        private val TAG = "ZoomableLayout"
        private val MIN_ZOOM = 0.8f
        private val MAX_ZOOM = 3.0f
    }

    data class Coordinates(
            var dx: Float,
            var dy: Float,
            var prevDx: Float = dx,
            var prevDy: Float = dy,
            var startX: Float = 0f,
            var startY: Float = 0f,
            val matrix: Matrix = Matrix())
}