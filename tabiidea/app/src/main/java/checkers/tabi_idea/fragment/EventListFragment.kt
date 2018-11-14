package checkers.tabi_idea.fragment


import android.animation.ObjectAnimator
import android.content.res.Resources
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
    private var eventPass: String? = null
    private val repository = Repository()
    private var userId = 0
    private lateinit var myuser : User
    private var mButtonState: ButtonState = ButtonState.CLOSE

    enum class ButtonState{
        OPEN,
        CLOSE
    }

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
                    adapter.removeAt(it.adapterPosition)
                }
                //TODO　データベースから削除機能
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipHandler)
        itemTouchHelper.attachToRecyclerView(eventListView)

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

                    repository.addEvent(userId, title) {event ->
                        eventId = event.id
                        eventPass = event.password
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

        join_fab.setOnClickListener{
            // TODO 参加処理
        }
        shareEvent.setOnClickListener{
            // TODO 招待処理（仮）
        }


        fab.setOnClickListener {
            if(mButtonState == ButtonState.CLOSE) {
                startRotateAnim(0F,180f,fab.pivotX,fab.pivotY,true)
                fabOpen(dpToPx(70))
            }
            else {
                startRotateAnim(180f,360f,fab.pivotX,fab.pivotY,false)
                fabClose()
            }
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
                        myuser = it
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

        edit_name_button_layout.setVisibility(View.GONE)
        anim = ObjectAnimator.ofFloat(edit_name_button_layout, "translationY", 0f)
        anim.setDuration(200)
        anim.start()

        share_event_button_layout.setVisibility(View.GONE)
        anim = ObjectAnimator.ofFloat(share_event_button_layout, "translationY", 0f)
        anim.setDuration(200)
        anim.start()

        fab_background.setVisibility(View.GONE)

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

        edit_name_button_layout.setVisibility(View.VISIBLE)
        anim = ObjectAnimator.ofFloat(edit_name_button_layout,"translationY",-size*3)
        anim.duration = 200
        anim.start()

        share_event_button_layout.setVisibility(View.VISIBLE)
        anim = ObjectAnimator.ofFloat(share_event_button_layout,"translationY",-size*4)
        anim.duration = 200
        anim.start()

        fab_background.setVisibility(View.VISIBLE)


        mButtonState = ButtonState.OPEN
    }

    private fun startRotateAnim(fromDegree : Float,toDegree : Float,pivotX : Float, pivotY : Float,fill:Boolean){
        Log.d("rotate","rottate")
        var rotate = RotateAnimation(fromDegree, toDegree, pivotX, pivotY)
        rotate.duration = 200
        rotate.setFillAfter(fill)
        fab.startAnimation(rotate)
    }
}
