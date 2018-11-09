package checkers.tabi_idea.provider

import android.util.Log
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.MindMapObject
import checkers.tabi_idea.data.User
import com.google.firebase.database.*
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

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
                        { err -> Log.d("errAddUser", err.toString()) }
                )
    }

    //userをedit
    fun editUser(editName: String) {
        requestService.editUser(editName)
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
    fun addEvent(userid: Int, title: Map<String, String>, callback: (Event) -> Unit) {
        requestService.addEvent(userid, title)
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
    fun getEventList(userid: Int, callback: (MutableList<Event>) -> Unit) {
        val eventList: MutableList<Event> = mutableListOf()
        requestService.getEvent(userid)
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

    /*---firebase---*/
    //firebaseからeidのmmoをゲット
    fun getMmo(event_id: String, callback: (Collection<Pair<String, MindMapObject>>) -> Unit) {
        FirebaseDatabase.getInstance()
                .getReference(event_id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        callback(dataSnapshot.children.mapNotNull {
                            it.key!! to it.getValue(MindMapObject::class.java)!!
                        })
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d("errGetMmo", databaseError.toString())
                    }
                })
    }

    fun mmoListener(event_id: String, callback: (Pair<String, MindMapObject>) -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference(event_id)
        val listener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("onChildAdded", previousChildName)
                Log.d("onchildAdded", dataSnapshot.toString())
                callback(dataSnapshot.key!! to dataSnapshot.getValue(MindMapObject::class.java)!!)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("onChildChaged", previousChildName)
                callback(dataSnapshot.key!! to dataSnapshot.getValue(MindMapObject::class.java)!!)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("onChildMoved", previousChildName)
                callback(dataSnapshot.key!! to dataSnapshot.getValue(MindMapObject::class.java)!!)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d("onChildRemoved", dataSnapshot.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("errGetMmo", databaseError.toString())
            }
        }

        ref.addChildEventListener(listener)
    }

    //eventをfbにadd
    fun addEventtoFb(event_id: String) {
        val mmo = MindMapObject(0, "旅行", 0f, 0f, 0)
        FirebaseDatabase.getInstance()
                .getReference(event_id)
                .push()
                .setValue(mmo)
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