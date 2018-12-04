package checkers.tabi_idea.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

/*
マインドマップの内、各テキストビューを生成するために必要な情報をまとめたデータクラス
 */
@Parcelize
data class MindMapObject(
        @Json(name = "viewIndex")
        var viewIndex: Int = 0,
        @Json(name = "text")
        var text: String = "",
        @Json(name = "positionX")
        var positionX: Float = 0f,
        @Json(name = "positionY")
        var positionY: Float = 0f,
        @Json(name = "parent")
        var parent : String ="",
        @Json(name = "point")
        var point : Int = 0,
        @Json(name = "type")
        var type: String = "",
        @Json(name = "likeList")
        var likeList: MutableList<Int> = mutableListOf()
) : Parcelable