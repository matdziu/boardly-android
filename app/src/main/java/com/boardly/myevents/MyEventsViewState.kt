package com.boardly.myevents

import com.boardly.common.events.models.Event

data class MyEventsViewState(val progress: Boolean = false,
                             val acceptedEvents: List<Event> = listOf(),
                             val pendingEvents: List<Event> = listOf(),
                             val createdEvents: List<Event> = listOf())