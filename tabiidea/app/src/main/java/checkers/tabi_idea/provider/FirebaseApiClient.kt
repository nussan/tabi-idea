package checkers.tabi_idea.provider

import checkers.tabi_idea.data.MindMapObject
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.FirebaseDatabase

class FirebaseApiClient(event_id: String) {
    private val ref = FirebaseDatabase.getInstance().getReference(event_id)

    fun setListener(listener: ChildEventListener) {
        ref.addChildEventListener(listener!!)
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