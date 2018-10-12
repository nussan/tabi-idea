package checkers.tabi_idea.custom.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import checkers.tabi_idea.fragment.TravelMindMapFragment

class DrawingLinesCanvasView : View {
    constructor(context: Context?) : super(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var lineDrawer: LineDrawer? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        (lineDrawer as? TravelMindMapFragment)?.drawLines(canvas)
    }

    interface LineDrawer {
        fun drawLines(canvas: Canvas?)
    }

}