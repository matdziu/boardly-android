package com.boardly.filter

import com.boardly.retrofit.gamesearch.models.Game

sealed class PartialFilterViewState {

    abstract fun reduce(previousState: FilterViewState): FilterViewState

    class GameDetailsFetched(private val game: Game) : PartialFilterViewState() {
        override fun reduce(previousState: FilterViewState): FilterViewState {
            return FilterViewState(game.image)
        }
    }
}