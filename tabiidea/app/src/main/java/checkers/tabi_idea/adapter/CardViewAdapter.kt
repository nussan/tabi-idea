package checkers.tabi_idea.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import checkers.tabi_idea.R
import checkers.tabi_idea.data.Category


class CardViewDataAdapter(private var categoryList: List<Category>) : RecyclerView.Adapter<CardViewDataAdapter.ViewHolder>() {
    private var selectedPosition = -1 // 選択された位置

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewDataAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_category_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(viewHolder: CardViewDataAdapter.ViewHolder, position: Int) {
        viewHolder.tvName.text = categoryList[position].name
        viewHolder.checkBox.tag = categoryList[position]
        viewHolder.checkBox.isChecked = categoryList[position].isChecked
        viewHolder.checkBox.setOnCheckedChangeListener(null)
        viewHolder.checkBox.setOnClickListener {
            selectedPosition =
                    if (viewHolder.checkBox.isChecked) {
                        viewHolder.adapterPosition
                    } else {
                        -1
                        return@setOnClickListener
                    }
            for (i in 0 until categoryList.size) {
                categoryList[i].isChecked = (i == selectedPosition)
            }
            viewHolder.checkBox.isChecked = (selectedPosition == position)
            notifyDataSetChanged()
        }

    }

    class ViewHolder(itemLayoutView: View) : RecyclerView.ViewHolder(itemLayoutView) {
        var tvName: TextView = itemLayoutView.findViewById(R.id.tvName)
        var checkBox: CheckBox = itemLayoutView.findViewById(R.id.chkSelected)

        init {
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            }
        }
    }
}