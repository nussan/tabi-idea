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
        var viewIndex: Int,
        @Json(name = "text")
        var text: String,
        @Json(name = "positionX")
        var positionX: Float,
        @Json(name = "positionY")
        var positionY: Float,
//        @Json(name = "children")
//        var children: MutableList<Int>
        @Json(name = "parent")
        var parent : Int
) : Parcelable