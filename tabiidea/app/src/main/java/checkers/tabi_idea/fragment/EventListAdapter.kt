package checkers.tabi_idea.fragment

import android.content.Context
import android.graphics.*
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import checkers.tabi_idea.R
import checkers.tabi_idea.data.Event

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
        var capital = event.title.substring(0,1)
        val objBitmap = createBitmap(capital)
        holder.image.setImageBitmap(objBitmap)

        Log.d("eventcapital",capital)
    }

    fun setOnClickListener(onClickListener: View.OnClickListener) {
        listener = onClickListener
    }

    fun removeAt(position: Int) {
        eventList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun createBitmap(capital:String) : Bitmap{
        var objPaint = Paint()
        val objBitmap : Bitmap
        val objCanvas : Canvas
        val textSize = 20
        var textWidth = textSize * capital.length
        var textHeight = textSize

        objPaint.setAntiAlias(true)
        objPaint.setColor(Color.BLACK)
        objPaint.setTextSize(20f)
        val fm :Paint.FontMetrics  = objPaint.getFontMetrics()
        objPaint.getTextBounds(capital,0,capital.length, Rect(0,0,textWidth,textHeight))

        textWidth = objPaint.measureText(capital) .toInt()
        textHeight = (Math.abs(fm.top) + fm.bottom) .toInt()
        objBitmap = Bitmap.createBitmap(textWidth, textHeight, Bitmap.Config.ARGB_8888)

        objCanvas = Canvas(objBitmap)
        objCanvas.drawText(capital,0f,Math.abs(fm.top), objPaint)

        return objBitmap
    }

    // Viewへの参照を持っておくViewHolder
    class EventListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val event = view.findViewById<TextView>(R.id.eventView)
        val image = view.findViewById<ImageView>(R.id.imageView)
    }
}