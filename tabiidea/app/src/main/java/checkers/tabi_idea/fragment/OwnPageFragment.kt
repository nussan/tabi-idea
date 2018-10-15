package checkers.tabi_idea.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import checkers.tabi_idea.R
import checkers.tabi_idea.data.User
import checkers.tabi_idea.provider.Repository
import kotlinx.android.synthetic.main.fragment_own_page.*

class OwnPageFragment : Fragment() {

    private var user: User? = null
    private val repository = Repository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getParcelable("userKey")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_own_page, container, false)
        //TODO ユーザ情報を取得してユーザ名、画像を表示する
        (activity as AppCompatActivity).supportActionBar?.title = user?.name
        (activity as AppCompatActivity).supportActionBar?.setLogo(android.R.drawable.sym_def_app_icon)
        (activity as AppCompatActivity).supportActionBar?.setDisplayUseLogoEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = mutableListOf("イベント", "フレンド", "設定")
        listView.adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, list)
        listView.setOnItemClickListener { parent: AdapterView<*>, view: View?, position: Int, id: Long ->
            when (list[position]) {
                "イベント" -> repository.getEventList(user!!.id){
                    (activity as AppCompatActivity)
                            .supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.container, EventListFragment.newInstance(user!!.id,it))
                            .addToBackStack(null)
                            .commit()
                }

                "フレンド" ->
                    (activity as AppCompatActivity)
                            .supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.container, FriendListFragment.newInstance())
                            .addToBackStack(null)
                            .commit()
//                "設定" ->
//                    (activity as AppCompatActivity)
//                            .supportFragmentManager
//                            .beginTransaction()
//                            .replace(R.id.container, SettingFragment.newInstance())
//                            .addToBackStack(null)
//                            .commit()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(user: User) =
                OwnPageFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("userKey", user)
                    }
                }
    }
}
