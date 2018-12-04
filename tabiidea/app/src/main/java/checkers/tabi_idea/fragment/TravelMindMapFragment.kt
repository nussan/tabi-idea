package checkers.tabi_idea.fragment


import android.app.Activity
import android.content.*
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import checkers.tabi_idea.R
import checkers.tabi_idea.custom.view.CustomActionsInAnimator
import checkers.tabi_idea.custom.view.CustomActionsTitleAnimator
import checkers.tabi_idea.custom.view.RoundRectTextView
import checkers.tabi_idea.custom.view.ZoomableLayout
import checkers.tabi_idea.data.Category
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.MindMapObject
import checkers.tabi_idea.data.User
import checkers.tabi_idea.provider.FirebaseApiClient
import checkers.tabi_idea.provider.Repository
import com.commit451.quickactionview.Action
import com.commit451.quickactionview.QuickActionView
import com.commit451.quickactionview.animator.PopAnimator
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.android.synthetic.main.fragment_travel_mind_map.*
import kotlin.reflect.jvm.internal.impl.resolve.constants.IntValue
import java.io.FileDescriptor


class TravelMindMapFragment :
        Fragment(),
        ZoomableLayout.LineDrawer,
        View.OnDragListener {
    private var fbApiClient: FirebaseApiClient? = null
    private var event: Event? = null
    private var user: User? = null
    private var map: Map<String, MindMapObject> = mutableMapOf()
    private var behavior: BottomSheetBehavior<LinearLayout>? = null
    private var listener: ChildEventListener? = null
    private val repository = Repository()
    private lateinit var myuser : User
    private var click: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            myuser = it.getParcelable("user")
            event = it.getParcelable("eventKey")
            user = it.getParcelable("userKey")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_travel_mind_map, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = event?.title
        (activity as AppCompatActivity).supportActionBar?.setDisplayUseLogoEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setIcon(R.drawable.ic_launcher_foreground)
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
                val key = dataSnapshot.key!!
                val mmo = dataSnapshot.getValue(MindMapObject::class.java)!!
                map = map.minus(key)
                map = map.plus(key to mmo)
                val target = mindMapConstraintLayout.findViewWithTag<RoundRectTextView>(key)
                target.text = TextUtils.ellipsize(mmo.text, target.paint, RoundRectTextView.MAX_SIZE.toFloat(), TextUtils.TruncateAt.END)
                mindMapConstraintLayout.invalidate()
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                Log.d("TravelMindMapFragment", "onChildAdded")
                Log.d("TravelMindMapFragment", "$dataSnapshot")

                val key = dataSnapshot.key!!
                val mmo = dataSnapshot.getValue(MindMapObject::class.java)!!

                val view = mindMapObjectToTextView(context, mmo)

                view.tag = key

                // 画面のタッチポイントの差分をビュー毎に分けるためにここで宣言
                val lastRaw = PointF(0f, 0f)
                val point = Point(0,0)

                val colorInt = (view.background as ColorDrawable).color
                Log.d("colorInt",Integer.toHexString(colorInt).substring(2))

                click = false

                view.setOnTouchListener { v, event ->
                    Log.d("TravelMindMapFragment", "${event.pointerCount}")
                    when (event.action and event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            Log.d("TravelMindMapFragment", "ACTION_DOWN")
                            (v as RoundRectTextView).drawStroke(colorInt!!,true)

                            click = true

                            lastRaw.set(event.rawX, event.rawY)
                            point.set((event.x*v.scaleX).toInt(),(event.y*v.scaleY).toInt())
                        }

                        MotionEvent.ACTION_MOVE -> {
                            Log.d("TravelMindMapFragment", "ACTION_MOVE")
                            (v as RoundRectTextView).drawStroke(colorInt!!,true)
                            val trans = PointF((event.rawX - lastRaw.x), (event.rawY - lastRaw.y))
                            if (trans.x * trans.x + trans.y * trans.y > 5) {
                                // 移動量が一定以上のときロングプレスをキャンセル
                                v.cancelLongPress()
                                click = false
                            }
                            val matrix = v.matrix
                            matrix?.postTranslate(trans.x, trans.y)
                            val f = FloatArray(9)
                            matrix?.getValues(f)
                            v.translationX += trans.x
                            v.translationY += trans.y
                            lastRaw.set(event.rawX, event.rawY)
                            mindMapConstraintLayout.invalidate()
                        }

                        MotionEvent.ACTION_UP -> {
                            Log.d("TravelMindMapFragment", "ACTION_UP")

                            rrvToQAV(context,view,point,colorInt)
                            if(!click){(v as RoundRectTextView).drawStroke(colorInt,false)}
                        }
                    }
                    false
                }

                view.setOnLongClickListener { v ->
                    behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                    val item = ClipData.Item(v.tag as? CharSequence)
                    val data = ClipData(v.tag.toString(), arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)
                    (v as RoundRectTextView).drawStroke(colorInt,false)
                    v.startDrag(data, View.DragShadowBuilder(v), v, 0)
                }

                map = map.plus(key to mmo)
                mindMapConstraintLayout.addView(view, mmo)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d("TravelMindMapFragment", "onChildRemoved")
                map.minus(dataSnapshot.key)
                mindMapConstraintLayout.removeView(mindMapConstraintLayout.findViewWithTag(dataSnapshot.key))
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

    private fun onLikeSelected(view: View,colorInt: Int){
        val tag = view.tag as String
        val mmo = map[tag] ?: return
        if(!mmo.likeList.contains(user!!.id)) {
            mmo.likeList.add(user!!.id)
        }else {
            mmo.likeList.remove(user!!.id)
        }
        mmo.point = mmo.likeList.size
        fbApiClient?.updateMmo(tag to mmo)
        (view as RoundRectTextView).drawStroke(colorInt,false)
    }

    private fun onAddSelected(view: View,colorInt: Int) {
        Log.d(javaClass.simpleName, "onAddSelected")
        Toast.makeText(context, "タップした位置に追加します", Toast.LENGTH_SHORT).show()
        val tag = view.tag as String

        mindMapConstraintLayout.tapListener = object : ZoomableLayout.TapListener {
            override fun onTap(e: MotionEvent, centerX: Float, centerY: Float, scale: Float) {
                val inflater = layoutInflater.inflate(R.layout.input_form, null, false)
                val inputText: EditText = inflater.findViewById(R.id.inputText)
                val spinner = inflater.findViewById<Spinner>(R.id.spinner)
                val adapter = ArrayAdapter(context,
                        android.R.layout.simple_spinner_item,
                        listOf(Category("行先"),
                                Category("食事"),
                                Category("宿泊"),
                                Category("行先"),
                                Category("宿泊"),
                                Category("宿泊"),
                                Category("宿泊"),
                                Category("宿泊"),
                                Category("宿泊"),
                                Category("宿泊"),
                                Category("宿泊"),
                                Category("宿泊宿泊宿泊宿泊宿泊宿泊宿泊宿泊")
                        ))
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
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
                        map[parent.tag as String]!!.type
                )
                // ダイアログの設定
                val inputForm = AlertDialog.Builder(context!!).apply {
                    setTitle("新しいアイデア")
                    setView(inflater)
                    setPositiveButton("OK") { _, _ ->
                        mmo.text = inputText.text.toString()
                        fbApiClient?.addMmo(mmo)
                    }
                    setNegativeButton("Cancel", null)
                }.create()

                // ダイアログ表示と同時にキーボードを表示
//                inputForm.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                inputForm.show()
                (view as RoundRectTextView).drawStroke(colorInt,false)
            }
        }
    }

    private fun onDeleteSelected(tag: String) {
        val mmo = map[tag] ?: return
        if (mmo.type == "root") {
            Toast.makeText(context, "ルートノードは削除できません", Toast.LENGTH_SHORT).show()
            return
        }
        fbApiClient?.deleteMmo(Pair(tag, mmo))
    }

    private fun onEditSelected(view: View,colorInt: Int) {
        val inflater = layoutInflater.inflate(R.layout.input_form, null, false)
        val tag = view.tag as String
        // ダイアログ内のテキストエリア
        val inputText: EditText = inflater.findViewById(R.id.inputText)
        inputText.requestFocus()

        // ダイアログの設定
        val inputForm = AlertDialog.Builder(context!!).apply {
            setTitle("アイデアを編集")
            setView(inflater)
            setPositiveButton("OK") { _, _ ->
                map[tag]!!.text = inputText.text.toString()
                fbApiClient?.updateMmo(tag to map[tag]!!)
            }
            setNegativeButton("Cancel", null)
        }.create()

        // ダイアログ表示と同時にキーボードを表示
        inputForm.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        inputForm.show()
        (view as RoundRectTextView).drawStroke(colorInt,false)
    }

    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        val action = event?.action
