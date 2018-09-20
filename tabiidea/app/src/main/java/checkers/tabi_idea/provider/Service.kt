package checkers.tabi_idea.provider

import checkers.tabi_idea.data.User
import retrofit2.http.GET

interface Service {
    @GET("user")
    fun user():retrofit2.Call<User>
}