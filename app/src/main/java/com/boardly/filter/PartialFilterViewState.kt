package com.boardly.filter

import com.boardly.retrofit.gamesearch.models.Game

sealed class PartialFilterViewState {

    abstract fun reduce(previousState: FilterViewState): FilterViewState

    data class GameDetailsFetched(private val game: Game) : PartialFilterViewState() {
        override fun reduce(previousState: FilterViewState): FilterViewState {
            return FilterViewState(game.image)
        }
    }

    class LocationProcessingState(private val processing: Boolean) : PartialFilterViewState() {
        override fun reduce(previousState: FilterViewState): FilterViewState {
            return previousState.copy(locationProcessing = processing)
        }
    }
}