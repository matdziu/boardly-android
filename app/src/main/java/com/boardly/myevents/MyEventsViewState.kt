package com.boardly.myevents

import com.boardly.common.events.models.Event

data class MyEventsViewState(val progress: Boolean = false,
                             val eventsList: List<Event> = listOf())