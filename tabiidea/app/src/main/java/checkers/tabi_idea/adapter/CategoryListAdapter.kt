package checkers.tabi_idea.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import checkers.tabi_idea.R
import checkers.tabi_idea.data.Category

class CategoryListAdapter(var context: Context?, private var categoryList: List<Category>) : RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>() {
    var imageViewListener: OnClickListener? = null
    var textViewListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_category_row, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.tvName.text = categoryList[position].name
        holder.imageView.setBackgroundColor(Color.parseColor(categoryList[position].color))
        holder.imageView.setOnClickListener {
            imageViewListener?.onClick(position)
        }
        holder.tvName.setOnClickListener {
            textViewListener?.onClick(position)
        }

//        holder.imageView.setOnClickListener {
//            ColorPickerDialog
//                    .newBuilder()
//                    .setColor(Color.parseColor(categoryList[position].color))
//                    .show(context as FragmentActivity?)
//        }
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }
    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvName: TextView = view.findViewById(R.id.tvName)
        var background: FrameLayout = view.findViewById(R.id.category_background)
        var imageView: ImageView = view.findViewById(R.id.color_select)
    }


}