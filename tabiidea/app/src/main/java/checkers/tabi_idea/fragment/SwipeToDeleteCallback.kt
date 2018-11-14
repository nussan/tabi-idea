package checkers.tabi_idea.fragment

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import checkers.tabi_idea.R

abstract class SwipeToDeleteCallback(context: Context) : ItemTouchHelper.SimpleCallback(0,(ItemTouchHelper.LEFT)) {

    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_black_24dp)
    private val deleteIconIntrinsicWidth = deleteIcon?.intrinsicWidth
    private val deleteIconIntrinsicHeight = deleteIcon?.intrinsicHeight

    private val background = ColorDrawable()
    private val redBackgroundColor = Color.parseColor("#f44336")
    private val clearPaint = Paint().apply{ xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun onMove(
            recyclerView: RecyclerView?,
            viewHolder: RecyclerView.ViewHolder?,
            target: RecyclerView.ViewHolder?
    ): Boolean {
        return false
    }

    override fun onChildDraw(
            c: Canvas?,
            recyclerView: RecyclerView?,
            viewHolder:RecyclerView.ViewHolder?,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
    ){
        val itemView = viewHolder?.itemView ?: return
        val isCanceled = dX == 0f && !isCurrentlyActive
        if (isCanceled) {
            clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        //Draw the red delete background
        background.color = redBackgroundColor
        background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        background.draw(c)

        val itemHeight = itemView.bottom - itemView.top
        if(deleteIcon != null
                && deleteIconIntrinsicWidth != null
                && deleteIconIntrinsicHeight != null
        ){
            val deleteIconTop = itemView.top + (itemHeight - deleteIconIntrinsicHeight)/2
            val deleteIconMargin = (itemHeight - deleteIconIntrinsicHeight) / 2
            val deleteIconLeft = itemView.right - deleteIconMargin - deleteIconIntrinsicWidth
            val deleteIconRight = itemView.right - deleteIconMargin
            val deleteIconBottom = deleteIconTop + deleteIconIntrinsicHeight

            deleteIcon.setBounds(deleteIconLeft,deleteIconTop,deleteIconRight,deleteIconBottom)
            deleteIcon.draw(c)
        }

        super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)

    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left,top,right,bottom,clearPaint)
    }
}
