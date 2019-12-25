package com.developers.rxanime

import com.developers.rxanime.model.CardItem
import com.developers.rxanime.model.Category

interface RxAnimeUseCase {

    fun fetchFilterOperators(categoryList: List<Category>): List<CardItem>

}

class RxAnimeUseCaseImpl : RxAnimeUseCase {

    private val cardItemList = mutableListOf<CardItem>()

    override fun fetchFilterOperators(categoryList: List<Category>): List<CardItem> {
        categoryList.forEach {
            it.operators.forEach { operator ->
                cardItemList.add(CardItem(name = operator.name, description = operator.description,
                        htmlLink = operator.operatorLink, operatorCategory = it.name))
            }
        }
        return cardItemList
    }
}