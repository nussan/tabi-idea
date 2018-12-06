package checkers.tabi_idea.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import checkers.tabi_idea.R
import checkers.tabi_idea.R.id.tvName
import checkers.tabi_idea.adapter.CategoryListAdapter
import checkers.tabi_idea.adapter.EventListAdapter
import checkers.tabi_idea.data.Category
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.User
import checkers.tabi_idea.provider.FirebaseApiClient
import checkers.tabi_idea.provider.Repository
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import kotlinx.android.synthetic.main.fragment_category_list.*
import kotlinx.android.synthetic.main.fragment_event_list.*

class CategoryListFragment : Fragment() {
    private var categoryList = listOf<Category>()
    private lateinit var user: User
    private lateinit var event:Event
    private val repository = Repository()
    private var targetPosition = -1
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryList = it.getParcelableArrayList<Category>("categoryList") as List<Category>
            user = it.getParcelable("user")
            event = it.getParcelable("event")
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
        adapter.imageViewListener = object : CategoryListAdapter.OnClickListener {
            override fun onClick(position: Int) {
                targetPosition = position

                ColorPickerDialog
                        .newBuilder()
                        .setColor(Color.parseColor(categoryList[position].color))
                        .show(context as FragmentActivity?)
            }

        }

        button4.setOnClickListener {
            eventListView?.isClickable = false
            it.isEnabled = false
            // レイアウトを取得
            val inflater = this.layoutInflater.inflate(R.layout.input_form_normal, null, false)

            // ダイアログ内のテキストエリア
            val inputText: EditText = inflater.findViewById(R.id.inputText)
            inputText.requestFocus()

            // ダイアログの設定
            val inputForm = AlertDialog.Builder(context!!).apply {
                setTitle("新しいカテゴリ")
                setView(inflater)
                setPositiveButton("OK") { _, _ ->
                    // OKボタンを押したときの処理
                    val name = mapOf(
                            "name" to "${inputText.text}"
                    )
                    if ("${inputText.text}" != "" && "${inputText.text}".substring(0, 1) != " " && "${inputText.text}".substring(0, 1) != "　") {
                        val category = Category(name["name"]!!, categoryList[targetPosition+2].color)
                        repository.addCategory(user.token,event.id,category){
                            categoryList
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

            it.isEnabled = true
        }

        adapter.textViewListener = object : CategoryListAdapter.OnClickListener {
            override fun onClick(position: Int) {
                targetPosition = position

                // レイアウトを取得
                val inflater = this@CategoryListFragment.layoutInflater.inflate(R.layout.input_form_normal, null, false)

                // ダイアログ内のテキストエリア
                val inputText: EditText = inflater.findViewById(R.id.inputText)
                inputText.requestFocus()

                // ダイアログの設定
                val inputForm = AlertDialog.Builder(context!!).apply {
                    setTitle("カテゴリの編集")
                    setView(inflater)
                    setPositiveButton("OK") { _, _ ->
                        // OKボタンを押したときの処理
                        val name = mapOf(
                                "name" to "${inputText.text}"
                        )
                        if ("${inputText.text}" != "" && "${inputText.text}".substring(0, 1) != " " && "${inputText.text}".substring(0, 1) != "　") {
                            val after = Category(name["name"]!!, categoryList[targetPosition].color)
                            repository.updateCategory(user.token, categoryList[targetPosition].id, after) { after ->
                                categoryList[targetPosition].name = after.name
                                category_recycler_view.adapter?.notifyItemChanged(targetPosition)
                                listener?.onCategoryChanged(targetPosition, categoryList[targetPosition])
                                targetPosition = -1
                                // TODO

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
        category_recycler_view.adapter = adapter

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
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
            listener?.onCategoryChanged(targetPosition, categoryList[targetPosition])
            targetPosition = -1
//            categoryList.forEach { category ->
//                if (mindMapObject.type == category.name)
//                    textView.setBackgroundColor(Color.parseColor(category.color))
//
//            }
        }

    }

    interface OnFragmentInteractionListener {
        fun onCategoryChanged(position: Int, category: Category)
    }

    companion object {
        @JvmStatic
        fun newInstance(list: List<Category>, user: User,event: Event) =
                CategoryListFragment().apply {
                    arguments = Bundle().apply {
                        putParcelableArrayList("categoryList", ArrayList(list))
                        putParcelable("user", user)
                        putParcelable("event",event)
                    }
                }
    }
}
