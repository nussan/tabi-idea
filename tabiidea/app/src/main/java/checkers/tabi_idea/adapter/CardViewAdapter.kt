package checkers.tabi_idea.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.TextView
import checkers.tabi_idea.R
import checkers.tabi_idea.data.Category


class CategoryViewDataAdapter(private var categoryList: List<Category>) : RecyclerView.Adapter<CategoryViewDataAdapter.ViewHolder>() {
    private var selectedPosition = -1 // 選択された位置

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewDataAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_category_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(viewHolder: CategoryViewDataAdapter.ViewHolder, position: Int) {
        viewHolder.tvName.text = categoryList[position].name
        viewHolder.checkBox.tag = categoryList[position]
        viewHolder.checkBox.isChecked = categoryList[position].isChecked
        viewHolder.categoryLayout.setOnClickListener {
            selectedPosition = viewHolder.adapterPosition
            for (i in 0 until categoryList.size) {
                categoryList[i].isChecked = (i == selectedPosition)
            }
            notifyDataSetChanged()
        }
    }

    class ViewHolder(itemLayoutView: View) : RecyclerView.ViewHolder(itemLayoutView) {
        var tvName: TextView = itemLayoutView.findViewById(R.id.tvName)
        var checkBox: CheckBox = itemLayoutView.findViewById(R.id.chkSelected)
        var categoryLayout: FrameLayout = itemLayoutView.findViewById(R.id.categoryLayout)
    }
}