package checkers.tabi_idea.extention

import android.content.Context
import android.util.AttributeSet
import android.view.View


class ViewExtention(context: Context?) : View (context){
    fun notPressTwice() {
        this.isEnabled = false
        this.postDelayed({
            this.isEnabled = true
        }, 500L)
    }
}