package checkers.tabi_idea.provider

import android.util.Log
import android.widget.Toast
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
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.FlexibleTypeDeserializer

class Repository {
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

    /*---heroku---*/
    //userをadd
    fun addUser(newUser: Map<String, String>, callback: (User) -> Unit) {
        requestService.addUser(newUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> callback(res) },
                        { err -> Log.d("errAddUser", err.toString())}
                )
    }

    //userをedit
    fun editUser(id: Int, editName: Map<String, String>, callback: (User) -> Unit) {
        requestService.editUser(id, editName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> callback(res) },
                        { err -> Log.d("errEditUser", err.toString()) }
                )
    }

    //user情報をget,rxjava2
    fun getUser(uuid: String, callback: (User) -> Unit) {
        val user: User = User(-1, "")
        requestService.getUser(uuid)
                .retry(3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> callback(res) },
                        { err ->
                            Log.d("errGetUser", err.toString())
                            callback(user)
                        }
                )
    }

    //eventlistをadd,rxjava2
    fun addEvent(user_id: Int, title: Map<String, String>, callback: (Event) -> Unit) {
        requestService.addEvent(user_id, title)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> callback(res) },
                        { err ->
                            Log.d("errAddEvent", err.toString())
                        }
                )
    }

    //eventListをget,rxjava2
    fun getEventList(user_id: Int, callback: (MutableList<Event>) -> Unit) {
        val eventList: MutableList<Event> = mutableListOf()
        requestService.getEvent(user_id)
                .retry(3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> callback(res) },
                        { err ->
                            Log.d("errGetEventList", err.toString())
                            callback(eventList)
                        }
                )
    }

    //eventへの参加
    fun joinEvent(userid: Int, eventId: String) {
        requestService.joinEvent(userid, eventId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> },
                        {err -> Log.d("errJoinEventList",err.toString()) }
                )
    }

    //eventの削除
    fun deleteEvent(user_id:Int,event_id: Int,callback: (Map<String,String>) -> Unit){
        requestService.deleteEvent(user_id,event_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {res -> callback(res)},
                        {err -> Log.d("errDeleteEvent",err.toString())}
                )
    }

    //urlの発行
    fun createUrl(user_id:Int,event_id: String,callback: (Map<String,String>) -> Unit){
        requestService.createUrl(user_id,event_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {res -> callback(res)},
                        {err -> Log.d("errCreateUrl",err.toString())}
                )
    }


    //eventをfbにadd
    fun addEventToFb(event_id: String) {
        val mmo = MindMapObject(0, "旅行", 0f, 0f, "", 0, "root")
        val ref = FirebaseDatabase.getInstance().getReference(event_id)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.mapNotNull {
                    val rootKey = it.key!!
                    Log.d("Repository", rootKey)
                    updateMmo(event_id, rootKey to MindMapObject(0, "旅行", 0f, 0f, rootKey, 0, "root"))
                    val ml = mutableListOf(
                            MindMapObject(1, "行先", 200f, 200f, rootKey, 0, "destination"),
                            MindMapObject(2, "予算", 200f, -200f, rootKey, 0, "budget"),
                            MindMapObject(3, "食事", -200f, 200f, rootKey, 0, "food"),
                            MindMapObject(4, "宿泊", -200f, -200f, rootKey, 0, "hotel"))

                    ml.forEach {child ->
                        addMmo(event_id, child)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("errGetMmo", databaseError.toString())
            }
        })
        ref.push().setValue(mmo)
    }

    //mmoをfbにadd
    fun addMmo(event_id: String, mmo: MindMapObject) {
        FirebaseDatabase.getInstance()
                .getReference(event_id)
                .push()
                .setValue(mmo)
    }

    //mmoのtextをアップデート
    fun updateMmo(event_id: String, pair: Pair<String, MindMapObject>) {
        FirebaseDatabase.getInstance()
                .getReference(event_id)
                .child(pair.first)
                .setValue(pair.second)
    }
}