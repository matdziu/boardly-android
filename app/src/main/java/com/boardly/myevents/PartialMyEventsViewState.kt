package com.boardly.myevents

import com.boardly.common.events.models.Event

sealed class PartialMyEventsViewState {

    abstract fun reduce(previousState: MyEventsViewState): MyEventsViewState

    class ProgressState : PartialMyEventsViewState() {
        override fun reduce(previousState: MyEventsViewState): MyEventsViewState {
            return previousState.copy(progress = true)
        }
    }

    data class EventsFetchedState(private val acceptedEvents: List<Event>,
                                  private val pendingEvents: List<Event>,
                                  private val createdEvents: List<Event>,
                                  private val interestingEvents: List<Event>) : PartialMyEventsViewState() {
        override fun reduce(previousState: MyEventsViewState): MyEventsViewState {
            return MyEventsViewState(acceptedEvents = acceptedEvents,
                    pendingEvents = pendingEvents,
                    createdEvents = createdEvents,
                    interestingEvents = interestingEvents)
        }
    }

    data class JoinRequestSent(private val render: Boolean = true) : PartialMyEventsViewState() {
        override fun reduce(previousState: MyEventsViewState): MyEventsViewState {
            return previousState.copy(joinRequestSent = render)
        }
    }
}