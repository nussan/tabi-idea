package checkers.tabi_idea.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import checkers.tabi_idea.R
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.User
import checkers.tabi_idea.provider.Repository
import java.io.ByteArrayOutputStream

class EventListAdapter(context: Context?, var eventList: MutableList<Event>,var user : User) : RecyclerView.Adapter<EventListAdapter.EventListViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private var listener: View.OnClickListener? = null
    private val repository = Repository()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventListViewHolder {
        val view = inflater.inflate(R.layout.list_event_row, parent, false)
        view.setOnClickListener(listener)
        return EventListViewHolder(view)
    }

    override fun getItemCount() = eventList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EventListViewHolder, position: Int) {
        val event = eventList[position]
        holder.event.text = event.title
        holder.creator.text = "作成者：" + event.creator + " 　作成日時：" + event.created

        val capital = if(event.title.isNotEmpty()) event.title.substring(0,1) else "a"
        val objBitmap = createBitmap(capital,holder)
        val  baos = ByteArrayOutputStream()
        objBitmap.compress(Bitmap.CompressFormat.JPEG,1,baos)
        val jpgarr  = baos.toByteArray()
        val options : BitmapFactory.Options = BitmapFactory.Options()
        options.inSampleSize = 10
        val bitmap = BitmapFactory.decodeByteArray(jpgarr,0,jpgarr.size,options)


        repository.getEventIcon(event!!.id, user.token) {
            val btmarr = it.get("icon")
            if(btmarr != null) {
                // アイコンを取得
                val baStrArr = btmarr!!.split(Regex(", |\\[|]"), 0)
                var baBytArr = ByteArray(baStrArr.size - 2)
                for (i in 1..baStrArr.size - 2) {
                    var tmp = baStrArr[i].toInt()
                    if (tmp > 127) tmp -= 256
                    baBytArr[i - 1] = tmp.toByte()
                }
                val options: BitmapFactory.Options = BitmapFactory.Options()
                options.inSampleSize = 10
                val bitmap = BitmapFactory.decodeByteArray(baBytArr, 0, baBytArr.size, options)
                holder.image.setImageBitmap(bitmap)
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG,1,baos)
                val bmparr = baos.toByteArray();
                // イベントアイコン初期セット
                repository.setEventIcon(bmparr,event.id,user.token){
                    Log.d("icon",it)
                }
                holder.image.setImageBitmap(bitmap)
            }
        }
    }

    fun setOnClickListener(onClickListener: View.OnClickListener) {
        listener = onClickListener
    }

    fun removeAt(position: Int) {
        eventList.removeAt(position)
        notifyItemRemoved(position)
    }

    //ビットマップを作成する関数
    private fun createBitmap(capital: String, holder: EventListViewHolder): Bitmap {
        val objPaint = Paint()
        var objBitmap: Bitmap
        val objCanvas: Canvas
        val fm: Paint.FontMetrics

        val base = "無"
        val textSize = 20
        val width = textSize * base.length
        val height = textSize
        val baseWidth: Int
        val baseHeight: Int
        val capitalWidth: Int
        val centerX: Int
        val modPow = 1.8f // 倍率調整
        val modAdd = 100 // 調整

        objPaint.isAntiAlias = true
        objPaint.color = Color.WHITE
        objPaint.textSize = 1000f
        fm = objPaint.fontMetrics
        // これがなんの役割を果たしているのか不明
        objPaint.getTextBounds(capital, 0, 0, Rect(0, 0, width, height))

        baseWidth = (objPaint.measureText(base).toInt()) * 2
        baseHeight = ((Math.abs(fm.top) - Math.abs(fm.bottom) + modAdd).toInt()) * 2
        objBitmap = Bitmap.createBitmap(baseWidth, baseHeight, Bitmap.Config.ARGB_8888)

        capitalWidth = objPaint.measureText(capital).toInt()
        centerX = (baseWidth / 2) - (capitalWidth / 2)

        objCanvas = Canvas(objBitmap)
        var g = holder.creator.text.toString().codePointAt(0) / 100//187
        var r = capital.codePointAt(0) / 100//200
        var b = 170// 190
        if (r > 244 && b > 244 && g > 244) {
            r = 240
            b = 240
            g = 240
        }
        objCanvas.drawRGB(r, g, b)
        objCanvas.drawText(capital, centerX.toFloat(), -(fm.ascent + fm.descent) * modPow, objPaint)

        return objBitmap
    }

    // Viewへの参照を持っておくViewHolder
    class EventListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val event = view.findViewById<TextView>(R.id.eventView)!!
        val image = view.findViewById<ImageView>(R.id.imageView)!!
        val creator = view.findViewById<TextView>(R.id.creatorView)!!
    }
}