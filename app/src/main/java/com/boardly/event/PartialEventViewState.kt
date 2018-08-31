package com.boardly.event

import com.boardly.retrofit.gamesearch.models.Game

sealed class PartialEventViewState {

    abstract fun reduce(previousState: EventViewState): EventViewState

    class ProgressState : PartialEventViewState() {
        override fun reduce(previousState: EventViewState) = previousState.copy(progress = true)
    }

    data class GameDetailsFetched(private val game: Game) : PartialEventViewState() {
        override fun reduce(previousState: EventViewState): EventViewState {
            return previousState.copy(selectedGame = game)
        }
    }

    class GamePickedState : PartialEventViewState() {
        override fun reduce(previousState: EventViewState): EventViewState {
            return previousState.copy(selectedGameValid = true)
        }
    }

    class PlacePickedState : PartialEventViewState() {
        override fun reduce(previousState: EventViewState): EventViewState {
            return previousState.copy(selectedPlaceValid = true)
        }
    }

    data class LocalValidation(private val eventNameValid: Boolean = true,
                               private val selectedGameValid: Boolean = true,
                               private val selectedPlaceValid: Boolean = true) : PartialEventViewState() {
        override fun reduce(previousState: EventViewState): EventViewState {
            return previousState.copy(
                    eventNameValid = eventNameValid,
                    selectedGameValid = selectedGameValid,
                    selectedPlaceValid = selectedPlaceValid)
        }
    }

    class SuccessState : PartialEventViewState() {
        override fun reduce(previousState: EventViewState): EventViewState {
            return EventViewState(
                    success = true,
                    selectedGame = previousState.selectedGame)
        }
    }
}