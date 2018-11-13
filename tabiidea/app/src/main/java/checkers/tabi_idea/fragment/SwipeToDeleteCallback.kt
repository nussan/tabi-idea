package checkers.tabi_idea.fragment

import android.content.Context
import android.support.v7.widget.helper.ItemTouchHelper

abstract class SwipeToDeleteCallback(context: Context) : ItemTouchHelper.SimpleCallback(0,(ItemTouchHelper.LEFT)) {

}
