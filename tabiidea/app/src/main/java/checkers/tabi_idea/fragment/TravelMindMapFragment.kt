package checkers.tabi_idea.fragment


import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.TextViewCompat
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.*
import checkers.tabi_idea.R
import checkers.tabi_idea.activity.MainActivity
import checkers.tabi_idea.custom.view.CircularTextView
import checkers.tabi_idea.custom.view.DrawingLinesCanvasView
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.MindMapObject
import kotlinx.android.synthetic.main.fragment_travel_mind_map.*


class TravelMindMapFragment : Fragment() {

    private var textViewList = mutableListOf<CircularTextView>()
    private var drawingLinesCanvasView: DrawingLinesCanvasView? = null
    private var event: Event? = null
    private var mindMapObjectList = mutableListOf<MindMapObject>()

    var layoutWidth = 0f
    var layoutHeight = 0f

    fun add(mindMapObject: MindMapObject) {
        val textView = CircularTextView(context!!)
        textView.text = mindMapObject.text
        textView.setTextColor(Color.WHITE)
        textView.strokeWidth = 3f
        textView.strokeColor = Color.parseColor("#ffffff")
        textView.solidColor = Color.parseColor("#00CED1")
        textView.setPositionXByCenterPositionX(mindMapObject.positionX * layoutWidth)
        textView.setPositionYByCenterPositionY(mindMapObject.positionY * layoutHeight)
        textView.gravity = Gravity.CENTER
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(textView, 10, 30, 2, TypedValue.COMPLEX_UNIT_SP)
        textViewList.add(mindMapObject.viewIndex, textView)
    }

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
        super.onViewCreated(view, savedInstanceState)

        //あまりよくなさそう
        layoutWidth = (activity as MainActivity).layoutWidth
        layoutHeight = (activity as MainActivity).layoutHeight

        drawingLinesCanvasView = DrawingLinesCanvasView(context!!)
        drawingLinesCanvasView?.layoutWidth = layoutWidth
        drawingLinesCanvasView?.layoutHeight = layoutHeight
        if (event != null) {
            drawingLinesCanvasView?.mindMapObjectList = event!!.mindMapObjectList
            // textViewListに追加
            event!!.mindMapObjectList.forEach {
                add(it)
            }
        }

        mindMapConstraintLayout.centerX = layoutWidth / 2
        mindMapConstraintLayout.centerY = layoutHeight / 2
        mindMapConstraintLayout.addView(drawingLinesCanvasView)
        textViewList.forEach {
            mindMapConstraintLayout.addView(it)
        }


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                (activity as AppCompatActivity).supportFragmentManager.popBackStack()
            }

        }
        return super.onOptionsItemSelected(item)
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
