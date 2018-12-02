package checkers.tabi_idea.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import checkers.tabi_idea.R
import checkers.tabi_idea.adapter.CategoryListAdapter
import checkers.tabi_idea.data.Category
import kotlinx.android.synthetic.main.fragment_category_list.*

class CategoryListFragment : Fragment() {
    var categoryList = listOf<Category>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryList = it.getParcelableArrayList("categoryList")
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
        category_recycler_view.adapter = CategoryListAdapter(categoryList)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                activity?.supportFragmentManager?.popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
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
