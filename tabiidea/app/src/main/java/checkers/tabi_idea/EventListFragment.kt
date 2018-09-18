package checkers.tabi_idea


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_event_list.*
import java.util.*

class EventListFragment : Fragment() {

    private val eventManager = EventManager()
    private var mindMapObjectList = mutableListOf<MindMapObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            eventManager.eventList = it.getParcelableArrayList<Event>("eventListKey") as MutableList<Event>
//            eventManager.eventList = it.getParcelable("eventListKey")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.title = "イベント"
        (activity as AppCompatActivity).supportActionBar?.setDisplayUseLogoEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
        setHasOptionsMenu(true)

        mindMapObjectList.add(MindMapObject(0, "旅行", 1f/2,1f/2, mutableListOf(1, 2, 3, 4)))
        mindMapObjectList.add(MindMapObject(1, "行先", 1f/2,1f/4, mutableListOf(0)))
        mindMapObjectList.add(MindMapObject(2, "予算", 1f/4,1f/2, mutableListOf(0)))
        mindMapObjectList.add(MindMapObject(3, "食事", 1f/2,3f/4, mutableListOf(0)))
        mindMapObjectList.add(MindMapObject(4, "宿泊", 3f/4,1f/2, mutableListOf(0)))

        return inflater.inflate(R.layout.fragment_event_list, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                (activity as AppCompatActivity).supportFragmentManager.popBackStack()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventListView.adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, eventManager.eventList)
        eventListView.setOnItemClickListener { parent: AdapterView<*>, view: View?, position: Int, id: Long ->
            (activity as AppCompatActivity)
                    .supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, TravelMindMapFragment.newInstance(eventManager.eventList[id.toInt()].title, mindMapObjectList))
                    .addToBackStack(null)
                    .commit()

        }



        fab.setOnClickListener {
            eventManager.add(Event("新しいイベント"))
            (eventListView.adapter as ArrayAdapter<*>).notifyDataSetChanged()
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(eventList: MutableList<Event>) = EventListFragment().apply {
            arguments = Bundle().apply {
                putParcelableArrayList("eventListKey", ArrayList(eventList))
            }
        }
    }
}
