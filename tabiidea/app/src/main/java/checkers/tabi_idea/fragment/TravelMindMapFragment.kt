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
import kotlinx.android.synthetic.main.fragment_travel_mind_map.*


class TravelMindMapFragment : Fragment(), MainActivity.IOnFocusListenable {

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
            event!!.mindMapObjectList.forEachIndexed { index, mind ->
                setTextViewPosition(textViewList[index], mind)
                textViewList[index].invalidate()
            }
            drawingLinesCanvasView?.layoutWidth = layoutWidth
            drawingLinesCanvasView?.layoutHeight = layoutHeight
            drawingLinesCanvasView?.invalidate()
        }
    }

    private fun prepareView() {
        if (event == null) {
            return
        }

        prepareCanvas(context!!, layoutWidth, layoutHeight)
        setMMOListToCanvasView(event!!.mindMapObjectList)
        // textViewListに追加
        event!!.mindMapObjectList.forEachIndexed {index, it ->
            textViewList.add(index, mindMapObjectToTextView(context!!, it))
        }

        mindMapConstraintLayout.addView(drawingLinesCanvasView)
        textViewList.forEach { view ->
            if (view.parent == null)
                mindMapConstraintLayout.addView(view)
            view.setOnClickListener {
                val bottomSheetDialog = CustomBottomSheetDialogFragment.newInstance()
                bottomSheetDialog.show(activity?.supportFragmentManager, bottomSheetDialog.tag)
            }
        }
    }


    private fun prepareCanvas(context: Context, width: Float, height: Float) {
        drawingLinesCanvasView = DrawingLinesCanvasView(context)
        drawingLinesCanvasView?.layoutWidth = width
        drawingLinesCanvasView?.layoutHeight = height
    }

    private fun setMMOListToCanvasView(list: MutableList<MindMapObject>) {
        drawingLinesCanvasView?.mindMapObjectList = list
    }

    private fun mindMapObjectToTextView(context: Context, mindMapObject: MindMapObject): RoundRectTextView {
        val textView = RoundRectTextView(context)
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
