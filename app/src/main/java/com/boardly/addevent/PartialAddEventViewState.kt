package com.boardly.addevent

import com.boardly.retrofit.gamesearch.models.Game

sealed class PartialAddEventViewState {

    abstract fun reduce(previousState: AddEventViewState): AddEventViewState

    class ProgressState : PartialAddEventViewState() {
        override fun reduce(previousState: AddEventViewState) = AddEventViewState(progress = true)
    }

    class GameDetailsFetched(private val game: Game) : PartialAddEventViewState() {
        override fun reduce(previousState: AddEventViewState): AddEventViewState {
            return previousState.copy(selectedGame = game)
        }
    }

    data class LocalValidation(private val eventNameValid: Boolean = true,
                               private val numberOfPlayersValid: Boolean = true,
                               private val selectedGameValid: Boolean = true,
                               private val selectedPlaceValid: Boolean = true) : PartialAddEventViewState() {
        override fun reduce(previousState: AddEventViewState): AddEventViewState {
            return previousState.copy(
                    eventNameValid = eventNameValid,
                    numberOfPlayersValid = numberOfPlayersValid,
                    selectedGameValid = selectedGameValid,
                    selectedPlaceValid = selectedPlaceValid)
        }
    }
}