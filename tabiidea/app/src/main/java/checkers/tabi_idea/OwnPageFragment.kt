package checkers.tabi_idea

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_own_page.*


class OwnPageFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_own_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = mutableListOf("イベント", "フレンド", "設定")
        val arrayAdapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, list)
        listView.adapter = arrayAdapter
    }
    companion object {
        @JvmStatic
        fun newInstance() =
                OwnPageFragment().apply {}
    }
}
