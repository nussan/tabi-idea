package checkers.tabi_idea.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(var title: String, var mindMapObjectList: MutableList<MindMapObject>): Parcelable {
    override fun toString(): String {
        return title
    }
}