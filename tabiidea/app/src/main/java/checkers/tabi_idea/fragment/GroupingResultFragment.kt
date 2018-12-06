package checkers.tabi_idea.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import checkers.tabi_idea.R
import checkers.tabi_idea.data.MindMapObject

class GroupingResultFragment : Fragment() {
    private var expandableListView: ExpandableListView? = null
    private var adapter: ExpandableListAdapter? = null
    private var titleList: List<MindMapObject>? = null
    private var mindMapObjectList: List<MindMapObject>? = null
    private var firstMindMapObjectList: List<MindMapObject>? = null
    private var mindMapObjectMap: Map<String, List<MindMapObject>>? = null
    private var map: Map<String, MindMapObject> = mutableMapOf()
    private var mEventId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEventId = savedInstanceState?.getInt("mEventId")
                ?: arguments?.getInt("mEventId")
                ?: 0
        firstMindMapObjectList = savedInstanceState?.getParcelableArrayList("mindMapObjectList")
                ?: arguments?.getParcelableArrayList("mindMapObjectList")

        mindMapObjectMap = firstMindMapObjectList?.filter { it.type != "root" }?.sortedByDescending { it.point }?.groupBy { it.type }
        titleList = firstMindMapObjectList?.filter { it.type != "root" }?.sortedByDescending { it.point }?.distinctBy { it.type }
    }

    override fun onSaveInstanceState(outState: Bundle) {
//        outState.putParcelableArrayList("mindMapObjectList", ArrayList(mindMapObjectList))
        outState.putInt("mEventId", mEventId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_grouping_result, container, false)
        expandableListView = view.findViewById(R.id.sample_list)
        if (map.isNotEmpty()) {
            mindMapObjectList = map.flatMap { listOf(it.value) }
            mindMapObjectMap = mindMapObjectList!!.filter { it.type != "root" }.sortedByDescending { it.point }.groupBy { it.type }
            titleList = mindMapObjectList!!.filter { it.type != "root" }.sortedByDescending { it.point }.distinctBy { it.type }
        }
        show()
        return view
    }

    fun show() {
        if (map.isNotEmpty()) {
            mindMapObjectList = map.flatMap { listOf(it.value) }
            mindMapObjectMap = mindMapObjectList!!.filter { it.type != "root" }.sortedByDescending { it.point }.groupBy { it.type }
            titleList = mindMapObjectList!!.filter { it.type != "root" }.sortedByDescending { it.point }.distinctBy { it.type }
        }
        val ctx = context ?: return
        val listData = mindMapObjectMap as? HashMap ?: return
        adapter = GroupingExpandableListAdapter(context = ctx, titleList = titleList as ArrayList<MindMapObject>, dataList = listData)
        expandableListView?.setAdapter(adapter)
    }

    fun update(map: Map<String, MindMapObject>) {
        this.map = map
        show()
    }

    companion object {
        @JvmStatic
        fun newInstance(mEventId: Int, map: Map<String, MindMapObject>) = GroupingResultFragment().apply {
            arguments?.putParcelableArrayList("mindMapObjectList", ArrayList(map.flatMap { listOf(it.value) }))
            arguments?.putInt("mEventId", mEventId)
        }
    }
}
