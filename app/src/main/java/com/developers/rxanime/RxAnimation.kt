package com.developers.rxanime

import android.animation.AnimatorSet
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.developers.rxanime.model.MarbleData
import com.developers.rxanime.util.toPx

interface RxAnimationCallbacks {
    fun updateLeftMarbleY(leftMarbleY: Float)
    fun updateLeftMarbleRadius(leftMarbleRadius: Float)
    fun updateEmissionLineOffset(emissionLineOffset: Float)
    fun updateRightMarbleRadius(rightMarbleRadius: Float)
}

/**
 * Initializes the following animators and sets up with a [AnimatorSet]:
 * 1. CircleAnimator: Translates the Y coordinate of marble i.e property [MarbleData.cy] of [leftMarble].
 * 2. Left Marble ScaleAnimator: Scales radius of left marble i.e property property [MarbleData.radius] of [leftMarble].
 * 3. LineTranslateAnimator: Line translation of the emissions with a [RxAnimation.emissionLineOffset].
 * 4. Right Marble ScaleAnimator: Scale radius of right marble i.e property [RxAnimation.rightMarbleRadius]
 */
class RxAnimation(private var emissionLineOffset: Float,
                  private var rightMarbleRadius: Float,
                  private var leftLineStartX: Float,
                  private var leftMarble: MarbleData?,
                  private var rxAnimationCallbacks: RxAnimationCallbacks?) {

    private lateinit var propertyHolderY: PropertyValuesHolder
    private lateinit var propertyLeftCircleScale: PropertyValuesHolder

    data class Builder(var emissionLineOffset: Float = 0f,
                       var rightMarbleRadius: Float = 0f,
                       var leftLineStartX: Float = 0f,
                       var leftMarble: MarbleData? = null,
                       var rxAnimationCallbacks: RxAnimationCallbacks? = null) {

        fun emissionLineOffset(emissionLineOffset: Float) = apply { this.emissionLineOffset = emissionLineOffset }
        fun rightMarbleRadius(rightMarbleRadius: Float) = apply { this.rightMarbleRadius = rightMarbleRadius }
        fun leftMarble(leftMarble: MarbleData?) = apply { this.leftMarble = leftMarble }
        fun rxAnimationCallback(rxAnimationCallbacks: RxAnimationCallbacks?) = apply { this.rxAnimationCallbacks = rxAnimationCallbacks }
        fun leftLineStart(leftLineStartX: Float) = apply { this.leftLineStartX = leftLineStartX }

        fun build() = RxAnimation(emissionLineOffset, rightMarbleRadius,
                leftLineStartX, leftMarble, rxAnimationCallbacks)
    }

    fun createAnimator(): Pair<PropertyValuesHolder, AnimatorSet> {
        leftMarble?.let {
            propertyHolderY = PropertyValuesHolder.ofFloat(MARBLE_TRANSLATION_Y, it.cy, it.cy + Y_OFFSET)
            propertyLeftCircleScale = PropertyValuesHolder.ofFloat(MARBLE_SCALE_PROPERTY, it.radius, 10.toPx().toFloat())
        }
        val propertyValueTranslateX = PropertyValuesHolder.ofFloat(EMISSION_OFFSET_X, leftLineStartX, 100.toPx() * 2f)
        val propertyRightCircleScale = PropertyValuesHolder.ofFloat(MARBLE_SCALE_PROPERTY, rightMarbleRadius, 10.toPx().toFloat())


        // Animator for Y coordinate of marble
        val circleYAnimator = ValueAnimator().apply {
            duration = 900
            setValues(propertyHolderY)
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                leftMarble?.let {
                    it.cy = valueAnimator.animatedValue as Float
                    rxAnimationCallbacks?.updateLeftMarbleY(it.cy)
                }
            }
        }

        // Animator for scaling left marble
        val leftMarbleScaleAnimation = ValueAnimator().apply {
            duration = 300
            setValues(propertyLeftCircleScale)
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                leftMarble?.let {
                    it.radius = valueAnimator.animatedValue as Float
                    rxAnimationCallbacks?.updateLeftMarbleRadius(it.radius)
                }
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