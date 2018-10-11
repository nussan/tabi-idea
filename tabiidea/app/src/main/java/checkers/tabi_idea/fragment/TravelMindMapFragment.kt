package checkers.tabi_idea.fragment


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.TextViewCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.*
import checkers.tabi_idea.R
import checkers.tabi_idea.activity.MainActivity
import checkers.tabi_idea.custom.view.CustomBottomSheetDialogFragment
import checkers.tabi_idea.custom.view.RoundRectTextView
import checkers.tabi_idea.custom.view.DrawingLinesCanvasView
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.MindMapObject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_travel_mind_map.*


class TravelMindMapFragment :
        Fragment(),
        MainActivity.IOnFocusListenable,
        CustomBottomSheetDialogFragment.Listener {
    private var textViewList = mutableListOf<RoundRectTextView>()
    private var drawingLinesCanvasView: DrawingLinesCanvasView? = null
    private var event: Event? = null

    var layoutWidth = 0f
    var layoutHeight = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            event = it.getParcelable("eventKey")
        }
        retainInstance = true
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
            event!!.mindMapObjectList.forEach { mind ->
                setTextViewPosition(textViewList[mind.viewIndex], mind)
                textViewList[mind.viewIndex].invalidate()
            }
            drawingLinesCanvasView?.layoutWidth = layoutWidth
            drawingLinesCanvasView?.layoutHeight = layoutHeight
            drawingLinesCanvasView?.invalidate()
        }
    }

    override fun onAddClicked(position: Int) {
        Log.d(javaClass.simpleName, "onAddClicked")
        val newId = event!!.mindMapObjectList.lastIndex + 1
        val mmo = MindMapObject(
                newId,
                "追加",
                0.8f,
                0.4f,
                mutableListOf()
        )
        event!!.mindMapObjectList.add(mmo)
        event!!.mindMapObjectList[position].children.add(newId)
        val view = mindMapObjectToTextView(context, mmo)
        textViewList.add(view)
        mindMapConstraintLayout.addView(view)

    }

    override fun onDeleteClicked(position: Int) {
    }

    override fun onEditClicked(position: Int) {
    }

    private fun prepareView() {
        if (event == null) {
            return
        }

        prepareCanvas(context!!, layoutWidth, layoutHeight)
        setMMOListToCanvasView(event!!.mindMapObjectList)
        // textViewListに追加
        event!!.mindMapObjectList.forEach { it ->
            textViewList.add(it.viewIndex, mindMapObjectToTextView(context!!, it))
        }

        mindMapConstraintLayout.addView(drawingLinesCanvasView)
        textViewList.forEach { view ->
            if (view.parent == null)
                mindMapConstraintLayout.addView(view)

            view.setOnClickListener {
                val bottomSheetDialog = CustomBottomSheetDialogFragment.newInstance(it.id)
                bottomSheetDialog.show(childFragmentManager, bottomSheetDialog.tag)
            }
        }
    }


    private fun prepareCanvas(context: Context?, width: Float, height: Float) {
        drawingLinesCanvasView = DrawingLinesCanvasView(context)
        drawingLinesCanvasView?.layoutWidth = width
        drawingLinesCanvasView?.layoutHeight = height
    }

    private fun setMMOListToCanvasView(list: MutableList<MindMapObject>) {
        drawingLinesCanvasView?.mindMapObjectList = list
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
