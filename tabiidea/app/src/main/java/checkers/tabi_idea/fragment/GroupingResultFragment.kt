package checkers.tabi_idea.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListAdapter
import androidx.fragment.app.Fragment
import checkers.tabi_idea.R
import checkers.tabi_idea.data.MindMapObject
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_grouping_result.*

class GroupingResultFragment : Fragment() {

    private var adapter: ExpandableListAdapter? = null

    private var titleList: List<MindMapObject>? = null

    private var mindMapObjectList: List<MindMapObject> = listOf()
    private var firstMindMapObjectList: List<MindMapObject> = listOf()

    private var mindMapObjectMap: Map<String, List<MindMapObject>> = mapOf()

    private var map: Map<String, MindMapObject> = mutableMapOf()

    private var mEventId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mEventId = savedInstanceState?.getInt("mEventId")
                ?: arguments?.getInt("mEventId")
                ?: 0

        firstMindMapObjectList = savedInstanceState?.getParcelableArrayList<MindMapObject>("mindMapObjectList") as List<MindMapObject>?
                ?: arguments?.getParcelableArrayList<MindMapObject>("mindMapObjectList") as List<MindMapObject>?
                ?: listOf()

        contactFirebase()

        mindMapObjectMap = firstMindMapObjectList!!.filter { it.type != "root" }.sortedByDescending { it.point }.groupBy { it.type }
        titleList = firstMindMapObjectList!!.filter { it.type != "root" }.sortedByDescending { it.point }.distinctBy { it.type }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList("mindMapObjectList", ArrayList(mindMapObjectList))
        outState.putInt("mEventId", mEventId)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_grouping_result, container, false)

//        expandableListView = view.findViewById(R.id.sample_list)
        if (map.isNotEmpty()) {
            mindMapObjectList = map.flatMap { listOf(it.value) }

            mindMapObjectMap = mindMapObjectList.filter { it.type != "root" }.sortedByDescending { it.point }.groupBy { it.type }
            titleList = mindMapObjectList.filter { it.type != "root" }.sortedByDescending { it.point }.distinctBy { it.type }
        }

        val listData = mindMapObjectMap as HashMap

        adapter = GroupingExpandableListAdapter(context!!, titleList = titleList as ArrayList<MindMapObject>, dataList = listData)
        sample_list?.setAdapter(adapter)
        return view
    }

    private fun contactFirebase() {
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
                map = map.minus(dataSnapshot.key!!)
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
        ref.addChildEventListener(childEventListener)
    }

    companion object {
        @JvmStatic
        fun newInstance(mEventId: Int, map: Map<String, MindMapObject>) = GroupingResultFragment().apply {
            arguments?.putParcelableArrayList("mindMapObjectList", ArrayList(map.flatMap { listOf(it.value) }))
            arguments?.putInt("mEventId", mEventId)
        }
    }
}
