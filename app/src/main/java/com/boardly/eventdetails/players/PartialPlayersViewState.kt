package com.boardly.eventdetails.players

import com.boardly.common.events.models.Event

sealed class PartialPlayersViewState {

    abstract fun reduce(previousState: PlayersViewState): PlayersViewState

    class ProgressState : PartialPlayersViewState() {
        override fun reduce(previousState: PlayersViewState): PlayersViewState {
            return previousState.copy(progress = true)
        }
    }

    class EventInfoFetchedState(private val event: Event) : PartialPlayersViewState() {
        override fun reduce(previousState: PlayersViewState): PlayersViewState {
            return PlayersViewState(event = event)
        }
    }
}