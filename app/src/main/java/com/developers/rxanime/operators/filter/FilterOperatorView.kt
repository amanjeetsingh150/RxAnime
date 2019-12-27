package com.developers.rxanime.operators.filter

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import com.developers.rxanime.BaseView
import com.developers.rxanime.model.MarbleData
import com.developers.rxanime.util.toPx

class FilterOperatorView(context: Context, attrs: AttributeSet? = null) : BaseView(context = context, attributeSet = attrs) {

    private var cx: Float = 0f

    /**
     * Filters even emissions
     */
    override fun drawOperator(canvas: Canvas?, currentData: MarbleData) {
        this.cx = currentData.cx
        currentData.takeIf { it.data % 2 == 0 }?.apply {
            Log.d("Base F ", "Evealuated base $offset")
            canvas?.drawLine(cx + 14.toPx().toFloat(), cy, cx + offset, cy, linePaint)
        }
    }


}