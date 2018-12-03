package checkers.tabi_idea.data

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
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
        var member: MutableList<Int> = mutableListOf(),
        @Json(name= "creator")
        var creator: String
): Parcelable,Comparable<Event> {
        override fun toString(): String {
                return title
        }
        override fun compareTo(other : Event) : Int {
                if (this.title.length > other.title.length) {
                        return 1
                } else if (this.title.length < other.title.length) {
                        return -1
                }
                return 0
        }
}

