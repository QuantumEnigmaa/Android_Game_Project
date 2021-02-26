package fr.isen.monsterfighter.Animations

import android.app.Activity
import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView

open class LoadingAnimation constructor(private val context: Activity, animationName: String) {
    private var lottieAnimationView : LottieAnimationView = LottieAnimationView(context)

    private var cLayout : ConstraintLayout = ConstraintLayout(context)
    private var cLayoutParams : ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
        ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT
    )

    private val lLayoutParams : ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
        ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT
    )

    init {
        lottieAnimationView.setAnimation(animationName)
        lottieAnimationView.layoutParams = lLayoutParams

        cLayout.setBackgroundColor(Color.BLACK)
        cLayout.addView(lottieAnimationView)
    }

    fun playAnimation() {
        context.setContentView(cLayout, cLayoutParams)
        lottieAnimationView.playAnimation()
    }

    fun stopAnimation(root: ConstraintLayout) {
        lottieAnimationView.cancelAnimation()
        context.setContentView(root)
    }
}