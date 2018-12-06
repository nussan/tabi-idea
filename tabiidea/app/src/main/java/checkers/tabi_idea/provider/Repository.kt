package checkers.tabi_idea.provider

import android.util.Log
import checkers.tabi_idea.data.Category
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.User
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class Repository {
    private var requestService: RequestService
    var api: CompositeDisposable? = CompositeDisposable()

    init {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
                .baseUrl("https://fast-peak-71769.herokuapp.com/")
                //fast-peak-71769
                //mysterious-shore-91717
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

    fun addUserMock(newUser: Map<String, String>, callback: (User) -> Unit) {
        val user: User = User(1, "MOCK", "MOCK")
        callback(user)
    }

    //userをedit
    fun editUser(token: String, id: Int, editName: Map<String, String>, callback: (Map<String, String>) -> Unit) {
        Log.d("tokentoken", token)
        requestService.editUser(token, id, editName)
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
                            callback(User(-1, "", ""))
                        }
                )
    }

    //eventlistをadd,rxjava2
    fun addEvent(token: String, user_id: Int, title: Map<String, String>, callback: (Event) -> Unit) {
        Log.d("tokentoken", token)
        requestService.addEvent(token, user_id, title)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> callback(res) },
                        { err ->
                            Log.d("errAddEvent", err.toString())
                        }
                )
    }

//    fun addEventMock(token:String,user_id: Int, title: Map<String, String>, callback: (Event) -> Unit){
//        callback(Event(2,title.get("title")!!,mutableListOf(),"mock","s","s"))
//    }


    //eventListをget,rxjava2
    fun getEventList(token: String, user_id: Int, callback: (MutableList<Event>) -> Unit) {
        requestService.getEvent(token, user_id)
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
    fun joinEvent(token: String, userId: Int, eventToken: String, callback: (Event) -> Unit) {
        requestService.joinEvent(token, userId, eventToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> callback(res) },
                        { err -> Log.d("errJoinEvent", err.toString()) }
                )
    }


    //eventの削除
    fun deleteEvent(token: String, user_id: Int, event_id: Int, callback: (Map<String, String>) -> Unit) {
        requestService.deleteEvent(token, user_id, event_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> callback(res) },
                        { err -> Log.d("errDeleteEvent", err.toString()) }
                )
    }

    //urlの発行

    fun createUrl(token: String, user_id: Int, event_id: Int, callback: (Map<String, String>) -> Unit) {
        requestService.createUrl(token, user_id, event_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> callback(res) },
                        { err -> Log.d("errCreateUrl", err.toString()) }
                )
    }

    //ユーザーアイコンの取得
    fun getUserIcon(user_id: Int, token: String, callback: (Map<String, String>) -> Unit) {
        requestService.getUserIcon(token, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> callback(res) },
                        { err -> Log.d("errIconGet", err.toString()) }
                )
    }

    //ユーザーアイコンを設定
    fun setUserIcon(btm: ByteArray, user_id: Int, token: String, callback: (String) -> Unit) {
        requestService.setUserIcon(token, user_id, mapOf("icon" to btm))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> callback(res) },
                        { err -> Log.d("errIconSet", err.toString()) }
                )
    }

    //イベントアイコンの取得
    fun getEventIcon(event_id: Int, token: String, callback: (Map<String, String>) -> Unit) {
        requestService.getEventIcon(token, event_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> callback(res) },
                        { err -> Log.d("errIconGetEvent", err.toString()) }
                )
    }

    //イベントアイコンを設定
    fun setEventIcon(btm: ByteArray, event_id: Int, token: String, callback: (String) -> Unit) {
        requestService.setEventIcon(token, event_id, mapOf("icon" to btm))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> callback(res) },
                        { err -> Log.d("errIconSetEvent", err.toString()) }
                )
    }

    fun addCategory(token: String, eventId: Int, category: Category, callback: (Category) -> Unit) {
        requestService.addCategory(
                token,
                eventId,
                mapOf("name" to category.name,
                        "color" to category.color))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> callback(res) },
                        { err -> Log.d("errAddCategory", err.toString()) }
                )
    }

    fun getCategoryList(token: String, eventId: Int, callback: (MutableList<Category>) -> Unit) {
        val task = requestService.getCategoryList(token, eventId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> callback(res) },
                        { err -> Log.d("errGetCategoryList", err.toString()) }
                )
        api?.add(task)
    }

    fun unsub() {
        api?.dispose()
    }

    fun updateCategory(token: String, categoryId: Int, category: Category, callback: (Category) -> Unit) {
        requestService.updateMmo(token, categoryId, mapOf("name" to category.name, "color" to category.color))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res -> callback(res) },
                        { err -> Log.d("errUpdateCategory", err.toString()) }
                )
    }
}