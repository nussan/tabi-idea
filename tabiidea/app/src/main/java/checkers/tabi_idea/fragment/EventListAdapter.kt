package checkers.tabi_idea.fragment

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }

    fun setOnClickListener(onClickListener: View.OnClickListener) {
        listener = onClickListener
    }

    fun removeAt(position: Int) {
        eventList.removeAt(position)
        notifyItemRemoved(position)
    }

    // Viewへの参照を持っておくViewHolder
    class EventListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val event = view.findViewById<TextView>(R.id.eventView)
    }
}