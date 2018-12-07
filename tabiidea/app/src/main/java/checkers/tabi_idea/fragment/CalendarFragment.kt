package checkers.tabi_idea.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import checkers.tabi_idea.R
import checkers.tabi_idea.data.EventDateSet
import checkers.tabi_idea.data.User
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_calendar_list.*
import java.util.*


class CalendarFragment : Fragment(){

    private var currentDate = Calendar.getInstance()
    private var recyclerView: RecyclerView? = null
    private var mEventId: Int = 0
    private var eventDateList: List<EventDateSet>? = null
    private var firstEventDateList: List<EventDateSet>? = null

    private var mUser: User? =null

    private var dateMap: Map<String, EventDateSet> = mutableMapOf()

    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mEventId = arguments?.getInt("mEventId")
                ?:0

        val mEventDateSet :EventDateSet = EventDateSet("更新してください。",1, mutableListOf())

        firstEventDateList = arguments?.getParcelableArrayList("eventDateList")
                ?: listOf(mEventDateSet)

        mUser = arguments?.getParcelable("mUser")

        contactFirebase()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_calendar_list, container, false)

        view.findViewById<Button>(R.id.dateAdd).setOnClickListener {
            // タップされたら日付選択カレンダーを表示
            val dialog = DatePickerFragment()
            dialog.arguments = Bundle().apply {
                putSerializable("current", currentDate)
            }
            dialog.show(getFragmentManager(), "calendar")
        }

        recyclerView = view.findViewById(R.id.date_list)

        show()
        return view
    }

    private fun show() {
        // contextのnullチェック,firstEventDateList as ArrayList<EventDateSet>
        val ctx = context ?: return

        val textList: MutableList<String> = mutableListOf()
        val pointList: MutableList<String> = mutableListOf()

        if(dateMap.isNotEmpty()){
            eventDateList = dateMap.flatMap { listOf(it.value) }.sortedBy { it.text }.distinctBy { it.text}
            eventDateList?.forEach {
                textList.add(it.text)
                pointList.add(it.point.toString())
            }
        }else{
            firstEventDateList?.forEach {
                textList.add(it.text)
                pointList.add(it.point.toString())
            }
        }


        val adapter = DatelistAdapter(ctx, textList, pointList){ date ->
            // タップされた位置にあるタイムゾーンをトースト表示する
            val ref = database.getReference(mEventId.toString())
            var mlikeList: MutableList<Int> = mutableListOf()
            var nameList: MutableList<String> = mutableListOf()
            var mkey: String = ""

            dateMap.forEach{
                if(it.value.text.equals(date)){
                    mkey = it.key
                    mlikeList = it.value.likeList
                    nameList = it.value.nameList
                }
            }

            if(mlikeList.contains(mUser?.id)){
                mlikeList.remove(mUser!!.id)
                nameList.remove(mUser!!.name)
                val toast = Toast.makeText(context, "投票を取り消しました！", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }else{
                mlikeList.add(mUser!!.id)
                nameList.remove(mUser!!.name)
                val toast = Toast.makeText(context, "投票しました！", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }

            if(date != null) {
                val mEventDateSet: EventDateSet = EventDateSet(date, mlikeList.size, nameList, mlikeList)

                if (mkey != null) {
                    ref.child("dates").child(mkey).setValue(mEventDateSet)
                }
            }

        }
        recyclerView!!.adapter = adapter

        recyclerView!!.layoutManager = LinearLayoutManager(
                ctx, RecyclerView.VERTICAL, false)
    }

    private fun contactFirebase(){
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("TravelActivity", "onChildAdded:" + dataSnapshot.key!!)

                val key = dataSnapshot.key!!
                val mdateMap = dataSnapshot.getValue(EventDateSet::class.java)!!
                dateMap = dateMap.minus(key)
                dateMap = dateMap.plus(key to mdateMap)
                date_list?.adapter?.notifyDataSetChanged()
                show()
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("TravelActivity", "onChildChanged:" + dataSnapshot.key!!)

                val key = dataSnapshot.key!!
                val mdateMap = dataSnapshot.getValue(EventDateSet::class.java)!!
                dateMap = dateMap.plus(key to mdateMap)

                date_list?.adapter?.notifyDataSetChanged()
                show()
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d("TravelActivity", "onChildRemoved:" + dataSnapshot.key!!)
                dateMap.minus(dataSnapshot.key)

                date_list?.adapter?.notifyDataSetChanged()
                show()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("TravelActivity", "onChildMoved:" + dataSnapshot.key!!)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TravelActivity", "postComments:onCancelled", databaseError.toException())
            }
        }
        val ref = database.getReference(mEventId.toString())
        ref.child("dates").addChildEventListener(childEventListener)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}

fun newCalendarFragment(mEventId:Int,dateMap:Map<String, EventDateSet>, mUser:User) : CalendarFragment {
    val fragment = CalendarFragment()

    val args = Bundle()

    val dateObjectList : List<EventDateSet> = dateMap.flatMap { listOf(it.value) }.sortedBy { it.text }.distinctBy { it.text}

    args.putParcelableArrayList("eventDateList", ArrayList(dateObjectList))

    args.putInt("mEventId",mEventId)

    args.putParcelable("mUser", mUser)

    fragment.arguments = args
    return fragment
}

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var calendar : Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calendar = arguments?.getSerializable("current") as? Calendar ?: Calendar.getInstance()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return DatePickerDialog(context, this,
                calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DATE])
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        if (context is DatePickerDialog.OnDateSetListener) {
            (context as DatePickerDialog.OnDateSetListener).onDateSet(view, year, month, day)
        }
    }
}

class DatelistAdapter(context: Context,val testList:List<String?>, val pointList:List<String?>,
                      private val onDateZoneClicked : (String?) -> Unit)
    : RecyclerView.Adapter<DatelistAdapter.DatelistViewHolder>() {

    // レイアウトからビューを生成するInflater
    private val inflater = LayoutInflater.from(context)

    // 表示するべき値をViewにセットする
    override fun onBindViewHolder(holder: DatelistViewHolder, position: Int) {
        // 位置に応じたタイムゾーンを得る
        val dateZone = testList[position]
        val datePoint :String? = pointList[position]
        // 表示内容を更新する
        holder.dateZone.text = dateZone
        holder.datePoint.text = datePoint
    }

    // 新しくViewを作る時に呼ばれる
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatelistViewHolder {
        // Viewを生成する
        val view = inflater.inflate(R.layout.fragment_calendar_row, parent, false)

        // ViewHolderを作る
        val viewHolder = DatelistViewHolder(view)

        // viewをタップしたときの処理
        view.setOnClickListener {
            // アダプター上の位置を得る
            val position = viewHolder.adapterPosition
            // 位置をもとに、タイムゾーンを得る
            val dateZone = testList[position]
            // コールバックを呼び出す
            onDateZoneClicked(dateZone)
        }

        return viewHolder
    }

    override fun getItemCount(): Int = testList.size

    // Viewへの参照をもっておくViewHolder
    class DatelistViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val dateZone = view.findViewById<TextView>(R.id.candidate_date)
        val datePoint = view.findViewById<TextView>(R.id.reviewNumber)
    }
}