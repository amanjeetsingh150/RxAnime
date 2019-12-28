package com.developers.rxanime.operators.filter

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.developers.rxanime.BaseView
import com.developers.rxanime.model.MarbleData

class SkipOperatorView(context: Context, attrs: AttributeSet? = null) : BaseView(context = context, attributeSet = attrs) {

    override fun drawOperator(canvas: Canvas?, currentData: MarbleData) {
        currentData.data.takeIf { it > 3 }.apply {

        }
    }

}