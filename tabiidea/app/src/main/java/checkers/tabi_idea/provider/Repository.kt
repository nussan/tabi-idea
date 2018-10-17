package checkers.tabi_idea.provider

import android.util.Log
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.MindMapObject
import checkers.tabi_idea.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.xml.sax.DTDHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class Repository{
    private var requestService: RequestService

    init {
        val okHttpClient = OkHttpClient.Builder().build()
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
                .baseUrl("https://fast-peak-71769.herokuapp.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
        requestService = retrofit.create(RequestService::class.java)
    }

    //mmoを更新
    fun updateMmoCallback(callback: (List<MindMapObject>) -> Unit){
        requestService.updateMmo().enqueue(object : Callback<List<MindMapObject>> {
            override fun onResponse(call: Call<List<MindMapObject>>?, response: Response<List<MindMapObject>>?) {
                Log.d("tubasa2" , "success")
                val mindmapobject: List<MindMapObject> = mutableListOf(
                        MindMapObject(0, "旅行", 1f / 2, 1f / 2, 0),
                        MindMapObject(1, "行先", 1f / 2, 1f / 4, 0),
                        MindMapObject(2, "予算", 1f / 4, 1f / 2, 0),
                        MindMapObject(3, "食事", 1f / 2, 3f / 4, 0),
                        MindMapObject(4, "宿泊", 3f / 4, 1f / 2, 0)
                )
                callback(mindmapobject)
                response?.let {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            callback(it)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<MindMapObject>>?, t: Throwable?) {
                Log.d("tubasa2",t.toString())
                val mindmapobject: List<MindMapObject> = mutableListOf(
                        MindMapObject(0, "旅行", 1f / 2, 1f / 2, 0),
                        MindMapObject(1, "行先", 1f / 2, 1f / 4, 0),
                        MindMapObject(2, "予算", 1f / 4, 1f / 2, 0),
                        MindMapObject(3, "食事", 1f / 2, 3f / 4, 0),
                        MindMapObject(4, "宿泊", 3f / 4, 1f / 2, 0)
                )
                callback(mindmapobject)
            }
        })
    }

    fun addMmoCallback(event_id:Int,mmo:Map<String,String>,callback: (MindMapObject) -> Unit){
        requestService.addMmo(event_id,mmo).enqueue(object : Callback<MindMapObject> {
            override fun onResponse(call: Call<MindMapObject>?, response: Response<MindMapObject>?) {
                Log.d("tubasa" , "success")
                response?.let {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            callback(it)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<MindMapObject>?, t: Throwable?) {
                Log.d("tubasa",t.toString())
            }
        })
    }

    //user情報をget,rxjava2
    fun getUser(callback:(User)->Unit){
        val user: User = User(0, "たきかわ")
        requestService.getUser("tsubasa")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {res -> callback(res) },
                        {err ->
                            Log.d("err",err.toString())
                            callback(user)
                        }
                )
    }

    //eventlistをadd,rxjava2
    //ここでついでにfirebaseにeid追加したと仮定する
    fun addEventList(userid:Int,title:Map<String,String>,callback:(MutableList<Event>) -> Unit){
        val eventList:MutableList<Event> = mutableListOf()
        requestService.addEvent(userid,title)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {res -> callback(res)},
                        {err ->
                            Log.d("err",err.toString())
                            callback(eventList)
                        }
                )
    }

    //eventListをget,rxjava2
    fun getEventList(userid: Int,callback:(MutableList<Event>) -> Unit){
        val eventList:MutableList<Event> = mutableListOf()
        requestService.getEvent(userid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {res -> callback(res)},
                        {err ->
                            Log.d("err",err.toString())
                            callback(eventList)
                        }
                )
    }

    //eventidを送る -> (トークンが帰ってくる) -> firebaseからeidのmmoをゲット
    fun getMmo(event_id: String,callback: (Collection<MindMapObject>) -> Unit){
        FirebaseDatabase.getInstance()
                .getReference(event_id)
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        callback(dataSnapshot.children.mapNotNull {
                            Log.d("err",it.toString())
                            it.getValue(MindMapObject::class.java)
                        })
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d("err",databaseError.toString())
                    }
                })
    }

    //mmoを更新
//    fun updateMmo(callback: (List<MindMapObject>) -> Unit){
//        val mmo: List<MindMapObject> = mutableListOf(
//                MindMapObject(0, "旅行", 1f / 2, 1f / 2, 0),
//                MindMapObject(1, "行先", 1f / 2, 1f / 4, 0),
//                MindMapObject(2, "予算", 1f / 4, 1f / 2, 0),
//                MindMapObject(3, "食事", 1f / 2, 3f / 4, 0),
//                MindMapObject(4, "宿泊", 3f / 4, 1f / 2, 0)
//        )
//        requestService3.updateMmo2()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        {res -> callback(res)},
//                        {err ->
//                            Log.d("err",err.toString())
//                            callback(mmo)
//                        }
//                )
//    }
}