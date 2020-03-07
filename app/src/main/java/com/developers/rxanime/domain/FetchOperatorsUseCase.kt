package com.developers.rxanime.domain

import com.developers.rxanime.model.Operators
import com.developers.rxanime.model.FilterOperator
import com.developers.rxanime.model.OperatorCategory
import com.developers.rxanime.model.OperatorCategory.FILTER
import com.developers.rxanime.model.OperatorCategory.TRANSFORM
import com.developers.rxanime.model.TransformingOperators

class FetchOperatorsUseCase {

    fun invoke(category: OperatorCategory): List<Operators> {
        return when (category) {
            FILTER -> FilterOperator.values().toList()
            TRANSFORM -> TransformingOperators.values().toList()
        }
    }
}
