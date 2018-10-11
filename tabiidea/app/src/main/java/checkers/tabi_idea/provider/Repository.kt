package checkers.tabi_idea.provider

import android.util.Log
import checkers.tabi_idea.data.Event
import checkers.tabi_idea.data.MindMapObject
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
    private var requestService: RequestService

    init {
        val okHttpClient = OkHttpClient.Builder().build()
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
                .baseUrl("https://damp-peak-50918.herokuapp.com/")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(okHttpClient)
                .build()
        requestService = retrofit.create(RequestService::class.java)
    }

    //user情報をget
    fun getUserCallback(callback: (User) -> Unit){
        requestService.getUser().enqueue(object : Callback<User> {
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
                val user: User = User(
                        0,
                        "たきかわ",
                        mutableListOf(
                                Event(0,"研究室旅行", mutableListOf(), mutableListOf(
                                        MindMapObject(0, "旅行旅行旅行旅行旅行旅行", 1f / 2, 1f / 2, mutableListOf(1, 2, 3, 4)),
                                        MindMapObject(1, "行先", 1f / 2, 1f / 4, mutableListOf(5, 6, 7, 8)),
                                        MindMapObject(2, "予算", 1f / 4, 1f / 2, mutableListOf()),
                                        MindMapObject(3, "食事", 1f / 2, 3f / 4, mutableListOf()),
                                        MindMapObject(4, "宿泊", 3f / 4, 1f / 2, mutableListOf()),
                                        MindMapObject(5, "熊本", 1f / 3, 1f / 15, mutableListOf()),
                                        MindMapObject(6, "山口", 3f / 4, 1f / 5, mutableListOf()),
                                        MindMapObject(7, "井澤", 1f / 4, 1f / 5, mutableListOf()),
                                        MindMapObject(8, "瀧川", 5f / 8, 1f / 13f, mutableListOf())
                                )),
                                Event(1,"学会", mutableListOf(), mutableListOf()),
                                Event(2,"USA", mutableListOf(), mutableListOf())
                        ))
                callback(user)
            }
        })
    }

    //mmoを更新
    fun updateMmoCallback(callback: (List<MindMapObject>) -> Unit){
        requestService.updateMmo().enqueue(object : Callback<List<MindMapObject>> {
            override fun onResponse(call: Call<List<MindMapObject>>?, response: Response<List<MindMapObject>>?) {
                Log.d("tubasa2" , "success")
                response?.let {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            callback(it)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<MindMapObject>>?, t: Throwable?) {
                Log.d("tubasa2","cannot connect")
                val mindmapobject: List<MindMapObject> = mutableListOf(
                        MindMapObject(0, "旅行", 1f / 2, 1f / 2, mutableListOf(1, 2, 3, 4)),
                        MindMapObject(1, "行先", 1f / 2, 1f / 4, mutableListOf(5, 6, 7, 8)),
                        MindMapObject(2, "予算", 1f / 4, 1f / 2, mutableListOf()),
                        MindMapObject(3, "食事", 1f / 2, 3f / 4, mutableListOf()),
                        MindMapObject(4, "宿泊", 3f / 4, 1f / 2, mutableListOf()),
                        MindMapObject(5, "熊本", 1f / 3, 1f / 15, mutableListOf()),
                        MindMapObject(6, "山口", 3f / 4, 1f / 5, mutableListOf()),
                        MindMapObject(7, "井澤", 1f / 4, 1f / 5, mutableListOf()),
                        MindMapObject(8, "瀧川", 5f / 8, 1f / 13f, mutableListOf())
                )
                callback(mindmapobject)
            }
        })
    }

    //eventを追加
    fun addEventCallback(callback: (Event) -> Unit){
        requestService.addEvent().enqueue(object : Callback<Event> {
            override fun onResponse(call: Call<Event>?, response: Response<Event>?) {
                Log.d("tubasa2" , "success")
                response?.let {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            callback(it)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<Event>?, t: Throwable?) {
                Log.d("tubasa2","cannot connect")
            }
        })
    }
}