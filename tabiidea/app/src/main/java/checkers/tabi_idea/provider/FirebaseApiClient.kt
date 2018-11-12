package checkers.tabi_idea.provider

import android.util.Log
import checkers.tabi_idea.data.MindMapObject
import com.google.firebase.database.*

class FirebaseApiClient(event_id: String) {
    private val ref = FirebaseDatabase.getInstance().getReference(event_id)

    //firebaseからeidのmmoをゲット
    fun getMmo(callback: (Collection<Pair<String, MindMapObject>>) -> Unit) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                callback(dataSnapshot.children.mapNotNull {
                    it.key!! to it.getValue(MindMapObject::class.java)!!
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("errGetMmo", databaseError.toString())
            }
        })
    }

    fun setListener(listener: ChildEventListener) {
        ref.addChildEventListener(listener!!)
    }

    //eventをfbにadd
    fun addEventtoFb() {
        val mmo = MindMapObject(0, "旅行", 0f, 0f, 0)
        ref.push().setValue(mmo)
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
}