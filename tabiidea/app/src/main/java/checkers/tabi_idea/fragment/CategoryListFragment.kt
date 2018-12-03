package checkers.tabi_idea.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
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
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import kotlinx.android.synthetic.main.fragment_category_list.*

class CategoryListFragment : Fragment() {
    var categoryList = listOf<Category>()
    var targetPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryList = it.getParcelableArrayList<Category>("categoryList") as List<Category>
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
        categoryList[targetPosition].color = colorString
        category_recycler_view.adapter?.notifyItemChanged(targetPosition)
        targetPosition = -1
    }


    companion object {
        @JvmStatic
        fun newInstance(list: List<Category>) =
                CategoryListFragment().apply {
                    arguments = Bundle().apply {
                        putParcelableArrayList("categoryList", ArrayList(list))
                    }
                }
    }
}
