package checkers.tabi_idea.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import checkers.tabi_idea.*
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.MindMapObject
import checkers.tabi_idea.manager.EventManager
import checkers.tabi_idea.provider.Repository
import kotlinx.android.synthetic.main.fragment_event_list.*
import java.util.*

class EventListFragment : Fragment() {

    private val eventManager = EventManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            eventManager.eventList = it.getParcelableArrayList<Event>("eventListKey") as MutableList<Event>
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.title = "イベント"
        (activity as AppCompatActivity).supportActionBar?.setDisplayUseLogoEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
        setHasOptionsMenu(true)



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
        val repository = Repository()
        eventListView.adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, eventManager.eventList)

        eventListView.setOnItemClickListener { parent: AdapterView<*>, view: View?, position: Int, id: Long ->
            repository.getMmoCallback { it ->
                eventManager.eventList[id.toInt()].mindMapObjectList = it as MutableList<MindMapObject>
                (activity as AppCompatActivity)
                        .supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, TravelMindMapFragment.newInstance(eventManager.eventList[id.toInt()]))
                        .addToBackStack(null)
                        .commit()
            }
        }

        fab.setOnClickListener {
            eventManager.add(Event(0,"新しいイベント", mutableListOf(), mutableListOf()))

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
