package checkers.tabi_idea

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(var title: String): Parcelable {
    //とりあえずタイトルだけ持つデータクラス
}