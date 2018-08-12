package com.boardly.eventdetails.players

import com.boardly.common.events.models.Player

sealed class PartialPlayersViewState {

    abstract fun reduce(previousState: PlayersViewState): PlayersViewState

    class ProgressState : PartialPlayersViewState() {
        override fun reduce(previousState: PlayersViewState): PlayersViewState {
            return previousState.copy(progress = true)
        }
    }

    class AcceptedPlayersFetchedState(private val playersList: List<Player>) : PartialPlayersViewState() {
        override fun reduce(previousState: PlayersViewState): PlayersViewState {
            return PlayersViewState(acceptedPlayersList = playersList)
        }
    }
}