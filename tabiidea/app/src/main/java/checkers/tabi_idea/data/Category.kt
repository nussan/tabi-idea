package checkers.tabi_idea.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
        var name: String,
        var color: String,
        var id: Int = 0
) : Parcelable {
    override fun toString(): String {
        return name
    }
}
