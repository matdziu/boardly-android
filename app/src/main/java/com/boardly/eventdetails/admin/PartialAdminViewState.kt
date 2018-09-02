package com.boardly.eventdetails.admin

import com.boardly.common.events.models.Event
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

    class EventProgressState : PartialAdminViewState() {
        override fun reduce(previousState: AdminViewState): AdminViewState {
            return previousState.copy(eventProgress = true)
        }
    }

    data class AcceptedListState(private val acceptedPlayers: List<Player>) : PartialAdminViewState() {
        override fun reduce(previousState: AdminViewState): AdminViewState {
            return previousState.copy(acceptedProgress = false,
                    acceptedPlayersList = acceptedPlayers)
        }
    }

    data class PendingListState(private val pendingPlayers: List<Player>) : PartialAdminViewState() {
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

    class RatingSent : PartialAdminViewState() {
        override fun reduce(previousState: AdminViewState): AdminViewState {
            return previousState
        }
    }

    class EventFetched(private val event: Event) : PartialAdminViewState() {
        override fun reduce(previousState: AdminViewState): AdminViewState {
            return previousState.copy(eventProgress = false,
                    event = event)
        }
    }
}