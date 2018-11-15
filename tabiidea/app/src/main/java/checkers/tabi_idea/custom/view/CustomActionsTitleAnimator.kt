package checkers.tabi_idea.custom.view

import android.view.View
import android.view.animation.OvershootInterpolator
import com.commit451.quickactionview.Action
import com.commit451.quickactionview.ActionsTitleInAnimator
import com.commit451.quickactionview.ActionsTitleOutAnimator

class CustomActionsTitleAnimator : ActionsTitleInAnimator, ActionsTitleOutAnimator {

    override fun animateActionTitleIn(action: Action, index: Int, view: View) {
        view.alpha = 0.0f
        view.scaleX = 0.0f
        view.scaleY = 0.0f
        view.animate()
                .alpha(1.0f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setInterpolator(OvershootInterpolator()).duration = DURATION.toLong()
    }

    override fun animateActionTitleOut(action: Action, index: Int, view: View): Int {
        view.animate()
                .alpha(0.0f)
                .scaleX(0.0f)
                .scaleY(0.0f).duration = DURATION.toLong()
        return DURATION
    }

    companion object {

        private val DURATION = 200 //ms
    }
}
