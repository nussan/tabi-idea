package checkers.tabi_idea.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import checkers.tabi_idea.R
import checkers.tabi_idea.data.Category
import com.jaredrummler.android.colorpicker.ColorPickerDialog

class CategoryListAdapter(var categoryList: List<Category>) : RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_category_row, parent, false)
        view.setOnClickListener {
            ColorPickerDialog.newBuilder().setColor(Color.WHITE).show(parent.context as FragmentActivity?)
        }
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.tvName.text = categoryList[position].name
        holder.backgroud.setBackgroundColor(Color.parseColor(categoryList[position].color))
    }

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvName: TextView = view.findViewById(R.id.tvName)
        var backgroud: FrameLayout = view.findViewById(R.id.category_background)
    }
}