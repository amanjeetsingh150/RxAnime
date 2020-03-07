package com.developers.rxanime

import androidx.lifecycle.ViewModel
import com.developers.rxanime.domain.FetchOperatorsUseCase
import com.developers.rxanime.model.OperatorCategory


class RxAnimeViewModel : ViewModel() {

    private val fetchOperatorUseCase by lazy { FetchOperatorsUseCase() }

    fun getOperators(operatorCategory: OperatorCategory) =
            fetchOperatorUseCase.invoke(operatorCategory)
}