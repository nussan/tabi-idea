package checkers.tabi_idea.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat

@Parcelize
data class Event(
        @Json(name = "id")
        var id :Int,
        @Json(name = "title")
        var title: String,
        @Json(name = "member")
        var member: MutableList<Int> = mutableListOf(),
        @Json(name= "creator")
        var creator: String,
        @Json(name = "updated_at")
        var upadated: String ,
        @Json(name = "created_at")
        var created: String
) : Parcelable, Comparable<Event> {
        override fun toString(): String {
                return title
        }

        override fun compareTo(other: Event): Int {
                val sdFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                val thisdate = sdFormat.parse(this.created)
                val otherdate = sdFormat.parse(other.created)
                if (thisdate.after(otherdate)) {
                        return 1
                } else if (otherdate.after(thisdate)) {
                        return -1
                }
                return 0
        }
}

