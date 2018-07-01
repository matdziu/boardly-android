package com.boardly.home

sealed class PartialHomeViewState {

    abstract fun reduce(previousState: HomeViewState): HomeViewState

    class ProgressState : PartialHomeViewState() {
        override fun reduce(previousState: HomeViewState) = HomeViewState(progress = true)
    }
}