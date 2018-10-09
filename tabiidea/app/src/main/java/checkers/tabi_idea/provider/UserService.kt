package checkers.tabi_idea.provider

import checkers.tabi_idea.data.User
import retrofit2.http.GET

interface UserService {
    @GET("mockapi")
    fun user():retrofit2.Call<User>
}