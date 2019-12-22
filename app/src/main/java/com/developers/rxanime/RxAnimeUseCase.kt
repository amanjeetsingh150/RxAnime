package com.developers.rxanime

import com.developers.rxanime.model.CardItem

interface RxAnimeUseCase {

    fun fetchFilterOperators(): List<CardItem>

    fun fetchTransformingOperators(): List<CardItem>

}

class RxAnimeUseCaseImpl : RxAnimeUseCase {

    override fun fetchFilterOperators(): List<CardItem> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun fetchTransformingOperators(): List<CardItem> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}