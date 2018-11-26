package checkers.tabi_idea.provider

import checkers.tabi_idea.data.User
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TokenService {
    //userの追加
    @POST("auth/create")
    fun addUser(@Body user:Map<String,String>) : Single<Map<String,String>>

    //tokenの取得
    @GET("auth/gettoken/{uuid}")
    fun getToken(@Path("uuid") uuid:String) : Single<Map<String,String>>
}