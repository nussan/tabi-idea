package checkers.tabi_idea.provider

import android.util.Log
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.User
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class Repository{
    private var service: Service

    init {
        val okHttpClient = OkHttpClient.Builder().build()
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.10.4:3000/")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(okHttpClient)
                .build()
        service = retrofit.create(Service::class.java)
    }

    //userのlistをget
    fun getUser(callback: (User) -> Unit){
        service.user().enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>?, response: Response<User>?) {
                Log.d("tubasa" , "success")
                response?.let {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            callback(it)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<User>?, t: Throwable?) {
                Log.d("tubasa","cannot connect")
                var it: User = User(
                        0,
                        "新しいユーザー",
                        mutableListOf(
                                Event(0, "新しいイベント", mutableListOf(1), mutableListOf())
                        ))
                callback(it)
            }
        })
    }
}