package com.boardly.home

import com.boardly.common.events.models.Event

data class HomeViewState(val progress: Boolean = false,
                         val eventList: List<Event> = listOf())