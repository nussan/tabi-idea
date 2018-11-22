package checkers.tabi_idea.custom.view

import android.graphics.Point
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.LinearInterpolator
import com.commit451.quickactionview.Action
import com.commit451.quickactionview.ActionView
import com.commit451.quickactionview.ActionsInAnimator
import com.commit451.quickactionview.QuickActionView

class CustomActionsInAnimator(private val mQuickActionView: QuickActionView) : ActionsInAnimator {

    private val mInterpolator = LinearInterpolator()

    override fun animateActionIn(action: Action, index: Int, view: ActionView, center: Point) {
        view.animate()
                .alpha(1.0f)
                .setDuration(200).interpolator = mInterpolator
    }

    override fun animateIndicatorIn(indicator: View) {
        indicator.alpha = 0f
        indicator.animate().alpha(1f).duration = 200
    }

    override fun animateScrimIn(scrim: View) {
        val center = mQuickActionView.centerPoint
//        if (Build.VERSION.SDK_INT >= 21 && center != null) {
//            ViewAnimationUtils.createCircularReveal(scrim, center.x, center.y, 0f, Math.max(scrim.height, scrim.width).toFloat())
//                    .start()
//        } else {
            scrim.alpha = 0f
            scrim.animate().alpha(1f).duration = 200
//        }
    }
}
