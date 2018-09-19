package checkers.tabi_idea.fragment


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import checkers.tabi_idea.R
import checkers.tabi_idea.activity.MainActivity
import checkers.tabi_idea.custom.view.DrawingLinesCanvasView
import checkers.tabi_idea.custom.view.EqualWidthHeightTextView
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.MindMapObject
import kotlinx.android.synthetic.main.fragment_travel_mind_map.*


class TravelMindMapFragment : Fragment() {

    private var textViewList = mutableListOf<EqualWidthHeightTextView>()
    private var drawingLinesCanvasView: DrawingLinesCanvasView? = null
    private var event: Event? = null

    var layoutWidth = 0f
    var layoutHeight = 0f

    fun add(text: String, textSize: Float, backGround: Drawable, gravity: Int, textColor: Int, centerPositionX: Float, centerPositionY: Float) {
        val textView = EqualWidthHeightTextView(context!!)
        textView.text = text
        textView.textSize = textSize
        textView.background = backGround
        textView.gravity = gravity
        textView.setTextColor(textColor)
        textView.setPositionXByCenterPositionX(centerPositionX)
        textView.setPositionYByCenterPositionY(centerPositionY)
        textViewList.add(textView)
    }

    fun add(mindMapObject: MindMapObject) {
        val textView = EqualWidthHeightTextView(context!!)
        textView.text = mindMapObject.text
        textView.textSize = 30f

        textView.setPositionXByCenterPositionX(mindMapObject.positionX * layoutWidth)
        textView.setPositionYByCenterPositionY(mindMapObject.positionY * layoutHeight)
        textView.background = Drawable.createFromXml(resources, resources.getXml(R.xml.oval_light_blue_bg))
        textView.gravity = Gravity.CENTER
//        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(textView, 10, 100, 2, TypedValue.COMPLEX_UNIT_SP)
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
