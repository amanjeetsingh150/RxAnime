package com.developers.rxanime.model

enum class CanvasAction {
    DRAW_OPERATOR,
    INITIAL_STATE
}

data class RxAnimeState(val canvasAction: CanvasAction = CanvasAction.INITIAL_STATE,
                        val currentData: MarbleData = MarbleData(),
                        val operatorCategory: OperatorCategory = OperatorCategory.FILTER)