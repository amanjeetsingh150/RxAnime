package com.developers.rxanime.operators.filter

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import com.developers.rxanime.BaseView
import com.developers.rxanime.model.MarbleData
import com.developers.rxanime.util.toPx

class FilterOperatorView(context: Context, attrs: AttributeSet? = null) : BaseView(context = context, attributeSet = attrs) {

    /**
     * Filters even emissions
     */
    override fun drawOperator(canvas: Canvas?, currentData: MarbleData) {
        currentData.takeIf { it.data % 2 == 0 }?.apply {
            canvas?.drawLine(currentData.cx + 14.toPx().toFloat(), cy, cx + offset, cy, linePaint)
        }
    }


}