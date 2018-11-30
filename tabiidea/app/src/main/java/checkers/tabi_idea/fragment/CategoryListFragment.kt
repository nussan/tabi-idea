package checkers.tabi_idea.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import checkers.tabi_idea.R
import checkers.tabi_idea.data.Category

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
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        fun newInstance(list: ArrayList<Category>) =
                CategoryListFragment().apply {
                    arguments = Bundle().apply {
                        putParcelableArrayList("categoryList", list)
                    }
                }
    }
}
