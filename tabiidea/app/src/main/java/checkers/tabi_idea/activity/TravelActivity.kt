package checkers.tabi_idea.activity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import checkers.tabi_idea.R
import checkers.tabi_idea.data.Category
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.User
import checkers.tabi_idea.fragment.CategoryListFragment
import checkers.tabi_idea.fragment.TravelMindMapFragment
import com.google.android.material.tabs.TabLayout
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlinx.android.synthetic.main.activity_travel.*


class TravelActivity : AppCompatActivity(), ColorPickerDialogListener {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private lateinit var mUser: User
    private lateinit var mEvent: Event
    private lateinit var mCategoryList: MutableList<Category>

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
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_travel, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDialogDismissed(dialogId: Int) {
        Log.d(TravelActivity.TAG, "onDialogDismissed() called with: dialogId = [$dialogId]")
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        Log.d(TravelActivity.TAG, "onColorSelected() called with: dialogId = [$dialogId], color = [$color]")
//        val currentFragment = supportFragmentManager.findFragmentById(R.id.container)

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
            return when (position) {
                TRAVEL_MIND_MAP -> TravelMindMapFragment.newInstance(mEvent, mCategoryList, mUser)
                CATEGORY_LIST -> CategoryListFragment.newInstance(mCategoryList, mUser)
//                RESULT -> TODO まとめ
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
        private const val RESULT = 2

        private const val DIALOG_ID = 0
        private const val TAG = "TravelActivity"
    }
}
