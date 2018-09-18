package checkers.tabi_idea


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import kotlinx.android.synthetic.main.fragment_travel_mind_map.*
import java.util.*


class TravelMindMapFragment : Fragment() {

    private var textViewList = mutableListOf<EqualWidthHeightTextView>()
    private var mindMapObjectList = mutableListOf<MindMapObject>()
    private var drawingLinesCanvasView: DrawingLinesCanvasView? = null
    private var eventTitle = ""

    //TODO viewのサイズをとってくる
    val viewWidth = 1080f
    val viewHeight = 1536f

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

        textView.setPositionXByCenterPositionX(mindMapObject.positionX * viewWidth)
        textView.setPositionYByCenterPositionY(mindMapObject.positionY * viewHeight)
        textView.background = Drawable.createFromXml(resources, resources.getXml(R.xml.oval_light_blue_bg))
        textView.gravity = Gravity.CENTER
//        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(textView, 10, 100, 2, TypedValue.COMPLEX_UNIT_SP)
        textViewList.add(mindMapObject.viewIndex, textView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            eventTitle = it.getString("eventTitleKey")
            mindMapObjectList = it.getParcelableArrayList<MindMapObject>("mindMapObjectKey") as MutableList<MindMapObject>
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_travel_mind_map, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = eventTitle
        (activity as AppCompatActivity).supportActionBar?.setDisplayUseLogoEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
        setHasOptionsMenu(true)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawingLinesCanvasView = DrawingLinesCanvasView(context!!)
        drawingLinesCanvasView?.mindMapObjectList = mindMapObjectList
        mindMapConstraintLayout.addView(drawingLinesCanvasView)

        // textViewListに追加
        mindMapObjectList.forEach {
            add(it)
        }

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
        fun newInstance(eventTitle: String, mindMapObject: MutableList<MindMapObject>) = TravelMindMapFragment().apply {
            arguments = Bundle().apply {
                putString("eventTitleKey", eventTitle)
                putParcelableArrayList("mindMapObjectKey", ArrayList(mindMapObject))
            }
        }
    }
}
