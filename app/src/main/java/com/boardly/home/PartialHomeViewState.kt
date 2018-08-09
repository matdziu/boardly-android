package com.boardly.home

import com.boardly.common.events.models.Event

sealed class PartialHomeViewState {

    abstract fun reduce(previousState: HomeViewState): HomeViewState

    class ProgressState : PartialHomeViewState() {
        override fun reduce(previousState: HomeViewState) = HomeViewState(progress = true)
    }

    data class EventListState(val eventsList: List<Event>) : PartialHomeViewState() {
        override fun reduce(previousState: HomeViewState): HomeViewState = HomeViewState(eventList = eventsList)
    }

    class JoinRequestSent : PartialHomeViewState() {
        override fun reduce(previousState: HomeViewState): HomeViewState {
            return previousState
        }
    }
}