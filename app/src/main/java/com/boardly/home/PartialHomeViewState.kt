package com.boardly.home

import com.boardly.common.events.models.Event

sealed class PartialHomeViewState {

    abstract fun reduce(previousState: HomeViewState): HomeViewState

    class ProgressState : PartialHomeViewState() {
        override fun reduce(previousState: HomeViewState) = HomeViewState(progress = true)
    }

    data class EventListState(private val eventsList: List<Event>) : PartialHomeViewState() {
        override fun reduce(previousState: HomeViewState): HomeViewState = HomeViewState(eventList = eventsList)
    }

    data class JoinRequestSent(private val render: Boolean = true) : PartialHomeViewState() {
        override fun reduce(previousState: HomeViewState): HomeViewState {
            return previousState.copy(joinRequestSent = render)
        }
    }

    class LocationProcessingState : PartialHomeViewState() {
        override fun reduce(previousState: HomeViewState): HomeViewState {
            return HomeViewState(locationProcessing = true)
        }
    }
}