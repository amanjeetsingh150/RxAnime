package com.developers.rxanime.operators.filter

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import com.developers.rxanime.BaseView
import com.developers.rxanime.model.MarbleData
import com.developers.rxanime.util.awaitEnd
import kotlinx.coroutines.launch

class TakeOperatorView(context: Context, attrs: AttributeSet? = null) : BaseView(context = context, attributeSet = attrs) {

    private var cx: Float = 0f
    private var offset = 0f
    private val lineTranslateAnimator by lazy {
        coroutineScope.launch {
            ValueAnimator.ofFloat(cx, cx + centreDistance).apply {
                duration = 500
                interpolator = LinearInterpolator()
                addUpdateListener {
                    offset = it.animatedValue as Float
                    invalidate()
                }
                awaitEnd()
                // TODO: draw circle
            }
        }
    }

    init {
        lineTranslateAnimator.start()
    }

    override fun drawOperator(canvas: Canvas?, currentData: MarbleData) {
        currentData.takeIf { it.data < 4 }?.apply {
            canvas?.drawLine(cx, cy, cx + offset, cy, linePaint)
        }
    }

}