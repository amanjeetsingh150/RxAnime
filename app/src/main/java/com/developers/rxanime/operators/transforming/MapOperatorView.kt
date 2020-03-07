package com.developers.rxanime.operators.transforming

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.developers.rxanime.BaseView
import com.developers.rxanime.model.MarbleData
import com.developers.rxanime.util.toPx

class MapOperatorView(context: Context, attrs: AttributeSet? = null) : BaseView(context = context, attributeSet = attrs) {


    override fun drawOperator(canvas: Canvas?, currentData: MarbleData) {
        currentData.apply {
            canvas?.drawLine(currentData.cx + 14.toPx().toFloat(), cy, cx + rxFrame.emissionLineX, cy, linePaint)
            canvas?.drawCircle(rightLineStart, cy, rightCircleRadius, leftCirclePaint)
        }
    }

    override fun addEmissions(currentData: MarbleData) {
        val operatorCurrentData = currentData.copy(data = currentData.data * 2)
        emissions.add(operatorCurrentData)
    }

}