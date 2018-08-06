package com.boardly.myevents

import com.boardly.myevents.models.Event

data class MyEventsViewState(val progress: Boolean = false,
                             val eventsList: List<Event> = listOf())