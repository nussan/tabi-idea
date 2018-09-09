package checkers.tabi_idea


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_event_list.*
import java.util.*

class EventListFragment : Fragment() {

    private val eventManager = checkers.tabi_idea.EventManager()

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
        fab.setOnClickListener {
            eventManager.add(Event("新しいイベント"))
            (eventListView.adapter as ArrayAdapter<*>).notifyDataSetChanged()
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(eventList: MutableList<Event>) = EventListFragment().apply {
            arguments = Bundle().apply{
                putParcelableArrayList("eventListKey", ArrayList(eventList))
            }
        }
    }
}
