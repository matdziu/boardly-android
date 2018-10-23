package com.boardly.notify

import com.boardly.retrofit.gamesearch.models.Game

sealed class PartialNotifyViewState {

    abstract fun reduce(previousState: NotifyViewState): NotifyViewState

    data class GameDetailsFetched(private val game: Game) : PartialNotifyViewState() {
        override fun reduce(previousState: NotifyViewState): NotifyViewState {
            return previousState.copy(gameImageUrl = game.image)
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