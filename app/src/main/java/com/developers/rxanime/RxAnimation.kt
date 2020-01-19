package com.developers.rxanime

import android.animation.AnimatorSet
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.developers.rxanime.util.toPx

interface RxAnimationCallbacks {
    fun updateLeftMarbleY(leftMarbleY: Float)
    fun updateLeftMarbleRadius(leftMarbleRadius: Float)
    fun updateEmissionLineOffset(emissionLineOffset: Float)
    fun updateRightMarbleRadius(rightMarbleRadius: Float)
}

/**
 * Initializes the following animators and sets up with a [AnimatorSet]:
 * 1. CircleAnimator: Translates the Y coordinate of marble i.e property [RxAnimation.leftMarbleY].
 * 2. ScaleAnimator: Scales radius of left marble i.e property [RxAnimation.leftMarbleRadius].
 * 3. LineTranslateAnimator: Line translation of the emissions with a [RxAnimation.emissionLineOffset].
 * 4. Right Marble ScaleAnimator: Scale radius of right marble i.e property [RxAnimation.rightMarbleRadius]
 */
class RxAnimation(private var leftMarbleY: Float,
                  private var leftMarbleRadius: Float,
                  private var emissionLineOffset: Float,
                  private var rightMarbleRadius: Float,
                  private var leftLineStartX: Float,
                  private var rxAnimationCallbacks: RxAnimationCallbacks?) {


    data class Builder(var leftMarbleY: Float = 0f,
                       var leftMarbleRadius: Float = 0f,
                       var emissionLineOffset: Float = 0f,
                       var rightMarbleRadius: Float = 0f,
                       var leftLineStartX: Float = 0f,
                       var rxAnimationCallbacks: RxAnimationCallbacks? = null) {

        fun leftMarbleY(leftMarbleY: Float) = apply { this.leftMarbleY = leftMarbleY }
        fun leftMarbleRadius(leftMarbleRadius: Float) = apply { this.leftMarbleRadius = leftMarbleRadius }
        fun emissionLineOffset(emissionLineOffset: Float) = apply { this.emissionLineOffset = emissionLineOffset }
        fun rightMarbleRadius(rightMarbleRadius: Float) = apply { this.rightMarbleRadius = rightMarbleRadius }
        fun rxAnimationCallback(rxAnimationCallbacks: RxAnimationCallbacks?) = apply { this.rxAnimationCallbacks = rxAnimationCallbacks }
        fun leftLineStart(leftLineStartX: Float) = apply { this.leftLineStartX = leftLineStartX }

        fun build() = RxAnimation(leftMarbleY, leftMarbleRadius,
                emissionLineOffset, rightMarbleRadius, leftLineStartX, rxAnimationCallbacks)
    }

    fun createAnimator(): Pair<PropertyValuesHolder, AnimatorSet> {
        val propertyHolderY = PropertyValuesHolder.ofFloat(MARBLE_TRANSLATION_Y, leftMarbleY, leftMarbleY + Y_OFFSET)
        val propertyLeftCircleScale = PropertyValuesHolder.ofFloat(MARBLE_SCALE_PROPERTY, leftMarbleRadius, 10.toPx().toFloat())
        val propertyValueTranslateX = PropertyValuesHolder.ofFloat(EMISSION_OFFSET_X, leftLineStartX, 100.toPx() * 2f)
        val propertyRightCircleScale = PropertyValuesHolder.ofFloat(MARBLE_SCALE_PROPERTY, rightMarbleRadius, 10.toPx().toFloat())


        // Animator for Y coordinate of marble
        val circleYAnimator = ValueAnimator().apply {
            duration = 900
            setValues(propertyHolderY)
            interpolator = LinearInterpolator()
            addUpdateListener {
                leftMarbleY = it.animatedValue as Float
                rxAnimationCallbacks?.updateLeftMarbleY(leftMarbleY)
            }
        }

        // Animator for scaling left marble
        val leftMarbleScaleAnimation = ValueAnimator().apply {
            duration = 300
            setValues(propertyLeftCircleScale)
            interpolator = LinearInterpolator()
            addUpdateListener {
                leftMarbleRadius = it.animatedValue as Float
                rxAnimationCallbacks?.updateLeftMarbleRadius(leftMarbleRadius)
            }
        }

        // Animator for scaling right marble
        val rightMarbleScaleAnimator = ValueAnimator().apply {
            duration = 400
            setValues(propertyRightCircleScale)
            interpolator = LinearInterpolator()
            addUpdateListener {
                rightMarbleRadius = it.animatedValue as Float
                rxAnimationCallbacks?.updateRightMarbleRadius(rightMarbleRadius)
            }
        }

        // Animator for translating the line to show emission
        val lineTranslateAnimator = ValueAnimator().apply {
            duration = 900
            setValues(propertyValueTranslateX)
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                emissionLineOffset = it.animatedValue as Float
                rxAnimationCallbacks?.updateEmissionLineOffset(emissionLineOffset)
            }
        }

        val lineMarbleAnimatorSet = AnimatorSet().apply {
            playSequentially(lineTranslateAnimator, rightMarbleScaleAnimator)
            interpolator = LinearInterpolator()
        }

        val marbleAnimatorSet = AnimatorSet().apply {
            playSequentially(circleYAnimator, leftMarbleScaleAnimation)
            playTogether(lineMarbleAnimatorSet)
            interpolator = LinearInterpolator()
        }

        return Pair(propertyHolderY, marbleAnimatorSet)
    }


    companion object {
        private const val MARBLE_SCALE_PROPERTY = "MARBLE_SCALE"
        private const val MARBLE_TRANSLATION_Y = "MARBLE_TRANSLATION"
        private const val EMISSION_OFFSET_X = "EMISSION_X"
        private const val Y_OFFSET = 200.toFloat()
    }
}