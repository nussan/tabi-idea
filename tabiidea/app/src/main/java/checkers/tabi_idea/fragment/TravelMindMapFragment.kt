package checkers.tabi_idea.fragment


import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.widget.TextViewCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import checkers.tabi_idea.R
import checkers.tabi_idea.custom.view.RoundRectTextView
import checkers.tabi_idea.custom.view.ZoomableLayout
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.MindMapObject
import checkers.tabi_idea.provider.Repository
import kotlinx.android.synthetic.main.fragment_travel_mind_map.*


class TravelMindMapFragment :
        Fragment(),
        ZoomableLayout.LineDrawer,
        View.OnDragListener {
    private val repository = Repository()
    private var event: Event? = null
    private var mindMapObjectList: MutableList<MindMapObject> = mutableListOf()
    private var behavior: BottomSheetBehavior<LinearLayout>? = null
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

        val l = coordinatorLayout.findViewById<LinearLayout>(R.id.linear_left)
        val c = coordinatorLayout.findViewById<LinearLayout>(R.id.linear_center)
        val r = coordinatorLayout.findViewById<LinearLayout>(R.id.linear_right)
        l.setOnDragListener(this)
        c.setOnDragListener(this)
        r.setOnDragListener(this)


        behavior = BottomSheetBehavior.from(coordinatorLayout.findViewById(R.id.bottom_sheet))
        behavior?.isHideable = true
        behavior?.state = BottomSheetBehavior.STATE_HIDDEN
        val callback = fun(it: Collection<MindMapObject>) {
            if (context == null) {
                Log.d(javaClass.simpleName, "context is null")
                return
            }

            var view: RoundRectTextView
            val ml = it as MutableList<MindMapObject>
            val offset = mindMapObjectList.size

            for (i in offset until ml.size) {
                mindMapObjectList.add(ml[i].viewIndex, ml[i])
                view = mindMapObjectToTextView(context, ml[i])
                view.tag = ml[i].viewIndex
                mindMapConstraintLayout.addView(view, ml[i])

                view.setOnLongClickListener {
                    behavior?.state = BottomSheetBehavior.STATE_COLLAPSED

                    val item = ClipData.Item(view.tag as? CharSequence)
                    val data = ClipData(view.tag.toString(), arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)
                    it.startDrag(data, View.DragShadowBuilder(it), it, 0)
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

    private fun onAddSelected(position: Int) {
        Log.d(javaClass.simpleName, "onAddSelected")

        val listener = object : ZoomableLayout.TapListener {
            override fun onTap(e: MotionEvent, centerX: Float, centerY: Float, scale: Float) {
                val newId = mindMapObjectList[mindMapObjectList.lastIndex].viewIndex + 1
                val parentId = mindMapObjectList[position].viewIndex
                val parent = mindMapConstraintLayout.getChildAt(parentId)
                val mmo = MindMapObject(
                        newId,
                        "追加",
                        (e.x -  parent.x - parent.width / 2),
                        (e.y -  parent.y - parent.height / 2),
                        parentId
                )
                Log.d("add", "${parent.x}, ${parent.y}, ${mmo.positionX}, ${mmo.positionY}")
                repository.addMmo(event!!.id.toString(), mmo) //"1"は追加先event.id
                mindMapConstraintLayout.invalidate()
            }
        }
        mindMapConstraintLayout.tapListener = listener
        Toast.makeText(context, "タップした位置に追加します", Toast.LENGTH_SHORT).show()
    }

    fun onEditSelected(position: Int) {
        mindMapObjectList[position].text
    }

    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        val action = event?.action
        Log.d("onDrag", v.toString())
        when (action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                Log.d("Drag", "DRAG_STARTED")
                if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    return true
                }
                return false
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                Log.d("Drag", "DRAG_ENTERED")
                v?.background?.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)//set background color to your v
                v?.invalidate()
                return true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                Log.d("Drag", "DRAG_LOCATION")
                return true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                Log.d("Drag", "DRAG_EXITED")
                v?.background?.clearColorFilter()
                v?.invalidate()
                return true
            }
            DragEvent.ACTION_DROP -> {
                Log.d("Drag", "DROP")
                val view = event.localState as View
                if (v!! == linear_left)
                    onAddSelected(view.id)
                behavior?.state = BottomSheetBehavior.STATE_HIDDEN
                return true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                Log.d("Drag", "DRAG_ENDED")
                v?.background?.clearColorFilter()
                v?.invalidate()
                behavior?.state = BottomSheetBehavior.STATE_HIDDEN
                return true
            }
            else -> {
                return false
            }
        }
    }

    override fun drawLines(canvas: Canvas?, scale: Float) {
        val paint = Paint()
        paint.setARGB(255, 0, 0, 0)
        paint.strokeWidth = 5f

        canvas?.scale(scale, scale, mindMapConstraintLayout.width.toFloat() / 2, mindMapConstraintLayout.height.toFloat() / 2)
        mindMapObjectList.forEach {
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
        canvas?.scale(1 / scale, 1 / scale, mindMapConstraintLayout.width.toFloat() / 2, mindMapConstraintLayout.height.toFloat() / 2)
    }

    private fun mindMapObjectToTextView(context: Context?, mindMapObject: MindMapObject): RoundRectTextView {
        val textView = RoundRectTextView(context)
        textView.id = mindMapObject.viewIndex
        textView.gravity = Gravity.CENTER
        textView.text = mindMapObject.text
        textView.setTextColor(Color.WHITE)
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                textView,
                10,
                30,
                2,
                TypedValue.COMPLEX_UNIT_SP)
        return textView
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
