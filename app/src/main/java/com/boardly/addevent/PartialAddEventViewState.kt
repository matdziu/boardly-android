package com.boardly.addevent

sealed class PartialAddEventViewState {

    abstract fun reduce(previousState: AddEventViewState): AddEventViewState

    class ProgressState : PartialAddEventViewState() {
        override fun reduce(previousState: AddEventViewState) = AddEventViewState(progress = true)
    }
}