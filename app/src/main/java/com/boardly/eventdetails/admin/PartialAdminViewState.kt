package com.boardly.eventdetails.admin

import com.boardly.common.players.models.Player

sealed class PartialAdminViewState {

    abstract fun reduce(previousState: AdminViewState): AdminViewState

    class AcceptedProgressState : PartialAdminViewState() {
        override fun reduce(previousState: AdminViewState): AdminViewState {
            return previousState.copy(acceptedProgress = true)
        }
    }

    class PendingProgressState : PartialAdminViewState() {
        override fun reduce(previousState: AdminViewState): AdminViewState {
            return previousState.copy(pendingProgress = true)
        }
    }

    class AcceptedPlayersFetched(private val acceptedPlayers: List<Player>) : PartialAdminViewState() {
        override fun reduce(previousState: AdminViewState): AdminViewState {
            return previousState.copy(acceptedProgress = false,
                    acceptedPlayersList = acceptedPlayers)
        }
    }

    class PendingPlayersFetched(private val pendingPlayers: List<Player>) : PartialAdminViewState() {
        override fun reduce(previousState: AdminViewState): AdminViewState {
            return previousState.copy(pendingProgress = false,
                    pendingPlayersList = pendingPlayers)
        }
    }
}