package com.developers.rxanime.domain

import com.developers.rxanime.model.FilterOperator
import com.developers.rxanime.model.OperatorCategory
import com.developers.rxanime.model.TransformingOperators
import org.junit.Test

class FetchOperatorsUseCaseTest {

    @Test
    fun `when the operators are fetched for filter operators then return filter operators data`() {
        // given
        val fetchOperatorsUseCase = FetchOperatorsUseCase()

        // when
        val result = fetchOperatorsUseCase.invoke(OperatorCategory.FILTER)

        // then
        result.first() is FilterOperator
    }

    @Test
    fun `when the operators are fetched for transforming operators then return transforming operators`() {
        // given
        val fetchOperatorsUseCase = FetchOperatorsUseCase()

        // when
        val result = fetchOperatorsUseCase.invoke(OperatorCategory.TRANSFORM)

        // then
        result.first() is TransformingOperators
    }
}