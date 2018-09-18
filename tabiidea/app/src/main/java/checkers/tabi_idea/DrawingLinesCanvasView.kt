package checkers.tabi_idea

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.graphics.PaintCompat
import android.util.AttributeSet
import android.view.View

class DrawingLinesCanvasView : View {
    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var mindMapObjectList = mutableListOf<MindMapObject>()
    val paint: Paint = Paint()

    //TODO viewのサイズをとってくる
    val viewWidth = 1080f
    val viewHeight = 1536f

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.setARGB(255, 0, 0, 0)
        paint.strokeWidth = 5f
        drawLines(canvas)
    }

    private fun drawLines(canvas: Canvas?) {
        paint.setARGB(255, 0, 0, 0)
        paint.strokeWidth = 5f

        mindMapObjectList.forEach {
            it.children.forEach { viewId ->
                canvas?.drawLine(
                        it.positionX * viewWidth,
                        it.positionY * viewHeight,
                        mindMapObjectList[viewId].positionX * viewWidth,
                        mindMapObjectList[viewId].positionY * viewHeight,
                        paint
                )
            }
        }
    }
}