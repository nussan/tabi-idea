package checkers.tabi_idea

import android.os.Parcelable
import checkers.tabi_idea.Event
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
        var id: Int,
        var name: String,
        var eventList: MutableList<Event>
) : Parcelable