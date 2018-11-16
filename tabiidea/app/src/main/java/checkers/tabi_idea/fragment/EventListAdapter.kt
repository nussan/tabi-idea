package checkers.tabi_idea.fragment

import android.content.Context
import android.graphics.*
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import checkers.tabi_idea.R
import checkers.tabi_idea.data.Event
import kotlinx.android.synthetic.main.list_event_row.view.*

class EventListAdapter(context: Context?, var eventList: MutableList<Event>) : RecyclerView.Adapter<EventListAdapter.EventListViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private var listener: View.OnClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventListViewHolder {
        val view = inflater.inflate(R.layout.list_event_row, parent, false)
        view.setOnClickListener(listener)
        val viewHolder = EventListViewHolder(view)

        return viewHolder
    }

    override fun getItemCount() = eventList.size

    override fun onBindViewHolder(holder: EventListViewHolder, position: Int) {
        val event = eventList[position]
        holder.event.text = event.title
        var capital = if(event.title.isNotEmpty()) event.title.substring(0,1) else "a"
        val objBitmap = createBitmap(capital,holder)
        holder.image.setImageBitmap(objBitmap)
    }

    fun setOnClickListener(onClickListener: View.OnClickListener) {
        listener = onClickListener
    }

    fun removeAt(position: Int) {
        eventList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun createBitmap(capital:String,holder: EventListViewHolder) : Bitmap{
        var objPaint = Paint()
        var objBitmap : Bitmap
        val objCanvas : Canvas
        val base = "無"
        val textSize = 10
        var textWidth = textSize * base.length //ここを全角に仕様図
        var textHeight = textSize
        var textWidthCap: Int

        objPaint.isAntiAlias = true
        objPaint.color = Color.WHITE
        objPaint.textSize = 1000f
        val fm :Paint.FontMetrics  = objPaint.fontMetrics
        objPaint.getTextBounds(capital,0,0, Rect(0,0,textWidth,textHeight))


        textWidth = (objPaint.measureText(base) .toInt())*2
        textHeight = ((Math.abs(fm.top) - Math.abs(fm.bottom)+100) .toInt())*2
        objBitmap = Bitmap.createBitmap(textWidth, textHeight, Bitmap.Config.ARGB_8888)

        textWidthCap = objPaint.measureText(capital) .toInt()
        val bitmapWidth = (textWidth/2) - (textWidthCap/2)

        objCanvas = Canvas(objBitmap)
        objCanvas.drawARGB(100,134,177,190)
        objCanvas.drawText(capital,bitmapWidth.toFloat(),-(fm.ascent+fm.descent)*1.8f,objPaint)

        return objBitmap
    }

    // Viewへの参照を持っておくViewHolder
    class EventListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val event = view.findViewById<TextView>(R.id.eventView)
        val image = view.findViewById<ImageView>(R.id.imageView)
    }
}