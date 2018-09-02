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

    class EventProgressStaate : PartialPlayersViewState() {
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

    class KickState : PartialPlayersViewState() {
        override fun reduce(previousState: PlayersViewState): PlayersViewState {
            return PlayersViewState(kick = true)
        }
    }

    class EventFetched(private val event: Event) : PartialPlayersViewState() {
        override fun reduce(previousState: PlayersViewState): PlayersViewState {
            return previousState.copy(eventProgress = false,
                    event = event)
        }
    }
}