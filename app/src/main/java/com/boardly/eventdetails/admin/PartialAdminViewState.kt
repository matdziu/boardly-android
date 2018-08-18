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

    class AcceptedListState(private val acceptedPlayers: List<Player>) : PartialAdminViewState() {
        override fun reduce(previousState: AdminViewState): AdminViewState {
            return previousState.copy(acceptedProgress = false,
                    acceptedPlayersList = acceptedPlayers)
        }
    }

    class PendingListState(private val pendingPlayers: List<Player>) : PartialAdminViewState() {
        override fun reduce(previousState: AdminViewState): AdminViewState {
            return previousState.copy(pendingProgress = false,
                    pendingPlayersList = pendingPlayers)
        }
    }

    class PlayerKicked : PartialAdminViewState() {
        override fun reduce(previousState: AdminViewState): AdminViewState {
            return previousState
        }
    }

    class PlayerAccepted : PartialAdminViewState() {
        override fun reduce(previousState: AdminViewState): AdminViewState {
            return previousState
        }
    }
}