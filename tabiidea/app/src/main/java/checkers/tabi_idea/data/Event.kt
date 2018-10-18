package checkers.tabi_idea.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import org.jetbrains.annotations.Nullable

@Parcelize
data class Event(
        @Json(name = "id")
        var id :Int,
        @Json(name = "title")
        var title: String,
        @Json(name = "member")
        var member: MutableList<Int> = mutableListOf()
): Parcelable {
        override fun toString(): String {
                return title
        }
}