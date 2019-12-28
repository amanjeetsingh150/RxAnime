package com.developers.rxanime.operators.filter

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.developers.rxanime.BaseView
import com.developers.rxanime.model.MarbleData
import com.developers.rxanime.util.toPx

class TakeOperatorView(context: Context, attrs: AttributeSet? = null) : BaseView(context = context, attributeSet = attrs) {


    /**
     * Take first three emissions
     */
    override fun drawOperator(canvas: Canvas?, currentData: MarbleData) {
        currentData.takeIf { it.data < 3 }?.apply {
            canvas?.drawLine(cx + 14.toPx().toFloat(), cy, cx + offset, cy, linePaint)
        }
    }

}