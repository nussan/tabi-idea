package checkers.tabi_idea.fragment


import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import checkers.tabi_idea.R
import checkers.tabi_idea.activity.TravelActivity
import checkers.tabi_idea.adapter.EventListAdapter
import checkers.tabi_idea.data.Category
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.User
import checkers.tabi_idea.manager.EventManager
import checkers.tabi_idea.provider.FirebaseApiClient
import checkers.tabi_idea.provider.Repository
import kotlinx.android.synthetic.main.fragment_event_list.*
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.util.*

class EventListFragment : Fragment() {
    private val eventManager = EventManager()
    private var eventId: Int? = null
    private var repository: Repository = Repository()
    private var fireBaseApiClient: FirebaseApiClient? = null
    private lateinit var myuser: User
    private var sortNewOld = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            myuser = it.getParcelable("user")
            eventManager.eventList = it.getParcelableArrayList<Event>("eventListKey") as MutableList<Event>
        }
        if (activity?.intent?.action != null && activity?.intent?.action == Intent.ACTION_VIEW) {
            if (activity?.intent?.data != null) {
                val url = activity!!.intent.data
                val eventToken = url.getQueryParameter("event")
                Log.d("intentdata", eventToken)
                repository.joinEvent(myuser.token, myuser.id, eventToken) { event: Event ->
                    eventId = event.id
                    Log.d("tubasa", event.id.toString())
                    eventManager.add(event)
                    eventListView.adapter?.notifyDataSetChanged()

                    val toast = Toast.makeText(context, "${event.title}に参加しました。", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        repository = Repository()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.title = myuser.name
        (activity as AppCompatActivity).supportActionBar?.setDisplayUseLogoEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
//        TODO ユーザーアイコンゲット
        repository.getUserIcon(myuser.id, myuser.token) {
            val btmarr = it["icon"]
            val baStrArr = btmarr?.split(Regex(", |\\[|]"), 0) ?: return@getUserIcon
            var baBytArr = ByteArray(baStrArr.size-2)
            for(i in 1 .. baStrArr.size-2) {
                    var tmp = baStrArr[i].toInt()
                if(tmp >127) tmp -= 256
                baBytArr[i-1] = tmp.toByte()
            }
            Log.d("uma",baBytArr[1].toString()+"と"+baBytArr.size + "と"+ baStrArr[1]+"です")
            val options : BitmapFactory.Options = BitmapFactory.Options()
            options.inSampleSize = 1
            val bitmap = BitmapFactory.decodeByteArray(baBytArr,0,baBytArr.size,options)
            val drw = BitmapDrawable(bitmap)
            (activity as AppCompatActivity).supportActionBar?.setIcon(drw)
        }
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
        eventListView.adapter = EventListAdapter(context, eventManager.eventList,myuser)
        eventListView.layoutManager = GridLayoutManager(context, 1)

        val swipHandler = object : SwipeToDeleteCallback(context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = eventListView.adapter as EventListAdapter
                viewHolder.let {
                    eventId = adapter.eventList[it.adapterPosition].id
                    adapter.removeAt(it.adapterPosition)
                    eventManager.eventList = adapter.eventList
                }
                repository.deleteEvent(myuser.token, myuser.id, eventId!!) {
                    Toast.makeText(context, it["title"] + "が削除されました", Toast.LENGTH_SHORT).show()
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipHandler)
        itemTouchHelper.attachToRecyclerView(eventListView)

        (eventListView.adapter as EventListAdapter).setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                Log.d(javaClass.simpleName, "onTouch!!")
                fab?.isEnabled = false

                val position = eventListView.getChildAdapterPosition(view)
                val event = (eventListView.adapter as EventListAdapter).eventList[position]
                Log.d("masaka", event.title)
                repository.getCategoryList(myuser.token, event.id) { list ->
                    repository.unsub()
                    val intent = Intent(activity, TravelActivity::class.java)
                    intent.putExtra("user", myuser)
                    intent.putExtra("event", event)
                    intent.putExtra("categoryList", ArrayList(list))
                    startActivity(intent)
                }

            }
        })

        fab.setOnClickListener {
            eventListView?.isClickable = false

            val adapter = eventListView.adapter as EventListAdapter
            adapter.eventList = eventManager.eventList //検索機能を実行したときの更新
            it.isEnabled = false
            // レイアウトを取得
            val inflater = this.layoutInflater.inflate(R.layout.input_form_normal, null, false)

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
                    if ("${inputText.text}" != "" && "${inputText.text}".substring(0, 1) != " " && "${inputText.text}".substring(0, 1) != "　") {
                        repository.addEvent(myuser.token, myuser.id, title) { event ->
                            eventId = event.id
                            Log.d("tubasa", event.id.toString())
                            fireBaseApiClient = FirebaseApiClient(eventId.toString())
                            fireBaseApiClient!!.addEventToFb()
                            eventManager.add(event)
                            (eventListView.adapter as EventListAdapter).notifyItemChanged(eventManager.eventList.indexOf(event))

                            // イベントにデフォルトのカテゴリを追加
                            val cl = listOf(
                                    Category("行先", "#ffb6c1"),
                                    Category("予算", "#32cd32"),
                                    Category("食物", "#ff8c00"),
                                    Category("宿泊", "#ffe4b5")
                            )
                            cl.forEach { category ->
                                repository.addCategory(myuser.token, event.id, category) {
                                    // 特にやることなし
                                }
                            }
                        }
                    } else {
                        val toast = Toast.makeText(context, "文字を入力してください", Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    }

                }
                setNegativeButton("Cancel", null)
            }.create()

            //ダイアログ表示と同時にキーボードを表示
            inputForm.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            inputForm.show()

            it.isEnabled = true
        }

    }

    override fun onStart() {
        super.onStart()
        fab.isEnabled = true
    }

    override fun onStop() {
        Log.d("EventListFragment", "onStop")
        repository.unsub()
        super.onStop()
    }

    //EventListFragmentでツールバーにメニュー機能を追加する
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.actions, menu)

        val item: MenuItem = menu.findItem(R.id.action_name_edit)
        item.setOnMenuItemClickListener {
            // レイアウトを取得
            val inflater = this.layoutInflater.inflate(R.layout.input_form_normal, null, false)

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
                    if ("${inputText.text}" != "" && "${inputText.text}".substring(0, 1) != " " && "${inputText.text}".substring(0, 1) != "　") {
                        repository.editUser(myuser.token, myuser.id, name) { name ->
                            // コールバックの操作
                            (activity as AppCompatActivity).supportActionBar?.title = name.get("name")
                            myuser.name = name.get("name")!!
                        }
                    } else {
                        val toast = Toast.makeText(context, "文字を入力してください", Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    }
                }
                setNegativeButton("Cancel", null)
            }.create()

            // ダイアログ表示と同時にキーボードを表示
            inputForm.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            inputForm.show()

            true
        }

        val icon: MenuItem = menu.findItem(R.id.icon_change)
        icon.setOnMenuItemClickListener {
            val intent: Intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.setType("image/*")
            startActivityForResult(intent, 1000)
            // OKが押されるとonActivityResutに処理が移行する
            true
        }


        val sort: MenuItem = menu.findItem(R.id.sort)
        sort.setOnMenuItemClickListener {
            //このソート手法は初期のソートを再現できなくする機能でもある
            if (sortNewOld) {

                eventManager.eventList.sort()
                sortNewOld = false
                (eventListView.adapter as EventListAdapter).eventList = eventManager.eventList
                (eventListView.adapter as EventListAdapter).notifyDataSetChanged()
            } else {
                //ソートを古いイベントが一番上に来るようにする
                eventManager.eventList.sortDescending()
                sortNewOld = true
                (eventListView.adapter as EventListAdapter).eventList = eventManager.eventList
                (eventListView.adapter as EventListAdapter).notifyDataSetChanged()
            }
            true
        }


        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                // 検索キーが押下された
                Log.d(TAG, "submit text: $text")
                return false
            }

            override fun onQueryTextChange(text: String?): Boolean {
                // テキストが変更された
                Log.d(TAG, "change text: $text")
                val adapter = eventListView.adapter as EventListAdapter
                if (text != null) {
                    val list = eventManager.eventList.filter {
                        it.title.contains(text)
                    }
                    adapter.eventList = list.toMutableList()
                    adapter.notifyDataSetChanged()
                }
                return false
            }

        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            var uri: Uri? = null
            if (data != null) {
                uri = data.data

                val bmp: Bitmap = getBitmapFromUri(uri)
                val reBmp = Bitmap.createScaledBitmap(bmp, 240, 240, false)
                val baos = ByteArrayOutputStream()
                reBmp.compress(Bitmap.CompressFormat.JPEG,1,baos)
                val bmparr = baos.toByteArray();
                // TODO ユーザーアイコンセット（任意）
                repository.setUserIcon(bmparr, myuser.id, myuser.token) {
                    Log.d("masaka" ,it)
                    val drw = BitmapDrawable(reBmp)
                    (activity as AppCompatActivity).supportActionBar?.setIcon(drw)
                }
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val parcelFileDescriptor: ParcelFileDescriptor = getContext()!!.getContentResolver().openFileDescriptor(uri, "r")
        val fileDescriptor: FileDescriptor = parcelFileDescriptor.getFileDescriptor()
        val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image
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
}


