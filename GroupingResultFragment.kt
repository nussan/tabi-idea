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

class GroupingResultFragment : Fragment(){

    private var expandableListView: ExpandableListView? = null
    private var adapter: ExpandableListAdapter? = null

    private var titleList: List<MindMapObject> ? = null

    private var mindMapObjectList: List<MindMapObject> ? = null

    private var mindMapObjectMap: Map<String, List<MindMapObject>> ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mindMapObjectList = savedInstanceState?.getParcelableArrayList("mindMapObjectList")
                ?: arguments?.getParcelableArrayList("mindMapObjectList")
                ?: null

        mindMapObjectMap = mindMapObjectList!!.filter{ it.type != "root"}.groupBy { it.type }
        titleList = mindMapObjectList!!.filter{ it.type != "root"}.sortedByDescending { it.point }.distinctBy { it.type }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList("mindMapObjectList", ArrayList(mindMapObjectList))
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_grouping_result, container, false)

        expandableListView = view.findViewById(R.id.sample_list)


        show()
        return view
    }

    private fun show() {
        // contextのnullチェック
        val ctx = context ?: return
        val listData = mindMapObjectMap as HashMap

        adapter = GroupingExpandableListAdapter(context = ctx, titleList = titleList as ArrayList<MindMapObject>, dataList = listData)
        expandableListView!!.setAdapter(adapter)
    }
}


fun newGroupingResultFragment(map : Map<String, MindMapObject>) : GroupingResultFragment {
    val fragment = GroupingResultFragment()

    val args = Bundle()

    val mindMapObjectList : List<MindMapObject> = map.flatMap { listOf(it.value) }
    args.putParcelableArrayList("mindMapObjectList", ArrayList(mindMapObjectList))

    fragment.arguments = args
    return fragment
}
