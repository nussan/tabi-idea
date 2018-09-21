package checkers.tabi_idea.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(
        @Json(name = "id")
        var id :Int,
        @Json(name = "title")
        var title: String,
        @Json(name = "member")
        var member: MutableList<Int>,
        @Json(name = "mmo")
        var mmo: MutableList<MindMapObject>
): Parcelable {
        override fun toString(): String {
                return title
        }
}