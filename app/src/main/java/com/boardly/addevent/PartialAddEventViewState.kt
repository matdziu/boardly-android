package com.boardly.addevent

import com.boardly.retrofit.gamesearch.models.Game

sealed class PartialAddEventViewState {

    abstract fun reduce(previousState: AddEventViewState): AddEventViewState

    class ProgressState : PartialAddEventViewState() {
        override fun reduce(previousState: AddEventViewState) = previousState.copy(progress = true)
    }

    data class GameDetailsFetched(private val game: Game) : PartialAddEventViewState() {
        override fun reduce(previousState: AddEventViewState): AddEventViewState {
            return previousState.copy(selectedGame = game)
        }
    }

    class GamePickedState : PartialAddEventViewState() {
        override fun reduce(previousState: AddEventViewState): AddEventViewState {
            return previousState.copy(selectedGameValid = true)
        }
    }

    class PlacePickedState : PartialAddEventViewState() {
        override fun reduce(previousState: AddEventViewState): AddEventViewState {
            return previousState.copy(selectedPlaceValid = true)
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

    class SuccessState : PartialAddEventViewState() {
        override fun reduce(previousState: AddEventViewState): AddEventViewState {
            return AddEventViewState(
                    success = true,
                    selectedGame = previousState.selectedGame)
        }
    }
}