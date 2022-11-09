package com.saeware.storyapp.utils

import android.animation.ObjectAnimator
import android.view.View

object AnimationUtility {
    fun setFadeViewAnimation(view: View?) =
        ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(300)
}