package com.developers.rxanime.model

enum class CanvasAction {
    INITIAL_STATE,
    DRAW_OPERATOR_WITH_LINE
}

data class RxAnimeState(val canvasAction: CanvasAction = CanvasAction.INITIAL_STATE,
                        val currentData: MarbleData = MarbleData(),
                        val operatorCategory: OperatorCategory = OperatorCategory.FILTER)