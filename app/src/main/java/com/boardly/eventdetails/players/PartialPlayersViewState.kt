package com.boardly.eventdetails.players

sealed class PartialPlayersViewState {

    abstract fun reduce(previousState: PlayersViewState): PlayersViewState

    class ProgressState : PartialPlayersViewState() {
        override fun reduce(previousState: PlayersViewState): PlayersViewState {
            return previousState.copy(progress = true)
        }
    }
}