package checkers.tabi_idea.provider

import checkers.tabi_idea.data.MindMapObject
import retrofit2.http.GET

interface MmoService {
    @GET("mindmap")
    fun mmo() : retrofit2.Call<List<MindMapObject>>
}