package checkers.tabi_idea.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import checkers.tabi_idea.R
import checkers.tabi_idea.data.Category
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.MindMapObject
import checkers.tabi_idea.data.User
import checkers.tabi_idea.fragment.CategoryListFragment
import checkers.tabi_idea.fragment.GroupingResultFragment
import checkers.tabi_idea.fragment.TravelMindMapFragment
import checkers.tabi_idea.fragment.newGroupingResultFragment
import checkers.tabi_idea.provider.Repository
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.FirebaseDatabase
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlinx.android.synthetic.main.activity_travel.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ChildEventListener
import org.w3c.dom.Comment


class TravelActivity : AppCompatActivity(), ColorPickerDialogListener{

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private lateinit var mUser: User
    private lateinit var mEvent: Event
    private lateinit var mCategoryList: MutableList<Category>
    private lateinit var mRepository: Repository
    private var map: Map<String, MindMapObject> = mutableMapOf()
    private var newMap: Map<String, MindMapObject> = mutableMapOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel)

        mUser = intent.getParcelableExtra("user")
        mEvent = intent.getParcelableExtra("event")
        mCategoryList = intent.getParcelableArrayListExtra<Category>("categoryList") as MutableList<Category>

        setSupportActionBar(toolbar)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        container.adapter = mSectionsPagerAdapter

        tabs.addTab(tabs.newTab().setText("0"), 0)
        tabs.addTab(tabs.newTab().setText("1"), 0)
        tabs.addTab(tabs.newTab().setText("2"), 0)
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        val childEventListener = object : ChildEventListener{
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
                map.minus(dataSnapshot.key)
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
        ref.addChildEventListener(childEventListener)
    }

    override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View? {
        return super.onCreateView(name, context, attrs)
    }

    override fun onResume() {
        super.onResume()
        mRepository = Repository()
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
                mRepository.createUrl(mUser.token, mUser.id, mEvent!!.id) {
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
        }

        return super.onOptionsItemSelected(item)
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

        val currentFragment = supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.container + ":" + container.currentItem)
        when (dialogId) {
            TravelActivity.DIALOG_ID -> {
                val color = Integer.toHexString(color).toUpperCase().substring(2)
                (currentFragment as? CategoryListFragment)?.changeColor("#$color")
            }
        }
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            newMap = map
            return when (position) {
                TRAVEL_MIND_MAP -> TravelMindMapFragment.newInstance(mEvent, mCategoryList, mUser)
                CATEGORY_LIST -> CategoryListFragment.newInstance(mCategoryList, mUser)
                GROUPING_RESULT -> newGroupingResultFragment(newMap)
                else -> TravelMindMapFragment.newInstance(mEvent, mCategoryList, mUser)
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }

    companion object {
        private const val TRAVEL_MIND_MAP = 0
        private const val CATEGORY_LIST = 1
        private const val GROUPING_RESULT = 2
        private const val DIALOG_ID = 0
        private const val TAG = "TravelActivity"
    }
}
