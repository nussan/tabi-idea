package checkers.tabi_idea.fragment


import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
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
            repository.updateMmoCallback { it ->
                eventManager.eventList[id.toInt()].mindMapObjectList = it as MutableList<MindMapObject>
                activity
                        ?.supportFragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.container, TravelMindMapFragment.newInstance(eventManager.eventList[id.toInt()]))
                        ?.addToBackStack(null)
                        ?.commit()
            }
        }

        fab.setOnClickListener {
            // レイアウトを取得
            val inflater = this.layoutInflater.inflate(R.layout.input_form, null, false)

            // ダイアログ内のテキストエリア
            val inputText : EditText = inflater.findViewById(R.id.inputText)
            inputText.requestFocus()

            // ダイアログの設定
            val inputForm = AlertDialog.Builder(context!!).apply {
                setTitle("新しいイベント")
                setView(inflater)
                setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                    // OKボタンを押したときの処理
                    eventManager.add(Event(0,"${inputText.text}", mutableListOf(), mutableListOf()))

                    (eventListView.adapter as ArrayAdapter<*>).notifyDataSetChanged()
                })
                setNegativeButton("Cancel", null)
            }.create()

            // ダイアログ表示と同時にキーボードを表示
            inputForm.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            inputForm.show()
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
