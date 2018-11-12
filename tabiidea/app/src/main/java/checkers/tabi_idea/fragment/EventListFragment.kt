package checkers.tabi_idea.fragment


import android.animation.ObjectAnimator
import android.content.res.Resources
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.EditText
import checkers.tabi_idea.R
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.MindMapObject
import checkers.tabi_idea.data.User
import checkers.tabi_idea.manager.EventManager
import checkers.tabi_idea.provider.Repository
import kotlinx.android.synthetic.main.fragment_event_list.*
import java.util.*

class EventListFragment : Fragment() {
    private val eventManager = EventManager()
    private var eventId = 0
    private var eventPass: String? = null
    private var mindMapObjectList: MutableList<MindMapObject> = mutableListOf(
            MindMapObject(1, "行先", 200f, 200f, 0,0,"destination"),
            MindMapObject(2, "予算", 200f, -200f, 0,0,"budget"),
            MindMapObject(3, "食事", -200f, 200f, 0,0,"food"),
            MindMapObject(4, "宿泊", -200f, -200f, 0,0,"hotel")
    )
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
        val repository = Repository()
        //RecyclerViewを設定
        eventListView.adapter = EventListAdapter(context,eventManager.eventList)
        eventListView.layoutManager = GridLayoutManager(context,2) as RecyclerView.LayoutManager?

        (eventListView.adapter as EventListAdapter).setOnClickListener (object: View.OnClickListener {
            override fun onClick(view: View?) {
                Log.d(javaClass.simpleName, "onTouch!!")
                val position = eventListView.getChildAdapterPosition(view)
                val eid = eventListView.adapter.getItemId(position)
                activity?.supportFragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.container, TravelMindMapFragment.newInstance(eventManager.eventList[position]))
                        ?.addToBackStack(null)
                        ?.commit()
            }
        })

        create_fab.setOnClickListener{
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
                    repository.addEvent(userId, title) {
                        eventId = it.id
                        eventPass = it.password
                        Log.d("tubasa", it.id.toString())
                        repository.addEventtoFb(eventId.toString())//event.id
                        mindMapObjectList.forEach {
                            repository.addMmo(eventId.toString(), it)
                        }
                        eventManager.add(it)
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

        join_fab.setOnClickListener{

        }


        fab.setOnClickListener {
            if(mButtonState == ButtonState.CLOSE) fabOpen(dpToPx(66))
            else fabClose()
        }

        nameEdit.setOnClickListener {
            it.isEnabled = false
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
                    repository.editUser(userId, name){
                        // コールバックの操作
                        (activity as AppCompatActivity).supportActionBar?.title = it.name
                    }

                }
                setNegativeButton("Cancel", null)
            }.create()

            // ダイアログ表示と同時にキーボードを表示
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
    private fun dpToPx(dp: Int): Float {
        return (dp * Resources.getSystem().getDisplayMetrics().density)
    }

    private fun fabClose() {
        join_button_layout.setVisibility(View.GONE)
        var anim = ObjectAnimator.ofFloat(join_button_layout, "translationY", 0f)
        anim.setDuration(200)
        anim.start()

        create_button_layout.setVisibility(View.GONE)
        anim = ObjectAnimator.ofFloat(create_button_layout, "translationY", 0f)
        anim.setDuration(200)
        anim.start()

        mButtonState = ButtonState.CLOSE
    }

    private fun fabOpen(size:Float) {
        join_button_layout.setVisibility(View.VISIBLE)
        var anim = ObjectAnimator.ofFloat(join_button_layout, "translationY", -size)
        anim.duration = 200
        anim.start()

        create_button_layout.setVisibility(View.VISIBLE)
        anim = ObjectAnimator.ofFloat(create_button_layout,"translationY",-size*2)
        anim.duration = 200
        anim.start()

        mButtonState = ButtonState.OPEN
    }

    enum class ButtonState{
        OPEN,
        CLOSE
    }

    var mButtonState: ButtonState = ButtonState.CLOSE
}
