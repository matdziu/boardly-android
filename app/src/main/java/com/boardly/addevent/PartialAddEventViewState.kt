package com.boardly.addevent

import com.boardly.retrofit.gamesearch.models.Game

sealed class PartialAddEventViewState {

    abstract fun reduce(previousState: AddEventViewState): AddEventViewState

    class ProgressState : PartialAddEventViewState() {
        override fun reduce(previousState: AddEventViewState) = AddEventViewState(progress = true)
    }

    class GameDetailsFetched(private val game: Game) : PartialAddEventViewState() {
        override fun reduce(previousState: AddEventViewState): AddEventViewState {
            return AddEventViewState(selectedGame = game)
        }
    }
}