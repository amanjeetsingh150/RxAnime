package com.developers.rxanime.operators.filter

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.developers.rxanime.BaseView
import com.developers.rxanime.model.MarbleData
import com.developers.rxanime.util.toPx

class SkipOperatorView(context: Context, attrs: AttributeSet? = null) : BaseView(context = context, attributeSet = attrs) {

    /**
     * Skips the first three emissions
     */
    override fun drawOperator(canvas: Canvas?, currentData: MarbleData) {
        currentData.takeIf { it.data > 2 }?.apply {
            canvas?.drawLine(currentData.cx + 14.toPx().toFloat(), cy, cx + offset, cy, linePaint)
        }
    }

    override fun addEmissions(currentData: MarbleData) {
        currentData.takeIf { it.data > 2 }?.apply { emissions.add(this) }
    }

}