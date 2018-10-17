package checkers.tabi_idea.extention

import android.util.Log
import checkers.tabi_idea.data.MindMapObject

class ListtoMutableList{
    fun listConverter(mindMapObject:MutableList<MindMapObject>,list:Collection<MindMapObject>): MutableList<MindMapObject> {
        list.forEach{
            Log.d(javaClass.simpleName, "${mindMapObject.size}, ${it.viewIndex}")
            mindMapObject.add(it.viewIndex, it)
        }
        return mindMapObject
    }
}