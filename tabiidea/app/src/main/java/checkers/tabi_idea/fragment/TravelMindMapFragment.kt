package checkers.tabi_idea.fragment


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.TextViewCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.EditText
import android.widget.Toast
import checkers.tabi_idea.R
import checkers.tabi_idea.activity.MainActivity
import checkers.tabi_idea.custom.view.CustomBottomSheetDialogFragment
import checkers.tabi_idea.custom.view.RoundRectTextView
import checkers.tabi_idea.custom.view.ZoomableLayout
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.MindMapObject
import checkers.tabi_idea.provider.Repository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_travel_mind_map.*


class TravelMindMapFragment :
        Fragment(),
        MainActivity.IOnFocusListenable,
        CustomBottomSheetDialogFragment.Listener,
        ZoomableLayout.LineDrawer {

    private val repository = Repository()
    private var event: Event? = null
    private var mindMapObjectList: MutableList<MindMapObject> = mutableListOf()

    var layoutWidth = 0f
    var layoutHeight = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            event = it.getParcelable("eventKey")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_travel_mind_map, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = event?.title
        (activity as AppCompatActivity).supportActionBar?.setDisplayUseLogoEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(this.javaClass.simpleName, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        layoutWidth = (activity as MainActivity).layoutWidth
        layoutHeight = (activity as MainActivity).layoutHeight
        mindMapConstraintLayout.centerX = layoutWidth / 2
        mindMapConstraintLayout.centerY = layoutHeight / 2

        val callback = fun(it: Collection<MindMapObject>) {

            if(context == null) {
                Log.d(javaClass.simpleName, "context is null")
                return
            }

            val offset = mindMapObjectList.size

            val ml = it as MutableList<MindMapObject>
            if (offset == 0) {
                mindMapObjectList = ml
                mindMapObjectList.forEach {
                    val v = mindMapObjectToTextView(context, it)
                    mindMapConstraintLayout.addView(v, it.viewIndex)

                    v.setOnClickListener { vv ->
                        val bottomSheetDialog = CustomBottomSheetDialogFragment.newInstance(vv.id)
                        bottomSheetDialog.show(childFragmentManager, bottomSheetDialog.tag)
                    }
                }
            } else {
                for (i in offset until ml.size) {
                    mindMapObjectList.add(ml[i].viewIndex, ml[i])
                    val v = mindMapObjectToTextView(context, ml[i])
                    mindMapConstraintLayout.addView(v, ml[i].viewIndex)

                    v.setOnClickListener { vv ->
                        val bottomSheetDialog = CustomBottomSheetDialogFragment.newInstance(vv.id)
                        bottomSheetDialog.show(childFragmentManager, bottomSheetDialog.tag)
                    }
                }
            }
        }

        repository.getMmo(event?.id.toString(), callback)
        mindMapConstraintLayout.lineDrawer = this
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                (activity as AppCompatActivity).supportFragmentManager.popBackStack()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            layoutWidth = (activity as MainActivity).layoutWidth
            layoutHeight = (activity as MainActivity).layoutHeight
            mindMapConstraintLayout.centerX = layoutWidth / 2
            mindMapConstraintLayout.centerY = layoutHeight / 2
        }
    }

    override fun onAddClicked(position: Int) {
        Log.d(javaClass.simpleName, "onAddClicked")
        // レイアウトを取得
        val inflater = this.layoutInflater.inflate(R.layout.input_form, null, false)

        // ダイアログ内のテキストエリア
        val inputText: EditText = inflater.findViewById(R.id.inputText)
        inputText.requestFocus()

        // ダイアログの設定
        val inputForm = AlertDialog.Builder(context!!).apply {
            setTitle("新しいアイデア")
            setView(inflater)
            setPositiveButton("OK") { _, _ ->

                val newId = mindMapObjectList!![mindMapObjectList!!.lastIndex].viewIndex + 1
                val parent = mindMapObjectList!![position]
                val mmo = MindMapObject(
                        newId,
                        "${inputText.text}",
                        -200f,
                        -200f,
                        parent.viewIndex
                )
                repository.addMmo(event!!.id.toString(), mmo) //"1"は追加先event.id
//                mindMapObjectList!!.add(newId, mmo)

                val view = mindMapObjectToTextView(context, mmo)
                view.setOnClickListener { v ->
                    val bottomSheetDialog = CustomBottomSheetDialogFragment.newInstance(v.id)
                    bottomSheetDialog.show(childFragmentManager, bottomSheetDialog.tag)
                }
//                mindMapConstraintLayout.addView(view, mmo.viewIndex)
                mindMapConstraintLayout.invalidate()
            }
            setNegativeButton("Cancel", null)
        }.create()

        // ダイアログ表示と同時にキーボードを表示
        inputForm.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        inputForm.show()
    }

    override fun onDeleteClicked(position: Int) {
    }

    override fun onEditClicked(position: Int) {
    }

    override fun drawLines(canvas: Canvas?, scale: Float) {
        val paint = Paint()
        paint.setARGB(255, 0, 0, 0)
        paint.strokeWidth = 5f

        canvas?.scale(scale, scale, mindMapConstraintLayout.centerX, mindMapConstraintLayout.centerY)
        mindMapObjectList!!.forEach {
            val child = mindMapConstraintLayout.getChildAt(it.viewIndex)
            val parent = mindMapConstraintLayout.getChildAt(it.parent)
            canvas?.drawLine(
                    child.x + child.width / 2,
                    child.y + child.height / 2,
                    parent.x + parent.width / 2,
                    parent.y + parent.height / 2,
                    paint
            )
        }
        canvas?.scale(1 / scale, 1 / scale, mindMapConstraintLayout.centerX, mindMapConstraintLayout.centerY)
    }

    private fun mindMapObjectToTextView(context: Context?, mindMapObject: MindMapObject): RoundRectTextView {
        val textView = RoundRectTextView(context)
        textView.id = mindMapObject.viewIndex
        textView.gravity = Gravity.CENTER
        textView.text = mindMapObject.text
        textView.setTextColor(Color.WHITE)
        setTextViewPosition(textView, mindMapObject)
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                textView,
                10,
                30,
                2,
                TypedValue.COMPLEX_UNIT_SP)
        return textView
    }

    private fun setTextViewPosition(textView: RoundRectTextView, mmo: MindMapObject) {
        if (mindMapObjectList == null) {
            Log.d(javaClass.simpleName, "mindMapObjectList is null")
            return
        }

        val parent = mindMapObjectList!![mmo.parent]

        textView.setPositionXByCenterPositionX(
                if (mmo.viewIndex == parent.viewIndex) mmo.positionX
                else parent.positionX - mmo.positionX)
        textView.setPositionYByCenterPositionY(
                if (mmo.viewIndex == parent.viewIndex) mmo.positionY
                else parent.positionX - mmo.positionY)
    }

    companion object {
        @JvmStatic
        fun newInstance(event: Event) = TravelMindMapFragment().apply {
            arguments = Bundle().apply {
                putParcelable("eventKey", event)
            }
        }
    }
}
