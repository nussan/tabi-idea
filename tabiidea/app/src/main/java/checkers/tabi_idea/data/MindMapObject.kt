package checkers.tabi_idea.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*
マインドマップの内、各テキストビューを生成するために必要な情報をまとめたデータクラス
 */
@Parcelize
data class MindMapObject(var viewIndex: Int, var text: String, var positionX: Float, var positionY: Float, var children: MutableList<Int>) : Parcelable