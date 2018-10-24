package com.boardly.notify

import com.boardly.notify.models.NotifySettings
import com.boardly.retrofit.gamesearch.models.Game

sealed class PartialNotifyViewState {

    abstract fun reduce(previousState: NotifyViewState): NotifyViewState

    data class GameDetailsFetched(private val game: Game) : PartialNotifyViewState() {
        override fun reduce(previousState: NotifyViewState): NotifyViewState {
            return previousState.copy(gameImageUrl = game.image)
        }
    }

    data class NotifySettingsFetched(private val notifySettings: NotifySettings) : PartialNotifyViewState() {
        override fun reduce(previousState: NotifyViewState): NotifyViewState {
            return previousState.copy(progress = false, notifySettings = notifySettings)
        }
    }

    class PlacePickedState : PartialNotifyViewState() {
        override fun reduce(previousState: NotifyViewState): NotifyViewState {
            return previousState.copy(selectedPlaceValid = true)
        }
    }

    data class LocalValidation(private val selectedPlaceValid: Boolean) : PartialNotifyViewState() {
        override fun reduce(previousState: NotifyViewState): NotifyViewState {
            return previousState.copy(selectedPlaceValid = selectedPlaceValid)
        }
    }

    class ProgressState : PartialNotifyViewState() {
        override fun reduce(previousState: NotifyViewState): NotifyViewState {
            return previousState.copy(progress = true)
        }
    }

    class SuccessState : PartialNotifyViewState() {
        override fun reduce(previousState: NotifyViewState): NotifyViewState {
            return NotifyViewState(success = true)
        }
    }
}