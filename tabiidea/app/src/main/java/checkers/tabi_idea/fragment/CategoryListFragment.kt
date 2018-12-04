package checkers.tabi_idea.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import checkers.tabi_idea.R
import checkers.tabi_idea.adapter.CategoryListAdapter
import checkers.tabi_idea.data.Category
import checkers.tabi_idea.data.User
import checkers.tabi_idea.provider.Repository
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import kotlinx.android.synthetic.main.fragment_category_list.*

class CategoryListFragment : Fragment() {
    private var categoryList = listOf<Category>()
    private lateinit var user: User
    private val repository = Repository()
    private var targetPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryList = it.getParcelableArrayList<Category>("categoryList") as List<Category>
            user = it.getParcelable("user")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        category_recycler_view.setHasFixedSize(true)
        category_recycler_view.layoutManager = LinearLayoutManager(context)
        val adapter = CategoryListAdapter(context, categoryList)
        adapter.listener = object : CategoryListAdapter.OnClickListener {
            override fun onClick(position: Int) {
                targetPosition = position

                ColorPickerDialog
                        .newBuilder()
                        .setColor(Color.parseColor(categoryList[position].color))
                        .show(context as FragmentActivity?)
            }

        }
        category_recycler_view.adapter = adapter
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                activity?.supportFragmentManager?.popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun changeColor(colorString: String) {
        if (targetPosition == -1) return
        val after = Category(categoryList[targetPosition].name, colorString)
        Log.d("CategoryListFragment", categoryList[targetPosition].id.toString())
        repository.updateCategory(user.token, categoryList[targetPosition].id, after) { after ->
            categoryList[targetPosition].color = after.color
            category_recycler_view.adapter?.notifyItemChanged(targetPosition)
            targetPosition = -1
        }

    }


    companion object {
        @JvmStatic
        fun newInstance(list: List<Category>, user: User) =
                CategoryListFragment().apply {
                    arguments = Bundle().apply {
                        putParcelableArrayList("categoryList", ArrayList(list))
                        putParcelable("user", user)
                    }
                }
    }
}
