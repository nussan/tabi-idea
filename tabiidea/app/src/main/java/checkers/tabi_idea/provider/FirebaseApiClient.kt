package checkers.tabi_idea.provider

import android.util.Log
import checkers.tabi_idea.data.MindMapObject
import com.google.firebase.database.*

class FirebaseApiClient(event_id: String) {
    private val ref = FirebaseDatabase.getInstance().getReference(event_id)

    fun setListener(listener: ChildEventListener) {
        ref.addChildEventListener(listener)
    }

    //mmoをfbにadd
    fun addMmo(mmo: MindMapObject) {
        ref.push().setValue(mmo)
    }

    //mmoのtextをアップデート
    fun updateMmo(pair: Pair<String, MindMapObject>) {
        ref.child(pair.first).setValue(pair.second)
    }

    fun deleteMmo(pair: Pair<String, MindMapObject>) {
        ref.child(pair.first).removeValue()
    }

    fun removeListener(listener: ChildEventListener) {
        ref.removeEventListener(listener)
    }

    //eventをfbにadd
    fun addEventToFb() {
        val mmo = MindMapObject(0, "旅行", 0f, 0f, "", 0, "root")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.mapNotNull {
                    val rootKey = it.key!!
                    Log.d("Repository", rootKey)
                    updateMmo(rootKey to MindMapObject(0, "旅行", 0f, 0f, rootKey, 0, "root"))
                    val ml = mutableListOf(
                            MindMapObject(1, "行先", 200f, 200f, rootKey, 0, "行先"),
                            MindMapObject(2, "予算", 200f, -200f, rootKey, 0, "予算"),
                            MindMapObject(3, "食事", -200f, 200f, rootKey, 0, "食物"),
                            MindMapObject(4, "宿泊", -200f, -200f, rootKey, 0, "宿泊"))
                    ml.forEach {child ->
                        addMmo(child)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("errGetMmo", databaseError.toString())
            }
        })
        ref.push().setValue(mmo)
    }

    fun addEventToFbMock() {

    }
}