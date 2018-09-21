package com.boardly.eventdetails.players

import com.boardly.common.events.models.Event
import com.boardly.common.players.models.Player

sealed class PartialPlayersViewState {

    abstract fun reduce(previousState: PlayersViewState): PlayersViewState

    class PlayersProgressState : PartialPlayersViewState() {
        override fun reduce(previousState: PlayersViewState): PlayersViewState {
            return previousState.copy(playersProgress = true)
        }
    }

    class EventProgressState : PartialPlayersViewState() {
        override fun reduce(previousState: PlayersViewState): PlayersViewState {
            return previousState.copy(eventProgress = true)
        }
    }

    data class AcceptedListState(private val playersList: List<Player>) : PartialPlayersViewState() {
        override fun reduce(previousState: PlayersViewState): PlayersViewState {
            return previousState.copy(playersProgress = false,
                    acceptedPlayersList = playersList)
        }
    }

    class RatingSent : PartialPlayersViewState() {
        override fun reduce(previousState: PlayersViewState): PlayersViewState {
            return previousState
        }
    }

    class KickedState : PartialPlayersViewState() {
        override fun reduce(previousState: PlayersViewState): PlayersViewState {
            return PlayersViewState(kicked = true)
        }
    }

    class LeftEventState : PartialPlayersViewState() {
        override fun reduce(previousState: PlayersViewState): PlayersViewState {
            return PlayersViewState(left = true)
        }
    }

    data class EventFetched(private val event: Event) : PartialPlayersViewState() {
        override fun reduce(previousState: PlayersViewState): PlayersViewState {
            return previousState.copy(eventProgress = false,
                    event = event)
        }
    }
}