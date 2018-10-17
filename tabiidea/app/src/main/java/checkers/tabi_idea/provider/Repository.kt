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
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class Repository{
    private var requestService: RequestService

    init {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
                .baseUrl("https://fast-peak-71769.herokuapp.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
        requestService = retrofit.create(RequestService::class.java)
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
    fun addEvent(userid:Int,title:Map<String,String>,callback:(MutableList<Event>) -> Unit){
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

    //userをadd
    fun addUser() {
    }

    //userをedit
    fun editUser(){

    }

    //eventをfbにadd
    fun addEventtoFb(event_id: String){
        FirebaseDatabase.getInstance()
                .getReference()
                .push()
                .setValue(event_id)
    }

    //mmoをfbにadd
    fun addMmo(event_id: String,mmo:MindMapObject){
        FirebaseDatabase.getInstance()
                .getReference(event_id)
                .push()
                .setValue(mmo)
    }
}