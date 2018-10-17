package checkers.tabi_idea.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
        @Json(name = "id")
        var id: Int,
        @Json(name = "name")
        var name: String
) : Parcelable