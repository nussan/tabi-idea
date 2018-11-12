package checkers.tabi_idea.fragment


import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.*
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
import checkers.tabi_idea.provider.FirebaseApiClient
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.android.synthetic.main.fragment_travel_mind_map.*


class TravelMindMapFragment :
        Fragment(),
        ZoomableLayout.LineDrawer,
        View.OnDragListener {
    private var fbApiClient: FirebaseApiClient? = null
    private var event: Event? = null
    private var map: Map<String, Pair<MindMapObject, RoundRectTextView>> = mutableMapOf()
    private var behavior: BottomSheetBehavior<LinearLayout>? = null
    private var listener: ChildEventListener? = null
    private var matrix: Matrix? = null
    private var lastRaw = PointF(0f, 0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            event = it.getParcelable("eventKey")
        }
    }

    override fun onResume() {
        super.onResume()
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

        fbApiClient = FirebaseApiClient(event!!.id.toString())

        listener = object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("TravelMindMapFragment", "onCancelled")
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                Log.d("TravelMindMapFragment", "onChildMoved")
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("TravelMindMapFragment", "onChildChanged")
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                Log.d("TravelMindMapFragment", "onChildAdded")
                Log.d("TravelMindMapFragment", "$dataSnapshot")
                val key = dataSnapshot.key!!
                val mmo = dataSnapshot.getValue(MindMapObject::class.java)!!

                val view = mindMapObjectToTextView(context, mmo)
                view.tag = key

                view.setOnLongClickListener { v ->
                    behavior?.state = BottomSheetBehavior.STATE_COLLAPSED

                    val item = ClipData.Item(v.tag as? CharSequence)
                    val data = ClipData(v.tag.toString(), arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)
                    v.startDrag(data, View.DragShadowBuilder(v), v, 0)
                }

                view.setOnTouchListener { v, event ->
                    when (event.action and event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
//                            Log.d("TravelMindMapFragment", "ACTION_DOWN")
                            lastRaw.set(event.rawX, event.rawY)
                            matrix = v.matrix
                        }

                        MotionEvent.ACTION_MOVE -> {
//                            Log.d("TravelMindMapFragment", "ACTION_MOVE")
                            val trans = PointF((event.rawX - lastRaw.x), (event.rawY - lastRaw.y))
                            matrix?.postTranslate(trans.x, trans.y)
                            val f = FloatArray(9)
                            matrix?.getValues(f)
                            v.translationX += trans.x
                            v.translationY += trans.y
                            Log.d("TravelMindMapFragment", v.matrix.toShortString())
                            lastRaw.set(event.rawX, event.rawY)
                            mindMapConstraintLayout.invalidate()
                        }

                        MotionEvent.ACTION_UP -> {
                            Log.d("TravelMindMapFragment", "ACTION_UP")
                        }
                    }
                    false
                }
                map = map.plus(key to Pair(mmo, view))
                mindMapConstraintLayout.addView(view, mmo)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d("TravelMindMapFragment", "onChildRemoved")
                map.minus(dataSnapshot.key)
                val target = map[dataSnapshot.key]?.second
                mindMapConstraintLayout.removeView(target)
            }
        }

        if (listener != null)
            fbApiClient?.setListener(listener!!)

        mindMapConstraintLayout.lineDrawer = this
    }

    override fun onStop() {
        Log.d("TravelMindMapFragment", "onStop")
        super.onStop()
        if (listener != null)
            fbApiClient?.removeListener(listener!!)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                (activity as AppCompatActivity).supportFragmentManager.popBackStack()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun onAddSelected(tag: String) {
        Log.d(javaClass.simpleName, "onAddSelected")
        Toast.makeText(context, "タップした位置に追加します", Toast.LENGTH_SHORT).show()

        mindMapConstraintLayout.tapListener = object : ZoomableLayout.TapListener {
            override fun onTap(e: MotionEvent, centerX: Float, centerY: Float, scale: Float) {
                val inflater = layoutInflater.inflate(R.layout.input_form, null, false)
                val inputText: EditText = inflater.findViewById(R.id.inputText)
                inputText.requestFocus()
                val newId = map.size
                val parent = mindMapConstraintLayout.findViewWithTag<RoundRectTextView>(tag)

                val matrix = FloatArray(9)
                parent.matrix.getValues(matrix)

                val mmo = MindMapObject(
                        newId,
                        "",
                        (e.x - matrix[Matrix.MTRANS_X]) - parent.width * scale / 2,
                        (e.y - matrix[Matrix.MTRANS_Y]) - parent.height * scale / 2,
                        parent.tag as String,
                        0,
                        map[parent.tag as String]!!.first.type
                )
                // ダイアログの設定
                val inputForm = AlertDialog.Builder(context!!).apply {
                    setTitle("新しいアイデア")
                    setView(inflater)
                    setPositiveButton("OK") { _, _ ->
                        mmo.text = inputText.text.toString()
                        fbApiClient?.addMmo(mmo)
                        mindMapConstraintLayout.invalidate()
                    }
                    setNegativeButton("Cancel", null)
                }.create()

                // ダイアログ表示と同時にキーボードを表示
                inputForm.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                inputForm.show()
            }
        }
    }

    private fun onDeleteSelected(tag: String) {
        val mmo = map[tag]?.first ?: return
        fbApiClient?.deleteMmo(Pair(tag, mmo))
    }

    private fun onEditSelected(tag: String) {
        val inflater = layoutInflater.inflate(R.layout.input_form, null, false)

        // ダイアログ内のテキストエリア
        val inputText: EditText = inflater.findViewById(R.id.inputText)
        inputText.requestFocus()

        // ダイアログの設定
        val inputForm = AlertDialog.Builder(context!!).apply {
            setTitle("アイデアを編集")
            setView(inflater)
            setPositiveButton("OK") { _, _ ->

            }
            setNegativeButton("Cancel", null)
        }.create()

        // ダイアログ表示と同時にキーボードを表示
        inputForm.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        inputForm.show()

        map[tag]!!.first.text = inputText.text.toString()
        fbApiClient?.updateMmo(tag to map[tag]!!.first)
    }

    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        val action = event?.action
        Log.d("onDrag", v.toString())
        when (action) {
            DragEvent.ACTION_DRAG_STARTED -> {
//                Log.d("Drag", "DRAG_STARTED")
                if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    return true
                }
                return false
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
//                Log.d("Drag", "DRAG_ENTERED")
                v?.background?.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)//set background color to your v
                v?.invalidate()
                return true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
//                Log.d("Drag", "DRAG_LOCATION")
                return true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
//                Log.d("Drag", "DRAG_EXITED")
                v?.background?.clearColorFilter()
                v?.invalidate()
                return true
            }
            DragEvent.ACTION_DROP -> {
//                Log.d("Drag", "DROP")
                val view = event.localState as View

                when (v) {
                    linear_left -> onAddSelected(view.tag as String)
                    linear_center -> onDeleteSelected(view.tag as String)
                    linear_right -> onEditSelected(view.tag as String)
                }

                behavior?.state = BottomSheetBehavior.STATE_HIDDEN
                return true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
//                Log.d("Drag", "DRAG_ENDED")
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
        paint.strokeWidth = 5f * scale

        map.forEach {
            val child = mindMapConstraintLayout.findViewWithTag<RoundRectTextView?>(it.key) ?: return@forEach
            val parent = mindMapConstraintLayout.findViewWithTag<RoundRectTextView?>(it.value.first.parent) ?: return@forEach
            Log.d("TravelMindMapFragment", it.value.first.parent)
            val ca = FloatArray(9)
            child.matrix.getValues(ca)
            val pa = FloatArray(9)
            parent.matrix.getValues(pa)
            canvas?.drawLine(
                    ca[Matrix.MTRANS_X] + child.width * child.scaleX / 2,
                    ca[Matrix.MTRANS_Y] + child.height * child.scaleY / 2,
                    pa[Matrix.MTRANS_X] + parent.width * parent.scaleX / 2,
                    pa[Matrix.MTRANS_Y] + parent.height * parent.scaleY / 2,
                    paint
            )
        }
    }


    private fun mindMapObjectToTextView(context: Context?, mindMapObject: MindMapObject): RoundRectTextView {
        val textView = RoundRectTextView(context)
        textView.id = mindMapObject.viewIndex
        textView.gravity = Gravity.CENTER
        textView.text = mindMapObject.text
        textView.setTextColor(Color.WHITE)
        Log.d("MindMapType",mindMapObject.type)
        when (mindMapObject.type){
            "destination" -> {
                textView.setBackgroundColor(Color.parseColor("#ffb6c1"))
            }
            "budget" -> {
                textView.setBackgroundColor(Color.parseColor("#32cd32"))
            }
            "food" -> {
                textView.setBackgroundColor(Color.parseColor("#ff8c00"))
            }
            "hotel" -> {
                textView.setBackgroundColor(Color.parseColor("#ffe4b5"))
            }

        }
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
