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
    fun editUser(@Path("id") id:Int,@Body editName:Map<String,String>) : Single<User>

    //Eventへの追加
    @POST("event/create/{id}")
    fun addEvent(@Path("id") id :Int, @Body title:Map<String,String>) : Single<Event>

    //Eventへの参加
    @POST("event/join/{id}")
    fun joinEvent(@Path("id") id:Int,@Body password:Map<String,String>) : Single<Event>

    //eventListの取得
    @GET("event/show/{id}")
    fun getEvent(@Path("id") id:Int):Single<MutableList<Event>>

    //MindMapObjectを追加
    @POST("mindmap/{id}")
    fun addMmo(@Path("id") id:Int, mmo:Map<String,String>):Call<MindMapObject>

    //viewIndexのMindMapObject削除
    @DELETE("event/{id}")
    fun deleteMmo(@Path("id") id : Int){}

    //mmoのtext変更
    fun changeMmoText(){}

    //メンバーを追加
    fun addMember(){}

    //mmoの座標変更 <- これいる？
    fun changeMmoPosition(){}
    //カットアンドペースト <- これいる？
    fun cutAndPaste(){}
    //いいね機能 <- これいる？
    fun addGood(){}
}