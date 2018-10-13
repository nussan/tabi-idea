package checkers.tabi_idea.fragment


import android.content.Context
import android.content.DialogInterface
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
import android.widget.ArrayAdapter
import android.widget.EditText
import checkers.tabi_idea.R
import checkers.tabi_idea.activity.MainActivity
import checkers.tabi_idea.custom.view.CustomBottomSheetDialogFragment
import checkers.tabi_idea.custom.view.RoundRectTextView
import checkers.tabi_idea.custom.view.ZoomableLayout
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.MindMapObject
import kotlinx.android.synthetic.main.fragment_event_list.*
import kotlinx.android.synthetic.main.fragment_travel_mind_map.*


class TravelMindMapFragment :
        Fragment(),
        MainActivity.IOnFocusListenable,
        CustomBottomSheetDialogFragment.Listener,
        ZoomableLayout.LineDrawer {


    private var textViewList = mutableListOf<RoundRectTextView>()
    private var event: Event? = null

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
        mindMapConstraintLayout.removeAllViews()
        prepareView()
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
        val inputText : EditText = inflater.findViewById(R.id.inputText)
        inputText.requestFocus()

        // ダイアログの設定
        val inputForm = AlertDialog.Builder(context!!).apply {
            setTitle("新しいアイデア")
            setView(inflater)
            setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                val newId = event!!.mindMapObjectList.lastIndex + 1
                val mmo = MindMapObject(
                        newId,
                        "${inputText.text}",
                        0.5f,
                        0.5f,
                        event!!.mindMapObjectList[position].viewIndex
                )
                event!!.mindMapObjectList.add(mmo)

                val view = mindMapObjectToTextView(context, mmo)
                view.setOnClickListener { v ->
                    val bottomSheetDialog = CustomBottomSheetDialogFragment.newInstance(v.id)
                    bottomSheetDialog.show(childFragmentManager, bottomSheetDialog.tag)
                }
                textViewList.add(view)
                mindMapConstraintLayout.addView(view, mmo.viewIndex)
                mindMapConstraintLayout.invalidate()
            })
            setNegativeButton("Cancel", null)
        }.create()

        // ダイアログ表示と同時にキーボードを表示
        inputForm.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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
        event!!.mindMapObjectList.forEach {
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

    private fun prepareView() {
        if (event == null) {
            return
        }

//        prepareCanvas(context!!)
        // textViewListに追加
        event!!.mindMapObjectList.forEach { it ->
            val view = mindMapObjectToTextView(context!!, it)
            textViewList.add(it.viewIndex, view)

            if (view.parent == null)
                mindMapConstraintLayout.addView(view, it.viewIndex)

            view.setOnClickListener { v ->
                val bottomSheetDialog = CustomBottomSheetDialogFragment.newInstance(v.id)
                bottomSheetDialog.show(childFragmentManager, bottomSheetDialog.tag)
            }
        }
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

    private fun setTextViewPosition(textView: RoundRectTextView, mindMapObject: MindMapObject) {
        textView.setPositionXByCenterPositionX(mindMapObject.positionX * layoutWidth)
        textView.setPositionYByCenterPositionY(mindMapObject.positionY * layoutHeight)
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
