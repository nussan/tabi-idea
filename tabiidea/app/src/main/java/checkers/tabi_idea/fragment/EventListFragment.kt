package checkers.tabi_idea.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.*
import android.view.animation.RotateAnimation
import android.widget.EditText
import android.widget.Toast
import checkers.tabi_idea.R
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.User
import checkers.tabi_idea.manager.EventManager
import checkers.tabi_idea.provider.Repository
import kotlinx.android.synthetic.main.fragment_event_list.*
import java.util.*

class EventListFragment : Fragment() {
    private val eventManager = EventManager()
    private var eventId = 0
    private val repository = Repository()
    private var userId = 0
    private lateinit var myuser : User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt("userId")
            myuser = it.getParcelable("user")
            eventManager.eventList = it.getParcelableArrayList<Event>("eventListKey") as MutableList<Event>
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.title = myuser.name
        (activity as AppCompatActivity).supportActionBar?.setDisplayUseLogoEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
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
        //RecyclerViewを設定
        eventListView.adapter = EventListAdapter(context,eventManager.eventList)
        eventListView.layoutManager = GridLayoutManager(context,1)

        val swipHandler = object : SwipeToDeleteCallback(context!!){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                val adapter = eventListView.adapter as EventListAdapter
                viewHolder?.let{
                    eventId = eventManager.eventList[it.adapterPosition].id
                    adapter.removeAt(it.adapterPosition)
                }
                repository.deleteEvent(userId,eventId){
                    Toast.makeText(context,it.get("title")+"が削除されました",Toast.LENGTH_SHORT).show()
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipHandler)
        itemTouchHelper.attachToRecyclerView(eventListView)

        (eventListView.adapter as EventListAdapter).setOnClickListener (object: View.OnClickListener {
            override fun onClick(view: View?) {
                Log.d(javaClass.simpleName, "onTouch!!")
                val position = eventListView.getChildAdapterPosition(view)
                activity?.supportFragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.container, TravelMindMapFragment.newInstance(eventManager.eventList[position]))
                        ?.addToBackStack(null)
                        ?.commit()
            }
        })

        fab.setOnClickListener {
            it.isEnabled = false
            // レイアウトを取得
            val inflater = this.layoutInflater.inflate(R.layout.input_form, null, false)

            // ダイアログ内のテキストエリア
            val inputText: EditText = inflater.findViewById(R.id.inputText)
            inputText.requestFocus()

            // ダイアログの設定
            val inputForm = AlertDialog.Builder(context!!).apply {
                setTitle("新しいイベント")
                setView(inflater)
                setPositiveButton("OK") { _, _ ->
                    // OKボタンを押したときの処理
                    val title = mapOf(
                            "title" to "${inputText.text}"
                    )

                    repository.addEvent(userId, title) {event ->
                        eventId = event.id
                        Log.d("tubasa", event.id.toString())
                        repository.addEventToFb(eventId.toString())//event.id
                        eventManager.add(event)
                        eventListView.adapter.notifyDataSetChanged()
                    }

                }
                setNegativeButton("Cancel", null)
            }.create()

            //ダイアログ表示と同時にキーボードを表示
            inputForm.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            inputForm.show()

            it.isEnabled = true

        }

    }

    companion object {
        @JvmStatic
        fun newInstance(user: User, eventList: MutableList<Event>) = EventListFragment().apply {
            arguments = Bundle().apply {
                putInt("userId", user.id)
                putParcelable("user", user)
                putParcelableArrayList("eventListKey", ArrayList(eventList))
            }
        }
    }

    //EventListFragmentでツールバーにメニュー機能を追加する
    override fun onCreateOptionsMenu(menu : Menu,inflater : MenuInflater){
        super.onCreateOptionsMenu(menu,inflater)
        inflater.inflate(R.menu.actions,menu)

        val item : MenuItem = menu.findItem(R.id.action_name_edit)
        item.setOnMenuItemClickListener{
            // レイアウトを取得
            val inflater = this.layoutInflater.inflate(R.layout.input_form, null, false)

            // ダイアログ内のテキストエリア
            val inputText: EditText = inflater.findViewById(R.id.inputText)
            inputText.requestFocus()

            // ダイアログの設定
            val inputForm = AlertDialog.Builder(context!!).apply {
                setTitle("名前の編集")
                setView(inflater)
                setPositiveButton("OK") { _, _ ->
                    // OKボタンを押したときの処理
                    val name = mapOf(
                            "name" to "${inputText.text}"
                    )
                    Log.d("EventListFragment", "")
                    repository.editUser(userId, name){user ->
                        (activity as AppCompatActivity).supportActionBar?.title = user.name
                        myuser = user
                    }

                }
                setNegativeButton("Cancel", null)
            }.create()

            // ダイアログ表示と同時にキーボードを表示
            inputForm.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            inputForm.show()

            true
        }
    }
}
