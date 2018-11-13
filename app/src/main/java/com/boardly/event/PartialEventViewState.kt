package com.boardly.event

import com.boardly.event.models.GamePickType
import com.boardly.retrofit.gameservice.models.Game

sealed class PartialEventViewState {

    abstract fun reduce(previousState: EventViewState): EventViewState

    class ProgressState : PartialEventViewState() {
        override fun reduce(previousState: EventViewState) = previousState.copy(progress = true)
    }

    data class GameDetailsFetched(private val game: Game, private val gamePickType: GamePickType) : PartialEventViewState() {
        override fun reduce(previousState: EventViewState): EventViewState {
            return when (gamePickType) {
                GamePickType.FIRST -> previousState.copy(selectedGame = game)
                GamePickType.SECOND -> previousState.copy(selectedGame2 = game)
                GamePickType.THIRD -> previousState.copy(selectedGame3 = game)
            }
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

    class RemovedState : PartialEventViewState() {
        override fun reduce(previousState: EventViewState): EventViewState {
            return EventViewState(removed = true)
        }
    }
}