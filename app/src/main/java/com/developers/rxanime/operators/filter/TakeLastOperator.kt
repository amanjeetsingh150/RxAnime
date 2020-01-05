package com.developers.rxanime.operators.filter

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.developers.rxanime.BaseView
import com.developers.rxanime.model.MarbleData
import com.developers.rxanime.util.toPx

class TakeLastOperator(context: Context, attrs: AttributeSet? = null) : BaseView(context = context, attributeSet = attrs) {

    /**
     * Takes last 2 emissions only
     */
    override fun drawOperator(canvas: Canvas?, currentData: MarbleData) {
        currentData.takeIf { it.data > 1 }?.apply {
            canvas?.drawLine(currentData.cx + 14.toPx().toFloat(), cy, cx + offset, cy, linePaint)
        }
    }

    override fun addEmissions(currentData: MarbleData) {
        currentData.takeIf { it.data > 1 }?.apply { emissions.add(this) }
    }

}