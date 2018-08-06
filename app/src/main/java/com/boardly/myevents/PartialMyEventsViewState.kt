package com.boardly.myevents

import com.boardly.myevents.models.Event

sealed class PartialMyEventsViewState {

    abstract fun reduce(previousState: MyEventsViewState): MyEventsViewState

    class ProgressState : PartialMyEventsViewState() {
        override fun reduce(previousState: MyEventsViewState): MyEventsViewState {
            return previousState.copy(progress = true)
        }
    }

    class EventsFetchedState(private val eventsList: List<Event>) : PartialMyEventsViewState() {
        override fun reduce(previousState: MyEventsViewState): MyEventsViewState {
            return MyEventsViewState(eventsList = eventsList)
        }
    }
}