package checkers.tabi_idea.provider

import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.MindMapObject
import checkers.tabi_idea.data.User
import com.squareup.moshi.Json
import retrofit2.Call
import retrofit2.http.*

interface RequestService {
    //user情報の取得
    @GET("home/index/{id}")
    fun getUser(@Path("id") id:String) :Call<User>

    //最新のmmoを取得
    @GET("home")
    fun updateMmo() : Call<List<MindMapObject>>

    //Eventへの追加
    @POST("home/{id}")
    fun addEvent(@Path("id") id :Int,@Body title:String) : Call<MutableList<Event>>

//    //Event新規作成(中間データベースへの追加 )
//    @POST("user_event")
//    fun addUserEvent() {}

    //MindMapObjectを追加
    @POST("mindmap/{id}")
    fun addMmo(@Path("id") id:Int, mmoJson:String):Call<MindMapObject>

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