package checkers.tabi_idea.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import checkers.tabi_idea.R
import checkers.tabi_idea.R.id.resultParent
import checkers.tabi_idea.data.Category
import checkers.tabi_idea.data.MindMapObject

import java.util.HashMap

class GroupingExpandableListAdapter internal constructor(private val context: Context, private val titleList: List<MindMapObject>, private val dataList: HashMap<String, List<MindMapObject>>,private val ct :List<Category>) : BaseExpandableListAdapter() {

    private var counter = 0
    private var childcnt =3
    private var openOrClose = false

    override fun getChild(listPosition: Int, expandedListPosition: Int): MindMapObject {
            return this.dataList[this.titleList[listPosition].type]!![expandedListPosition]
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }


    override fun getChildView(listPosition: Int, expandedListPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        if(counter >= 3 || childcnt<=0){
            counter =0
        } else {
            childcnt--
        }
        var convertView = convertView
        val expandedListText = getChild(listPosition, expandedListPosition).text
        val expandedListNumber = getChild(listPosition, expandedListPosition).point
        val listTitle = getGroup(listPosition).type

        if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.fragment_grouping_result_child, null)
        }
        val expandedListTextView = convertView!!.findViewById<TextView>(R.id.expand_child)
        expandedListTextView.text = expandedListText
        val expandedListNumberView = convertView!!.findViewById<TextView>(R.id.reviewNumber)
        expandedListNumberView.text = Integer.toString(expandedListNumber)
        val consLay = convertView.findViewById<ConstraintLayout>(R.id.reviewChild)
        ct.forEach {category ->
            if(category.name == listTitle){
                val color = "#66" + category.color.substring(1..6)
                Log.d("masaka",color)
                consLay.setBackgroundColor(Color.parseColor(color))
            }
        }
        val imageView = convertView.findViewById<ImageView>(R.id.ranking_view)

        when (expandedListPosition) {
            0 -> {
                val drw   = ContextCompat.getDrawable(context, R.drawable.ic_first)
                imageView.setImageDrawable(drw)
            }
            1 -> {
                val drw   = ContextCompat.getDrawable(context, R.drawable.ic_second);
                imageView.setImageDrawable(drw)
            }
            2 -> {
                val drw   = ContextCompat.getDrawable(context, R.drawable.ic_third);
                imageView.setImageDrawable(drw)
            }
            else -> {
                val drw   = ContextCompat.getDrawable(context, R.color.springgreen);
                imageView.setImageDrawable(drw)
            }

        }
        counter++
        return convertView
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return this.dataList[this.titleList[listPosition].type]!!.size
    }

    override fun getGroup(listPosition: Int): MindMapObject {
        return this.titleList[listPosition]
    }

    override fun getGroupCount(): Int {
        return this.titleList.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun getGroupView(listPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        counter=0
        childcnt = 3
        var convertView = convertView
        val listTitle = getGroup(listPosition).type

        val listReviewValue = getGroup(listPosition).text

        if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.fragment_grouping_result_partent, null)
        }
        val listTitleTextView = convertView!!.findViewById<TextView>(R.id.expand_partent)
        listTitleTextView.setTypeface(null, Typeface.BOLD)
        listTitleTextView.text = listTitle

        //まとめの親ビューの色を変更
        val consLay = convertView.findViewById<ConstraintLayout>(R.id.resultParent)
        ct.forEach {category ->
            if(category.name == listTitle){
                consLay.setBackgroundColor(Color.parseColor(category.color))
            }
        }

//        val expandedListReviewValueView = convertView!!.findViewById<TextView>(R.id.reviewValue)
//        expandedListReviewValueView.text = listReviewValue



        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}