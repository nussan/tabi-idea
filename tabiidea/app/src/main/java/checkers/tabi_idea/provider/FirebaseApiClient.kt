package checkers.tabi_idea.provider

import android.util.Log
import checkers.tabi_idea.data.MindMapObject
import com.google.firebase.database.*

class FirebaseApiClient(event_id: String) {
    private val oldRef = FirebaseDatabase.getInstance().getReference(event_id)
    private val ref = oldRef.child("mmoMaps")


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