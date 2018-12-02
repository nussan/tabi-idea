package checkers.tabi_idea.provider

import android.util.Log
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.User
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlinx.android.synthetic.main.activity_main.*

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
    fun editUser(token:String, id: Int, editName: Map<String, String>, callback: (Map<String,String>) -> Unit) {
        Log.d("tokentoken",token)
        requestService.editUser(token,id, editName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> callback(res) },
                        { err -> Log.d("errEditUser", err.toString()) }
                )
    }

    //user情報をget,rxjava2
    fun getUser(uuid: String, callback: (User) -> Unit) {
        requestService.getUser(uuid)
                .retry(3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> callback(res) },
                        { err ->
                            Log.d("errGetUser", err.toString())
                            callback(User(-1,"",""))
                        }
                )
    }

    //eventlistをadd,rxjava2
    fun addEvent(token:String,user_id: Int, title: Map<String, String>, callback: (Event) -> Unit) {
        Log.d("tokentoken",token)
        requestService.addEvent(token,user_id, title)
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
    fun getEventList(token:String,user_id: Int, callback: (MutableList<Event>) -> Unit) {
        requestService.getEvent(token,user_id)
                .retry(3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> callback(res) },
                        { err ->
                            Log.d("errGetEventList", err.toString())
                            callback(mutableListOf())
                        }
                )
    }

    //eventへの参加
    fun joinEvent(token:String,userid: Int, eventId: String) {
        requestService.joinEvent(token,userid, eventId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> },
                        {err -> Log.d("errJoinEventList",err.toString()) }
                )
    }

    //eventの削除
    fun deleteEvent(token:String,user_id:Int,event_id: Int,callback: (Map<String,String>) -> Unit){
        requestService.deleteEvent(token,user_id,event_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {res -> callback(res)},
                        {err -> Log.d("errDeleteEvent",err.toString())}
                )
    }

    //urlの発行
    fun createUrl(token:String,user_id:Int,event_id: String,callback: (Map<String,String>) -> Unit){
        requestService.createUrl(token,user_id,event_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {res -> callback(res)},
                        {err -> Log.d("errCreateUrl",err.toString())}
                )
    }
}