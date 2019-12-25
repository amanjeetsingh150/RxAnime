package com.developers.rxanime

import android.content.Context
import android.view.View
import com.developers.rxanime.model.Category
import com.developers.rxanime.model.FilterOperator
import com.developers.rxanime.model.OperatorCategory
import com.developers.rxanime.model.Transforming
import com.developers.rxanime.operators.filter.FilterOperatorView
import com.developers.rxanime.operators.filter.SkipOperatorView
import com.developers.rxanime.operators.filter.TakeLastOperator
import com.developers.rxanime.operators.filter.TakeOperatorView
import com.developers.rxanime.operators.transforming.BufferOperatorView
import com.developers.rxanime.operators.transforming.MapOperatorView
import com.developers.rxanime.util.getOperator

class OperatorViewInitializer(private val categoryList: List<Category>,
                              private val context: Context) {

    fun fetchFilterViews(): List<View> {
        val filterViews = mutableListOf<View>()

        categoryList.filter { it.name == OperatorCategory.FILTER }
                .forEach {
                    it.operators.forEach { operator ->
                        when (operator.name.getOperator<FilterOperator>()) {
                            FilterOperator.TAKE -> filterViews.add(FilterOperatorView(context = context))
                            FilterOperator.FILTER -> filterViews.add(TakeOperatorView(context = context))
                            FilterOperator.SKIP -> filterViews.add(SkipOperatorView(context = context))
                            FilterOperator.TAKE_LAST -> filterViews.add(TakeLastOperator(context = context))
                        }
                    }
                }
        return filterViews
    }

    fun fetchTransformingViews(): List<View> {
        val transformingViews = mutableListOf<View>()
        categoryList.filter { it.name == OperatorCategory.FILTER }
                .forEach {
                    it.operators.forEach { operator ->
                        when (operator.name.getOperator<Transforming>()) {
                            Transforming.MAP -> transformingViews.add(MapOperatorView(context = context))
                            Transforming.BUFFER -> transformingViews.add(BufferOperatorView(context = context))
                        }
                    }
                }
        return transformingViews
    }
}