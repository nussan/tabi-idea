package checkers.tabi_idea.provider

import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.MindMapObject
import checkers.tabi_idea.data.User
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*

interface RequestService {
    //userの追加
    @POST("home/usercreate")
    fun addUser(@Body user:Map<String,String>) : Single<User>

    //user情報の取得
    @GET("home/show/{uuid}")
    fun getUser(@Path("uuid") uuid:String) : Single<User>

    //user情報の編集
    @POST("home/edit/{id}")
    fun editUser(@Header("token") token:String,@Path("id") id:Int,@Body editName:Map<String,String>) : Single<User>

    //Eventへの追加
    @POST("event/create/{id}")
    fun addEvent(@Header("token") token:String,@Path("id") id :Int, @Body title:Map<String,String>) : Single<Event>

    //Eventへの参加
    @GET("event/join/{id}/{eid}")
    fun joinEvent(@Header("token") token:String,@Path("id") id: Int, @Path("eid") eid: String): Single<Event>

    @GET("{url}")
    fun getEvent(@Path("url") url: String): Single<Event>
  
    //Eventの削除
    @GET("event/withdrawal/{uid}/{eid}")
    fun deleteEvent(@Header("token") token:String,@Path("uid") id:Int,@Path("eid") eid:Int) : Single<Map<String,String>>

    //eventListの取得
    @GET("event/show/{id}")
    fun getEvent(@Header("token") token:String,@Path("id") id:Int):Single<MutableList<Event>>

    @POST("event/invitation/{uid}/{eid}")
    fun createUrl(@Header("token") token:String,@Path("uid") uid: Int, @Path("eid") eid: String): Single<Map<String,String>>
}