package checkers.tabi_idea.fragment


import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.support.customtabs.R.id.image
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import android.widget.Toolbar
import checkers.tabi_idea.R
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.User
import checkers.tabi_idea.manager.EventManager
import checkers.tabi_idea.provider.FirebaseApiClient
import checkers.tabi_idea.provider.Repository
import checkers.tabi_idea.provider.RequestService
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_event_list.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.FileDescriptor
import java.util.*

class EventListFragment : Fragment() {
    private val eventManager = EventManager()
    private var eventId:Int? = null
    private val repository = Repository()
    private var fireBaseApiClient:FirebaseApiClient? = null
    private lateinit var myuser : User
    private var sortNewOld = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            myuser = it.getParcelable("user")
            eventManager.eventList = it.getParcelableArrayList<Event>("eventListKey") as MutableList<Event>
        }
        if (activity?.intent?.action != null && activity?.intent?.action == Intent.ACTION_VIEW) {
            if (activity?.intent?.data != null) {
                val url = activity?.intent?.data!!.buildUpon().scheme("http").build().toString()
                getEvent(url)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.title = myuser.name
        (activity as AppCompatActivity).supportActionBar?.setDisplayUseLogoEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
//        TODO repository.getUserIcon(myuser.id,myuser.token){
//            val drw = BitmapDrawable(it)
//            (activity as AppCompatActivity).supportActionBar?.setIcon(drw)
//        }
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
        eventListView.adapter = EventListAdapter(context, eventManager.eventList)
        eventListView.layoutManager = GridLayoutManager(context, 1)

        val swipHandler = object : SwipeToDeleteCallback(context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                val adapter = eventListView.adapter as EventListAdapter
                viewHolder?.let {
                    eventId = adapter.eventList[it.adapterPosition].id
                    adapter.removeAt(it.adapterPosition)
                    eventManager.eventList = adapter.eventList
                }
                repository.deleteEvent(myuser.token,myuser.id,eventId!!){
                    Toast.makeText(context,it.get("title")+"が削除されました",Toast.LENGTH_SHORT).show()
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipHandler)
        itemTouchHelper.attachToRecyclerView(eventListView)

        (eventListView.adapter as EventListAdapter).setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                Log.d(javaClass.simpleName, "onTouch!!")
                val position = eventListView.getChildAdapterPosition(view)
                Log.d("masaka", (eventListView.adapter as EventListAdapter).eventList[position].title)
                activity?.supportFragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.container, TravelMindMapFragment.newInstance((eventListView.adapter as EventListAdapter).eventList[position]))
                        ?.addToBackStack(null)
                        ?.commit()
            }
        })

        fab.setOnClickListener {

            val adapter = eventListView.adapter as EventListAdapter
            adapter.eventList = eventManager.eventList //検索機能を実行したときの更新
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

                    repository.addEvent(myuser.token,myuser.id, title) {event ->
                        eventId = event.id
                        Log.d("tubasa", event.id.toString())
                        fireBaseApiClient = FirebaseApiClient(eventId.toString())
                        fireBaseApiClient!!.addEventToFb()
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


    //EventListFragmentでツールバーにメニュー機能を追加する
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.actions, menu)

        val item: MenuItem = menu.findItem(R.id.action_name_edit)
        item.setOnMenuItemClickListener {
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
                    repository.editUser(myuser.token,myuser.id, name){name ->
                        // コールバックの操作
                        (activity as AppCompatActivity).supportActionBar?.title = name.get("name")
                        myuser.name = name.get("name")!!
                    }

                }
                setNegativeButton("Cancel", null)
            }.create()

            // ダイアログ表示と同時にキーボードを表示
            inputForm.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            inputForm.show()

            true
        }

        val icon : MenuItem = menu.findItem(R.id.icon_change)
        icon.setOnMenuItemClickListener{
            val intent : Intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.setType("image/*")
            startActivityForResult(intent,1000)
            // OKが押されるとonActivityResutに処理が移行する
            true
        }

        val sort : MenuItem = menu.findItem(R.id.sort)
        sort.setOnMenuItemClickListener{
            //このソート手法は初期のソートを再現できなくする機能でもある
            //そこはこだわらなくてよいと判断
            if(sortNewOld){
                //ソートを新しいイベントが一番上に来るようにする
                eventManager.eventList.sortedBy{
                    it.title.length
                    Log.d("masaka",it.title)
                }

                (eventListView.adapter as EventListAdapter).eventList = eventManager.eventList
                (eventListView.adapter as EventListAdapter).notifyDataSetChanged()
            }else {
                //ソートを古いイベントが一番上に来るようにする
                eventManager.eventList.sortedBy{
                    it.id * -1
                }
                (eventListView.adapter as EventListAdapter).eventList = eventManager.eventList
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if(requestCode == 1000 && resultCode == RESULT_OK) {
            var uri : Uri? = null
            if(data != null) {
                uri = data.data

                val bmp : Bitmap = getBitmapFromUri(uri)
                val reBmp = Bitmap.createScaledBitmap(bmp,240,240,false)
                repository.setUserIcon(reBmp,myuser.id,myuser.token){
                    val drw = BitmapDrawable(it)
                    (activity as AppCompatActivity).supportActionBar?.setIcon(drw)
                }
            }
        }
    }

    private fun getBitmapFromUri(uri : Uri) : Bitmap{
        val parcelFileDescriptor : ParcelFileDescriptor = getContext()!!.getContentResolver().openFileDescriptor(uri, "r")
        val fileDescriptor : FileDescriptor = parcelFileDescriptor.getFileDescriptor()
        val image : Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image
    }

    fun getEvent(url: String) {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
                .baseUrl("http://bit.ly/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
        val requestService = retrofit.create(RequestService::class.java)
        val url = url.replace("http://bit.ly/", "")
        Log.d("EventListFragment", url)
        requestService.getEvent(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> repository.joinEvent(myuser.token,myuser!!.id, res.id.toString()) },
                        { err -> Log.d("EventListFragment", err.toString()) }
                )
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
