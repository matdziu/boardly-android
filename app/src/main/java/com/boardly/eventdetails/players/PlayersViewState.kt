package com.boardly.eventdetails.players

import com.boardly.common.events.models.Event

data class PlayersViewState(val progress: Boolean = false,
                            val event: Event = Event())