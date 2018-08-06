package com.boardly.myevents

sealed class PartialMyEventsViewState {

    abstract fun reduce(previousState: MyEventsViewState): MyEventsViewState

    class ProgressState : PartialMyEventsViewState() {
        override fun reduce(previousState: MyEventsViewState): MyEventsViewState {
            return previousState.copy(progress = true)
        }
    }
}