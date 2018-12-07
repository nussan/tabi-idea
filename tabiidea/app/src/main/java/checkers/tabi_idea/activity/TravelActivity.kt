package checkers.tabi_idea.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.text.format.DateFormat
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import checkers.tabi_idea.R
import checkers.tabi_idea.adapter.EventListAdapter
import checkers.tabi_idea.custom.view.RoundRectTextView
import checkers.tabi_idea.custom.view.ZoomableLayout
import checkers.tabi_idea.data.*
import checkers.tabi_idea.fragment.*
import checkers.tabi_idea.data.Category
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.MindMapObject
import checkers.tabi_idea.data.User
import checkers.tabi_idea.fragment.CategoryListFragment
import checkers.tabi_idea.fragment.EventListFragment
import checkers.tabi_idea.fragment.GroupingResultFragment
import checkers.tabi_idea.fragment.TravelMindMapFragment
import checkers.tabi_idea.provider.Repository
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlinx.android.synthetic.main.activity_travel.*
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.util.*


class TravelActivity : AppCompatActivity(),
        ColorPickerDialogListener,
        CategoryListFragment.OnFragmentInteractionListener,
        DatePickerDialog.OnDateSetListener,
        ViewPager.OnPageChangeListener {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private lateinit var mUser: User
    private lateinit var mEvent: Event
    private lateinit var mCategoryList: MutableList<Category>
    private lateinit var mRepository: Repository
    private var map: Map<String, MindMapObject> = mutableMapOf()
    private var dateMap: Map<String, EventDateSet> = mutableMapOf()

    private fun contactFirebaseOfmmoMap() {
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("TravelActivity", "onChildAdded:" + dataSnapshot.key!!)

                val key = dataSnapshot.key!!
                val mmo = dataSnapshot.getValue(MindMapObject::class.java)!!
                map = map.minus(key)
                map = map.plus(key to mmo)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("TravelActivity", "onChildChanged:" + dataSnapshot.key!!)

                val key = dataSnapshot.key!!
                val mmo = dataSnapshot.getValue(MindMapObject::class.java)!!
                map = map.plus(key to mmo)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d("TravelActivity", "onChildRemoved:" + dataSnapshot.key!!)
                map = map.minus(dataSnapshot.key!!)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("TravelActivity", "onChildMoved:" + dataSnapshot.key!!)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TravelActivity", "postComments:onCancelled", databaseError.toException())
            }
        }
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference(mEvent.id.toString())
        ref.child("mmoMaps").addChildEventListener(childEventListener)
    }

    private fun contactFirebaseOfeventDate() {
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("TravelActivity", "onChildAdded:" + dataSnapshot.key!!)

                val key = dataSnapshot.key!!
                val mdateMap = dataSnapshot.getValue(EventDateSet::class.java)!!
                dateMap = dateMap.minus(key)
                dateMap = dateMap.plus(key to mdateMap)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("TravelActivity", "onChildChanged:" + dataSnapshot.key!!)

                val key = dataSnapshot.key!!
                val mdateMap = dataSnapshot.getValue(EventDateSet::class.java)!!
                dateMap = dateMap.plus(key to mdateMap)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d("TravelActivity", "onChildRemoved:" + dataSnapshot.key!!)
                dateMap.minus(dataSnapshot.key)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("TravelActivity", "onChildMoved:" + dataSnapshot.key!!)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TravelActivity", "postComments:onCancelled", databaseError.toException())
            }
        }
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference(mEvent.id.toString())
        ref.child("dates").addChildEventListener(childEventListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel)

        mUser = intent.getParcelableExtra("user")
        mEvent = intent.getParcelableExtra("event")
        mCategoryList = intent.getParcelableArrayListExtra<Category>("categoryList") as MutableList<Category>

        setSupportActionBar(toolbar)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter
        container.setOnPageChangeListener(this)

        tabs.addTab(tabs.newTab().setText("マインドマップ"))
        tabs.addTab(tabs.newTab().setText("カテゴリ"))
        tabs.addTab(tabs.newTab().setText("マトメ"))
        tabs.addTab(tabs.newTab().setText("ヒヅケ"))
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        contactFirebaseOfmmoMap()
        contactFirebaseOfeventDate()
    }


    override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View? {
        return super.onCreateView(name, context, attrs)
    }

    override fun onResume() {
        super.onResume()
        mRepository = Repository()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
    }

    override fun onPageScrollStateChanged(state: Int) {
        when (state) {
            ViewPager.SCROLL_STATE_IDLE -> {
                val currentFragment = mSectionsPagerAdapter?.instantiateItem(container, container.currentItem) as? Fragment
                when (currentFragment) {
                    is TravelMindMapFragment -> {
                        currentFragment.setHighlightEnabled(false)
                        mRepository.getCategoryList(mUser.token, mEvent.id) { categoryList ->
                            this.mCategoryList = categoryList
                            currentFragment.updateCategoryList(mCategoryList)
                        }
                    }
                    is GroupingResultFragment -> {
                        currentFragment.update(map, mCategoryList)
                    }

                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mmomenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> finish()
            R.id.mmomenu_invite -> {
                mRepository.createUrl(mUser.token, mUser.id, mEvent.id) {
                    Log.d("masak", it.getValue("url"))
                    AlertDialog.Builder(this).apply {
                        setTitle("招待URLを発行しました")
                        setMessage(it.getValue("url"))
                        setPositiveButton("コピー") { _, _ ->
                            // OKをタップしたときの処理
                            copyToClipboard(context, "", it.getValue("url"))
                            Toast.makeText(context, "コピーしました", Toast.LENGTH_LONG).show()
                        }
                        setNegativeButton("Cancel", null)
                        show()
                    }
                }
            }
            R.id.mmomenu_icon -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                startActivityForResult(intent, 1000)
                // OKが押されるとonActivityResutに処理が移行する
            }
            R.id.mmomenu_nameEdit -> {
                // レイアウトを取得
                val inflater = this.layoutInflater.inflate(R.layout.input_form_normal, null, false)

                // ダイアログ内のテキストエリア
                val inputText: EditText = inflater.findViewById(R.id.inputText)
                inputText.requestFocus()

                // ダイアログの設定
                val inputForm = AlertDialog.Builder(this).apply {
                    setTitle("イベント名の編集")
                    setView(inflater)
                    setPositiveButton("OK") { _, _ ->
                        // OKボタンを押したときの処理
                        val title = mapOf(
                                "title" to "${inputText.text}"
                        )
                        if ("${inputText.text}" != "" && "${inputText.text}".substring(0, 1) != " " && "${inputText.text}".substring(0, 1) != "　") {
                            mRepository.updateEventName(mUser.token, mEvent.id, inputText.text.toString()) { eventTitle ->
                                supportActionBar?.title = eventTitle.getValue("title")
                                mEvent.title = eventTitle.getValue("title")
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
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val parcelFileDescriptor: ParcelFileDescriptor? = contentResolver?.openFileDescriptor(uri, "r")
        val fileDescriptor: FileDescriptor? = parcelFileDescriptor?.fileDescriptor
        val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor?.close()
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            var uri: Uri? = null
            if (data != null) {
                uri = data.data

                val bmp: Bitmap = getBitmapFromUri(uri)
                val reBmp = Bitmap.createScaledBitmap(bmp, 240, 240, false)
                val baos = ByteArrayOutputStream()
                reBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val bmparr = baos.toByteArray()
                mRepository.setEventIcon(bmparr, mEvent!!.id, mUser.token) {
                    val drw = BitmapDrawable(reBmp)
                    supportActionBar?.setIcon(drw)
                }
            }
        }
    }

    //招待ＵＲＬをクリップボードにコピーするメソッド
    private fun copyToClipboard(context: Context, label: String, text: String) {
        // copy to clipboard
        val clipboardManager: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.primaryClip = ClipData.newPlainText(label, text)
    }

    override fun onDialogDismissed(dialogId: Int) {
        Log.d(TravelActivity.TAG, "onDialogDismissed() called with: dialogId = [$dialogId]")
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        Log.d(TravelActivity.TAG, "onColorSelected() called with: dialogId = [$dialogId], color = [$color]")

//        val currentFragment = supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.container + ":" + container.currentItem)
        val currentFragment = mSectionsPagerAdapter?.instantiateItem(container, container.currentItem) as? Fragment
        Log.d(TAG, "android:switcher:" + R.id.container + ":" + container.currentItem)
        when (dialogId) {
            TravelActivity.DIALOG_ID -> {
                val color = Integer.toHexString(color).toUpperCase().substring(2)
                (currentFragment as? CategoryListFragment)?.changeColor("#$color")
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference(mEvent.id.toString())

        var currentDate = Calendar.getInstance()
        currentDate.set(year, month, dayOfMonth)

        val mdate: String = DateFormat.format("yyyy年 MM月 dd日", currentDate.time).toString()

        if (dateMap.flatMap { listOf(it.value.text) }.contains(mdate)) {
            Toast.makeText(this, "既に存在しています。", Toast.LENGTH_SHORT).show()
            return
        }

        val key = ref.child("dates").push().getKey()
        val mEventDateSet: EventDateSet = EventDateSet(mdate, 1, mutableListOf<String>(mUser!!.name), mutableListOf<Int>(mUser!!.id))
        if (key != null) {
            ref.child("dates").child(key).setValue(mEventDateSet)
        }
    }

    override fun onCategoryChanged(position: Int, category: Category) {
//        mCategoryList[position].name = category.name
//        mCategoryList[position].color = category.color
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                TRAVEL_MIND_MAP -> {
                    container?.requestDisallowInterceptTouchEvent(true)
                    TravelMindMapFragment.newInstance(mEvent, mCategoryList, mUser)
                }
                CATEGORY_LIST -> {
                    container?.requestDisallowInterceptTouchEvent(false)
                    CategoryListFragment.newInstance(mCategoryList, mUser, mEvent)
                }
                GROUPING_RESULT -> {
                    container?.requestDisallowInterceptTouchEvent(false)
                    GroupingResultFragment.newInstance(mEvent.id, map, mCategoryList)
                }
                TRAVEL_CALENDAR -> {
                    container?.requestDisallowInterceptTouchEvent(false)
                    newCalendarFragment(mEvent.id, dateMap, mUser)
                    //CategoryListFragment.newInstance(mCategoryList, mUser)
                }
                else -> TravelMindMapFragment.newInstance(mEvent, mCategoryList, mUser)
            }
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

        override fun getCount(): Int {
            return 4
        }
    }

    companion object {
        private const val TRAVEL_MIND_MAP = 0
        private const val CATEGORY_LIST = 1
        private const val GROUPING_RESULT = 2
        private const val TRAVEL_CALENDAR = 3

        private const val DIALOG_ID = 0
        private const val TAG = "TravelActivity"
    }
}

