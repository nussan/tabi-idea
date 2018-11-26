package checkers.tabi_idea.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import checkers.tabi_idea.R


class CardViewDataAdapter(var categoryList: List<String>) : RecyclerView.Adapter<CardViewDataAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewDataAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_raw, null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(viewHolder: CardViewDataAdapter.ViewHolder, position: Int) {
        viewHolder.tvName.text = categoryList[position]
        viewHolder.chkSelected.isChecked = true
        viewHolder.chkSelected.tag = categoryList[position]
    }

    class ViewHolder(itemLayoutView: View) : RecyclerView.ViewHolder(itemLayoutView) {
        var tvName: TextView = itemLayoutView.findViewById(R.id.tvName)
        var chkSelected: CheckBox = itemLayoutView.findViewById(R.id.chkSelected)
    }
}