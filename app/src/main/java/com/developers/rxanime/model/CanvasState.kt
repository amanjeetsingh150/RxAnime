package com.developers.rxanime.model

enum class CanvasState {
    DRAW_TEXT_MARBLE,
    DRAW_OPERATOR,
    TRANSLATING_STATE
}

data class RxAnimeState(val canvasState: CanvasState = CanvasState.TRANSLATING_STATE)