package checkers.tabi_idea.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import checkers.tabi_idea.R
import checkers.tabi_idea.data.MindMapObject
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class GroupingResultFragment : Fragment(){

    private var expandableListView: ExpandableListView? = null
    private var adapter: ExpandableListAdapter? = null

    private var titleList: List<MindMapObject> ? = null

    private var mindMapObjectList: List<MindMapObject> ? = null
    private var firstMindMapObjectList: List<MindMapObject> ? = null

    private var mindMapObjectMap: Map<String, List<MindMapObject>> ? = null

    private var map: Map<String, MindMapObject> = mutableMapOf()

    private var mEventId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mEventId = arguments?.getInt("mEventId")
                ?:0

        firstMindMapObjectList = arguments?.getParcelableArrayList("mindMapObjectList")
                ?: null

        contactFirebase()

        mindMapObjectMap = firstMindMapObjectList!!.filter{ it.type != "root"}.sortedByDescending { it.point }.groupBy { it.type }
        titleList = firstMindMapObjectList!!.filter{ it.type != "root"}.sortedByDescending { it.point }.distinctBy { it.type }

    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_grouping_result, container, false)

        expandableListView = view.findViewById(R.id.sample_list)
        if(map.isNotEmpty()) {
            mindMapObjectList = map.flatMap { listOf(it.value) }

            mindMapObjectMap = mindMapObjectList!!.filter { it.type != "root" }.sortedByDescending { it.point }.groupBy { it.type }
            titleList = mindMapObjectList!!.filter { it.type != "root" }.sortedByDescending { it.point }.distinctBy { it.type }
        }
        show()

        return view
    }

    private fun contactFirebase(){
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("TravelActivity", "onChildAdded:" + dataSnapshot.key!!)

                val key = dataSnapshot.key!!
                val mmo = dataSnapshot.getValue(MindMapObject::class.java)!!
                map = map.minus(key)
                map = map.plus(key to mmo)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("TravelActivity", "onChildChanged:" + dataSnapshot.key!!)

                val key = dataSnapshot.key!!
                val mmo = dataSnapshot.getValue(MindMapObject::class.java)!!
                map = map.plus(key to mmo)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d("TravelActivity", "onChildRemoved:" + dataSnapshot.key!!)
                map.minus(dataSnapshot.key)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("TravelActivity", "onChildMoved:" + dataSnapshot.key!!)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TravelActivity", "postComments:onCancelled", databaseError.toException())
            }
        }
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference(mEventId.toString())
        ref.child("mmoMaps").addChildEventListener(childEventListener)
    }

    private fun show() {
        // contextのnullチェック
        val ctx = context ?: return
        val listData = mindMapObjectMap as HashMap

        adapter = GroupingExpandableListAdapter(context = ctx, titleList = titleList as ArrayList<MindMapObject>, dataList = listData)
        expandableListView!!.setAdapter(adapter)
    }
}


fun newGroupingResultFragment(mEventId : Int, map : Map<String, MindMapObject>) : GroupingResultFragment {
    val fragment = GroupingResultFragment()

    val args = Bundle()

    val mindMapObjectList : List<MindMapObject> = map.flatMap { listOf(it.value) }
    args.putParcelableArrayList("mindMapObjectList", ArrayList(mindMapObjectList))
    args.putInt("mEventId",mEventId)

    fragment.arguments = args
    return fragment
}
