package checkers.tabi_idea.provider

import android.graphics.Bitmap
import checkers.tabi_idea.data.Category
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.User
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.*

interface RequestService {
    //userの追加
    @POST("home/usercreate")
    fun addUser(@Body user: Map<String, String>): Single<User>

    //user情報の取得
    @GET("home/show/{uuid}")
    fun getUser(@Path("uuid") uuid: String): Single<User>

    //user情報の編集
    @POST("home/edit/{id}")
    fun editUser(@Header("Authorization") token: String, @Path("id") id: Int, @Body editName: Map<String, String>): Single<Map<String, String>>

    //Eventへの追加
    @POST("event/create/{id}")
    fun addEvent(@Header("Authorization") token: String, @Path("id") id: Int, @Body title: Map<String, String>): Single<Event>

    //Eventへの参加
    @GET("event/join/{id}/{eid}")
    fun joinEvent(@Header("Authorization") token: String, @Path("id") id: Int, @Path("eid") eid: String): Single<Event>

    @GET("{url}")
    fun getEvent(@Path("url") url: String): Single<Event>

    //Eventの削除
    @GET("event/withdrawal/{uid}/{eid}")
    fun deleteEvent(@Header("Authorization") token: String, @Path("uid") id: Int, @Path("eid") eid: Int): Single<Map<String, String>>

    //eventListの取得
    @GET("event/show/{id}")
    fun getEvent(@Header("Authorization") token: String, @Path("id") id: Int): Single<MutableList<Event>>

    @GET("event/invitation/{uid}/{eid}")
    fun createUrl(@Header("Authorization") token: String, @Path("uid") uid: Int, @Path("eid") eid: Int): Single<Map<String, String>>

    @POST("home/update/{uid}")
    fun setUserIcon(@Header("Authorization") token: String, @Path("uid") uid: Int, @Body btm: Map<String, ByteArray>): Single<String>

    @GET("home/download/{uid}")
    fun getUserIcon(@Header("Authorization") token: String, @Path("uid") uid: Int): Single<Map<String, String>>

    @POST("event/update/{eid}")
    fun setEventIcon(@Header("Authorization") token: String, @Path("eid") eid: Int, @Body btm: Map<String, ByteArray>): Single<String>

    @GET("event/download/{eid}")
    fun getEventIcon(@Header("Authorization") token: String, @Path("eid") eid: Int): Single<Map<String, String>>

    @POST("category/create/{eid}")
    fun addCategory(@Header("Authorization") token: String, @Path("eid") eid: Int, @Body category: Map<String, String>): Single<Category>

    @GET("category/show/{eid}")
    fun getCategoryList(@Header("Authorization") token: String, @Path("eid") eid: Int): Observable<MutableList<Category>>

    @PATCH("category/edit/{cid}")
    fun updateMmo(@Header("Authorization") token: String, @Path("cid") cid: Int, @Body category: Map<String, String>): Single<Category>

    @POST("event/edit/{eid}")
    fun updateEventName(@Header("Authorization") token: String, @Path("eid") eid : Int, @Body event: Map<String,String>): Single<Map<String,String>>
}