package com.developers.rxanime

import androidx.lifecycle.ViewModel
import com.developers.rxanime.model.CardItem
import com.developers.rxanime.model.Category
import com.developers.rxanime.model.DisplayData
import com.developers.rxanime.model.OperatorCategory
import com.squareup.moshi.Moshi

class RxAnimeViewModel : ViewModel() {

    private val moshi = Moshi.Builder().build()
    private val adapter by lazy { moshi.adapter<DisplayData>(DisplayData::class.java) }
    private val rxAnimeUseCase by lazy { RxAnimeUseCaseImpl() }

    fun fetchCategories(displayJson: String): DisplayData? {
        return adapter.fromJson(displayJson)
    }

    fun fetchCurrentOperators(categoryList: List<Category>?): List<CardItem> {
        categoryList?.let {
            return rxAnimeUseCase.fetchFilterOperators(it)
        }
        return emptyList()
    }


}