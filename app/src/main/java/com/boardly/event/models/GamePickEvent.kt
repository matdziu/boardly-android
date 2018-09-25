package com.boardly.event.models

data class GamePickEvent(val gameId: String = "", val type: GamePickType = GamePickType.FIRST)

enum class GamePickType {
    FIRST, SECOND, THIRD
}