//        Log.d("onDrag", v.toString())
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
//                    linear_left -> onAddSelected(view.tag as String,)
                    linear_center -> onDeleteSelected(view.tag as String)
//                    linear_right -> onEditSelected(view.tag as String)
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
        paint.strokeWidth = scale

        map.forEach {
            val child = mindMapConstraintLayout.findViewWithTag<RoundRectTextView?>(it.key)
                    ?: return@forEach
            val parent = mindMapConstraintLayout.findViewWithTag<RoundRectTextView?>(it.value.parent)
                    ?: return@forEach
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
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                textView,
                10,
                30,
                2,
                TypedValue.COMPLEX_UNIT_SP)
        textView.id = mindMapObject.viewIndex
        textView.gravity = Gravity.CENTER
        textView.text = TextUtils.ellipsize(mindMapObject.text, textView.paint, RoundRectTextView.MAX_SIZE.toFloat(), TextUtils.TruncateAt.END)
        textView.setTextColor(Color.WHITE)
        when (mindMapObject.type) {
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
        return textView
    }

    private fun rrvToQAV(context: Context?, view: View, point:Point,colorInt:Int){
        val qav = QuickActionView.make(context)
        qav.setTouchPoint(point)
        qav.setColorInt(colorInt)
        qav.setClick(click)
        val mQuickActionListener = QuickActionView.OnActionSelectedListener { action, quickActionView ->
            Log.d("aaa", "aaa")
            val view = quickActionView.longPressedView
            if (view != null) {
                Snackbar.make(view, "Clicked on " + action.id, Snackbar.LENGTH_SHORT).show()
                when (action.title) {
                    "追加" -> onAddSelected(view,colorInt)
                    "編集" -> onEditSelected(view,colorInt)
                    "いいね" -> onLikeSelected(view,colorInt)
                }
            }
        }
        val popAnimator = PopAnimator(true)
        val actionTitleAnimator = CustomActionsTitleAnimator()
        val pareIconTitle = listOf(
                ContextCompat.getDrawable(context!!, R.drawable.ic_add_black_24dp)!! to getString(R.string.add),
                ContextCompat.getDrawable(context, R.drawable.ic_edit_black_24dp)!! to getString(R.string.edit),
                ContextCompat.getDrawable(context, R.drawable.ic_favorite_black_24dp)!! to getString(R.string.good)
        )
        val actionList = mutableListOf<Action>()
        pareIconTitle.forEach {
            actionList.add(Action(1337, it.first, it.second))
        }
        qav.addActions(actionList)
                .setActionsTitleInAnimator(actionTitleAnimator)
                .setActionsTitleOutAnimator(actionTitleAnimator)
                .setOnActionSelectedListener(mQuickActionListener)
                .setActionsOutAnimator(popAnimator)
                .register(view)

        val customActionsInAnimator = CustomActionsInAnimator(qav)
        qav.setActionsInAnimator(customActionsInAnimator)
    }

    //TravelMIndMapFragmentでツールバーにメニュー機能を追加する
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.mmomenu, menu)

        val icon: MenuItem = menu.findItem(R.id.mmomenu_icon)
        icon.setOnMenuItemClickListener {
            val intent: Intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.setType("image/*")
            startActivityForResult(intent, 1000)
            // OKが押されるとonActivityResutに処理が移行する
            true
        }

        val invite: MenuItem = menu.findItem(R.id.mmomenu_invite)
        invite.setOnMenuItemClickListener{
            repository.createUrl(myuser.token,myuser.id,event!!.id){
                Log.d("masak",it.getValue("url"))
                AlertDialog.Builder(context!!).apply {
                    setTitle("招待URLを発行しました")
                    setMessage(it.getValue("url"))
                    setPositiveButton("コピー") { _, _ ->
                        // OKをタップしたときの処理
                        copyToClipboard(context,"",it.getValue("url"))
                        Toast.makeText(context, "コピーしました", Toast.LENGTH_LONG).show()
                    }
                    setNegativeButton("Cancel", null)
                    show()
                }
            }
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            var uri: Uri? = null
            if (data != null) {
                uri = data.data

                val bmp: Bitmap = getBitmapFromUri(uri)
                val reBmp = Bitmap.createScaledBitmap(bmp, 240, 240, false)
                val drw = BitmapDrawable(reBmp)
                (activity as AppCompatActivity).supportActionBar?.setIcon(drw)
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val parcelFileDescriptor: ParcelFileDescriptor = getContext()!!.getContentResolver().openFileDescriptor(uri, "r")
        val fileDescriptor: FileDescriptor = parcelFileDescriptor.getFileDescriptor()
        val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close()
        return image
    }


    //招待ＵＲＬをクリップボードにコピーするメソッド
    private fun copyToClipboard(context: Context, label:String, text:String) {
        // copy to clipboard
        val clipboardManager: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.primaryClip = ClipData.newPlainText(label, text)
    }

    companion object {
        @JvmStatic
        fun newInstance(event: Event, user:User) = TravelMindMapFragment().apply {
            arguments = Bundle().apply {
                putParcelable("userKey",user)
                putParcelable("eventKey", event)
                putParcelable("user",user)
            }
        }
    }
}
