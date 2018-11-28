package checkers.tabi_idea.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import checkers.tabi_idea.R
import checkers.tabi_idea.adapter.CategoryViewDataAdapter
import checkers.tabi_idea.data.Category
import kotlinx.android.synthetic.main.fragment_category_list.*

class CategoryListFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        category_recycler_view.setHasFixedSize(true)
        category_recycler_view.layoutManager = LinearLayoutManager(context)
        category_recycler_view.adapter = CategoryViewDataAdapter(listOf(Category("行先"), Category("宿泊")))
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                CategoryListFragment().apply {
                    arguments = Bundle().apply {}
                }
    }
}
