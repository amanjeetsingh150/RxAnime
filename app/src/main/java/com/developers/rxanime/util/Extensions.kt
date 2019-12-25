package com.developers.rxanime.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import com.developers.rxanime.model.BaseOperator as Base

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.spToPx(): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(),
        Resources.getSystem().displayMetrics).toInt()


inline fun <reified BaseOperator : Enum<BaseOperator>> String.getOperator(): BaseOperator? {
    return enumValues<BaseOperator>().firstOrNull { (it as Base).getOperatorName() == this }
}

suspend fun Animator.awaitEnd() = suspendCancellableCoroutine<Unit> { cont ->

    // Invoke cancel on cancellation
    cont.invokeOnCancellation { cancel() }

    addListener(object : AnimatorListenerAdapter() {

        private var endedSuccessFully = true

        override fun onAnimationCancel(animation: Animator?) {
            super.onAnimationCancel(animation)
            endedSuccessFully = false
        }

        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)

            // Prevent leaks
            animation?.removeListener(this)

            // Resume if success
            if (cont.isActive) {
                if (endedSuccessFully) {
                    cont.resume(Unit)
                } else {
                    cont.cancel()
                }
            }

        }
    })
}

suspend fun View.awaitViewDrawn() = suspendCancellableCoroutine<Unit> { cont ->

    val listener = object : View.OnLayoutChangeListener {
        override fun onLayoutChange(view: View?, left: Int, top: Int, right: Int, bottom: Int,
                                    oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {

            // Remove to prevent leaks
            view?.removeOnLayoutChangeListener(this)

            cont.resume(Unit)
        }
    }

    cont.invokeOnCancellation {
        // Remove if the coroutine cancels itself
        removeOnLayoutChangeListener(listener)
    }

    // Finally adding the listener
    addOnLayoutChangeListener(listener)
}