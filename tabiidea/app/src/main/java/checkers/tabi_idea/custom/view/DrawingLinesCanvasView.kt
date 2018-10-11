package checkers.tabi_idea.custom.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import checkers.tabi_idea.data.MindMapObject

class DrawingLinesCanvasView : View {
    constructor(context: Context?) : super(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var mindMapObjectList = mutableListOf<MindMapObject>()
    val paint: Paint = Paint()

    var layoutWidth = 0f
    var layoutHeight = 0f

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.setARGB(255, 0, 0, 0)
        paint.strokeWidth = 5f
        drawLines(canvas)
    }

    private fun drawLines(canvas: Canvas?) {
        mindMapObjectList.forEach {
            it.children.forEach { viewId ->
                canvas?.drawLine(
                        it.positionX * layoutWidth,
                        it.positionY * layoutHeight,
                        mindMapObjectList[viewId].positionX * layoutWidth,
                        mindMapObjectList[viewId].positionY * layoutHeight,
                        paint
                )
            }
        }
    }
}