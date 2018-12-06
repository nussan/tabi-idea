package checkers.tabi_idea.fragment

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import checkers.tabi_idea.R
import checkers.tabi_idea.data.MindMapObject

import java.util.HashMap

class GroupingExpandableListAdapter internal constructor(private val context: Context, private val titleList: List<MindMapObject>, private val dataList: HashMap<String, List<MindMapObject>>) : BaseExpandableListAdapter() {

    override fun getChild(listPosition: Int, expandedListPosition: Int): MindMapObject {
            return this.dataList[this.titleList[listPosition].type]!![expandedListPosition]
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    override fun getChildView(listPosition: Int, expandedListPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val expandedListText = getChild(listPosition, expandedListPosition).text
        val expandedListNumber = getChild(listPosition, expandedListPosition).point

        if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.fragment_grouping_result_child, null)
        }
        val expandedListTextView = convertView!!.findViewById<TextView>(R.id.expand_child)
        expandedListTextView.text = expandedListText
        val expandedListNumberView = convertView!!.findViewById<TextView>(R.id.reviewNumber)
        expandedListNumberView.text = Integer.toString(expandedListNumber)

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

        val expandedListReviewValueView = convertView!!.findViewById<TextView>(R.id.reviewValue)
        expandedListReviewValueView.text = listReviewValue


        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}