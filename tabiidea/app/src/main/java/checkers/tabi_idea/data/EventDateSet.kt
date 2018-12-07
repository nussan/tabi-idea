package checkers.tabi_idea.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

/*
マインドマップの内、各テキストビューを生成するために必要な情報をまとめたデータクラス
 */
@Parcelize
data class EventDateSet(
        @Json(name = "text")
        var text: String = "",
        @Json(name = "point")
        var point : Int = 0,
        @Json(name = "nameList")
        var nameList: MutableList<String> = mutableListOf(),
        @Json(name = "likeList")
        var likeList: MutableList<Int> = mutableListOf()
) : Parcelable